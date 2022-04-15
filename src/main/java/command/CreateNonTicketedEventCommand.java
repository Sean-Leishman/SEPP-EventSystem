package command;

import controller.Context;
import logging.Logger;
import model.EntertainmentProvider;
import model.EventType;
import model.NonTicketedEvent;

/**
 * CreateNonTicketedEventCommand allows EntertainmentProviders to create
 * NonTicketedEvents.
 */
public class CreateNonTicketedEventCommand extends CreateEventCommand {

    public CreateNonTicketedEventCommand(String title, EventType type) {
        super(title, type);
    }

    public enum LogStatus{
        CREATE_NON_TICKETED_SUCCESS
    }
    /**
     * @param context object that provides access to global application state
     * @verifies.that current user is logged in
     * @verifies.that currently logged-in user is an EntertainmentProvider
     * {@inheritDoc}
     */
    public void execute(Context context) {
        if (isUserAllowedToCreateEvent(context)){
            EntertainmentProvider current = (EntertainmentProvider) context.getUserState().getCurrentUser();
            NonTicketedEvent event = context.getEventState().createNonTicketedEvent(current,this.title,this.type);
            this.eventNumberResult = event.getEventNumber();
            current.getProviderSystem().recordNewEvent(this.eventNumberResult,this.title,0);
            current.addEvent(event);
            Logger.getInstance().logAction("CreateNonTicketedEvent",LogStatus.CREATE_NON_TICKETED_SUCCESS);
        }
        else{
            assert false: Logger.getInstance().getLog();
        }
    }

}
