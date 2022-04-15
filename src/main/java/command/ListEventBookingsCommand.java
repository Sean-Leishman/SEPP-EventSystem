package command;

import controller.Context;
import logging.Logger;
import model.*;
import state.BookingState;

import java.util.List;
import java.util.Map;

/**
 * ListEventBookingsCommand allows an event organiser (EntertainmentProvider) or
 * GovernmentRepresentative to get a list of all Bookings for a chosen TicketedEvent.
 */
public class ListEventBookingsCommand extends Object implements ICommand {
    private long eventNumber;
    private List<Booking> bookingListResult;
    /**
     * @param eventNumber identifier of the TicketedEvent to look up Bookings for
     */
    public ListEventBookingsCommand(long eventNumber) {
        this.eventNumber = eventNumber;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that current user is logged in
     * @verifies.that the event identifier corresponds to an existing event
     * @verifies.that the event is a ticketed event
     * @verifies.that currently logged-in user is either a government representative or the organiser of the event
     * {@inheritDoc}
     */
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();
        Event event = context.getEventState().findEventByNumber(this.eventNumber);
        if (user != null) {
            if (event != null) {
                if (event instanceof TicketedEvent){
                    if (user instanceof  GovernmentRepresentative | event.getOrganiser().equals(user)){
                        // use deep copy here
                        BookingState bookingStateCopy = new BookingState(context.getBookingState());
                        bookingListResult = bookingStateCopy.findBookingsByEventNumber(eventNumber);
                        Logger.getInstance().logAction("ListConsumerBookingsCommand", LogStatus.LIST_EVENT_BOOKINGS_SUCCESS, Map.of("BookingSize",bookingListResult.size()));
                    }
                    else{
                        Logger.getInstance().logAction("ListConsumerBookingsCommand", LogStatus.LIST_EVENT_BOOKINGS_USER_NOT_ORGANISER_NOR_GOV);
                        //assert false;
                    }
                }
                else{
                    Logger.getInstance().logAction("ListConsumerBookingsCommand", LogStatus.LIST_EVENT_BOOKINGS_EVENT_NOT_TICKETED);
                    //assert false;
                }
            }
            else{
                Logger.getInstance().logAction("ListConsumerBookingsCommand", LogStatus.LIST_EVENT_BOOKINGS_EVENT_NOT_FOUND);
                //assert false;
            }
        }
        else{
            Logger.getInstance().logAction("ListConsumerBookingsCommand", LogStatus.LIST_EVENT_BOOKINGS_USER_NOT_LOGGED_IN);
            //assert false;
        }
    }

    /**
     * @return List of Bookings if successful and null otherwise
     * {@inheritDoc}
     */
    public List<Booking> getResult() {
        return bookingListResult;
    }

    public enum LogStatus {
        LIST_EVENT_BOOKINGS_EVENT_NOT_FOUND,
        LIST_EVENT_BOOKINGS_EVENT_NOT_TICKETED,
        LIST_EVENT_BOOKINGS_SUCCESS,
        LIST_EVENT_BOOKINGS_USER_NOT_LOGGED_IN,
        LIST_EVENT_BOOKINGS_USER_NOT_ORGANISER_NOR_GOV,
    }

}
