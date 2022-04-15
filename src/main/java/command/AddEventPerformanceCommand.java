package command;

import controller.Context;
import logging.Logger;
import model.EntertainmentProvider;
import model.Event;
import model.EventPerformance;
import model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AddEventPerformanceCommand allows EntertainmentProviders to add performances
 * to an existing event that they have created. The command applies for the currently logged-in user.
 */
public class AddEventPerformanceCommand extends Object implements ICommand {
    long eventNumber;
    String venueAddress;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    List<String> performerNames;
    boolean hasSocialDistancing;
    boolean hasAirFiltration;
    boolean isOutdoors;
    int capacityLimit;
    int venueSize;
    EventPerformance eventPerformanceResult = null;

    /**
     * @param eventNumber identifier of the Event that has previously been created
     * @param venueAddress indicates where this performance will take place, would be displayed to users in app
     * @param startDateTime indicates the date and time when this performance is due to start
     * @param endDateTime indicates the date and time when this performance is due to end
     * @param performerNames a list of names of those who will be performing, e.g.
     * List.of("Bon Jovi", "The Scorpions")
     * @param hasSocialDistancing indicates whether social distancing will be enforced at this performance.
     * Users can filter events based on this field if they have Covid-19 safety preferences
     * @param hasAirFiltration indicates whether air filtration will be in place at this performance.
     * Users can filter events based on this field if they have Covid-19 safety preferences
     * @param isOutdoors indicates whether this performance will take place outdoors. Normally would imply
     * hasAirFiltration, but kept as a separate field for simplicity.
     * Users can filter events based on this field if they have Covid-19 safety preferences
     * @param capacityLimit indicates the maximum number of people that will be allowed to book this performance
     * Implementing the limit is delegated to the external EntertainmentProviderSystem.
     * Users can filter events based on this field if they have Covid-19 safety preferences
     * @param venueSize indicates the maximum number of people that would legally allowed in the venue (ignoring
     * capacityLimit). If there is no social distancing, the two fields will usually be equal.
     * Users can filter events based on this field if they have Covid-19 safety preferences
     */
    public AddEventPerformanceCommand(long eventNumber, String venueAddress, LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> performerNames, boolean hasSocialDistancing, boolean hasAirFiltration, boolean isOutdoors, int capacityLimit, int venueSize) {
        this.eventNumber = eventNumber;
        this.venueAddress = venueAddress;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.performerNames = performerNames;
        this.hasSocialDistancing = hasSocialDistancing;
        this.hasAirFiltration = hasAirFiltration;
        this.isOutdoors = isOutdoors;
        this.capacityLimit = capacityLimit;
        this.venueSize = venueSize;
    }

    /**
     *
     * @param checkEvent Event to be checked
     * @param setEvent Event that is having a Performance added to it
     * @return true if checkEvent has same title as setEvent and if a performance of checkEvent occurs at the same
     *          time as the to be created Performance. false otherwise
     */
    private boolean checkEventClash(Event checkEvent, Event setEvent){
        if (checkEvent.getTitle().equals(setEvent.getTitle()) & checkEvent.getEventNumber() != setEvent.getEventNumber()){
            for (EventPerformance ep: checkEvent.getPerformances()){
                if (ep.getStartDateTime().isEqual(this.startDateTime) & ep.getEndDateTime().isEqual(this.endDateTime)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that performance startDateTime is not after endDateTime
     * @verifies.that capacityLimit is not less than 1
     * @verifies.that venueSize is not less than 1
     * @verifies.that currently logged-in user is an EntertainmentProvider
     * @verifies.that the eventId corresponds to an existing event
     * @verifies.that the current user is the event organiser
     * @verifies.that no other event with the same title has a performance with the same startDateTime and endDateTime
     * {@inheritDoc}
     */
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();
        Event event = context.getEventState().findEventByNumber(this.eventNumber);
        if (startDateTime.isAfter(endDateTime)){
            Logger.getInstance().logAction("AddPerformanceCommand",LogStatus.ADD_PERFORMANCE_START_AFTER_END);
            //assert false:Logger.getInstance().getLog();
            return;
        }
        else if (capacityLimit < 1){
            Logger.getInstance().logAction("AddPerformanceCommand",LogStatus.ADD_PERFORMANCE_CAPACITY_LESS_THAN_1);
            //assert false;
            return;
        }
        else if (venueSize < 1){
            Logger.getInstance().logAction("AddPerformanceCommand",LogStatus.ADD_PERFORMANCE_VENUE_SIZE_LESS_THAN_1);
            //assert false;
            return;
        }
        else if (context.getUserState().getCurrentUser() == null){
            Logger.getInstance().logAction("AddPerformanceCommand",LogStatus.ADD_PERFORMANCE_USER_NOT_LOGGED_IN);
            //assert false;
            return;
        }
        else if (!(user instanceof EntertainmentProvider)){
            Logger.getInstance().logAction("AddPerformanceCommand",LogStatus.ADD_PERFORMANCE_USER_NOT_ENTERTAINMENT_PROVIDER);
            //assert false;
            return;
        }
        else if (event == null){
            Logger.getInstance().logAction("AddPerformanceCommand",LogStatus.ADD_PERFORMANCE_EVENT_NOT_FOUND);
            //assert false;
            return;
        }
        else if (user != event.getOrganiser()){
            Logger.getInstance().logAction("AddPerformanceCommand",LogStatus.ADD_PERFORMANCE_USER_NOT_EVENT_ORGANISER);
            //assert false;
            return;
        }
        else {
            // Checks no event with the same title with the same performance start and end time exists
            boolean isClash = false;
            for (Event e : context.getEventState().getAllEvents()) {
                if (checkEventClash(e, event)) {
                    isClash = true;
                }
            }
            if (!isClash){
                this.eventPerformanceResult = context.getEventState().createEventPerformance(event, this.venueAddress
                        , this.startDateTime, this.endDateTime, this.performerNames
                        , this.hasSocialDistancing, this.hasAirFiltration, this.isOutdoors
                        , this.capacityLimit, this.venueSize);
                ((EntertainmentProvider) user).getProviderSystem().recordNewPerformance(this.eventNumber,
                        this.eventPerformanceResult.getPerformanceNumber(),
                        this.startDateTime,
                        this.endDateTime);
                Logger.getInstance().logAction("AddPerformanceCommand", LogStatus.ADD_PERFORMANCE_SUCCESS);
            }
            else{
                Logger.getInstance().logAction("AddPerformanceCommand", LogStatus.ADD_PERFORMANCE_EVENTS_WITH_SAME_TITLE_CLASH);
                //assert false;
                return;
            }
        }
    }

    /**
     * @return The EventPerformance created by the command if successful and null otherwise
     * {@inheritDoc}
     */
    public EventPerformance getResult() {
        return eventPerformanceResult;
    }

    public enum LogStatus {
        ADD_PERFORMANCE_CAPACITY_LESS_THAN_1,
        ADD_PERFORMANCE_EVENTS_WITH_SAME_TITLE_CLASH,
        ADD_PERFORMANCE_EVENT_NOT_FOUND,
        ADD_PERFORMANCE_START_AFTER_END,
        ADD_PERFORMANCE_SUCCESS,
        ADD_PERFORMANCE_USER_NOT_ENTERTAINMENT_PROVIDER,
        ADD_PERFORMANCE_USER_NOT_EVENT_ORGANISER,
        ADD_PERFORMANCE_USER_NOT_LOGGED_IN,
        ADD_PERFORMANCE_VENUE_SIZE_LESS_THAN_1,
    }
}
