package command;

import controller.Context;
import logging.Logger;
import model.*;

/**
 * RespondSponsorshipCommand allows GovernmentRepresentatives to accept or reject a pending
 * SponsorshipRequest. When a request is accepted, sponsorship money is transferred from the government
 * representative's PaymentSystem account to the EntertainmentProvider's account (a lump sum
 * of specified % * the maximum number of allocated tickets for the event * the ticket price in GBP).
 */
public class RespondSponsorshipCommand extends Object implements ICommand {
    private long requestNumber;
    private int percentToSponsor;
    private boolean successResult;
    /**
     * @param requestNumber unique identifier of the sponsorship request to accept or reject
     * @param percentToSponsor indicates what % of the ticket price shall be sponsored by the government.
     * If percentToSponsor == 0, the sponsorship request is rejected, and otherwise
     * the request is accepted.
     * Must be in the range 0 <= percentToSponsor <= 100
     */
    public RespondSponsorshipCommand(long requestNumber, int percentToSponsor) {
        this.requestNumber = requestNumber;
        this.percentToSponsor = percentToSponsor;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that the current user is logged in
     * @verifies.that the logged-in user is a GovernmentRepresentative
     * @verifies.that percentToSponsor is between 0 and 100 (inclusive)
     * @verifies.that the provided request identifier corresponds to an existing SponsorshipRequest
     * @verifies.that the sponsorship request has pending status
     * {@inheritDoc}
     */
    public void execute(Context context) {
        User user = context.getUserState().getCurrentUser();
        SponsorshipRequest request = context.getSponsorshipState().findRequestByNumber(this.requestNumber);
        /*Ticketed Event ? or Event*/
        if (user == null){
            Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_USER_NOT_LOGGED_IN);
            //assert false:Logger.getInstance().getLog();
        }
        else if (!(user instanceof GovernmentRepresentative)){
            Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_USER_NOT_GOVERNMENT_REPRESENTATIVE);
            //assert false;
        }
        else if (this.percentToSponsor < 0 | this.percentToSponsor > 100){
            Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_INVALID_PERCENTAGE);
            //assert false;
        }
        else if (request == null){
            Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_REQUEST_NOT_FOUND);
            //assert false;
        }
        else if ((request.getStatus() != SponsorshipStatus.PENDING)){
            Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_REQUEST_NOT_PENDING);
            //assert false;
        }
        else{
            if (this.percentToSponsor == 0){
                request.reject();
                request.getEvent().getOrganiser().getProviderSystem().recordSponsorshipRejection(request.getEvent().getEventNumber());
                Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_REJECT);
                this.successResult = true;
            }
            else{
                TicketedEvent event = request.getEvent();
                double price = this.percentToSponsor * 0.01 * event.getOriginalTicketPrice()
                        * event.getNumTickets();
                if (context.getPaymentSystem().processPayment(user.getPaymentAccountEmail(),
                        context.getSponsorshipState().findRequestByNumber(this.requestNumber).getEvent().getOrganiser().getPaymentAccountEmail(),
                        price
                        )){
                    // Assume government email is payment account email
                    request.accept(this.percentToSponsor,user.getPaymentAccountEmail());
                    request.getEvent().getOrganiser().getProviderSystem().recordSponsorshipAcceptance(request.getEvent().getEventNumber(),this.percentToSponsor);
                    this.successResult = true;
                    Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_PAYMENT_SUCCESS);
                    Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_APPROVE);
                }
                else{
                    Logger.getInstance().logAction("RespondSponsorshipCommand",LogStatus.RESPOND_SPONSORSHIP_PAYMENT_FAILED);
                    return;
                }
            }
        }
    }

    /**
     * @return True if successful and false otherwise
     * {@inheritDoc}
     */
    public Boolean getResult() {
        return this.successResult;
    }

    public enum LogStatus{
        RESPOND_SPONSORSHIP_APPROVE,
        RESPOND_SPONSORSHIP_REJECT,
        RESPOND_SPONSORSHIP_USER_NOT_LOGGED_IN,
        RESPOND_SPONSORSHIP_USER_NOT_GOVERNMENT_REPRESENTATIVE,
        RESPOND_SPONSORSHIP_REQUEST_NOT_FOUND,
        RESPOND_SPONSORSHIP_INVALID_PERCENTAGE,
        RESPOND_SPONSORSHIP_REQUEST_NOT_PENDING,
        RESPOND_SPONSORSHIP_PAYMENT_SUCCESS,
        RESPOND_SPONSORSHIP_PAYMENT_FAILED,
    }

}
