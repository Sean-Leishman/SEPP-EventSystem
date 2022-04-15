package state;

import model.SponsorshipRequest;
import model.TicketedEvent;

import java.util.List;

/**
 * ISponsorshipState is an interface representing the portion of the application that contains all the
 * SponsorshipRequest information.
 */
public interface ISponsorshipState {

    /**
     * Create a new SponsorshipRequest (includes generating a new unique request number) and add it to
     * a TicketedEvent and the sponsorship state
     * 
     * @param event event that the sponsorship request is for
     * @return The newly created SponsorshipRequest
     */
    SponsorshipRequest addSponsorshipRequest(TicketedEvent event);

    /**
     * @return List of all registered SponsorshipRequests
     */
    List<SponsorshipRequest> getAllSponsorshipRequests();

    /**
     * @return List of those SponsorshipRequests that have SponsorshipStatus.PENDING
     */
    List<SponsorshipRequest> getPendingSponsorshipRequests();

    /**
     * @param requestNumber unique request identifier to look up in the sponsorship state
     * @return SponsorshipRequest corresponding to the specified request number if there is one,
     * or null otherwise
     */
    SponsorshipRequest findRequestByNumber(long requestNumber);

}
