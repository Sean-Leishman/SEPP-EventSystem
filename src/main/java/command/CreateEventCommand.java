package command;

import controller.Context;
import logging.Logger;
import model.EntertainmentProvider;
import model.EventType;

/**
 * CreateEventCommand contains common behaviour shared by commands to create different kinds of
 * events.
 */
public abstract class CreateEventCommand extends Object implements ICommand {

    protected final String title;

    protected final EventType type;

    protected Long eventNumberResult;
    /**
     * @param title title of the event
     * @param type type of the event
     */
    public CreateEventCommand(String title, EventType type) {
        this.title = title;
        this.type = type;
    }

    public enum LogStatus{
        CREATE_EVENT_USER_NOT_LOGGED_IN, CREATE_EVENT_USER_NOT_ENTERTAINMENT_PROVIDER
    }

    /**
     * Common error checking method for all events that are created.
     * 
     * @param context object that provides access to global application state
     * @return True/false based on whether the currently logged-in user is allowed to create events.
     * @verifies.that current user is logged in
     * @verifies.that currently logged-in user is an EntertainmentProvider
     */
    protected boolean isUserAllowedToCreateEvent(Context context) {
        if (context.getUserState().getCurrentUser() == null){
            Logger.getInstance().logAction("CreateEventCommand.isUserAllowedToCreateEvent()",LogStatus.CREATE_EVENT_USER_NOT_LOGGED_IN);
            return false;
        }
        if (!(context.getUserState().getCurrentUser() instanceof EntertainmentProvider)){
            Logger.getInstance().logAction("CreateEventCommand.isUserAllowedToCreateEvent()",LogStatus.CREATE_EVENT_USER_NOT_ENTERTAINMENT_PROVIDER);
            return false;
        }
        return true;
    }

    /**
     * @return event number corresponding to the created event if successful and null otherwise
     * {@inheritDoc}
     */
    public Long getResult() {
        return eventNumberResult;
    }

}
