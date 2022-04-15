package model;

/**
 * GovernmentRepresentative is a user who represents the government. They are able to view all Events
 * and Bookings, cancel events, and accept or reject SponsorshipRequests.
 */
public class GovernmentRepresentative extends User {

    public GovernmentRepresentative(String email, String password, String paymentAccountEmail) {
        super(email, password, paymentAccountEmail);
    }

    @Override
    public String toString() {
        return "GovernmentRepresentative{} " + super.toString();
    }
}
