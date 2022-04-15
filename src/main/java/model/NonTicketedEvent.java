package model;

/**
 * NonTicketedEvent is an event open to everyone, it does not accept bookings.
 */
public class NonTicketedEvent extends Event {

    public NonTicketedEvent(long eventNumber, EntertainmentProvider organiser, String title, EventType type) {
        super(eventNumber, organiser, title, type);
    }

    @Override
    public String toString() {
        return "NonTicketedEvent{}";
    }
}
