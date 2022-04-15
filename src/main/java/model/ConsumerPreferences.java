package model;

/**
 * A set of Covid-19 preferences that Consumers can save in their profile and filter Events based on
 * them.
 */
public class ConsumerPreferences extends Object {
    /**
     * Default values are:
     * 
     * preferSocialDistancing = false
     * preferAirFiltration = false
     * preferOutdoorsOnly = false
     * preferredMaxCapacity = Integer.MAX_VALUE
     * preferredMaxVenueSize = Integer.MAX_VALUE
     * 
     */
    public boolean preferSocialDistancing;
    public boolean preferAirFiltration;
    public boolean preferOutdoorsOnly;
    public int preferredMaxCapacity;
    public int preferredMaxVenueSize;

    public ConsumerPreferences() {
        this.preferSocialDistancing = false;
        this.preferAirFiltration = false;
        this.preferOutdoorsOnly = false;
        this.preferredMaxCapacity = Integer.MAX_VALUE;
        this.preferredMaxVenueSize = Integer.MAX_VALUE;
    }

    public ConsumerPreferences(boolean preferSocialDistancing, boolean preferAirFiltration, boolean preferOutdoorsOnly, int preferredMaxCapacity, int preferredMaxVenueSize){
        this.preferSocialDistancing = preferSocialDistancing;
        this.preferAirFiltration = preferAirFiltration;
        this.preferOutdoorsOnly = preferOutdoorsOnly;
        this.preferredMaxCapacity = preferredMaxCapacity;
        this.preferredMaxVenueSize = preferredMaxVenueSize;
    }

}
