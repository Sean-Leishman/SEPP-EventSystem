package command;

import controller.Context;
import logging.Logger;
import model.*;
import state.EventState;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ListEventsCommand allows anyone to get a list of Events available on the system.
 */
public class ListEventsCommand extends Object implements ICommand {
    Boolean userEventsOnly;
    Boolean activeEventsOnly;
    List<Event> eventListResult;
    /**
     * @param userEventsOnly if true, the returned events will be filtered depending on the logged-in user:
     * for EntertainmentProviders only the Events they have created,
     * and for Consumers only the Events that match their ConsumerPreferences
     * @param activeEventsOnly if true, returned Events will be filtered to contain only Events with
     * EventStatus.ACTIVE
     */
    public ListEventsCommand(boolean userEventsOnly, boolean activeEventsOnly) {
        this.userEventsOnly = userEventsOnly;
        this.activeEventsOnly = activeEventsOnly;
        this.eventListResult = null;
    }

    protected boolean eventSatisfiesPreferences(ConsumerPreferences preferences, Event event){
        Collection<EventPerformance> performances = event.getPerformances();
        for (EventPerformance e: performances){
            if ((e.hasAirFiltration() == preferences.preferAirFiltration | !preferences.preferAirFiltration)
                    && e.hasSocialDistancing() == preferences.preferSocialDistancing | !preferences.preferSocialDistancing
                    && e.isOutdoors() == preferences.preferOutdoorsOnly | !preferences.preferOutdoorsOnly
                    && e.getCapacityLimit() <= preferences.preferredMaxCapacity
                    && e.getVenueSize() <= preferences.preferredMaxVenueSize
            // And is in the future. Relevant to additional advice doc no20
                    && e.getStartDateTime().isAfter(LocalDateTime.now())){
                return true;
            }
        }
        return false;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that if userEventsOnly is set, the current user must be logged in
     * {@inheritDoc}
     */
    public void execute(Context context) {
        if (context.getUserState().getCurrentUser() == null){
            Logger.getInstance().logAction("ListEventsCommand",LogStatus.LIST_USER_EVENTS_NOT_LOGGED_IN);
            //assert false;
            return;
        }
        else if (this.userEventsOnly){

            List<Event> returnableEvents = new ArrayList<Event>();
            User user = context.getUserState().getCurrentUser();

            if (user instanceof Consumer){
                List<Event> events = context.getEventState().getAllEvents();
                ConsumerPreferences preferences = ((Consumer) user).getPreferences();
                if (this.activeEventsOnly){
                    for (Event event : events) {
                        if (this.eventSatisfiesPreferences(preferences, event)) {
                            if (event.getStatus().equals(EventStatus.ACTIVE)) {
                                returnableEvents.add(event);
                            }
                        }
                    }
                }
                else{
                    for (Event event : events) {
                        if (this.eventSatisfiesPreferences(preferences, event)) {
                            returnableEvents.add(event);
                        }
                    }
                }
                this.eventListResult = returnableEvents;
            }
            if (user instanceof EntertainmentProvider){
                System.out.println("here");
                List<Event> events = ((EntertainmentProvider) user).getEvents();
                System.out.println(events);
                if (this.activeEventsOnly){
                    for (Event event : events) {
                        if (event.getStatus().equals(EventStatus.ACTIVE)) {
                            System.out.println("active");
                            returnableEvents.add(event);
                        }
                    }
                    this.eventListResult = returnableEvents;
                }
                else{
                    this.eventListResult = events;
                }

            }
        }
        else{
            EventState copyEventState = new EventState(context.getEventState());
            if (this.activeEventsOnly) {
                this.eventListResult = new ArrayList<Event>();
                for (Event event : copyEventState.getAllEvents()) {
                    if (event.getPerformances().stream().anyMatch(i -> i.getStartDateTime().isAfter(LocalDateTime.now()))) {
                        if (event.getStatus() == EventStatus.ACTIVE) {
                            this.eventListResult.add(event);
                        }
                    }
                }
            }
            else{
                this.eventListResult = copyEventState.getAllEvents().stream().filter(
                        event -> event.getPerformances().stream().anyMatch(p -> p.getStartDateTime().isAfter(LocalDateTime.now()))).collect(Collectors.toList());
            }
        }

        Logger.getInstance().logAction("ListEventsCommand",LogStatus.LIST_USER_EVENTS_SUCCESS, Map.of("list",this.eventListResult,"active",this.activeEventsOnly,"user",this.userEventsOnly));
    }

    /**
     * @return List of Events if successful and null otherwise
     * {@inheritDoc}
     */
    public List<Event> getResult() {
        return eventListResult;
    }

    public enum LogStatus{
        LIST_USER_EVENTS_SUCCESS,
        LIST_USER_EVENTS_NOT_LOGGED_IN
    }

}
