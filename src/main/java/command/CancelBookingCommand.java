package command;

import com.sun.security.auth.NTSidUserPrincipal;
import controller.Context;
import external.MockPaymentSystem;
import external.PaymentSystem;
import logging.Logger;
import model.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * CancelBookingCommand allows Consumers to cancel a Booking given
 * its unique booking number. The command applies for the currently logged-in user.
 */
public class CancelBookingCommand extends Object implements ICommand {
    private long bookingNumber;
    private boolean cancelBookingResult = false;
    /**
     * @param bookingNumber booking number uniquely identifying a Booking that was previously
     * made by the currently logged in Consumer
     */
    public CancelBookingCommand(long bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that currently logged-in user is a Consumer
     * @verifies.that the booking number corresponds to an existing Booking
     * @verifies.that the logged-in user is the booking owner
     * @verifies.that the booking is still active (i.e., not cancelled previously)
     * @verifies.that the booked performance start is at least 24h away from now
     * @verifies.that the payment system refund succeeds
     * {@inheritDoc}
     */
    public void execute(Context context) {

        User user = context.getUserState().getCurrentUser();
        Booking booking = context.getBookingState().findBookingByNumber(bookingNumber);
        if (!(user instanceof Consumer)){
            Logger.getInstance().logAction("CancelBookingCommand",LogStatus.CANCEL_BOOKING_USER_NOT_CONSUMER);
            //assert false;
            return;
        }
        else if (booking == null){
            Logger.getInstance().logAction("CancelBookingCommand",LogStatus.CANCEL_BOOKING_BOOKING_NOT_FOUND);
            //assert false;
            return;
        }
        else if (!user.equals(booking.getBooker())){
            Logger.getInstance().logAction("CancelBookingCommand",LogStatus.CANCEL_BOOKING_USER_IS_NOT_BOOKER);
            //assert false;
            return;
        }
        else if (booking.getStatus() != BookingStatus.Active){
            Logger.getInstance().logAction("CancelBookingCommand",LogStatus.CANCEL_BOOKING_BOOKING_NOT_ACTIVE);
            //assert false;
            return;
        }
        else if (booking.getEventPerformance().getStartDateTime().minusHours(24).isBefore(LocalDateTime.now())){
            System.out.println(booking.getEventPerformance().getStartDateTime().plusHours(24).compareTo(LocalDateTime.now()));
            Logger.getInstance().logAction("CancelBookingCommand",LogStatus.CANCEL_BOOKING_NO_CANCELLATIONS_WITHIN_24H);
            //assert false;
            return;
        }
        else if (!context.getPaymentSystem().processRefund(user.getPaymentAccountEmail(),
                booking.getEventPerformance().getEvent().getOrganiser().getPaymentAccountEmail(),
                booking.getAmountPaid())){
            Logger.getInstance().logAction("CancelBookingCommand",LogStatus.CANCEL_BOOKING_REFUND_FAILED);
            //assert false;
            return;
        }
        else {
            this.cancelBookingResult = true;
            booking.cancelByConsumer();
            booking.getEventPerformance().getEvent().getOrganiser().getProviderSystem().cancelBooking(this.bookingNumber);
            Logger.getInstance().logAction("CancelBookingCommand", LogStatus.CANCEL_BOOKING_SUCCESS, Map.of("Number",booking.getBookingNumber(),"Status",booking.getStatus()));
        }
    }

    /**
     * @return True if successful and false otherwise
     * {@inheritDoc}
     */
    public Boolean getResult() {
        return this.cancelBookingResult;
    }

    public enum LogStatus{
        CANCEL_BOOKING_SUCCESS,
        CANCEL_BOOKING_USER_NOT_CONSUMER,
        CANCEL_BOOKING_BOOKING_NOT_FOUND,
        CANCEL_BOOKING_USER_IS_NOT_BOOKER,
        CANCEL_BOOKING_BOOKING_NOT_ACTIVE,
        CANCEL_BOOKING_REFUND_FAILED,
        CANCEL_BOOKING_NO_CANCELLATIONS_WITHIN_24H,
    }



}
