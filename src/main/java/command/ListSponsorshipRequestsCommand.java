package command;

import controller.Context;
import logging.Logger;
import model.GovernmentRepresentative;
import model.SponsorshipRequest;
import model.User;
import state.ISponsorshipState;
import state.SponsorshipState;

import java.util.List;

/**
 * ListSponsorshipRequestsCommand allows GovernmentRepresentatives to get a list of registered
 * SponsorshipRequests.
 */
public class ListSponsorshipRequestsCommand extends Object implements ICommand {
    private boolean pendingRequestsOnly;
    private List<SponsorshipRequest> requestListResult;
    /**
     * @param pendingRequestsOnly whether to filter the results to include only SponsorshipRequests with
     * SponsorshipStatus.PENDING
     */
    public ListSponsorshipRequestsCommand(boolean pendingRequestsOnly) {
        this.pendingRequestsOnly = pendingRequestsOnly;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that the current user is logged in
     * @verifies.that the logged-in user is a GovernmentRepresentative
     * {@inheritDoc}
     */
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();
        if (user == null){
            Logger.getInstance().logAction("ListSponsorshipRequestCommand",LogStatus.LIST_SPONSORSHIP_REQUESTS_NOT_LOGGED_IN);
            //assert false;
        }
        else if (!(user instanceof GovernmentRepresentative)){
            Logger.getInstance().logAction("ListSponsorshipRequestCommand",LogStatus.LIST_SPONSORSHIP_REQUESTS_NOT_GOVERNMENT_REPRESENTATIVE);
            //assert false;
        }
        else if (this.pendingRequestsOnly){
            this.requestListResult = context.getSponsorshipState().getPendingSponsorshipRequests();
        }
        else{
            SponsorshipState copySponsorshipState = new SponsorshipState(context.getSponsorshipState());
            this.requestListResult = copySponsorshipState.getAllSponsorshipRequests();
        }
    }

    /**
     * @return A list of SponsorshipRequests if successful and null otherwise
     * {@inheritDoc}
     */
    public List<SponsorshipRequest> getResult() {
        return this.requestListResult;
    }

    public enum LogStatus{
        LIST_SPONSORSHIP_REQUESTS_NOT_LOGGED_IN,
        LIST_SPONSORSHIP_REQUESTS_NOT_GOVERNMENT_REPRESENTATIVE
    }
}
