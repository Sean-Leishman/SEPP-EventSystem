package external;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A mock implementation of EntertainmentProviderSystem, substituting real systems for testing purposes.
 * In a real life application, this implementation would be making network requests to a service endpoint provided
 * by each EntertainmentProvider, that adheres to a common API contract. However, networking is the topic
 * of another course (if this sounds interesting, you may want to take COMN - Computer Communications and Networks in year 3 or 4).
 * 
 * For this course, the mock implementation simply needs to keep track of the number of tickets initially available when
 * a TicketedEvent is created and decrease this number when bookings are made or increase it, when bookings
 * are cancelled. We can assume no bookings are made outside this application.
 * 
 * You may find it helpful to print messages to STDOUT whenever EntertainmentProviderSystem methods are called.
 */
public class MockEntertainmentProviderSystem extends Object implements EntertainmentProviderSystem {

    private String orgName;
    private String orgAddress;
    private HashMap<Long,Integer> eventNumberAndTickets;
    private HashMap<Long,Long> performanceNumberAndEvent;
    private HashMap<Long,Integer> bookingNumberAndTickets;
    private HashMap<Long,Long> bookingNumberAndEvent;
    private HashMap<Long,Float> eventNumberAndPercentage;

    /**
     * Create a new MockEntertainmentProviderSystem for the given organisation
     * 
     * @param orgName name of the organisation
     * @param orgAddress address of the organisation
     */
    public MockEntertainmentProviderSystem(String orgName, String orgAddress) {
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.eventNumberAndTickets = new HashMap<Long,Integer>();
        this.performanceNumberAndEvent = new HashMap<Long,Long>();
        this.bookingNumberAndTickets = new HashMap<Long,Integer>();
        this.bookingNumberAndEvent = new HashMap<Long,Long>();
        this.eventNumberAndPercentage = new HashMap<Long, Float>();
    }

    /**
     * @param eventNumber unique event identifier corresponding to the newly created event
     * @param title title of the newly created event
     * @param numTickets number of tickets initially available in the newly created event
     * {@inheritDoc}
     */
    public void recordNewEvent(long eventNumber, String title, int numTickets) {
        System.out.println("New Event Created\n" + "Event Number:" + eventNumber + "\nTitle"
        + title + "\nInitial Ticket Number" + numTickets);
        this.eventNumberAndTickets.put(eventNumber,numTickets);
    }

    /**
     * @param eventNumber unique identifier corresponding to the cancelled Event
     * @param message a message from the organiser to all the Consumers who had Bookings for
     * any EventPerformances of this event
     * {@inheritDoc}
     */
    public void cancelEvent(long eventNumber, String message) {
        System.out.println("Event Number:" + eventNumber + " has been cancelled\n" + message);

        this.performanceNumberAndEvent.entrySet().removeIf(e -> e.getValue() == eventNumber);

        /*for (Long performanceNumber : copyPerformanceNumberAndEvent.keySet()){
            if (copyPerformanceNumberAndEvent.get(performanceNumber) == eventNumber){
                copyPerformanceNumberAndEvent.remove(performanceNumber);
            }
        }*/
        this.bookingNumberAndEvent.entrySet().removeIf(e -> e.getValue() == eventNumber);
        /*for (Long bookingNumber : this.bookingNumberAndEvent.keySet()){
            if (this.bookingNumberAndEvent.get(bookingNumber) == eventNumber){
                this.bookingNumberAndEvent.remove(bookingNumber);
                this.bookingNumberAndTickets.remove(bookingNumber);
            }
        }*/
        if (this.eventNumberAndPercentage.containsKey(eventNumber)){
            this.eventNumberAndPercentage.remove(eventNumber);
        }
    }

    /**
     * @param eventNumber unique event identifier corresponding to the event to which the performance was added
     * @param performanceNumber unique performance identifier corresponding to the newly added performance
     * @param startDateTime date and time when the performance is due to start
     * @param endDateTime date and time when the performance is due to end
     * {@inheritDoc}
     */
    public void recordNewPerformance(long eventNumber, long performanceNumber, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        System.out.println("New Performance Created\n" + "Event Number:" + eventNumber + "Performance Number:" + performanceNumber);
        this.performanceNumberAndEvent.put(performanceNumber,eventNumber);
    }

    /**
     * @param eventNumber unique event identifier corresponding to the event to query
     * @param performanceNumber unique performance identifier corresponding to the event performance to query
     * @return number of available tickets
     * {@inheritDoc}
     */
    public int getNumTicketsLeft(long eventNumber, long performanceNumber) {
        Integer ticketsLeft = this.eventNumberAndTickets.get(eventNumber);
        System.out.println("Event Number:" + eventNumber + " has " + ticketsLeft + " tickets left");
        return ticketsLeft;
    }

    /**
     * @param eventNumber unique event identifier corresponding to the event the booking was made for
     * @param performanceNumber unique performance identifier corresponding to the performance the booking was made for
     * @param bookingNumber unique booking identifier corresponding to the new booking
     * @param consumerName full name of the consumer who made the booking
     * @param consumerEmail email address of the consumer who made the booking
     * @param bookedTickets number of booked tickets
     * {@inheritDoc}
     */
    public void recordNewBooking(long eventNumber, long performanceNumber, long bookingNumber, String consumerName, String consumerEmail, int bookedTickets) {
        System.out.println("Event Number:" + eventNumber + "\nPerformance Number" + performanceNumber + "\nBooking Number" + bookingNumber);
        this.eventNumberAndTickets.put(eventNumber, this.eventNumberAndTickets.get(eventNumber) - bookedTickets);
        this.bookingNumberAndTickets.put(bookingNumber, bookedTickets);
        this.bookingNumberAndEvent.put(bookingNumber, eventNumber);
    }

    /**
     * @param bookingNumber unique booking identifier corresponding to the cancelled booking
     * {@inheritDoc}
     */
    public void cancelBooking(long bookingNumber) {
        Integer ticketsToCancel = this.bookingNumberAndTickets.get(bookingNumber);
        Long eventNumber = this.bookingNumberAndEvent.get(bookingNumber);
        this.eventNumberAndTickets.put(eventNumber, this.eventNumberAndTickets.get(eventNumber) + ticketsToCancel);
        this.bookingNumberAndEvent.remove(bookingNumber);
        this.bookingNumberAndTickets.remove(bookingNumber);
        System.out.println("Event Number:" + eventNumber + "\nBooking Number" + bookingNumber + " has been cancelled");
    }

    /**
     * @param eventNumber unique event identifier corresponding to the event that was sponsored
     * @param sponsoredPricePercent sponsorship amount in % of the maximum number of tickets * ticket price
     * {@inheritDoc}
     */
    public void recordSponsorshipAcceptance(long eventNumber, int sponsoredPricePercent) {
        this.eventNumberAndTickets.put(eventNumber, sponsoredPricePercent);
        System.out.println("Event Number:" + eventNumber + " has a sponsor percentage of " + sponsoredPricePercent);
    }

    /**
     * @param eventNumber unique event identifier corresponding to the event that was not sponsored
     * {@inheritDoc}
     */
    public void recordSponsorshipRejection(long eventNumber) {
        this.eventNumberAndTickets.put(eventNumber, 0);
        System.out.println("Event Number:" + eventNumber + " has not been sponsored");
    }

}
