package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EventPerformance represents an event taking place at a concrete place, date, and time.
 */
public class EventPerformance extends Object {

    private long performanceNumber;
    private Event event;
    private String venueAddress;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<String> performerNames;
    private boolean hasSocialDistancing;
    private boolean hasAirFiltration;
    private boolean isOutdoors;
    private int capacityLimit;
    private int venueSize;
    /**
     * @param performanceNumber unique performance identifier
     * @param event Event that this performance belongs to
     * @param venueAddress address where the performance will be taking place
     * @param startDateTime date and time when the performance will begin
     * @param endDateTime date and time when the performance will end
     * @param performerNames a list of names of those who will be performing
     * @param hasSocialDistancing whether social distancing will be in place at the performance
     * @param hasAirFiltration whether air filtration will be in place at the performance
     * @param isOutdoors whether the performance will take place outdoors
     * @param capacityLimit maximum number of people who will be allowed to attend the performance
     * @param venueSize maximum number of people who could legally be allowed in the venue (barring other limitations)
     */
    public EventPerformance(long performanceNumber, Event event, String venueAddress, LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> performerNames, boolean hasSocialDistancing, boolean hasAirFiltration, boolean isOutdoors, int capacityLimit, int venueSize) {
        this.performanceNumber = performanceNumber;
        this.event = event;
        this.venueAddress = venueAddress;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.performerNames = performerNames;
        this.hasSocialDistancing = hasSocialDistancing;
        this.hasAirFiltration = hasAirFiltration;
        this.isOutdoors = isOutdoors;
        this.capacityLimit = capacityLimit;
        this.venueSize = venueSize;
    }


    public long getPerformanceNumber() {
        return this.performanceNumber;
    }


    public Event getEvent() {
        return this.event;
    }


    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }


    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }


    public boolean hasSocialDistancing() {
        return this.hasSocialDistancing;
    }


    public boolean hasAirFiltration() {
        return this.hasAirFiltration;
    }


    public boolean isOutdoors() {
        return this.isOutdoors;
    }


    public int getCapacityLimit() {
        return this.capacityLimit;
    }


    public int getVenueSize() {
        return this.venueSize;
    }



}
