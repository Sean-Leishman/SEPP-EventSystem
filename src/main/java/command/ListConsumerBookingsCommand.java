package command;

import controller.Context;
import logging.Logger;
import model.*;
import state.BookingState;
import state.UserState;

import java.awt.print.Book;
import java.util.List;
import java.util.Map;

/**
 * ListConsumerBookingsCommand allows a logged-in Consumer to get a list of all their own
 * Bookings.
 */
public class ListConsumerBookingsCommand extends Object implements ICommand {
    private List<Booking> bookingListResult;
    public ListConsumerBookingsCommand() {}

    /**
     * @param context object that provides access to global application state
     * @verifies.that the current user is logged in
     * @verifies.that the logged-in user is a Consumer
     * {@inheritDoc}
     */
    public void execute(Context context) {
        if (context.getUserState().getCurrentUser() != null) {
            if (context.getUserState().getCurrentUser() instanceof Consumer) {
                UserState copyUserState = new UserState(context.getUserState());
                bookingListResult = ((Consumer) copyUserState.getCurrentUser()).getBookings();
                Logger.getInstance().logAction("ListConsumerBookingsCommand",LogStatus.LIST_CONSUMER_BOOKINGS_SUCCESS, Map.of("Bookings",bookingListResult.size()));
            }
            else{
                Logger.getInstance().logAction("ListConsumerBookingsCommand",LogStatus.LIST_CONSUMER_BOOKINGS_USER_NOT_CONSUMER);
                //assert false;
            }
        }
        else{
            Logger.getInstance().logAction("ListConsumerBookingsCommand",LogStatus.LIST_CONSUMER_BOOKINGS_NOT_LOGGED_IN);
           //assert false;
        }

    }

    /**
     * @return A list of the Consumer's Bookings if successful and null otherwise
     * {@inheritDoc}
     */
    public List<Booking> getResult() {
        return bookingListResult;
    }

    public enum LogStatus{
        LIST_CONSUMER_BOOKINGS_NOT_LOGGED_IN,
        LIST_CONSUMER_BOOKINGS_USER_NOT_CONSUMER,
        LIST_CONSUMER_BOOKINGS_SUCCESS
    }
}
