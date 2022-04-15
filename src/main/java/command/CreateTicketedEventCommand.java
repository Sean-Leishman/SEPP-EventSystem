package command;

import controller.Context;
import logging.Logger;
import model.*;

/**
 * CreateTicketedEventCommand allows EntertainmentProviders to create
 * TicketedEvents. Optionally, it also puts in a SponsorshipRequest for the event.
 */
public class CreateTicketedEventCommand extends CreateEventCommand {

    int numTickets;
    double ticketPrice;
    boolean requestSponsorship;
    /**
     * @param title title of the event
     * @param type type of the event
     * @param numTickets maximum number of initially available tickets through all performances of the event.
     * This is used to calculate the government sponsorship amount if the event is sponsored
     * @param ticketPrice initial price per ticket in pounds (a discount is applied if the government sponsors the event)
     * @param requestSponsorship whether to add a SponsorshipRequest for the event
     */
    public CreateTicketedEventCommand(String title, EventType type, int numTickets, double ticketPrice, boolean requestSponsorship) {
        super(title, type);
        this.numTickets = numTickets;
        this.ticketPrice = ticketPrice;
        this.requestSponsorship = requestSponsorship;
    }

    public enum LogStatus{
        CREATE_TICKETED_EVENT_SUCCESS,
        CREATE_EVENT_REQUESTED_SPONSORSHIP
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that current user is logged in
     * @verifies.that currently logged-in user is an EntertainmentProvider
     * {@inheritDoc}
     */
    public void execute(Context context) {
        if (isUserAllowedToCreateEvent(context)){
            EntertainmentProvider entertainmentProvider = (EntertainmentProvider) context.getUserState().getCurrentUser();
            TicketedEvent event = context.getEventState().createTicketedEvent(entertainmentProvider,this.title,this.type,this.ticketPrice,this.numTickets);
            this.eventNumberResult = event.getEventNumber();

            if (this.requestSponsorship){
                SponsorshipRequest sponsorshipRequest = context.getSponsorshipState().addSponsorshipRequest(event);
                event.setSponsorshipRequest(sponsorshipRequest);
                Logger.getInstance().logAction("CreateTicketedEvent", LogStatus.CREATE_EVENT_REQUESTED_SPONSORSHIP);
            }

            entertainmentProvider.getProviderSystem().recordNewEvent(this.eventNumberResult,this.title,this.numTickets);
            entertainmentProvider.addEvent(event);
            Logger.getInstance().logAction("CreateTicketedEvent", LogStatus.CREATE_TICKETED_EVENT_SUCCESS);
        }
        else{
            //assert false:Logger.getInstance().getLog();
            return;
        }
    }

}
