package command;

import controller.Context;
import logging.Logger;
import model.User;

import java.util.Map;

/**
 * UpdateProfileCommand contains common behaviour shared between profile update commands
 */
public abstract class UpdateProfileCommand extends Object implements ICommand {
    protected Boolean successResult = false;

    public UpdateProfileCommand() {}

    /**
     * Common error checking method for all profile updates.
     * 
     * @param context object that provides access to global application state
     * @param oldPassword password before the change, which must match the account's password
     * @param newEmail specified email address to use for the account after the change
     * @return True/false based on whether the profile update is valid
     */
    protected boolean isProfileUpdateInvalid(Context context, String oldPassword, String newEmail) {
        User user = context.getUserState().getCurrentUser();
        if (user == null){
            Logger.getInstance().logAction("UpdateProfileCommand.execute()", LogStatus.USER_UPDATE_PROFILE_NOT_LOGGED_IN);
            return false;
        }
        if (user.checkPasswordMatch(oldPassword)){
            for (Map.Entry<String, User> entry : context.getUserState().getAllUsers().entrySet()) {
                if (entry.getValue().getEmail().equals(newEmail)){
                    Logger.getInstance().logAction("UpdateProfileCommand.execute()", LogStatus.USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE);
                    return true;
                }
            }
            return false;
        }
        else {
            Logger.getInstance().logAction("UpdateProfileCommand.execute()", LogStatus.USER_UPDATE_PROFILE_WRONG_PASSWORD);
            return true;
        }
    }

    /**
     * @return True if successful and false otherwise
     * {@inheritDoc}
     */
    public Boolean getResult() {
        return this.successResult;
    }

    enum LogStatus{
        USER_UPDATE_PROFILE_NOT_LOGGED_IN,
        USER_UPDATE_PROFILE_WRONG_PASSWORD,
        USER_UPDATE_PROFILE_EMAIL_ALREADY_IN_USE
    }

}
