package state;

import model.Booking;
import model.EventPerformance;
import model.Consumer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BookingState is a concrete implementation of IBookingState.
 */
public class BookingState extends Object implements IBookingState {

    private long nextBookingNumber;
    private List<Booking> bookings;

    /**
     * Create a new BookingState that keeps track of the next booking number it will generate (starting from 1 and
     * incrementing by 1 each time a new booking number is needed), and an empty list of bookings
     */

    public BookingState() {
        this.nextBookingNumber = 1;
        this.bookings = new ArrayList<Booking>();
    }

    /**
     * Copy constructor to make a deep copy of another BookingState instance
     * 
     * @param other instance to copy
     */
    public BookingState(IBookingState other) {
        BookingState otherBookingState = (BookingState) other;
        this.nextBookingNumber = otherBookingState.nextBookingNumber;
        this.bookings = new ArrayList<>();
        for (Booking booking: otherBookingState.bookings){
            this.bookings.add(booking);
        }
    }

    /**
     * @param bookingNumber unique booking identifier to look up in the booking state
     * @return Booking corresponding to the bookingNumber if there is one, and null otherwise
     * {@inheritDoc}
     */
    public Booking findBookingByNumber(long bookingNumber) {
        for (Booking booking : this.bookings) {
            if (booking.getBookingNumber() == bookingNumber){
                return booking;
            }
        }
        return null;
    }

    /**
     * @param eventNumber unique event identifier to find bookings for
     * @return List of Bookings for the TicketedEvent corresponding to the provided event number
     * {@inheritDoc}
     */
    public List<Booking> findBookingsByEventNumber(long eventNumber) {
        List<Booking> matchingBookings = new ArrayList<Booking>();
        for (Booking booking : this.bookings) {
            if (booking.getEventPerformance().getEvent().getEventNumber() == eventNumber){
                matchingBookings.add(booking);
            }
        }
        return matchingBookings;
    }

    /**
     * @param booker Consumer who made the booking
     * @param performance EventPerformance that the booking is for
     * @param numTickets number of tickets booked
     * @param amountPaid amount paid for the booking in GBP
     * @return The newly created Booking instance
     * {@inheritDoc}
     */
    public Booking createBooking(Consumer booker, EventPerformance performance, int numTickets, double amountPaid) {
        Booking newBooking = new Booking(this.nextBookingNumber, booker, performance, numTickets, amountPaid, LocalDateTime.now());
        this.nextBookingNumber += 1;
        this.bookings.add(newBooking);
        return newBooking;
    }

}
