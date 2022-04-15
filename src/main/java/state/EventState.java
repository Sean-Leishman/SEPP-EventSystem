package state;

import model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * EventState is a concrete implementation of IEventState.
 */
public class EventState extends Object implements IEventState {

    private long nextEventId;
    private long nextPerformanceId;
    List<Event> events;

    /**
     * Create a new EventState with an empty list of events, which keeps track of the next event and performance numbers
     * it will generate, starting from 1 and incrementing by 1 each time when requested
     */

    public EventState() {
        this.events = new ArrayList<Event>();
        this.nextEventId = 1;
        this.nextPerformanceId = 1;
    }

    /**
     * Copy constructor to make a deep copy of another EventState instance
     * 
     * @param other instance to copy
     */
    public EventState(IEventState other) {
        EventState otherEventState = (EventState) other;
        this.events = new ArrayList<>();
        for (Event event : otherEventState.getAllEvents()){
            this.events.add(event);
        }
        this.nextPerformanceId = otherEventState.nextPerformanceId;
        this.nextEventId = otherEventState.nextEventId;
    }


    /**
     * @return List of all registered Events in the application
     */
    public List<Event> getAllEvents() {
        return this.events;
    }

    /**
     * @param eventNumber unique event identifier to look up in the event state
     * @return Event corresponding to the specified event number if there is one, and null otherwise
     */
    public Event findEventByNumber(long eventNumber) {
        for (Event event : this.events) {
            if (event.getEventNumber() == eventNumber){
                return event;
            }
        }
        return null;
    }

    /**
     * @param organiser organiser of the new event
     * @param title name of the new event
     * @param type type of the new event
     * @return The newly created NonTicketedEvent
     * {@inheritDoc}
     */
    public NonTicketedEvent createNonTicketedEvent(EntertainmentProvider organiser, String title, EventType type) {
        NonTicketedEvent event = new NonTicketedEvent(this.nextEventId, organiser, title, type);
        this.nextEventId += 1;
        this.events.add(event);
        return event;
    }

    /**
     * @param organiser organiser of the new event
     * @param title name of the new event
     * @param type type of the new event
     * @param ticketPrice price per ticket of the new event in GBP
     * @param numTickets maximum number of tickets for the new event, which are all initially available
     * @return The newly created TicketedEvent
     * {@inheritDoc}
     */
    public TicketedEvent createTicketedEvent(EntertainmentProvider organiser, String title, EventType type, double ticketPrice, int numTickets) {
        TicketedEvent event = new TicketedEvent(this.nextEventId, organiser,title,type,ticketPrice,numTickets);
        this.nextEventId += 1;
        this.events.add(event);
        return event;
    }

    /**
     * @param event event to add the new performance to
     * @param venueAddress address where the new performance will take place
     * @param startDateTime date and time when the new performance will begin
     * @param endDateTime date and time when the new performance will end
     * @param performerNames list of names of those who will be performing
     * @param hasSocialDistancing whether the new performance will have social distancing in place
     * @param hasAirFiltration whether the new performance will have air filtration in place
     * @param isOutdoors whether the new performance will be outdoors
     * @param capacityLimit the maximum number of attendees that will be allowed at this performance
     * @param venueSize the maximum number of visitors that can legally attend the venue (all other restrictions aside)
     * @return The newly created EventPerformance
     * {@inheritDoc}
     */
    public EventPerformance createEventPerformance(Event event, String venueAddress, LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> performerNames, boolean hasSocialDistancing, boolean hasAirFiltration, boolean isOutdoors, int capacityLimit, int venueSize) {
        EventPerformance performance = new EventPerformance(this.nextPerformanceId, event, venueAddress, startDateTime, endDateTime,performerNames,hasSocialDistancing,hasAirFiltration,isOutdoors,capacityLimit,venueSize);
        this.nextPerformanceId += 1;
        event.addPerformance(performance);
        return performance;
    }

}
