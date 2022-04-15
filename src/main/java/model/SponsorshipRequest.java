package model;

/**
 * SponsorshipRequests can be optionally created when creating a TicketedEvent.
 * GovernmentRepresentatives review these requests and can accept or reject them.
 */
public class SponsorshipRequest extends Object {

    private long requestNumber;
    private TicketedEvent event;
    private SponsorshipStatus status;
    private Integer sponsoredPricePercent;
    private String sponsorAccountEmail;

    /**
     * Create a new SponsorshipRequest with status = SponsorshipStatus.PENDING
     * 
     * @param requestNumber unique identifier for this request
     * @param event event that this request is for
     */
    public SponsorshipRequest(long requestNumber, TicketedEvent event) {
        this.requestNumber = requestNumber;
        this.event = event;
        this.status = SponsorshipStatus.PENDING;
    }


    public long getRequestNumber() {
        return this.requestNumber;
    }


    public TicketedEvent getEvent() {
        return this.event;
    }


    public SponsorshipStatus getStatus() {
        return this.status;
    }

    /**
     * @return Sponsored price %, if the request is accepted by the government, and null otherwise
     */
    public Integer getSponsoredPricePercent() {
        return this.sponsoredPricePercent;
    }

    /**
     * @return The sponsor's PaymentSystem account email address if the request is accepted by the
     * government, and null otherwise
     */
    public String getSponsorAccountEmail() {
        return this.sponsorAccountEmail;
    }

    /**
     * Set status to SponsorshipStatus.ACCEPTED
     * 
     * @param percent sponsored price %
     * @param sponsorAccountEmail sponsor's PaymentSystem account email address
     */
    public void accept(int percent, String sponsorAccountEmail) {
        this.sponsoredPricePercent = percent;
        this.sponsorAccountEmail = sponsorAccountEmail;
        this.status = SponsorshipStatus.ACCEPTED;
    }

    /**
     * Set status to SponsorshipStatus.REJECTED
     */
    public void reject() {
        this.status = SponsorshipStatus.REJECTED;
    }

}
