import command.*;
import controller.Controller;
import logging.Logger;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreateEventSystemTests {
    @BeforeEach
    void printTestName(TestInfo testInfo){
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs(){
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    private static EntertainmentProvider registerEntertainmentProvider(Controller controller){
        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Cinema Conglomerate",
                "Global Office, International Space Station",
                "$$$@there'sNoEmailValidation.wahey!",
                "Mrs Representative",
                "odeon@cineworld.com",
                "F!ghT th3 R@Pture",
                List.of("Dr Strangelove"),
                List.of("we_dont_get_involved@cineworld.com"));
        controller.runCommand(cmd);
        return cmd.getResult();
    }

    private static Event getEventsFromListOfEvents(List<Event> events,long eventNo){
        for (Event e : events){
            if (e.getEventNumber() == eventNo){
                return e;
            }
        }
        return null;
    }

    private static SponsorshipRequest getSponsershipRequestInSponsershipRequests(Controller controller, Event event){
        ListSponsorshipRequestsCommand cmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(cmd);
        List<SponsorshipRequest> sponsorshipRequests = cmd.getResult();
        for (SponsorshipRequest s: sponsorshipRequests){
            if (s.getEvent().equals(event)){
                return s;
            }
        }
        return null;
    }

    /**
     * Verifies that CreateNonTicketedEventCommand works correctly with expected inputs
     *
     * Register an entertainment provider and gather the list of all events before and after the command has run
     * Run the command CreateNonTicketedEventCommand with some inputs and add a performance to the event in the future
     *      - this to ensure ListEventsCommand finds the event as to be listed a performance must be in the future
     *
     * Assert: number of events before and after creation increases
     *         event returned is not null
     *         log is correct
     *         events belonging to the entertainment provider match the newly inputted value
     *         the event information is inputted correctly
     *
     */
    @Test
    void createNonTicketedEvent(){
        Controller controller = new Controller();

        EntertainmentProvider entertainmentProvider = registerEntertainmentProvider(controller);

        /*
        Gets the list of all events in the future prior to creating a new event
         */
        ListEventsCommand getPriorEvents = new ListEventsCommand(false,false);
        controller.runCommand(getPriorEvents);
        int priorEventSize = getPriorEvents.getResult().size();

        CreateNonTicketedEventCommand cmd = new CreateNonTicketedEventCommand(
                "The LEGO Movie",
                EventType.Movie
        );
        controller.runCommand(cmd);

        String createEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        controller.runCommand(new AddEventPerformanceCommand(cmd.getResult(),
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50));

        /*
        Gets list of all events after creating a new event
         */
        ListEventsCommand getAfterEvents = new ListEventsCommand(false,false);
        controller.runCommand(getAfterEvents);
        List <Event> afterEvents = getAfterEvents.getResult();

        NonTicketedEvent event = (NonTicketedEvent) getEventsFromListOfEvents(afterEvents,cmd.getResult());

        assertAll("Event is created correctly",
                () -> assertEquals(priorEventSize+1,afterEvents.size()),
                () -> assertNotNull(event),
                () -> assertEquals(CreateNonTicketedEventCommand.LogStatus.CREATE_NON_TICKETED_SUCCESS.toString(), createEventLog),
                () -> assertTrue(entertainmentProvider.getEvents().stream().anyMatch(i -> i.getEventNumber() == event.getEventNumber())),
                () -> assertAll("Event information is correct",
                        () -> assertEquals("The LEGO Movie",event.getTitle()),
                        () -> assertEquals(EventType.Movie,event.getType()),
                        () -> assertEquals(entertainmentProvider, event.getOrganiser()))
        );
    }
    /**
     * Verifies that CreateTicketedEvent works correctly with expected inputs while not requesting a sponsorship
     *
     * Register an entertainment provider and gather the list of all events before and after the command has run
     * Run the command CreateTicketedEvent with some inputs and add a performance to the event in the future
     *      - this to ensure ListEventsCommand finds the event as to be listed a performance must be in the future
     *
     * Assert: number of events before and after creation increases
     *         event returned is not null
     *         log is correct
     *         event added to list of events for entertainment provider is correct
     *         the event information is inputted correctly
     *         the sponsorship has been handled correctly such that there is no sponsorship request
     *
     */
    @Test
    void createTicketedEventWithoutSponsorship(){
        Controller controller = new Controller();

        EntertainmentProvider entertainmentProvider = registerEntertainmentProvider(controller);

        /*
        Gets the list of all events in the future prior to creating a new event
         */
        ListEventsCommand getPriorEvents = new ListEventsCommand(false,false);
        controller.runCommand(getPriorEvents);
        int priorEventSize = getPriorEvents.getResult().size();

        CreateTicketedEventCommand cmd = new CreateTicketedEventCommand(
                "The LEGO Movie",
                EventType.Movie,
                50,
                15.75,
                false
        );
        controller.runCommand(cmd);
        String createEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        controller.runCommand(new AddEventPerformanceCommand(cmd.getResult(),
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50));

        /*
        Gets list of all events after creating a new event
         */
        ListEventsCommand getAfterEvents = new ListEventsCommand(false,false);
        controller.runCommand(getAfterEvents);
        List <Event> afterEvents = getAfterEvents.getResult();

        TicketedEvent event = (TicketedEvent) getEventsFromListOfEvents(afterEvents,cmd.getResult());

        controller.runCommand(new LoginCommand(
                "margaret.thatcher@gov.uk", "The Good times  "
        ));

        SponsorshipRequest sponsorshipRequest = getSponsershipRequestInSponsershipRequests(controller,event);

        assertAll("Event is created correctly",
                () -> assertEquals(priorEventSize+1,afterEvents.size()),
                () -> assertNotNull(event),
                () -> assertEquals(CreateTicketedEventCommand.LogStatus.CREATE_TICKETED_EVENT_SUCCESS.toString(), createEventLog),
                () -> assertTrue(entertainmentProvider.getEvents().stream().anyMatch(i -> i.getEventNumber() == event.getEventNumber())),
                () -> assertAll("Event information is correct",
                        () -> assertEquals("The LEGO Movie",event.getTitle()),
                        () -> assertEquals(EventType.Movie,event.getType()),
                        () -> assertEquals(50,event.getNumTickets()),
                        () -> assertEquals(15.75,event.getOriginalTicketPrice()),
                        () -> assertEquals(entertainmentProvider, event.getOrganiser())),
                () -> assertAll("Verify sponsorship details",
                        () -> assertNull(sponsorshipRequest))
        );
    }

    /**
     * Verifies that CreateTicketedEvent works correctly with expected inputs while requesting a sponsorship
     *
     * Register an entertainment provider and gather the list of all events before and after the command has run
     * Run the command CreateTicketedEvent with some inputs and add a performance to the event in the future
     *      - this to ensure ListEventsCommand finds the event as to be listed a performance must be in the future
     *
     * Assert: number of events before and after creation increases
     *         event returned is not null
     *         log is correct
     *         event added to list of events for entertainment provider is correct
     *         the event information is inputted correctly
     *         the sponsorship has been handled correctly such that the details of the request is correct
     *
     */

    @Test
    void createTicketedEventWithSponsorship(){
        Controller controller = new Controller();

        EntertainmentProvider entertainmentProvider = registerEntertainmentProvider(controller);

        /*
        Gets the list of all events in the future prior to creating a new event
         */
        ListEventsCommand getPriorEvents = new ListEventsCommand(false,false);
        controller.runCommand(getPriorEvents);
        int priorEventSize = getPriorEvents.getResult().size();

        CreateTicketedEventCommand cmd = new CreateTicketedEventCommand(
                "The LEGO Movie",
                EventType.Movie,
                50,
                15.75,
                true
        );
        controller.runCommand(cmd);

        String createEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        // Requires performance due to additional advice no20. relating to future events for output of ListEventsCommand
        controller.runCommand(new AddEventPerformanceCommand(cmd.getResult(),
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50));

        /*
        Gets list of all events after creating a new event
         */
        ListEventsCommand getAfterEvents = new ListEventsCommand(false,false);
        controller.runCommand(getAfterEvents);
        List <Event> afterEvents = getAfterEvents.getResult();

        TicketedEvent event = (TicketedEvent) getEventsFromListOfEvents(afterEvents,cmd.getResult());

        controller.runCommand(new LoginCommand(
                "margaret.thatcher@gov.uk", "The Good times  "
        ));
        SponsorshipRequest sponsorshipRequest = getSponsershipRequestInSponsershipRequests(controller,event);

        assertAll("Event is created correctly",
                () -> assertEquals(priorEventSize+1,afterEvents.size()),
                () -> assertNotNull(event),
                () -> assertEquals(CreateTicketedEventCommand.LogStatus.CREATE_TICKETED_EVENT_SUCCESS.toString(), createEventLog),
                () -> assertTrue(entertainmentProvider.getEvents().stream().anyMatch(i -> i.getEventNumber() == event.getEventNumber())),
                () -> assertAll("Event information is correct",
                        () -> assertEquals("The LEGO Movie",event.getTitle()),
                        () -> assertEquals(EventType.Movie,event.getType()),
                        () -> assertEquals(50,event.getNumTickets()),
                        () -> assertEquals(15.75,event.getOriginalTicketPrice()),
                        () -> assertEquals(entertainmentProvider, event.getOrganiser()),
                        () -> assertEquals(EventStatus.ACTIVE,event.getStatus())),
                () -> assertAll("Verify sponsorship details",
                        () -> assertNotNull(sponsorshipRequest),
                        () -> assertEquals(event,sponsorshipRequest.getEvent()),
                        () -> assertEquals(SponsorshipStatus.PENDING,sponsorshipRequest.getStatus()))
        );
    }
    /**
     * Attempts to createEvent when no user is logged in
     *
     * Register an entertainment provider and gather the list of all events before and after the command has run
     * Logout of entertainment provider account before running CreateTicketedEventCommand
     * Run the command CreateTicketedEvent with some inputs and add a performance to the event in the future
     *      - this to ensure ListEventsCommand finds the event as to be listed a performance must be in the future
     *
     * Assert: Command output is null
     *         output from CreateEventCommand is correct
     *         events belonging to the entertainment provider is unchanged
     *         the number of events is unchanged
     *
     */
    @Test
    void verifyEntertainmentProviderLoggedIn(){
        Controller controller = new Controller();

        EntertainmentProvider entertainmentProvider = registerEntertainmentProvider(controller);
        controller.runCommand(new LogoutCommand());
        /*
        Gets the list of all events in the future prior to creating a new event
         */
        ListEventsCommand getPriorEvents = new ListEventsCommand(false,false);
        controller.runCommand(getPriorEvents);
        List <Event> priorEvents = getPriorEvents.getResult();

        CreateTicketedEventCommand cmd = new CreateTicketedEventCommand(
                "The LEGO Movie",
                EventType.Movie,
                50,
                15.75,
                false
        );
        controller.runCommand(cmd);

        String createEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        /*
        Gets list of all events after creating a new event
         */
        ListEventsCommand getAfterEvents = new ListEventsCommand(false,false);
        controller.runCommand(getAfterEvents);
        List <Event> afterEvents = getAfterEvents.getResult();

        System.out.println(Logger.getInstance().getLog());
        assertAll("Event is null and Log is correct",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(CreateEventCommand.LogStatus.CREATE_EVENT_USER_NOT_LOGGED_IN.toString(),
                        createEventLog),
                () -> assertTrue(entertainmentProvider.getEvents().isEmpty()),
                () -> assertNull(priorEvents),
                () -> assertNull(afterEvents)
        );
    }

    /**
     * Attempts to createEvent when the user is a consumer
     *
     * Register an entertainment provider and gather the list of all events before and after the command has run
     * Login to a consumer account
     * Run the command CreateTicketedEvent with some inputs and add a performance to the event in the future
     *      - this to ensure ListEventsCommand finds the event as to be listed a performance must be in the future
     *
     * Assert: Command output is null
     *         output from CreateEventCommand is correct
     *         events belonging to the entertainment provider is unchanged
     *         the number of events is unchanged
     *
     */
    @Test
    void verifyUserIsEntertainmentProvider(){
        Controller controller = new Controller();

        EntertainmentProvider entertainmentProvider = registerEntertainmentProvider(controller);
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new RegisterConsumerCommand("Mr Pickles",
                "picker345@gmail.com",
                "0983453678",
                "picklesRCool34",
                "picker345@hotmail.com"));

        /*
        Gets the list of all events in the future prior to creating a new event
         */
        ListEventsCommand getPriorEvents = new ListEventsCommand(false,false);
        controller.runCommand(getPriorEvents);
        int priorEventSize = getPriorEvents.getResult().size();


        CreateTicketedEventCommand cmd = new CreateTicketedEventCommand(
                "The LEGO Movie",
                EventType.Movie,
                50,
                15.75,
                false
        );
        controller.runCommand(cmd);

        String createEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        /*
        Gets list of all events after creating a new event
         */
        ListEventsCommand getAfterEvents = new ListEventsCommand(false,false);
        controller.runCommand(getAfterEvents);
        List <Event> afterEvents = getAfterEvents.getResult();

        System.out.println(Logger.getInstance().getLog());
        assertAll("Event is null and Log is correct",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(CreateEventCommand.LogStatus.CREATE_EVENT_USER_NOT_ENTERTAINMENT_PROVIDER.toString(),
                        createEventLog)
        );
    }
}
