package command;

import controller.Context;
import logging.Logger;
import model.Consumer;
import model.ConsumerPreferences;
import model.User;

/**
 * UpdateConsumerProfileCommand allows Consumers to update their account details and change the default
 * Covid-19 preferences.
 */
public class UpdateConsumerProfileCommand extends UpdateProfileCommand {
     private String oldPassword = null;
     private String newName = null;
     private String newEmail = null;
     private String newPhoneNumber = null;
     private String newPassword = null;
     private String newPaymentAccountEmail = null;
     private ConsumerPreferences newPreferences = null;
    /**
     * @param oldPassword account password before the change, required for extra security verification. Must not be null
     * @param newName full name of the person holding this account. Must not be null
     * @param newEmail email address to use for this account. Must not be null
     * @param newPhoneNumber phone number to use for this account (used for notifying the Consumer of any
     * TicketedEvent cancellations that they have bookings for). Must not be null
     * @param newPassword password to use for this account. Must not be null
     * @param newPaymentAccountEmail email address corresponding to an account in the external PaymentSystem.
     * Must not be null
     * @param newPreferences a ConsumerPreferences object of Covid-19 preferences, used for filtering events
     * in the ListEventsCommand.
     */
    public UpdateConsumerProfileCommand(String oldPassword, String newName, String newEmail, String newPhoneNumber, String newPassword, String newPaymentAccountEmail, ConsumerPreferences newPreferences) {
        if (newName != null && newName.isEmpty()) {
            this.newName = null;
        }
        else{
            this.newName = newName;
        }
        if (newEmail != null && newEmail.isEmpty()) {
            this.newEmail = null;
        }
        else{
            this.newEmail = newEmail;
        }
        if (newPhoneNumber != null && newPhoneNumber.isEmpty()) {
            this.newPhoneNumber = null;
        }
        else{
            this.newPhoneNumber = newPhoneNumber;
        }
        if (newPassword != null && newPassword.isEmpty()) {
            this.newPassword = null;
        }
        else{
            this.newPassword = newPassword;
        }
        if (newPaymentAccountEmail != null && newPaymentAccountEmail.isEmpty()) {
            this.newPaymentAccountEmail = null;
        }
        else{
            this.newPaymentAccountEmail = newPaymentAccountEmail;
        }
        this.newPreferences = newPreferences;
        this.oldPassword = oldPassword;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that oldPassword, newName, newEmail, newPhoneNumber, newPassword, newPaymentAccountEmail, newPreferences
     * are all not null
     * @verifies.that current user is logged in
     * @verifies.that oldPassword matches the current user's password
     * @verifies.that there is no other user already registered with the same email address as newEmail
     * @verifies.that currently logged-in user is a Consumer
     * {@inheritDoc}
     */
    public void execute(Context context) {
        if (newName == null | newEmail == null | newPhoneNumber == null | newPassword == null | newPaymentAccountEmail == null | newPreferences == null ){
            Logger.getInstance().logAction("UpdateConsumerProfileCommand.execute()", LogStatus.USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL);
        }
        else if (!(isProfileUpdateInvalid(context,oldPassword,newEmail)) & context.getUserState().getCurrentUser() instanceof Consumer){
            Consumer consumer = (Consumer) context.getUserState().getCurrentUser();
            context.getUserState().getAllUsers().remove(consumer.getEmail());
            consumer.setName(newName);
            consumer.setPhoneNumber(newPhoneNumber);
            consumer.setEmail(newEmail);
            consumer.updatePassword(newPassword);
            consumer.setPaymentAccountEmail(newPaymentAccountEmail);
            consumer.setPreferences(newPreferences);
            context.getUserState().getAllUsers().put(consumer.getEmail(),consumer);
            this.successResult = true;
            Logger.getInstance().logAction("UpdateConsumerProfileCommand.execute()", LogStatus.USER_UPDATE_PROFILE_SUCCESS);
        }
        else{
            Logger.getInstance().logAction("UpdateConsumerProfileCommand.execute()", LogStatus.USER_UPDATE_PROFILE_NOT_CONSUMER);
            assert false;
        }
    }

    enum LogStatus{
        USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL,
        USER_UPDATE_PROFILE_NOT_CONSUMER,
        USER_UPDATE_PROFILE_SUCCESS
    }

}
