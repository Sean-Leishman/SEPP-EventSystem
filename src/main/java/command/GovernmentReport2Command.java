package command;

import controller.Context;
import logging.Logger;
import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * GovernmentReport2Command allows government representatives to get a list of all consumers who have
 * active bookings for events provided by the specified entertainment provider
 */
public class GovernmentReport2Command extends Object implements ICommand {
    private List<Consumer> reportResult;
    private String orgname;

    /**
     * @param orgName identifier of the entertainment provider organisation to provide information
     */
    public GovernmentReport2Command(String orgName) {
        this.orgname = orgName;
        this.reportResult = new ArrayList<Consumer>();
    }

    private EntertainmentProvider getEntertainmentProvider(Context context){
        EntertainmentProvider entertainmentProvider = null;
        for (Event event : context.getEventState().getAllEvents()){
            if (event.getOrganiser().getOrgName().equals(this.orgname)){
                return event.getOrganiser();
            }
        }
        return null;
    }
    /**
     * Verifies that  current user logged in is a Government Representative and
     * Verifies that the organisation name specified belongs to an Entertainment Provider
     *
     * Gets all consumers with bookings belonging to each active and ticketed event belonging to the
     * EntertainmentProvider
     */
    public void execute(Context context) {
        EntertainmentProvider entertainmentProvider = getEntertainmentProvider(context);
        if (!(context.getUserState().getCurrentUser() instanceof GovernmentRepresentative)){
            Logger.getInstance().logAction("GovernmentReport2Command",LogStatus.GOVERNMENT_REPORT_2_USER_NOT_GOVERNMENT_REPRESENTATIVE);
            return;
        }
        else if (entertainmentProvider == null){
            Logger.getInstance().logAction("GovernmentReport2Command",LogStatus.GOVERNMENT_REPORT_2_ENTERTAINMENT_PROVIDER_NOT_FOUND);
            return;
        }
        else {
            for (Event event : entertainmentProvider.getEvents()) {
                if (event.getStatus() == EventStatus.ACTIVE & event instanceof TicketedEvent) {
                    for (Booking booking : context.getBookingState().findBookingsByEventNumber(event.getEventNumber())) {
                        if (booking.getStatus() == BookingStatus.Active) {
                            this.reportResult.add(booking.getBooker());
                        }
                    }
                }
            }
            Logger.getInstance().logAction("GovernmentReport2Command",LogStatus.GOVERNMENT_REPORT_2_SUCCESS);
        }
    }

    enum LogStatus{
        GOVERNMENT_REPORT_2_SUCCESS,
        GOVERNMENT_REPORT_2_USER_NOT_GOVERNMENT_REPRESENTATIVE,
        GOVERNMENT_REPORT_2_ENTERTAINMENT_PROVIDER_NOT_FOUND
    }

    /**
     * @return List of Consumers if successful and null otherwise
     */
    public List<Consumer> getResult() {
        return this.reportResult;
    }

}
