package command;

import controller.Context;
import logging.Logger;
import model.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * CancelEventCommand allows EntertainmentProviders to cancel a previously added
 * Event (and all corresponding EventPerformances). This cancels and refunds all bookings linked
 * to the performances (if ticketed), and refunds government sponsorship (if the event was sponsored).
 */
public class CancelEventCommand extends Object implements ICommand {
    private long eventNumber;
    private String organiserMessage;
    private boolean successResult;
    /**
     * @param eventNumber identifier of the Event to cancel
     * @param organiserMessage message from the organiser to the Consumers who had
     * Bookings for the event
     */
    public CancelEventCommand(long eventNumber, String organiserMessage) {
        this.eventNumber = eventNumber;
        this.organiserMessage = organiserMessage;
    }

    /**
     *
     * @param context global state wrapper
     * @param event the event which is to be cancelled
     * @return true if the user can proceed to cancel the event using conditions listed in the next function. false, otherwise
     */
    private boolean userIsAllowedToCancelEvent(Context context, Event event){
        User user = context.getUserState().getCurrentUser();
        if (this.organiserMessage.isEmpty()){
            Logger.getInstance().logAction("CancelEventCommand",LogStatus.CANCEL_EVENT_MESSAGE_MUST_NOT_BE_BLANK);
            return false;
        }
        if (!(user instanceof EntertainmentProvider)){
            Logger.getInstance().logAction("CancelEventCommand",LogStatus.CANCEL_EVENT_USER_NOT_ENTERTAINMENT_PROVIDER);
            return false;
        }
        if (event == null){
            Logger.getInstance().logAction("CancelEventCommand",LogStatus.CANCEL_EVENT_EVENT_NOT_FOUND);
            return false;
        }
        if (event.getStatus() != EventStatus.ACTIVE){
            Logger.getInstance().logAction("CancelEventCommand",LogStatus.CANCEL_EVENT_NOT_ACTIVE);
            return false;
        }
        if (!user.equals(event.getOrganiser())){
            Logger.getInstance().logAction("CancelEventCommand",LogStatus.CANCEL_EVENT_USER_NOT_ORGANISER);
            return false;
        }
        // Not allowed to cancel if any performance has started
        for (EventPerformance ep: event.getPerformances()){
            if (ep.getStartDateTime().isBefore(LocalDateTime.now())){
                Logger.getInstance().logAction("CancelEventCommand",LogStatus.CANCEL_EVENT_PERFORMANCE_ALREADY_STARTED);
                return false;
            }
        }
        /* Refund process
        boolean isRefundProcessed = context.getPaymentSystem().processRefund("ds",user.getPaymentAccountEmail(),((EntertainmentProvider) user).getProviderSystem().)
        */
        return true;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that the organiser message is not blank
     * @verifies.that currently logged-in user in an EntertainmentProvider
     * @verifies.that provided event number corresponds to an existing event
     * @verifies.that the event is active
     * @verifies.that the logged-in user is the organiser of the event
     * @verifies.that the event has no performances that have already started or ended
     * @verifies.that if the event is ticketed, that the sponsorship amount is successfully refunded to the government
     * {@inheritDoc}
     */
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();
        Event event = context.getEventState().findEventByNumber(this.eventNumber);

        if (userIsAllowedToCancelEvent(context,event)){
            if (event instanceof TicketedEvent) {
                if (((TicketedEvent) event).isSponsored()) {
                    double price = (((TicketedEvent) event).getOriginalTicketPrice() - ((TicketedEvent) event).getDiscountedTicketPrice())
                            * ((TicketedEvent) event).getNumTickets();
                    if (context.getPaymentSystem().processRefund(((TicketedEvent) event).getSponsorAccountEmail(),
                            user.getPaymentAccountEmail(), price)){
                        this.successResult = true;
                        Logger.getInstance().logAction("CancelEventCommand", LogStatus.CANCEL_EVENT_REFUND_SPONSORSHIP_SUCCESS);
                    }
                    else{
                        this.successResult = false;
                        Logger.getInstance().logAction("CancelEventComman",LogStatus.CANCEL_EVENT_REFUND_SPONSORSHIP_FAILED);
                        // Do not allow cancellation if refund fails
                        return;
                    }
                }
                else{
                    this.successResult = true;
                }
                for (Booking booking : context.getBookingState().findBookingsByEventNumber(this.eventNumber)) {
                    if (context.getPaymentSystem().processRefund(booking.getBooker().getPaymentAccountEmail(), event.getOrganiser().getPaymentAccountEmail(), booking.getAmountPaid())) {
                        Logger.getInstance().logAction("CancelEventCommand", LogStatus.CANCEL_EVENT_REFUND_BOOKING_SUCCESS);
                        booking.cancelByProvider();
                        this.successResult = true;
                    } else {
                        // Booking cancelled irrespective of whether payment succeeds
                        Logger.getInstance().logAction("CancelEventCommand", LogStatus.CANCEL_EVENT_REFUND_BOOKING_ERROR);
                        booking.cancelByProvider();
                        this.successResult = true;
                    }
                }
            }
            else{
                this.successResult = true;
            }
        }
        else{
            this.successResult = false;
            //assert false:Logger.getInstance().getLog();
        }
        if (this.successResult){
            System.out.println("her"+((EntertainmentProvider) context.getUserState().getCurrentUser()).getEvents().get(0).getStatus());
            event.cancel();
            System.out.println("her"+((EntertainmentProvider) context.getUserState().getCurrentUser()).getEvents().get(0).getStatus());
            event.getOrganiser().getProviderSystem().cancelEvent(this.eventNumber,this.organiserMessage);
            Logger.getInstance().logAction("CancelEventCommand",LogStatus.CANCEL_EVENT_SUCCESS);
        }

    }

    /**
     * @return True if successful and false otherwise
     * {@inheritDoc}
     */
    public Boolean getResult() {
        return this.successResult;
    }

    public enum LogStatus {
        CANCEL_EVENT_EVENT_NOT_FOUND,
        CANCEL_EVENT_MESSAGE_MUST_NOT_BE_BLANK,
        CANCEL_EVENT_NOT_ACTIVE,
        CANCEL_EVENT_PERFORMANCE_ALREADY_STARTED,
        CANCEL_EVENT_REFUND_BOOKING_ERROR,
        CANCEL_EVENT_REFUND_BOOKING_SUCCESS,
        CANCEL_EVENT_REFUND_SPONSORSHIP_FAILED,
        CANCEL_EVENT_REFUND_SPONSORSHIP_SUCCESS,
        CANCEL_EVENT_SUCCESS,
        CANCEL_EVENT_USER_NOT_ENTERTAINMENT_PROVIDER,
        CANCEL_EVENT_USER_NOT_ORGANISER

    }
}
