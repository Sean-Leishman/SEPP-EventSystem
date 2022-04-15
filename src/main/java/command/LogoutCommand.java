package command;

import controller.Context;
import logging.Logger;
import model.User;
import state.UserState;

/**
 * LogoutCommand allows the currently logged in User to log out
 */
public class LogoutCommand extends Object implements ICommand {

    public LogoutCommand() {}

    public enum LogStatus{
        USER_LOGOUT_SUCCESS,
        USER_LOGOUT_NOT_LOGGED_IN
    }
    /**
     * @param context object that provides access to global application state
     * @verifies.that the current user is logged in
     * {@inheritDoc}
     */
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();
        if (user == null){
            Logger.getInstance().logAction("LogoutCommand.execute",LogStatus.USER_LOGOUT_NOT_LOGGED_IN);
            //assert false;
            return;
        }
        context.getUserState().setCurrentUser(null);
        Logger.getInstance().logAction("LogoutCommand.execute",LogStatus.USER_LOGOUT_SUCCESS);
    }

    /**
     * @return Always null
     * {@inheritDoc}
     */
    public Void getResult() {
        return null;
    }

}
