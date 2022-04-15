package controller;

import external.MockPaymentSystem;
import external.PaymentSystem;
import state.IBookingState;
import state.BookingState;
import state.IEventState;
import state.EventState;
import state.ISponsorshipState;
import state.SponsorshipState;
import state.IUserState;
import state.UserState;

/**
 * Context is a wrapper around the entire app state. It keeps references to the external PaymentSystem,
 * and the internal states: IUserState, IEventState, IBookingState, and ISponsorshipState.
 * The state classes are kept as interfaces, so that other classes using the context cannot depend on their implementation
 * details.
 */
public class Context extends Object {

    private PaymentSystem paymentSystem;
    private IUserState userState;
    private IEventState eventState;
    private IBookingState bookingState;
    private ISponsorshipState sponsorshipState;

    /**
     * Initialises all the state members with default constructors of the concrete implementations: MockPaymentSystem,
     * UserState, EventState, BookingState, and SponsorshipState.
     */

    public Context() {
        this.paymentSystem = new MockPaymentSystem();
        this.userState = new UserState();
        this.eventState = new EventState();
        this.bookingState = new BookingState();
        this.sponsorshipState = new SponsorshipState();
    }

    /**
     * Copy constructor, makes a deep copy of another Context, except for the paymentSystem, for
     * which a shallow reference copy is made.
     * 
     * @param other context to copy
     */
    public Context(Context other) {
        this.paymentSystem = other.getPaymentSystem();
        this.userState = other.getUserState();
        this.eventState = other.getEventState();
        this.bookingState = other.getBookingState();
        this.sponsorshipState = other.getSponsorshipState();
    }


    public PaymentSystem getPaymentSystem() {
        return this.paymentSystem;
    }


    public IUserState getUserState() {
        return this.userState;
    }


    public IBookingState getBookingState() {
        return this.bookingState;
    }


    public IEventState getEventState() {
        return this.eventState;
    }


    public ISponsorshipState getSponsorshipState() {
        return this.sponsorshipState;
    }

}
