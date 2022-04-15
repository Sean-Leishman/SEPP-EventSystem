package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Consumer represents a user of the application, who can browse Events and book TicketedEvents.
 */
public class Consumer extends User {

    private List<Booking> bookings;
    private String name;
    private String phoneNumber;
    private ConsumerPreferences preferences;
    /**
     * Create a new Consumer with an empty list of bookings and default Covid-19 preferences
     * 
     * @param name full name of the Consumer
     * @param email email address of the Consumer (used to log in to the application and for event cancellation
     * notifications)
     * @param phoneNumber phone number of the Consumer (used for event cancellation notifications)
     * @param password password used to log in to the application
     * @param paymentAccountEmail email address corresponding to the Consumer's account on the external
     * PaymentSystem
     */

    public Consumer(String name, String email, String phoneNumber, String password, String paymentAccountEmail) {
        super(email, password, paymentAccountEmail);
        this.bookings = new ArrayList<Booking>();
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.preferences = new ConsumerPreferences();
    }


    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }


    public String getName() {
        return this.name;
    }


    public ConsumerPreferences getPreferences() {
        return this.preferences;
    }


    public void setPreferences(ConsumerPreferences preferences) {
        this.preferences = preferences;
    }


    public List<Booking> getBookings() {
        return this.bookings;
    }

    /**
     * Mock method: print out a message to STDOUT. A real implementation would send an email and/or text to the
     * Consumer's phoneNumber.
     * 
     * @param message message from an EntertainmentProvider regarding an event cancellation
     */
    public void notify(String message) {
        System.out.println(message);
    }


    public void setName(String newName) {
        this.name = newName;
    }


    public void setPhoneNumber(String newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
    }

    @Override
    public String toString() {
        return "Consumer{" +
                "bookings=" + bookings +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", preferences=" + preferences +
                "} " + super.toString();
    }
}
