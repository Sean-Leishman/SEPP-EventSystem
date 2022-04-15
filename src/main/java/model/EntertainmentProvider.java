package model;

import external.EntertainmentProviderSystem;
import external.MockEntertainmentProviderSystem;

import java.util.*;


/**
 * EntertainmentProvider is a user of the application, who represents an organisation that hosts Events.
 * They can browse events, create new events, cancel their own events and see Bookings on their own events.
 */
public class EntertainmentProvider extends User {

    private List<Event> events;
    private String orgName;
    private String orgAddress;
    private String mainRepName;
    private List<String> otherRepNames;
    private List<String> otherRepEmails;
    private EntertainmentProviderSystem orgSystem;
    /**
     * Create a new EntertainmentProvider with an empty list of own events and a MockEntertainmentProviderSystem
     * for their organisation.
     * 
     * @param orgName name of the organisation for this account
     * @param orgAddress address of the organisation for this account
     * @param paymentAccountEmail email address corresponding to the EntertainmentProvider's account on the external
     * PaymentSystem
     * @param mainRepName full name of the main representative of the organisation
     * @param mainRepEmail email address of the main representative of the organisation (used as the account email address)
     * @param password password for this account
     * @param otherRepNames list of full names of other representatives of the organisation
     * @param otherRepEmails list of emails of other representatives of the organisation
     */



    public EntertainmentProvider(String orgName, String orgAddress, String paymentAccountEmail, String mainRepName, String mainRepEmail, String password, List<String> otherRepNames, List<String> otherRepEmails) {
        super(mainRepEmail, password, paymentAccountEmail);
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.mainRepName = mainRepName;
        this.otherRepEmails = otherRepEmails;
        this.otherRepNames = otherRepNames;
        this.events = new ArrayList<>();
        this.orgSystem = new MockEntertainmentProviderSystem(this.orgName, this.orgAddress);
    }


    public void addEvent(Event event) {
        this.events.add(event);
    }


    public String getOrgName() {
        return this.orgName;
    }


    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }


    public String getOrgAddress() {
        return this.orgAddress;
    }


    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }


    public List<Event> getEvents() {
        return this.events;
    }


    public void setMainRepName(String mainRepName) {
        this.mainRepName = mainRepName;
    }


    public void setMainRepEmail(String mainRepEmail) {
        super.setEmail(mainRepEmail);
    }


    public void setOtherRepNames(List<String> otherRepNames) {
        this.otherRepNames = otherRepNames;
    }


    public void setOtherRepEmails(List<String> otherRepEmails) {
        this.otherRepEmails = otherRepEmails;
    }


    public EntertainmentProviderSystem getProviderSystem() {
        return this.orgSystem;
    }

    @Override
    public String toString() {
        return "EntertainmentProvider{" +
                "events=" + events +
                ", orgName='" + orgName + '\'' +
                ", orgAddress='" + orgAddress + '\'' +
                ", mainRepName='" + mainRepName + '\'' +
                ", mainRepEmail='" + super.getEmail() + '\'' +
                ", password='" + super.getPassword() + '\'' +
                ", otherRepNames=" + otherRepNames +
                ", otherRepEmails=" + otherRepEmails +
                ", orgSystem=" + orgSystem +
                '}';
    }
}
