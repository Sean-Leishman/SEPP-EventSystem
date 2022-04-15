package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Event is an abstraction, containing common state and behaviour that NonTicketedEvents and
 * TicketedEvents share.
 */
public abstract class Event extends Object {

    private long eventNumber;
    private EntertainmentProvider organiser;
    private String title;
    private EventType type;
    private EventStatus status;
    private List<EventPerformance> performances;

    /**
     * Create a new Event with status = EventStatus.ACTIVE and an empty collection of performances
     * 
     * @param eventNumber unique event identifier
     * @param organiser organising EntertainmentProvider of this event
     * @param title name of this event
     * @param type type of this event
     */
    protected Event(long eventNumber, EntertainmentProvider organiser, String title, EventType type) {
        this.eventNumber = eventNumber;
        this.organiser = organiser;
        this.title = title;
        this.type = type;
        this.status = EventStatus.ACTIVE;
        this.performances = new ArrayList<EventPerformance>();
    }


    public long getEventNumber() {
        return this.eventNumber;
    }


    public EntertainmentProvider getOrganiser() {
        return this.organiser;
    }


    public String getTitle() {
        return this.title;
    }


    public EventType getType() {
        return this.type;
    }


    public EventStatus getStatus() {
        return this.status;
    }

    /**
     * Set status to EventStatus.CANCELLED
     */
    public void cancel() {
        this.status = EventStatus.CANCELLED;
    }


    public void addPerformance(EventPerformance performance) {
        this.performances.add(performance);
    }


    public EventPerformance getPerformanceByNumber(long performanceNumber) {
       return this.performances.stream()
                .filter(x -> x.getPerformanceNumber() == performanceNumber)
                .findFirst()
                .orElse(null);
    }

    public List<EventPerformance> getPerformances() {
        return this.performances;
    }

}
