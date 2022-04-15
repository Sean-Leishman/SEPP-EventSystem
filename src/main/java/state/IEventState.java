package state;

import model.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IEventState is an interface representing the portion of application state that contains all the Event
 * information.
 */
public interface IEventState {

    /**
     * @return List of all registered Events in the application
     */
    List<Event> getAllEvents();

    /**
     * @param eventNumber unique event identifier to look up in the event state
     * @return Event corresponding to the specified event number if there is one, and null otherwise
     */
    Event findEventByNumber(long eventNumber);

    /**
     * Create a new NonTicketedEvent (includes generating a new unique event number) and add it to the event state
     * 
     * @param organiser organiser of the new event
     * @param title name of the new event
     * @param type type of the new event
     * @return The newly created NonTicketedEvent
     */
    NonTicketedEvent createNonTicketedEvent(EntertainmentProvider organiser, String title, EventType type);

    /**
     * Create a new TicketedEvent (includes generating a new unique event number) and add it to the event state
     * 
     * @param organiser organiser of the new event
     * @param title name of the new event
     * @param type type of the new event
     * @param ticketPrice price per ticket of the new event in GBP
     * @param numTickets maximum number of tickets for the new event, which are all initially available
     * @return The newly created TicketedEvent
     */
    TicketedEvent createTicketedEvent(EntertainmentProvider organiser, String title, EventType type, double ticketPrice, int numTickets);

    /**
     * Create a new EventPerformance (includes generating a new unique performance number) and add it to the
     * list of an Event's performances
     * 
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
     */
    EventPerformance createEventPerformance(Event event, String venueAddress, LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> performerNames, boolean hasSocialDistancing, boolean hasAirFiltration, boolean isOutdoors, int capacityLimit, int venueSize);

}
