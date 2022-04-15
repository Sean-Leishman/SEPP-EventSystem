package command;

import controller.Context;
import logging.Logger;
import model.*;
import state.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * ListEventsOnGivenDateCommand extends ListEventsCommand to allow retrieving Events on
 * a given particular LocalDateTime +- 1 day
 */
public class ListEventsOnGivenDateCommand extends ListEventsCommand {
    /**
     * @param userEventsOnly whether to filter for user events
     * @param activeEventsOnly whether to filter for active events
     * @param searchDateTime chosen date to look for events if an Event includes at least 1
     * EventPerformance that starts up to 1 day before searchDateTime and ends up to 1 day after
     * searchDateTime, it will be included
     * @See Also: ListEventsCommand
     */
    LocalDateTime searchDateTime;
    public ListEventsOnGivenDateCommand(boolean userEventsOnly, boolean activeEventsOnly, LocalDateTime searchDateTime) {
        super(userEventsOnly, activeEventsOnly);
        this.searchDateTime = searchDateTime;
    }

    /**
     * @param event event to be checked to be within date and time
     * @param date LocalDateTime object that is the baseline to be checked against
     * @return true if the event times are within one day of the date parameter, false otherwise
     */
    private static boolean eventSatisifiesDateTime(Event event, LocalDateTime date){
        for (EventPerformance eventPerformance: event.getPerformances()){
            if (eventPerformance.getStartDateTime().isAfter(date.minusDays(1)) && eventPerformance.getStartDateTime().isBefore(date.plusDays(1))){
                if (eventPerformance.getEndDateTime().isBefore(date.plusDays(1)) && eventPerformance.getEndDateTime().isAfter(date.minusDays(1))){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @Overrides: execute in class ListEventsCommand
     * @param context object that provides access to global application state
     * {@inheritDoc}
     */
    public void execute(Context context) {
        List<Event> returnableEvents = new ArrayList<Event>();
        EventState copyEventState = new EventState(context.getEventState());
        for (Event e: copyEventState.getAllEvents()){
            if (eventSatisifiesDateTime(e, this.searchDateTime)){
                returnableEvents.add(e);
            }
        }

        if (context.getUserState().getCurrentUser() == null){
            Logger.getInstance().logAction("ListEventsCommand",LogStatus.LIST_USER_EVENTS_NOT_LOGGED_IN);
        }

        if (super.userEventsOnly){
            User user = context.getUserState().getCurrentUser();

            if (user instanceof Consumer){
                ConsumerPreferences preferences = ((Consumer) user).getPreferences();
                if (super.activeEventsOnly){
                    for (int i=0; i<returnableEvents.size(); i++){
                        if (super.eventSatisfiesPreferences(preferences,returnableEvents.get(i))){
                            if (!returnableEvents.get(i).getStatus().equals(EventStatus.ACTIVE)){
                                returnableEvents.remove(returnableEvents.get(i));
                            }
                        }
                        else{
                            returnableEvents.remove(returnableEvents.get(i));
                        }
                    }
                }
                else{
                    for (int i=0; i<returnableEvents.size(); i++){
                        if (!super.eventSatisfiesPreferences(preferences,returnableEvents.get(i))){
                            returnableEvents.remove(returnableEvents.get(i));
                        }
                    }
                }
                this.eventListResult = returnableEvents;
            }
            if (user instanceof EntertainmentProvider){
                List<Event> events = ((EntertainmentProvider) user).getEvents();
                if (super.activeEventsOnly){
                    for (int i=0; i<events.size(); i++){
                        if (!events.get(i).getStatus().equals(EventStatus.ACTIVE) & returnableEvents.contains(events.get(i))){
                            returnableEvents.remove(returnableEvents.get(i));
                        }
                    }
                }
                this.eventListResult = returnableEvents;
            }
        }
        else{
            this.eventListResult = new ArrayList<Event>();
            if (this.activeEventsOnly) {
                for (Event event : returnableEvents) {
                    if (event.getPerformances().stream().anyMatch(i -> i.getStartDateTime().isAfter(LocalDateTime.now()))) {
                        if (event.getStatus() == EventStatus.ACTIVE) {
                            this.eventListResult.add(event);
                        }
                    }
                }
            }
            else{
                this.eventListResult = returnableEvents;
            }
        }
        Logger.getInstance().logAction("ListEventsCommand", LogStatus.LIST_USER_EVENTS_SUCCESS);
    }

    /**
     * @Overrides: getResult in class ListEventsCommand
     * @return List of Events if successful and null otherwise
     * {@inheritDoc}
     */
    public List<Event> getResult() {
        return eventListResult;
    }

}
