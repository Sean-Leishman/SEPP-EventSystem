package external;

import java.time.LocalDateTime;

/**
 * Interface for the external EntertainmentProviderSystem, it allows informing about changes made to events and
 * bookings on our system, and querying for up-to-date number of available tickets for a given EventPerformance
 * (in case ticket sales are made outside this system). Each EntertainmentProvider has their own
 * EntertainmentProviderSystem and only relevant information for that provider is sent/queried by the application.
 */
public interface EntertainmentProviderSystem {
    /**
     * Inform the EntertainmentProviderSystem that a new Event was created on this application.
     * 
     * @param eventNumber unique event identifier corresponding to the newly created event
     * @param title title of the newly created event
     * @param numTickets number of tickets initially available in the newly created event
     */
    void recordNewEvent(long eventNumber, String title, int numTickets);

    /**
     * Inform the EntertainmentProviderSystem that an Event was cancelled on this application.
     * 
     * @param eventNumber unique identifier corresponding to the cancelled Event
     * @param message a message from the organiser to all the Consumers who had Bookings for
     * any EventPerformances of this event
     */
    void cancelEvent(long eventNumber, String message);

    /**
     * Inform the EntertainmentProviderSystem that a new EventPerformance was added in this
     * application
     * 
     * @param eventNumber unique event identifier corresponding to the event to which the performance was added
     * @param performanceNumber unique performance identifier corresponding to the newly added performance
     * @param startDateTime date and time when the performance is due to start
     * @param endDateTime date and time when the performance is due to end
     */
    void recordNewPerformance(long eventNumber, long performanceNumber, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * Query the EntertainmentProviderSystem for the latest number of available tickets for a given
     * EventPerformance
     * 
     * @param eventNumber unique event identifier corresponding to the event to query
     * @param performanceNumber unique performance identifier corresponding to the event performance to query
     * @return number of available tickets
     */
    int getNumTicketsLeft(long eventNumber, long performanceNumber);

    /**
     * Inform the EntertainmentProviderSystem that a new Booking was made by a Consumer
     * for an EventPerformance of a TicketedEvent
     * 
     * @param eventNumber unique event identifier corresponding to the event the booking was made for
     * @param performanceNumber unique performance identifier corresponding to the performance the booking was made for
     * @param bookingNumber unique booking identifier corresponding to the new booking
     * @param consumerName full name of the consumer who made the booking
     * @param consumerEmail email address of the consumer who made the booking
     * @param bookedTickets number of booked tickets
     */
    void recordNewBooking(long eventNumber, long performanceNumber, long bookingNumber, String consumerName, String consumerEmail, int bookedTickets);

    /**
     * Inform the EntertainmentProviderSystem that a Booking has been cancelled
     * 
     * @param bookingNumber unique booking identifier corresponding to the cancelled booking
     */
    void cancelBooking(long bookingNumber);

    /**
     * Inform the EntertainmentProviderSystem that a GovernmentRepresentative has approved a
     * SponsorshipRequest for a TicketedEvent
     * 
     * @param eventNumber unique event identifier corresponding to the event that was sponsored
     * @param sponsoredPricePercent sponsorship amount in % of the maximum number of tickets * ticket price
     */
    void recordSponsorshipAcceptance(long eventNumber, int sponsoredPricePercent);

    /**
     * Inform the EntertainmentProviderSystem that a GovernmentRepresentative has rejected a
     * SponsorshipRequest for a TicketedEvent
     * 
     * @param eventNumber unique event identifier corresponding to the event that was not sponsored
     */
    void recordSponsorshipRejection(long eventNumber);

}
