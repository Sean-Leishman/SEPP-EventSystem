package command;

import controller.Context;
import logging.Logger;
import model.User;
import state.UserState;
import java.util.Map;

/**
 * LoginCommand allows a previously registered User to log in to the system by providing their login
 * details.
 */
public class LoginCommand extends Object implements ICommand {

    /**
     * @param email account email
     * @param password account password
     */
    private String email;
    private String password;
    private User userResult;

    public LoginCommand(String email, String password)
    {
        this.email = email;
        this.password = password;
        this.userResult= null;
    }

    public enum LogStatus{
        USER_LOGIN_SUCCESS,
        USER_LOGIN_EMAIL_NOT_REGISTERED,
        USER_LOGIN_WRONG_PASSWORD
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that the account email is registered on the system
     * @verifies.that the password matches the saved password for the corresponding account
     * {@inheritDoc}
     */
    public void execute(Context context) {
        /**
         * Verify currentUser is null???
         */
        UserState userState = (UserState) context.getUserState();
        Map<String, User> users = userState.getAllUsers();
        if (users.containsKey(this.email)){
            User registeredUser = users.get(this.email);
            if (registeredUser.checkPasswordMatch(this.password)){
                userState.setCurrentUser(registeredUser);
                this.userResult = registeredUser;
                Logger.getInstance().logAction("LoginCommand",LogStatus.USER_LOGIN_SUCCESS,Map.of("Email",this.email));
            }
            else {
                Logger.getInstance().logAction("LoginCommand", LogStatus.USER_LOGIN_WRONG_PASSWORD, Map.of("Password",this.password));
                //assert false:Logger.getInstance().getLog();
                return;
            }
        }
        else {
            Logger.getInstance().logAction("LoginCommand", LogStatus.USER_LOGIN_EMAIL_NOT_REGISTERED, Map.of("Email",this.email));
            //assert false:Logger.getInstance().getLog();
            return;
        }
    }

    /**
     * @return The logged in User instance if successful and null otherwise
     * {@inheritDoc}
     */
    public User getResult() {
        return userResult;
    }

}
