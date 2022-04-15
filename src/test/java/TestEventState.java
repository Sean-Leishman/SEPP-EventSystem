import model.EntertainmentProvider;
import model.Event;
import model.EventType;
import org.junit.jupiter.api.Test;
import state.EventState;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestEventState{

    /**
     * Test the deepcopy constructors such that after 3 events are created and the eventState is
     * copied and then an event is created with this event state we assert that:
     *      - the newly created eventNumber begins at 4
     *      - sizeOfAllEvents in eventState is 3
     *      - sizeOfAllEvents in copied eventState is 4
     */
    @Test
    void deepCopy(){
        EventState eventState = new EventState();
        EntertainmentProvider entertainmentProvider = new EntertainmentProvider("Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com"));

        Event e1 = eventState.createNonTicketedEvent(entertainmentProvider,"Title", EventType.Dance);
        Event e2 = eventState.createNonTicketedEvent(entertainmentProvider,"Title2", EventType.Dance);
        Event e3 = eventState.createNonTicketedEvent(entertainmentProvider,"Title3", EventType.Dance);

        eventState.createEventPerformance(e1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50);

        EventState copyEventState = new EventState(eventState);
        Event e4 = copyEventState.createNonTicketedEvent(entertainmentProvider,"Title4", EventType.Dance);

        assertAll("Verify number of events in EventState across copy and original",
                () -> assertEquals(4, e4.getEventNumber()),
                () -> assertEquals(3,eventState.getAllEvents().size()),
                () -> assertEquals(4, copyEventState.getAllEvents().size())
        );
    }

    /**
     * Tests that everytime an event is created the next event created will have an event number one
     * greater than the previous event
     */
    @Test
    void testEventNumberIncreases(){
        EventState eventState = new EventState();

        EntertainmentProvider entertainmentProvider = new EntertainmentProvider("Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com"));

        Event e1 = eventState.createNonTicketedEvent(entertainmentProvider,"Title", EventType.Dance);
        Event e2 = eventState.createNonTicketedEvent(entertainmentProvider,"Title2", EventType.Dance);
        Event e3 = eventState.createNonTicketedEvent(entertainmentProvider,"Title3", EventType.Dance);

        assertAll("Verify eventID increases",
                () -> assertEquals(e1.getEventNumber() + 1,e2.getEventNumber()),
                () -> assertEquals(e2.getEventNumber() + 1,e3.getEventNumber())
        );
    }

    /**
     * Tests that whenever we search using an event number the corresponding event is returned
     */
    @Test
    void testEventNumberSearch(){
        EventState eventState = new EventState();

        EntertainmentProvider entertainmentProvider = new EntertainmentProvider("Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com"));

        Event e1 = eventState.createNonTicketedEvent(entertainmentProvider,"Title", EventType.Dance);
        Event e2 = eventState.createNonTicketedEvent(entertainmentProvider,"Title2", EventType.Dance);
        Event e3 = eventState.createNonTicketedEvent(entertainmentProvider,"Title3", EventType.Dance);

        assertAll("Verify event search works",
                () -> assertEquals(eventState.findEventByNumber(e1.getEventNumber()),e1),
                () -> assertEquals(eventState.findEventByNumber(e2.getEventNumber()),e2),
                () -> assertEquals(eventState.findEventByNumber(e3.getEventNumber()),e3)
        );
    }

    /**
     * Tests that searching for an event number that does not correspond with any Event will only
     * return null
     */
    @Test
    void testEventNumberDoesNotExist(){
        EventState eventState = new EventState();

        assertNull(eventState.findEventByNumber(2));
    }

    /**
     * Tests that creating events will keep adding to the array of allEvents in the EventState
     */
    @Test
    void testAddEventsToListofEvents(){
        EventState eventState = new EventState();

        EntertainmentProvider entertainmentProvider = new EntertainmentProvider("Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com"));

        Event e1 = eventState.createNonTicketedEvent(entertainmentProvider,"Title", EventType.Dance);
        Event e2 = eventState.createNonTicketedEvent(entertainmentProvider,"Title2", EventType.Dance);
        Event e3 = eventState.createNonTicketedEvent(entertainmentProvider,"Title3", EventType.Dance);

        assertEquals(3,eventState.getAllEvents().size());
    }

    /**
     * Tests that searching for an event number that does not correspond with any Event will only
     * return null
     */
    @Test
    void testEventPerformanceCreation(){
        EventState eventState = new EventState();

        EntertainmentProvider entertainmentProvider = new EntertainmentProvider("Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com"));

        Event e1 = eventState.createNonTicketedEvent(entertainmentProvider,"Title", EventType.Dance);

        eventState.createEventPerformance(e1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50);

        assertAll("Verify performances can be created in events",
                () -> assertEquals(1, e1.getPerformances().size(), "Performance count doesn't match the amount we added"),
                () -> assertEquals(e1, e1.getPerformances().get(0).getEvent(), "Parent event of a performance is different to the original event")
        );
    }
}
