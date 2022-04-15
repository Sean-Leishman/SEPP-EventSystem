package command;

import controller.Context;
import external.EntertainmentProviderSystem;
import external.MockEntertainmentProviderSystem;
import logging.Logger;
import model.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * BookEventCommand allows Consumers to book tickets for a specific
 * EventPerformance. The command applies for the currently logged-in user.
 */
public class BookEventCommand extends Object implements ICommand {
    int numTickektsRequested;
    Long bookingNumberResult;
    long eventNumber;
    long performanceNumber;

    /**
     * @param eventNumber         identifier of the TicketedEvent to book
     * @param performanceNumber   identifier of the EventPerformance of the event to book
     * @param numTicketsRequested number of tickets to book
     */
    public BookEventCommand(long eventNumber, long performanceNumber, int numTicketsRequested) {
        this.numTickektsRequested = numTicketsRequested;
        this.eventNumber = eventNumber;
        this.performanceNumber = performanceNumber;
        this.bookingNumberResult = null;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that currently logged-in user is a Consumer
     * @verifies.that event number corresponds to an existing event
     * @verifies.that event is a ticketed event
     * @verifies.that number of requested tickets is not less than 1
     * @verifies.that performance number corresponds to an existing performance of the event
     * @verifies.that the selected performance has not ended yet
     * @verifies.that the requested number of tickets is still available according to the organiser's EntertainmentProviderSystem
     * @verifies.that booking payment via the PaymentSystem succeeds
     * {@inheritDoc}
     */
    public void execute(Context context) {
        User user =  context.getUserState().getCurrentUser();
        Event event = context.getEventState().findEventByNumber(this.eventNumber);

        if (!(user instanceof Consumer)) {
            Logger.getInstance().logAction("BookEventCommand",LogStatus.BOOK_EVENT_USER_NOT_CONSUMER);
            //assert false;
            return;
        }

        else if (event == null) {
            Logger.getInstance().logAction("BookEventCommand",LogStatus.BOOK_EVENT_EVENT_NOT_FOUND);
            //assert false;
            return;
        }

        else if (!(event instanceof TicketedEvent)) {
            Logger.getInstance().logAction("BookEventCommand",LogStatus.BOOK_EVENT_NOT_A_TICKETED_EVENT);
            //assert false;
            return;
        }

        else if (event.getStatus() != EventStatus.ACTIVE){
            Logger.getInstance().logAction("BookEventCommand",LogStatus.BOOK_EVENT_EVENT_NOT_ACTIVE);
            //assert false;
            return;
        }

        else if (this.numTickektsRequested < 1) {
            Logger.getInstance().logAction("BookEventCommand",LogStatus.BOOK_EVENT_INVALID_NUM_TICKETS);
            //assert false;
            return;
        }

        else if (event.getPerformanceByNumber(this.performanceNumber) == null) {
            Logger.getInstance().logAction("BookEventCommand",LogStatus.BOOK_EVENT_PERFORMANCE_NOT_FOUND);
            //assert false;
            return;
        }

        else if (event.getPerformanceByNumber(this.performanceNumber).getEndDateTime().isBefore(LocalDateTime.now())) {
            Logger.getInstance().logAction("BookEventCommand",LogStatus.BOOK_EVENT_ALREADY_OVER);
            //assert false;
            return;
        }

        else if (event.getOrganiser().getProviderSystem().getNumTicketsLeft(this.eventNumber,this.performanceNumber) < this.numTickektsRequested){
            Logger.getInstance().logAction("BookEventCommand",LogStatus.BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT);
            //assert false;
            return;
        }

        else{
            EventPerformance performance = event.getPerformanceByNumber(this.performanceNumber);
            EntertainmentProvider entertainmentProvider = event.getOrganiser();
            EntertainmentProviderSystem entertainmentProviderSystem = entertainmentProvider.getProviderSystem();
            double price;
            if (((TicketedEvent) event).isSponsored()) {
                price = this.numTickektsRequested * ((TicketedEvent) event).getDiscountedTicketPrice();
            } else {
                price = this.numTickektsRequested * ((TicketedEvent) event).getOriginalTicketPrice();
            }
            this.bookingNumberResult = context.getBookingState().createBooking((Consumer) user,
                    performance,
                    this.numTickektsRequested,
                    price).getBookingNumber();
            if (!(context.getPaymentSystem().processPayment(user.getPaymentAccountEmail(), entertainmentProvider.getPaymentAccountEmail(), price))) {
                context.getBookingState().findBookingByNumber(this.bookingNumberResult).cancelPaymentFailed();
                Logger.getInstance().logAction("BookEventCommand", LogStatus.BOOK_EVENT_PAYMENT_FAILED);
                //assert false;
                return;
            } else {
                Logger.getInstance().logAction("BookEventCommand", LogStatus.BOOK_EVENT_SUCCESS, Map.of("Booking No", this.bookingNumberResult));
                ((Consumer) user).addBooking(context.getBookingState().findBookingByNumber(bookingNumberResult));
                entertainmentProviderSystem.recordNewBooking(this.eventNumber, this.performanceNumber, this.bookingNumberResult, ((Consumer) user).getName(), user.getEmail(), this.numTickektsRequested);
            }
        }
    }


    /**
     * @return A unique booking number corresponding to a Booking if successful and null otherwise
     * {@inheritDoc}
     */
    public Long getResult() {
        return bookingNumberResult;
    }
    public enum LogStatus{
        BOOK_EVENT_SUCCESS,
        BOOK_EVENT_USER_NOT_CONSUMER,
        BOOK_EVENT_NOT_A_TICKETED_EVENT,
        BOOK_EVENT_EVENT_NOT_ACTIVE,
        BOOK_EVENT_ALREADY_OVER,
        BOOK_EVENT_INVALID_NUM_TICKETS,
        BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT,
        BOOK_EVENT_PAYMENT_FAILED,
        BOOK_EVENT_EVENT_NOT_FOUND,
        BOOK_EVENT_PERFORMANCE_NOT_FOUND,
    }
}
