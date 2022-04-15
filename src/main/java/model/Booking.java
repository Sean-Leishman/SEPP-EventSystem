package model;

import java.time.LocalDateTime;

/**
 * Booking represents a booking made by a Consumer for a TicketedEvent EventPerformance.
 */
public class Booking extends Object {

    private long bookingNumber;
    private Consumer booker;
    private EventPerformance performance;
    private int numTickets;
    private double amountPaid;
    private LocalDateTime bookingDateTime;
    private BookingStatus status;

    /**
     * @param bookingNumber unique identifier for this booking
     * @param booker the Consumer who made this booking
     * @param performance the EventPerformance this booking is for
     * @param numTickets the number of booked tickets
     * @param amountPaid the amount paid (needed in case of a refund)
     * @param bookingDateTime the date and time when this booking was made
     */

    public Booking(long bookingNumber, Consumer booker, EventPerformance performance, int numTickets, double amountPaid, LocalDateTime bookingDateTime) {
        this.bookingNumber = bookingNumber;
        this.booker = booker;
        this.performance = performance;
        this.numTickets = numTickets;
        this.amountPaid = amountPaid;
        this.bookingDateTime = bookingDateTime;
        this.status = BookingStatus.Active;
    }


    public long getBookingNumber() {
        return this.bookingNumber;
    }


    public BookingStatus getStatus() {
        return this.status;
    }


    public Consumer getBooker() {
        return this.booker;
    }


    public EventPerformance getEventPerformance() {
        return this.performance;
    }


    public double getAmountPaid() {
        return this.amountPaid;
    }

    /**
     * Sets the status to BookingStatus.CancelledByConsumer.
     */
    public void cancelByConsumer() {
        this.status = BookingStatus.CancelledByConsumer;
    }

    /**
     * Sets the status to BookingStatus.PaymentFailed.
     */
    public void cancelPaymentFailed() {
        this.status = BookingStatus.PaymentFailed;
    }

    /**
     * Sets the status to BookingStatus.CancelledByProvider.
     */
    public void cancelByProvider() {
        this.status = BookingStatus.CancelledByProvider;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingNumber=" + bookingNumber +
                ", booker=" + booker +
                ", performance=" + performance +
                ", numTickets=" + numTickets +
                ", amountPaid=" + amountPaid +
                ", bookingDateTime=" + bookingDateTime +
                ", status=" + status +
                '}';
    }
}
