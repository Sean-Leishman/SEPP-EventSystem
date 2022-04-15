package state;

import model.Booking;
import model.Consumer;
import model.EventPerformance;

import java.util.List;

/**
 * IBookingState is an interface representing the portion of application state that contains all the
 * Booking information.
 */
public interface IBookingState {


    /**
     * Get a Booking with the specified booking number
     * 
     * @param bookingNumber unique booking identifier to look up in the booking state
     * @return Booking corresponding to the bookingNumber if there is one, and null otherwise
     */
    Booking findBookingByNumber(long bookingNumber);

    /**
     * Get a list of all the Bookings for a TicketedEvent with the given event number
     * 
     * @param eventNumber unique event identifier to find bookings for
     * @return List of Bookings for the TicketedEvent corresponding to the provided event number
     */
    List<Booking> findBookingsByEventNumber(long eventNumber);

    /**
     * Create a new Booking (includes generating a new unique booking number) and add it to the booking state
     * 
     * @param booker Consumer who made the booking
     * @param performance EventPerformance that the booking is for
     * @param numTickets number of tickets booked
     * @param amountPaid amount paid for the booking in GBP
     * @return The newly created Booking instance
     */
    Booking createBooking(Consumer booker, EventPerformance performance, int numTickets, double amountPaid);

}
