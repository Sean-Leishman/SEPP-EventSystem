package command;

import controller.Context;
import logging.Logger;
import model.EntertainmentProvider;
import model.User;

import java.util.List;
import java.util.Map;

/**
 * UpdateEntertainmentProviderProfileCommand allows EntertainmentProviders to update their account
 * details in the system.
 */
public class UpdateEntertainmentProviderProfileCommand extends UpdateProfileCommand {
    private String oldPassword = null;
    private String newOrgName = null;
    private String newOrgAddress = null;
    private String newPaymentAccountEmail = null;
    private String newMainRepName = null;
    private String newMainRepEmail = null;
    private String newPassword = null;
    private List<String> newOtherRepNames = null;
    private List<String> newOtherRepEmails = null;

    /**
     * @param oldPassword account password before the change, required for extra security verification. Must not be null
     * @param newOrgName organisation name to use for this account. Must not be null
     * @param newOrgAddress organisation address to use for this account. Must not be null
     * @param newPaymentAccountEmail email address corresponding to an account in the external PaymentSystem.
     * Must not be null
     * @param newMainRepName full name of the main representative of the organisation. Must not be null
     * @param newMainRepEmail email address of the main representative of the organisation. This is used as the account
     * email address. Must not be null
     * @param newPassword new password for this account. Must not be null
     * @param newOtherRepNames list of full names of other representatives of the organisation. Must not be null
     * @param newOtherRepEmails list of email addresses of other representatives of the organisation. Must not be null
     */
    public UpdateEntertainmentProviderProfileCommand(String oldPassword, String newOrgName, String newOrgAddress, String newPaymentAccountEmail, String newMainRepName, String newMainRepEmail, String newPassword, List<String> newOtherRepNames, List<String> newOtherRepEmails) {
        if (newPaymentAccountEmail != null && newPaymentAccountEmail.isEmpty()) {
            this.newPaymentAccountEmail = null;
        }
        else{
            this.newPaymentAccountEmail = newPaymentAccountEmail;
        }
        if (newOrgName != null && newOrgName.isEmpty()) {
            this.newOrgName = null;
        }
        else{
            this.newOrgName = newOrgName;
        }
        if (newOrgAddress != null && newOrgAddress.isEmpty()) {
            this.newOrgAddress = null;
        }
        else{
            this.newOrgAddress = newOrgAddress;
        }
        if (newPassword != null && newPassword.isEmpty()) {
            this.newPassword = null;
        }
        else{
            this.newPassword = newPassword;
        }
        if (newMainRepName != null && newMainRepName.isEmpty()) {
            this.newMainRepName = null;
        }
        else{
            this.newMainRepName = newMainRepName;
        }
        if (newMainRepEmail != null && newMainRepEmail.isEmpty()) {
            this.newMainRepEmail = null;
        }
        else{
            this.newMainRepEmail = newMainRepEmail;
        }
        this.newOtherRepNames = newOtherRepNames;
        this.newOtherRepEmails = newOtherRepEmails;
        this.oldPassword = oldPassword;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that oldPassword, newOrgName, newOrgAddress, newPaymentAccountEmail, newMainRepName, newMainRepEmail,
     * newPassword, newOtherRepNames, newOtherRepEmails are all not null
     * @verifies.that current user is logged in
     * @verifies.that oldPassword matches the current user's password
     * @verifies.that there is no other user already registered with the same email address as newEmail
     * @verifies.that currently logged-in user is an EntertainmentProvider
     * @verifies.that there is no other organisation with the same name and address as newName and newAddress already
     * registered
     * {@inheritDoc}
     */
    public void execute(Context context) {
        if (newMainRepEmail == null | newMainRepName == null | newOrgName == null | newOrgAddress == null | newOtherRepEmails == null | newOtherRepNames == null |
                newPaymentAccountEmail == null | newPassword == null){
            Logger.getInstance().logAction("UpdateEntertainmentProviderProfileCommand.execute()",LogStatus.USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL);
            //assert false;
        }
        else if (!(isProfileUpdateInvalid(context,oldPassword,newMainRepEmail)) && context.getUserState().getCurrentUser() instanceof EntertainmentProvider){
            boolean isOrgNameAddressTaken = false;
            EntertainmentProvider entertainmentProvider = (EntertainmentProvider) context.getUserState().getCurrentUser();
            for (Map.Entry<String, User> entry : context.getUserState().getAllUsers().entrySet()){
                if (entry.getValue() instanceof EntertainmentProvider) {
                    EntertainmentProvider checkAgainst = (EntertainmentProvider) entry.getValue();
                    if (checkAgainst.getOrgName().equals(newOrgName) & checkAgainst.getOrgAddress().equals(newOrgAddress)) {
                        Logger.getInstance().logAction("UpdateEntertainmentProviderProfileCommand.execute()", LogStatus.USER_UPDATE_PROFILE_ORG_ALREADY_REGISTERED);
                        isOrgNameAddressTaken = true;
                        //assert false;
                    }
                }
            }
            if (!isOrgNameAddressTaken) {
                entertainmentProvider.setMainRepEmail(newMainRepEmail);
                entertainmentProvider.setMainRepName(newMainRepName);
                entertainmentProvider.setOrgName(newOrgName);
                entertainmentProvider.setOrgAddress(newOrgAddress);
                entertainmentProvider.setOtherRepEmails(newOtherRepEmails);
                entertainmentProvider.setOtherRepNames(newOtherRepNames);
                entertainmentProvider.setPaymentAccountEmail(newPaymentAccountEmail);
                entertainmentProvider.updatePassword(newPassword);
                successResult = true;
                Logger.getInstance().logAction("UpdateEntertainmentProviderProfileCommand.execute()", LogStatus.USER_UPDATE_PROFILE_SUCCESS);
            }
        }
        else{
            Logger.getInstance().logAction("UpdateEntertainmentProviderProfileCommand.execute()",LogStatus.USER_UPDATE_PROFILE_NOT_ENTERTAINMENT_PROVIDER);
            //assert false;
        }
    }

    enum LogStatus{
        USER_UPDATE_PROFILE_SUCCESS,
        USER_UPDATE_PROFILE_FIELDS_CANNOT_BE_NULL,
        USER_UPDATE_PROFILE_NOT_ENTERTAINMENT_PROVIDER,
        USER_UPDATE_PROFILE_ORG_ALREADY_REGISTERED
    }

}
