import command.*;
import controller.Controller;
import logging.Logger;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CancelEventSystemTests {
    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    private static void loginGovernmentRepresentative(Controller controller) {
        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));
    }

    private static void loginOlympicsProvider(Controller controller) {
        controller.runCommand(new LoginCommand("anonymous@gmail.com", "anonymous"));
    }

    private static void loginBuskerProvider(Controller controller) {
        controller.runCommand(new LoginCommand("busk@every.day", "When they say 'you can't do this': Ding Dong! You are wrong!"));
    }

    private static void loginConsumer1(Controller controller) {
        controller.runCommand(new LoginCommand("jbiggson1@hotmail.co.uk", "jbiggson2"));
    }

    private static void loginConsumer2(Controller controller) {
        controller.runCommand(new LoginCommand("jane@inf.ed.ac.uk", "giantsRverycool"));
    }

    private static void loginConsumer3(Controller controller) {
        controller.runCommand(new LoginCommand("i-will-kick-your@gmail.com", "it is wednesday my dudes"));
    }

    private static Consumer register3Consumers(Controller controller) {
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand(
                "John Biggson",
                "jbiggson1@hotmail.co.uk",
                "077893153480",
                "jbiggson2",
                "jbiggson1@hotmail.co.uk"
        );
        controller.runCommand(registerConsumerCommand);
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand(
                "Jane Giantsdottir",
                "jane@inf.ed.ac.uk",
                "04462187232",
                "giantsRverycool",
                "jane@aol.com"
        ));
        controller.runCommand(new LogoutCommand());
        controller.runCommand(new RegisterConsumerCommand(
                "Wednesday Kebede",
                "i-will-kick-your@gmail.com",
                "-",
                "it is wednesday my dudes",
                "i-will-kick-your@gmail.com"
        ));
        controller.runCommand(new LogoutCommand());
        return registerConsumerCommand.getResult();
    }

    private static void createBuskingProviderWith1Event(Controller controller) {
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "No org",
                "Leith Walk",
                "a hat on the ground",
                "the best musicican ever",
                "busk@every.day",
                "When they say 'you can't do this': Ding Dong! You are wrong!",
                Collections.emptyList(),
                Collections.emptyList()
        ));

        CreateNonTicketedEventCommand eventCmd = new CreateNonTicketedEventCommand(
                "Music for everyone!",
                EventType.Music
        );
        controller.runCommand(eventCmd);
        long eventNumber = eventCmd.getResult();

        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber,
                "Leith as usual",
                LocalDateTime.of(2030, 3, 20, 4, 20),
                LocalDateTime.of(2030, 3, 20, 6, 45),
                List.of("The same musician"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber,
                "You know it",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                List.of("The usual"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));

        controller.runCommand(new LogoutCommand());
    }

    private static void createCinemaProviderWith3Events(Controller controller) {
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "Cinema Conglomerate",
                "Global Office, International Space Station",
                "$$$@there'sNoEmailValidation.wahey!",
                "Mrs Representative",
                "odeon@cineworld.com",
                "F!ghT th3 R@Pture",
                List.of("Dr Strangelove"),
                List.of("we_dont_get_involved@cineworld.com")
        ));

        CreateTicketedEventCommand eventCmd1 = new CreateTicketedEventCommand(
                "The LEGO Movie",
                EventType.Movie,
                50,
                15.75,
                false
        );
        controller.runCommand(eventCmd1);
        long eventNumber1 = eventCmd1.getResult();
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().minusWeeks(1),
                LocalDateTime.now().minusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        ));

        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand(
                "Frozen Ballet",
                EventType.Dance,
                50,
                35,
                true
        );
        controller.runCommand(eventCmd2);
        long eventNumber2 = eventCmd2.getResult();
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber2,
                "Odeon cinema",
                LocalDateTime.now().plusWeeks(1).plusDays(2).plusHours(3),
                LocalDateTime.now().plusWeeks(1).plusDays(2).plusHours(6),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        ));

        CreateNonTicketedEventCommand eventCmd3 = new CreateNonTicketedEventCommand(
                "The Shining at the Meadows (Free Screening) (Live Action)",
                EventType.Sports
        );
        controller.runCommand(eventCmd3);
        long eventNumber3 = eventCmd3.getResult();
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber3,
                "The Meadows, Edinburgh",
                LocalDateTime.now(),
                LocalDateTime.now().plusYears(1),
                List.of("You"),
                false,
                false,
                true,
                1000,
                9999
        ));

        controller.runCommand(new LogoutCommand());
    }

    private static void createOlympicsProviderWith2Events(Controller controller) {
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com")
        ));

        CreateTicketedEventCommand eventCmd1 = new CreateTicketedEventCommand(
                "London Summer Olympics",
                EventType.Sports,
                123456,
                25,
                true
        );
        controller.runCommand(eventCmd1);
        long eventNumber1 = eventCmd1.getResult();

        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber1,
                "Wimbledon",
                LocalDateTime.now().plusMonths(1),
                LocalDateTime.now().plusMonths(1).plusHours(8),
                List.of("Everyone in disc throw and 400m sprint"),
                false,
                true,
                true,
                3000,
                3000
        ));
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber1,
                "Swimming arena",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(8),
                List.of("Everyone in swimming"),
                true,
                true,
                false,
                200,
                300
        ));
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber1,
                "Wimbledon",
                LocalDateTime.now().plusMonths(1).plusDays(1),
                LocalDateTime.now().plusMonths(1).plusDays(1).plusHours(6),
                List.of("Everyone in javelin throw and long jump"),
                false,
                true,
                true,
                3000,
                3000
        ));

        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand(
                "Winter Olympics",
                EventType.Sports,
                40000,
                400,
                true
        );
        controller.runCommand(eventCmd2);
        long eventNumber2 = eventCmd2.getResult();

        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber2,
                "The Alps",
                LocalDateTime.now().plusYears(2).plusMonths(7),
                LocalDateTime.now().plusYears(2).plusMonths(7).plusDays(3),
                List.of("Everyone in slalom skiing"),
                true,
                true,
                true,
                4000,
                10000
        ));
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber2,
                "Somewhere else",
                LocalDateTime.now().plusYears(2).plusMonths(7).plusDays(2),
                LocalDateTime.now().plusYears(2).plusMonths(7).plusDays(4),
                List.of("Everyone in ski jump"),
                true,
                true,
                true,
                4000,
                10000
        ));

        controller.runCommand(new LogoutCommand());
    }

    private static Event getEventByEventNumber(List<Event> events, long eventNo){
        for (Event e: events){
            if (e.getEventNumber() == eventNo){
                return e;
            }
        }
        return null;
    }
    private static Event consumerBookNthTicketedEvent(Controller controller, int n) {
        ListEventsCommand cmd = new ListEventsCommand(false, true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        for (Event event : events) {
            if (event instanceof TicketedEvent) {
                n--;
            }

            if (n <= 0) {
                Collection<EventPerformance> performances = event.getPerformances();
                controller.runCommand(new BookEventCommand(
                        event.getEventNumber(),
                        performances.iterator().next().getPerformanceNumber(),
                        1
                ));
                return event;
            }
        }
        return null;
    }

    private static void governmentAcceptAllSponsorships(Controller controller) {
        controller.runCommand(new LoginCommand(
                "margaret.thatcher@gov.uk", "The Good times  "
        ));
        ListSponsorshipRequestsCommand cmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(cmd);
        List<SponsorshipRequest> requests = cmd.getResult();

        for (SponsorshipRequest request : requests) {
            controller.runCommand(new RespondSponsorshipCommand(
                    request.getRequestNumber(), 25
            ));
        }
        controller.runCommand(new LogoutCommand());
    }

    /**
     * Verifies CancelEventCommand works correctly when cancelling an unsponsored event
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * Register some consumers then login as one so that we can book the event with eventNumber 1
     * Login as an entertainment provider
     * Before and after the CancelEventCommand we get a list of active events belonging to the loggged in entertainment
     * provider
     * Run the CancelEventCommand with some message and eventNumber 1
     *
     * Assert: number of events before and after successful cancellation reflect change
     *         number of total events including cancelled events is the same as the number of event before cancellation
     *         CancelEventCommand returns true
     *         log output is correct after CancelEventCommand
     *         status of a consumer booking of the cancelled event
     *         booking has been refunded at least once
     */
    @Test
    void cancelEventNotSponsored() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = register3Consumers(controller);

        loginConsumer1(controller);
        // Book event with EventNumber 1
        consumerBookNthTicketedEvent(controller,1);
        /*
        Gets active events which belong to the entertainment provider before attempting to cancel event
         */
        loginOlympicsProvider(controller);
        ListEventsCommand cmd1 = new ListEventsCommand(true, true);
        controller.runCommand(cmd1);
        List<Event> eventsBeforeCancel = cmd1.getResult();

        CancelEventCommand cancelEventCmd = new CancelEventCommand(1, "Trololol");
        controller.runCommand(cancelEventCmd);

        /*
        Gets active events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd2 = new ListEventsCommand(true, true);
        controller.runCommand(cmd2);
        List<Event> eventsAfterCancel = cmd2.getResult();

         /*
        Gets all events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd3 = new ListEventsCommand(true, false);
        controller.runCommand(cmd3);
        List<Event> allEvents = cmd3.getResult();


        Event event = allEvents.stream().filter(i -> i.getEventNumber() == 1).findFirst().get();

        assertAll("Verify cancellation with size of listed events",
                () -> assertEquals(eventsBeforeCancel.size()  - 1,eventsAfterCancel.size()),
                () -> assertEquals(allEvents.size(), eventsBeforeCancel.size()),
                () -> assertTrue(cancelEventCmd.getResult()),
                () -> assertEquals(EventStatus.CANCELLED, eventsBeforeCancel.get(0).getStatus()),
                () -> assertEquals(BookingStatus.CancelledByProvider,consumer.getBookings().stream().filter(
                        i -> i.getEventPerformance().getEvent() == event).findFirst().get().getStatus()),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        i -> i.getResult().equals(CancelEventCommand.LogStatus.CANCEL_EVENT_REFUND_BOOKING_SUCCESS.toString())))

        );


    }
    /**
     * Verifies CancelEventCommand works correctly when cancelling a sponsored event
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * Login as Government Representative and accept all sponsorships
     * Register some consumers then login as one so that we can book the event with eventNumber 1
     * Login as an entertainment provider
     * Before and after the CancelEventCommand we get a list of active events belonging to the logged in entertainment
     * provider
     * Run the CancelEventCommand with some message and eventNumber 1
     *
     * Assert: number of events before and after successful cancellation reflect change
     *         number of total events including cancelled events is the same as the number of event before cancellation
     *         CancelEventCommand returns true
     *         log output is correct after CancelEventCommand
     *         status of a consumer booking of the cancelled event
     *         booking has been refunded at least once
     *         booking has been refunded for the government
     */
    @Test
    void cancelEventIsSponsored() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = register3Consumers(controller);

        governmentAcceptAllSponsorships(controller);

        loginConsumer1(controller);
        // Book event with EventNumber 1
        consumerBookNthTicketedEvent(controller,1);
        /*
        Gets active events which belong to the entertainment provider before attempting to cancel event
         */
        loginOlympicsProvider(controller);
        ListEventsCommand cmd1 = new ListEventsCommand(true, true);
        controller.runCommand(cmd1);
        List<Event> eventsBeforeCancel = cmd1.getResult();

        CancelEventCommand cancelEventCmd = new CancelEventCommand(1, "Trololol");
        controller.runCommand(cancelEventCmd);

        /*
        Gets active events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd2 = new ListEventsCommand(true, true);
        controller.runCommand(cmd2);
        List<Event> eventsAfterCancel = cmd2.getResult();

         /*
        Gets all events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd3 = new ListEventsCommand(true, false);
        controller.runCommand(cmd3);
        List<Event> allEvents = cmd3.getResult();


        Event event = allEvents.stream().filter(i -> i.getEventNumber() == 1).findFirst().get();

        assertAll("Verify cancellation with size of listed events",
                () -> assertEquals(eventsBeforeCancel.size()  - 1,eventsAfterCancel.size()),
                () -> assertEquals(allEvents.size(), eventsBeforeCancel.size()),
                () -> assertTrue(cancelEventCmd.getResult()),
                () -> assertEquals(EventStatus.CANCELLED, eventsBeforeCancel.get(0).getStatus()),
                () -> assertEquals(BookingStatus.CancelledByProvider,consumer.getBookings().stream().filter(
                        i -> i.getEventPerformance().getEvent() == event).findFirst().get().getStatus()),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        i -> i.getResult().equals(CancelEventCommand.LogStatus.CANCEL_EVENT_REFUND_BOOKING_SUCCESS.toString()))),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        i -> i.getResult().equals(CancelEventCommand.LogStatus.CANCEL_EVENT_REFUND_SPONSORSHIP_SUCCESS.toString())))
        );


    }

    /**
     * Attempt CancelEventCommand when the message is empty
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * Register some consumers then login as one so that we can book the event with eventNumber 1
     * Login as an entertainment provider
     * Before and after the CancelEventCommand we get a list of active events belonging to the logged in entertainment
     * provider
     * Run the CancelEventCommand with an empty message and eventNumber 1
     *
     * Assert : the number of events before and after attempted cancellation is the same
     *          return value of false after cancellation
     *          the event status is still active
     *          the log is correct
     *          no refunds have taken place??
     */
    @Test
    void verifyMessage() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = register3Consumers(controller);

        /*
        Gets active events which belong to the entertainment provider before attempting to cancel event
         */
        loginOlympicsProvider(controller);
        ListEventsCommand cmd1 = new ListEventsCommand(true, true);
        controller.runCommand(cmd1);
        List<Event> eventsBeforeCancel = cmd1.getResult();

        CancelEventCommand cancelEventCmd = new CancelEventCommand(eventsBeforeCancel.get(0).getEventNumber(), "");
        controller.runCommand(cancelEventCmd);

        String cancelEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        /*
        Gets active events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd2 = new ListEventsCommand(true, true);
        controller.runCommand(cmd2);
        List<Event> eventsAfterCancel = cmd2.getResult();

         /*
        Gets all events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd3 = new ListEventsCommand(true, false);
        controller.runCommand(cmd3);
        List<Event> allEvents = cmd3.getResult();

        assertAll("Verify cancellation with size of listed events",
                () -> assertEquals(eventsBeforeCancel.size() ,eventsAfterCancel.size()),
                () -> assertFalse(cancelEventCmd.getResult()),
                () -> assertEquals(EventStatus.ACTIVE, eventsBeforeCancel.get(0).getStatus()),
                () -> assertEquals(CancelEventCommand.LogStatus.CANCEL_EVENT_MESSAGE_MUST_NOT_BE_BLANK.toString(), cancelEventLog)
        );


    }
    /**
     * Attempt CancelEventCommand when logged in user is not an Entertainment Representative
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * Register some consumers
     * Login as an entertainment provider
     * Before and after the CancelEventCommand we get a list of active events belonging to the logged in entertainment
     * provider
     * Login as a consumer then
     * Run the CancelEventCommand with some message and eventNumber 1
     *
     * Assert : the number of events before and after attempted cancellation is the same
     *          return value of false after cancellation
     *          the event status is still active
     *          the log is correct
     *          no refunds have taken place??
     */
    @Test
    void verifyLoggedInUserIsEntertainmentProvider() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        register3Consumers(controller);

        /*
        Gets active events which belong to the entertainment provider before attempting to cancel event
         */
        loginOlympicsProvider(controller);
        ListEventsCommand cmd1 = new ListEventsCommand(true, true);
        controller.runCommand(cmd1);
        List<Event> eventsBeforeCancel = cmd1.getResult();

        loginConsumer1(controller);
        CancelEventCommand cancelEventCmd = new CancelEventCommand(eventsBeforeCancel.get(0).getEventNumber(), "Message");
        controller.runCommand(cancelEventCmd);

        String cancelEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        loginOlympicsProvider(controller);
       /*
        Gets active events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd2 = new ListEventsCommand(true, true);
        controller.runCommand(cmd2);
        List<Event> eventsAfterCancel = cmd2.getResult();

         /*
        Gets all events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd3 = new ListEventsCommand(true, false);
        controller.runCommand(cmd3);
        List<Event> allEvents = cmd3.getResult();

        assertAll("Verify cancellation with size of listed events",
                () -> assertEquals(eventsBeforeCancel.size() ,eventsAfterCancel.size()),
                () -> assertFalse(cancelEventCmd.getResult()),
                () -> assertEquals(EventStatus.ACTIVE, eventsBeforeCancel.get(0).getStatus()),
                () -> assertEquals(CancelEventCommand.LogStatus.CANCEL_EVENT_USER_NOT_ENTERTAINMENT_PROVIDER.toString(), cancelEventLog)
        );
    }

    /**
     * Attempt CancelEventCommand when eventNumber used does not reference an Event
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * Register some consumers
     * Login as an entertainment provider
     * Before and after the CancelEventCommand we get a list of active events belonging to the logged in entertainment
     * provider
     * Run the CancelEventCommand with some message and some invalid eventNumber e.g. 200
     *
     * Assert : the number of events before and after attempted cancellation is the same
     *          return value of false after cancellation
     *          the event status is still active
     *          the log is correct
     *          no refunds have taken place??
     */
    @Test
    void verifyEventNumberExists() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        register3Consumers(controller);

        /*
        Gets active events which belong to the entertainment provider before attempting to cancel event
         */
        loginOlympicsProvider(controller);
        ListEventsCommand cmd1 = new ListEventsCommand(true, true);
        controller.runCommand(cmd1);
        List<Event> eventsBeforeCancel = cmd1.getResult();

        CancelEventCommand cancelEventCmd = new CancelEventCommand(200, "Message");
        controller.runCommand(cancelEventCmd);

        String cancelEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        loginOlympicsProvider(controller);
        /*
        Gets active events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd2 = new ListEventsCommand(true, true);
        controller.runCommand(cmd2);
        List<Event> eventsAfterCancel = cmd2.getResult();

         /*
        Gets all events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd3 = new ListEventsCommand(true, false);
        controller.runCommand(cmd3);
        List<Event> allEvents = cmd3.getResult();

        /*
        Verifies the number of events before and after attempted cancellation is the same
        Verifies return value of false after cancellation
        Verifies the event status is still active
        Verifies the log is correct
         */
        assertAll("Verify cancellation with size of listed events",
                () -> assertEquals(eventsBeforeCancel.size() ,eventsAfterCancel.size()),
                () -> assertFalse(cancelEventCmd.getResult()),
                () -> assertEquals(EventStatus.ACTIVE, eventsBeforeCancel.get(0).getStatus()),
                () -> assertEquals(CancelEventCommand.LogStatus.CANCEL_EVENT_EVENT_NOT_FOUND.toString(), cancelEventLog)
        );
    }

    /**
     * Attempt CancelEventCommand when the Event referenced is not Active
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * Register some consumers
     * Login as an entertainment provider
     * Cancel the event with eventNumber 1
     * Before and after the CancelEventCommand we get a list of active events belonging to the logged in entertainment
     * provider
     * Run the CancelEventCommand again with some message and eventNumber belonging to the Event which is not
     *         active and has been cancelled already e.g. eventNumber 1
     *
     * Assert : the number of events before and after attempted cancellation is the same
     *          return value of false after cancellation
     *          the event status is still Cancelled
     *          the log is correct
     */
    @Test
    void verifyEventIsActive() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        register3Consumers(controller);

        /*
        Gets active events which belong to the entertainment provider before attempting to cancel event
         */
        loginOlympicsProvider(controller);
        ListEventsCommand cmd1 = new ListEventsCommand(true, true);
        controller.runCommand(cmd1);
        List<Event> eventsBeforeCancel = cmd1.getResult();

        CancelEventCommand cancelEventCmd1 = new CancelEventCommand(eventsBeforeCancel.get(0).getEventNumber(), "Message");
        controller.runCommand(cancelEventCmd1);

        loginOlympicsProvider(controller);
        ListEventsCommand cmd4 = new ListEventsCommand(true, true);
        controller.runCommand(cmd4);
        List<Event> eventsBeforeCancel1 = cmd4.getResult();

        CancelEventCommand cancelEventCmd = new CancelEventCommand(eventsBeforeCancel.get(0).getEventNumber(), "Message");
        controller.runCommand(cancelEventCmd);

        String cancelEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        loginOlympicsProvider(controller);
        /*
        Gets active events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd2 = new ListEventsCommand(true, true);
        controller.runCommand(cmd2);
        List<Event> eventsAfterCancel = cmd2.getResult();

         /*
        Gets all events which match user preferences after attempting to cancel event
         */
        ListEventsCommand cmd3 = new ListEventsCommand(true, false);
        controller.runCommand(cmd3);
        List<Event> allEvents = cmd3.getResult();

        assertAll("Verify cancellation with size of listed events",
                () -> assertEquals(eventsBeforeCancel1.size() ,eventsAfterCancel.size()),
                () -> assertFalse(cancelEventCmd.getResult()),
                // Cancelled event remains cancelled
                () -> assertEquals(EventStatus.CANCELLED, eventsBeforeCancel.get(0).getStatus()),
                () -> assertEquals(CancelEventCommand.LogStatus.CANCEL_EVENT_NOT_ACTIVE.toString(), cancelEventLog)
        );
    }
    /**
     * Attempt CancelEventCommand when the logged in user is an Entertainment Provider but did not create the
     *      event in question.
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * Register some consumers
     * Login as an entertainment provider
     * Before and after the CancelEventCommand we get a list of active events belonging to the logged in entertainment
     * provider
     * Login as some other entertainment provider
     * Run the CancelEventCommand again with some message and eventNumber and use the list of events to get an EventNumber
     *      which belong to another Entertainment Provider
     *
     * Assert : the number of events before and after attempted cancellation is the same
     *          return value of false after cancellation
     *          the event status is still Active
     *          the log is correct
     */
    @Test
    void verifyLoggedInUserIsOrganiser() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        register3Consumers(controller);

        /*
        Gets active events which belong to the entertainment provider before attempting to cancel event
         */
        loginOlympicsProvider(controller);
        ListEventsCommand cmd1 = new ListEventsCommand(true, true);
        controller.runCommand(cmd1);
        List<Event> eventsBeforeCancel = cmd1.getResult();

        loginBuskerProvider(controller);
        CancelEventCommand cancelEventCmd = new CancelEventCommand(eventsBeforeCancel.get(0).getEventNumber(), "Message");
        controller.runCommand(cancelEventCmd);

        String cancelEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        loginOlympicsProvider(controller);
        /*
        Gets active events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd2 = new ListEventsCommand(true, true);
        controller.runCommand(cmd2);
        List<Event> eventsAfterCancel = cmd2.getResult();

         /*
        Gets all events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd3 = new ListEventsCommand(true, false);
        controller.runCommand(cmd3);
        List<Event> allEvents = cmd3.getResult();

        /*
        Verifies the number of events before and after attempted cancellation is the same
        Verifies return value of false after cancellation
        Verifies the event status is still active
        Verifies the log is correct
         */
        assertAll("Verify cancellation with size of listed events",
                () -> assertEquals(eventsBeforeCancel.size() ,eventsAfterCancel.size()),
                () -> assertFalse(cancelEventCmd.getResult()),
                () -> assertEquals(EventStatus.ACTIVE, eventsBeforeCancel.get(0).getStatus()),
                () -> assertEquals(CancelEventCommand.LogStatus.CANCEL_EVENT_USER_NOT_ORGANISER.toString(), cancelEventLog)
        );
    }
    /**
     * Attempt CancelEventCommand when an Event has Performances which have started
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * Register some consumers
     * Login as an entertainment provider
     * Before and after the CancelEventCommand we get a list of active events belonging to the logged in entertainment
     * provider
     * Add a performance to the Event with eventNumber 1 which has a start-time in the past
     * Run the CancelEventCommand again with some message and eventNumber 1
     *
     * Assert : the number of events before and after attempted cancellation is the same
     *          return value of false after cancellation
     *          the event status is still Cancelled
     *          the log is correct
     */
    @Test
    void verifyNoOngoingPerformances() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        register3Consumers(controller);

        /*
        Gets active events which belong to the entertainment provider before attempting to cancel event
         */
        loginOlympicsProvider(controller);
        ListEventsCommand cmd1 = new ListEventsCommand(true, true);
        controller.runCommand(cmd1);
        List<Event> eventsBeforeCancel = cmd1.getResult();

        AddEventPerformanceCommand addPerformanceCmd = new AddEventPerformanceCommand(eventsBeforeCancel.get(0).getEventNumber(),
                "Address",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                List.of("The same musician"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE);
        controller.runCommand(addPerformanceCmd);

        CancelEventCommand cancelEventCmd = new CancelEventCommand(eventsBeforeCancel.get(0).getEventNumber(), "Message");
        controller.runCommand(cancelEventCmd);

        String cancelEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        /*
        Gets active events which belong to the entertainment provider after attempting to cancel event
         */
        ListEventsCommand cmd2 = new ListEventsCommand(true, true);
        controller.runCommand(cmd2);
        List<Event> eventsAfterCancel = cmd2.getResult();

         /*
        Gets all events which match user preferences after attempting to cancel event
         */
        ListEventsCommand cmd3 = new ListEventsCommand(true, false);
        controller.runCommand(cmd3);
        List<Event> allEvents = cmd3.getResult();

        assertAll("Verify cancellation with size of listed events",
                () -> assertEquals(eventsBeforeCancel.size() ,eventsAfterCancel.size()),
                () -> assertFalse(cancelEventCmd.getResult()),
                () -> assertEquals(EventStatus.ACTIVE, eventsBeforeCancel.get(0).getStatus()),
                () -> assertEquals(CancelEventCommand.LogStatus.CANCEL_EVENT_PERFORMANCE_ALREADY_STARTED.toString(), cancelEventLog)
        );
    }
}