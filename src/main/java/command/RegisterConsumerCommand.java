package command;

import controller.Context;
import logging.Logger;
import model.Consumer;
import model.User;
import state.UserState;

import java.util.Map;

/**
 * RegisterConsumerCommand allows users to register a new Consumer account on the system.
 * After registration, they are automatically logged in.
 */
public class RegisterConsumerCommand extends Object implements ICommand {
    /**
     * @param name full name of the consumer
     * @param email personal email address (which will be used as the account email)
     * @param phoneNumber phone number (to allow notification of Event cancellations)
     * @param password password to log in to the system in the future
     * @param paymentAccountEmail email address corresponding to an accounting in the external
     * PaymentSystem
     */
    String name = null;
    String email = null;
    String phoneNumber = null;
    String password = null;
    String paymentAccountEmail = null;

    Consumer newConsumerResult = null;

    public RegisterConsumerCommand(String name, String email, String phoneNumber, String password, String paymentAccountEmail) {
        if (name != null && name.equals("")){
            this.name = null;
        }
        else{
            this.name = name;
        }
        if (email != null && email.equals("")){
            this.email = null;
        }
        else{
            this.email = email;
        }
        if (phoneNumber != null && phoneNumber.equals("")){
            this.phoneNumber = null;
        }
        else{
            this.phoneNumber = phoneNumber;
        }
        if (password != null && password.equals("")){
            this.password = null;
        }
        else{
            this.password = password;
        }
        if (paymentAccountEmail != null && paymentAccountEmail.equals("")){
            this.paymentAccountEmail = null;
        }
        else{
            this.paymentAccountEmail = paymentAccountEmail;
        }
    }

    public enum LogStatus{
        REGISTER_CONSUMER_SUCCESS,
        USER_REGISTER_FIELDS_CANNOT_BE_NULL,
        USER_REGISTER_EMAIL_ALREADY_REGISTERED,
        USER_LOGIN_SUCCESS
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that name, email, phoneNumber, password, and paymentAccountEmail are all not null
     * @verifies.that there is already registered User with the provided email address
     * {@inheritDoc}
     */
    public void execute(Context context) {
        Map<String, User> users = context.getUserState().getAllUsers();
        if (name == null | email == null | phoneNumber== null | password== null | paymentAccountEmail== null ){
            Logger.getInstance().logAction("RegisterConsumerCommand.execute",LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL);
            //assert false: LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL;
        }
        else if (users.containsKey(email)){
            Logger.getInstance().logAction("RegisterConsumerCommand.execute",LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED);
            //assert false: LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED;
        }
        else {

            Consumer new_consumer = new Consumer(name, email, phoneNumber, password, paymentAccountEmail);
            context.getUserState().addUser(new_consumer);
            Logger.getInstance().logAction("RegisterConsumerCommand.execute", LogStatus.REGISTER_CONSUMER_SUCCESS);
            context.getUserState().setCurrentUser(context.getUserState().getAllUsers().get(email));
            Logger.getInstance().logAction("RegisterConsumerCommand.execute", LogStatus.USER_LOGIN_SUCCESS);
            newConsumerResult = new_consumer;
        }}

    /**
     * @return Instance of the newly registered Consumer if successful and null otherwise
     * {@inheritDoc}
     */
    public Consumer getResult() {
        return newConsumerResult;
    }

}
