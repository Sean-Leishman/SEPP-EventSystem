package command;

import controller.Context;
import model.Event;
import model.EventPerformance;
import model.TicketedEvent;

/**
 * GetAvailablePerformanceTicketsCommand allows users (even if they are not logged in) to see up-to-date
 * information about how many tickets are available for a specific TicketedEvent EventPerformance.
 */
public class GetAvailablePerformanceTicketsCommand extends Object implements ICommand {
    private long eventNumber;
    private long performanceNumber;

    private int availablePerformanceTickets;
    /**
     * @param eventNumber identifier for the event to query
     * @param performanceNumber identifier for the event performance to query
     */
    public GetAvailablePerformanceTicketsCommand(long eventNumber, long performanceNumber) {
        this.eventNumber = eventNumber;
        this.performanceNumber = performanceNumber;
    }

    /**
     * @param context object that provides access to global application state
     * @verifies.that the specified event identifier corresponds to an existing event
     * @verifies.that the event is a ticketed event
     * @verifies.that the specified performance identifier corresponds to an existing event performance
     * {@inheritDoc}
     */
    public void execute(Context context) {
        Event event = context.getEventState().findEventByNumber(this.eventNumber);
        EventPerformance eventPerformance = context.getEventState().findEventByNumber(this.eventNumber).getPerformanceByNumber(this.performanceNumber);
        if (event == null){
            assert false;
        }
        if (!(event instanceof TicketedEvent)){
            assert false;
        }
        if (eventPerformance == null) {
            assert false;
        }
        this.availablePerformanceTickets = event.getOrganiser().getProviderSystem().getNumTicketsLeft(this.eventNumber,this.performanceNumber);

    }

    /**
     * @return Number of available tickets for the specified event performance if successful and null otherwise
     * {@inheritDoc}
     */
    public Integer getResult() {
        return this.availablePerformanceTickets;
    }

}
