import model.*;
import org.junit.jupiter.api.Test;
import state.BookingState;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestBookingState {
    /**
     * Test that the copy constructor works. We create an event and a performance first.
     * We then create three bookings, two of which are for the event just created.
     * After creating a copy, we perform another booking for the event, then assert that:
     * - its booking number is 4, due to the three previous bookings
     * - the new and old booking state both have 3 bookings for the Event in question.
     *
     * (Perhaps the copy of booking state should also deep-copy the list of bookings, such
     * that the original would have 2 bookings while the copy would have 3. However, this seemed
     * to not add any benefits, so we opted to only shallow-copy the list)
     */
    @Test
    void testDeepClone() {
        BookingState bookingState = new BookingState();

        TicketedEvent event1 = new TicketedEvent(1, null, "Fun Event", EventType.Movie, 0.50, 50);
        EventPerformance performance1 = new EventPerformance(
                42,
                event1,
                "Appleton Tower",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                List.of("The usual"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE);

        bookingState.createBooking(null, performance1, 5, 0.5);
        bookingState.createBooking(null, performance1, 5, 0.5);
        bookingState.createBooking(null, performance1, 5, 0.5);
        BookingState bookingState1 = new BookingState(bookingState);
        Booking b = bookingState1.createBooking(null, performance1, 5, 0.5);

        assertAll("Verify that deep cloning works",
                () -> assertEquals(4, b.getBookingNumber(), "Booking number is not properly incremented in cloned copy"),
                () -> assertEquals(3, bookingState.findBookingsByEventNumber(1).size(), "Old copy increased in size when it shouldn't"),
                () -> assertEquals(4, bookingState1.findBookingsByEventNumber(1).size(), "New copy did not increase in size when it should")
        );
    }

    /**
     * Test whether successive bookings are assigned an increasing booking number.
     * We create three bookings, and assert that b1.number + 1 = b2.number, and that b2.number + 1 = b3.number.
     */
    @Test
    void testBookingNumberIncreases() {
        BookingState bookingState = new BookingState();

        Consumer booker1 = new Consumer("Mr Pickles", "picker345@gmail.com", "0983453678", "picklesRCool34", "picker345@hotmail.com");

        Booking b1 = bookingState.createBooking(booker1, null, 2, 500);
        Booking b2 = bookingState.createBooking(booker1, null, 1, 500);
        Booking b3 = bookingState.createBooking(booker1, null, 5, 300);

        assertAll("Verify that the booking number increases in the same copy in successive bookings",
                () -> assertEquals(b1.getBookingNumber() + 1, b2.getBookingNumber()),
                () -> assertEquals(b2.getBookingNumber() + 1, b3.getBookingNumber())
        );
    }

    /**
     * Test whether we can search and find bookings by number, and that the search result is the same instance as
     * the original Booking (not a copy).
     */
    @Test
    void testBookingNumberSearch() {
        BookingState bookingState = new BookingState();

        Consumer booker1 = new Consumer("Mr Pickles", "picker345@gmail.com", "0983453678", "picklesRCool34", "picker345@hotmail.com");

        Booking b1 = bookingState.createBooking(booker1, null, 2, 500);
        Booking b2 = bookingState.createBooking(booker1, null, 1, 500);
        Booking b3 = bookingState.createBooking(booker1, null, 5, 300);

        assertAll("Verify that bookings can be found with findBookingByNumber",
                () -> assertEquals(bookingState.findBookingByNumber(b1.getBookingNumber()), b1),
                () -> assertEquals(bookingState.findBookingByNumber(b2.getBookingNumber()), b2),
                () -> assertEquals(bookingState.findBookingByNumber(b3.getBookingNumber()), b3)
        );
    }

    /**
     * Test whether searching for a non-existing booking number will return null.
     */
    @Test
    void testBookingNumberSearchNotExist() {
        BookingState bookingState = new BookingState();

        assertNull(bookingState.findBookingByNumber(0));
    }

    /**
     * Test whether we can search for a collection of bookings with an event number.
     * We create two Ticketed Events, the first has two performances and the second has one.
     * We create a booking for each performance, then test that the search results for each event returns 2 and 1.
     */
    @Test
    void testBookingSearchByEventNumber() {
        BookingState bookingState = new BookingState();
        EntertainmentProvider provider = new EntertainmentProvider(
            "No org",
            "Leith Walk",
            "a hat on the ground",
            "the best musicican ever",
            "busk@every.day",
            "When they say 'you can't do this': Ding Dong! You are wrong!",
            Collections.emptyList(),
            Collections.emptyList());
        TicketedEvent event1 = new TicketedEvent(1, provider, "Fun Event", EventType.Movie, 0.50, 50);
        EventPerformance performance1 = new EventPerformance(
                42,
                event1,
                "Appleton Tower",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                List.of("The usual"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE);
        EventPerformance performance2 = new EventPerformance(
                43,
                event1,
                "Appleton Tower",
                LocalDateTime.of(2040, 3, 21, 4, 20),
                LocalDateTime.of(2040, 3, 21, 7, 0),
                List.of("The usual"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE);
        TicketedEvent event2 = new TicketedEvent(2, provider, "Fun Event", EventType.Movie, 0.30, 50);
        EventPerformance performance3 = new EventPerformance(
                44,
                event2,
                "40 George Square",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                List.of("The usual"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE);
        Consumer booker = new Consumer("Mr Pickles", "picker345@gmail.com", "0983453678", "picklesRCool34", "picker345@hotmail.com");

        Booking booking1 = bookingState.createBooking(booker, performance1, 4, 0.50);
        Booking booking2 = bookingState.createBooking(booker, performance2, 5, 0.50);
        Booking booking3 = bookingState.createBooking(booker, performance3, 6, 0.30);

        assertAll("Verify that events created for separate events are registered under different events properly",
                () -> assertEquals(2, bookingState.findBookingsByEventNumber(1).size()),
                () -> assertEquals(1, bookingState.findBookingsByEventNumber(2).size())
        );
    }
}
