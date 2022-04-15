package command;

import controller.Context;
import logging.Logger;
import model.EntertainmentProvider;
import model.Event;
import model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RegisterEntertainmentProviderCommand allows users to register a new EntertainmentProvider account
 * on the system. After registration, they are automatically logged in.
 */
public class RegisterEntertainmentProviderCommand extends Object implements ICommand {
    /**
     * @param orgName organisation name that this account will be representing. Must not be null
     * @param orgAddress address of the organisation (in case there are multiple organisations with the same name). Must
     * not be null
     * @param paymentAccountEmail email address corresponding to an account in the external PaymentSystem.
     * Must not be null
     * @param mainRepName name of the main organisation representative. Must not be null
     * @param mainRepEmail email address of the main organisation representative (also used as the account email).
     * Must not be null
     * @param password password to log in to the system in the future. Must not be null
     * @param otherRepNames list of names of other organisation representatives. Must not be null
     * @param otherRepEmails list of emails of other organisation representatives. Must be not null
     */

    String orgName = null;
    String orgAddress = null;
    String paymentAccountEmail = null;
    String mainRepName = null;
    String mainRepEmail = null;
    String password = null;
    List<String> otherRepNames = null;
    List<String> otherRepEmails = null;
    EntertainmentProvider newEntertainmentProviderResult;
    EntertainmentProvider attribute;

    public RegisterEntertainmentProviderCommand(String orgName, String orgAddress, String paymentAccountEmail, String mainRepName, String mainRepEmail, String password, List<String> otherRepNames, List<String> otherRepEmails) {
        if (orgName != null && orgName.isEmpty()){
            this.orgName = null;
        }
        else{
            this.orgName = orgName;
        }
        if (orgAddress != null && orgAddress.isEmpty()){
            this.orgAddress = null;
        }
        else{
            this.orgAddress = orgAddress;
        }
        if (paymentAccountEmail !=null && paymentAccountEmail.isEmpty()){
            this.paymentAccountEmail = null;
        }
        else{
            this.paymentAccountEmail = paymentAccountEmail;
        }
        if (mainRepName != null && mainRepName.isEmpty()){
            this.mainRepName = null;
        }
        else{
            this.mainRepName = mainRepName;
        }
        if (mainRepEmail != null && mainRepEmail.isEmpty()){
            this.mainRepEmail = null;
        }
        else{
            this.mainRepEmail = mainRepEmail;
        }
        if (password != null && password.isEmpty()){
            this.password = null;
        }
        else{
            this.password = password;
        }
        this.otherRepNames = otherRepNames;
        this.otherRepEmails = otherRepEmails;
    }

    public enum LogStatus{
        REGISTER_ENTERTAINMENT_PROVIDER_SUCCESS,
        USER_REGISTER_FIELDS_CANNOT_BE_NULL,
        USER_REGISTER_EMAIL_ALREADY_REGISTERED,
        USER_REGISTER_ORG_ALREADY_REGISTERED,
        USER_LOGIN_SUCCESS
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that orgName, orgAddress, paymentAccountEmail, mainRepName, mainRepEmail, password, otherRepNames, and
     * otherRepEmails are all not null
     * @verifies.that there is no account registered with the same email address as mainRepEmail
     * @verifies.that there is no already registered organisation with the same name and address
     * {@inheritDoc}
     */
    public void execute(Context context) {
        Map<String, User> users = context.getUserState().getAllUsers();
        if (orgName== null | orgAddress== null| paymentAccountEmail== null | mainRepName== null| mainRepEmail== null|
                password== null | otherRepNames== null | otherRepEmails== null){
            Logger.getInstance().logAction("RegisterEntertainmentProviderCommand", LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL);
            //  Map.of("Names",orgName,"Address",orgAddress,"PayEmail",
            //                    paymentAccountEmail,"MName",mainRepName,"MEmail",mainRepEmail,"Password",password,"ONames",otherRepNames,"OEmails",otherRepEmails)
            //assert false:LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL;
        }
        else if (users.containsKey(mainRepEmail)){
            Logger.getInstance().logAction("RegisterEntertainmentProviderCommand", LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED);
            //assert false:LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED;
        }
        else{
            boolean isOrgNameTaken = false;
            for (Map.Entry<String, User> entry : users.entrySet()) {
                User user = entry.getValue();
                if (user instanceof EntertainmentProvider) {
                    EntertainmentProvider entertainmentProvider = (EntertainmentProvider) user;
                    if (orgName.equals(entertainmentProvider.getOrgName())) {
                        if (orgAddress.equals(entertainmentProvider.getOrgAddress()))
                            Logger.getInstance().logAction("RegisterEntertainmentProviderCommand", LogStatus.USER_REGISTER_ORG_ALREADY_REGISTERED);
                        isOrgNameTaken = true;
                        //assert false : LogStatus.USER_REGISTER_ORG_ALREADY_REGISTERED;
                    }
                }
                /*assert false;*/
            }
            if (!isOrgNameTaken){
                newEntertainmentProviderResult = new EntertainmentProvider(orgName,orgAddress,paymentAccountEmail,mainRepName,mainRepEmail,password,otherRepNames,otherRepNames);
                context.getUserState().addUser(newEntertainmentProviderResult);
                Logger.getInstance().logAction("RegisterEntertainmentProviderCommand",
                        LogStatus.REGISTER_ENTERTAINMENT_PROVIDER_SUCCESS,
                        new HashMap<String,Object>() {{put("User", newEntertainmentProviderResult);}});
                context.getUserState().setCurrentUser(context.getUserState().getAllUsers().get(mainRepEmail));
                Logger.getInstance().logAction("RegisterEntertainmentProviderCommand", LogStatus.USER_LOGIN_SUCCESS,
                        new HashMap<String,Object>() {{put("Current", context.getUserState().getAllUsers().get(mainRepEmail));}});
            }
        }
    }

    /**
     * @return Instance of the newly registered EntertainmentProvider and null otherwise.
     * {@inheritDoc}
     */
    public EntertainmentProvider getResult() {
        return newEntertainmentProviderResult;
    }

}
