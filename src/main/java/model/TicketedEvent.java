package model;

/**
 * TicketedEvent represents an event that can be booked by Consumers. Tickets can be free, but they are
 * required to attend, and there is a maximum cap on the number of tickets that can be booked.
 */
public class TicketedEvent extends Event {

    private double ticketPrice;
    private int numTickets;
    private SponsorshipRequest sponsorshipRequest;

    /**
     * @param eventNumber unique event identifier
     * @param organiser the EntertainmentProvider who created this event
     * @param title name of the event
     * @param type type of the event
     * @param ticketPrice price per ticket in GBP
     * @param numTickets maximum number of tickets, initially all available
     */
    public TicketedEvent(long eventNumber, EntertainmentProvider organiser, String title, EventType type, double ticketPrice, int numTickets) {
        super(eventNumber, organiser, title, type);
        this.ticketPrice = ticketPrice;
        this.numTickets = numTickets;
    }

    /**
     * @return The original ticket price (whether the event is sponsored or not)
     */
    public double getOriginalTicketPrice() {
        return this.ticketPrice;
    }

    /**
     * @return The original ticket price if the event is not sponsored, and a discounted price if it is
     */
    public double getDiscountedTicketPrice() {
        if (this.isSponsored()) {
            return (100 - this.sponsorshipRequest.getSponsoredPricePercent()) / 100.0 * this.ticketPrice;
        }
        else{
            return this.getOriginalTicketPrice();
        }
    }

    /**
     * @return Number of the maximum cap of tickets which were initially available
     */
    public int getNumTickets() {
        return this.numTickets;
    }

    /**
     * @return The sponsor's PaymentSystem account email address if the event is sponsored, and null
     * otherwise
     */
    public String getSponsorAccountEmail() {
        return this.sponsorshipRequest.getSponsorAccountEmail();
    }


    public boolean isSponsored() {
        // if the TicketedEvent does contain a sponsorship request, return whether it has been accepted or not
        if (this.sponsorshipRequest != null) {
            return this.sponsorshipRequest.getStatus() == SponsorshipStatus.ACCEPTED;
        }
        else{
            return false;
        }
    }


    public void setSponsorshipRequest(SponsorshipRequest sponsorshipRequest) {
        this.sponsorshipRequest = sponsorshipRequest;
    }

    @Override
    public String toString() {
        return "TicketedEvent{" +
                "ticketPrice=" + ticketPrice +
                ", numTickets=" + numTickets +
                '}';
    }
}
