package state;

import model.Booking;
import model.SponsorshipRequest;
import model.SponsorshipStatus;
import model.TicketedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SponsorshipState is a concrete implementation of ISponsorshipState.
 */
public class SponsorshipState extends Object implements ISponsorshipState {

    private long nextSponsorshipId;
    private List<SponsorshipRequest> sponsorshipRequests;

    /**
     * Create a new SponsorshipState with an empty list of sponsorship requests, which keeps track of the next request
     * number it will generate, starting from 1 and incrementing by 1 each time a new number is requested
     */
    public SponsorshipState() {
        this.nextSponsorshipId = 1;
        this.sponsorshipRequests = new ArrayList<SponsorshipRequest>();
    }

    /**
     * Copy constructor to create a deep copy of another SponsorshipState instance
     * 
     * @param other instance to copy
     */
    public SponsorshipState(ISponsorshipState other) {
        SponsorshipState otherSponsorshipState = (SponsorshipState) other;
        this.nextSponsorshipId = otherSponsorshipState.nextSponsorshipId;
        this.sponsorshipRequests = new ArrayList<>();
        for (SponsorshipRequest sponsorshipRequest : otherSponsorshipState.getAllSponsorshipRequests()){
            this.sponsorshipRequests.add(sponsorshipRequest);
        }
    }

    /**
     * @param event event that the sponsorship request is for
     * @return The newly created SponsorshipRequest
     * {@inheritDoc}
     */
    public SponsorshipRequest addSponsorshipRequest(TicketedEvent event) {
        SponsorshipRequest sponsorshipRequest = new SponsorshipRequest(this.nextSponsorshipId, event);
        this.nextSponsorshipId += 1;
        this.sponsorshipRequests.add(sponsorshipRequest);
        return sponsorshipRequest;
    }

    /**
     * @return List of all registered SponsorshipRequests
     */
    public List<SponsorshipRequest> getAllSponsorshipRequests() {
        return this.sponsorshipRequests;
    }

    /**
     * @return List of those SponsorshipRequests that have SponsorshipStatus.PENDING
     */
    public List<SponsorshipRequest> getPendingSponsorshipRequests() {
        List<SponsorshipRequest> pendingSponsorshipRequests = new ArrayList<SponsorshipRequest>();
        for (SponsorshipRequest sponsorshipRequest: this.sponsorshipRequests) {
            if (sponsorshipRequest.getStatus() == SponsorshipStatus.PENDING){
                pendingSponsorshipRequests.add(sponsorshipRequest);
            }
        }
        return pendingSponsorshipRequests;
    }

    /**
     * @param requestNumber unique request identifier to look up in the sponsorship state
     * @return SponsorshipRequest corresponding to the specified request number if there is one,
     * or null otherwise
     */
    public SponsorshipRequest findRequestByNumber(long requestNumber) {
        for (SponsorshipRequest sponsorshipRequest: this.sponsorshipRequests) {
            if (sponsorshipRequest.getRequestNumber() == requestNumber){
                return sponsorshipRequest;
            }
        }
        return null;
    }

}
