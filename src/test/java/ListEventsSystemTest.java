
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ListEventsSystemTest {
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

    private static void loginConsumer1(Controller controller) {
        controller.runCommand(new LoginCommand("jbiggson1@hotmail.co.uk", "jbiggson2"));
    }

    private static void loginConsumer2(Controller controller) {
        controller.runCommand(new LoginCommand("jane@inf.ed.ac.uk", "giantsRverycool"));
    }

    private static void loginConsumer3(Controller controller) {
        controller.runCommand(new LoginCommand("i-will-kick-your@gmail.com", "it is wednesday my dudes"));
    }

    private static void register3Consumers(Controller controller) {
        controller.runCommand(new RegisterConsumerCommand(
                "John Biggson",
                "jbiggson1@hotmail.co.uk",
                "077893153480",
                "jbiggson2",
                "jbiggson1@hotmail.co.uk"
        ));
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

        controller.runCommand(new LogoutCommand());
    }

    private static void providerCancelFirstEvent(Controller controller) {
        ListEventsCommand cmd = new ListEventsCommand(true, true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();
        controller.runCommand(new CancelEventCommand(events.get(0).getEventNumber(), "Trololol"));
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

        controller.runCommand(new LogoutCommand());
    }

    /**
     * Verify ListEventsCommand when a consumer calls the command with a preference of having air filtration with only
     * activeEvents being listed too
     *
     * Various events are created and a consumer is created to run the command
     * The Consumer's profile is updated to test the command
     * There is limited events compared to the perceived total as 2 of the events are in the "past"
     *
     * Assert: length of the list of events is 4
     *         log is correct
     *         that all events in list have airFiltration
     */
    @Test
    void testAirFiltration() {
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        controller.runCommand(new UpdateConsumerProfileCommand("giantsRverycool",
                "Jane Giantsdottir",
                "jane2@inf.ed.ac.uk",
                "04462187232",
                "giantsRverycool",
                "jane@aol.com",
                new ConsumerPreferences(false,true,false,Integer.MAX_VALUE,Integer.MAX_VALUE)));

        ListEventsCommand cmd = new ListEventsCommand(true,true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();
        System.out.println(Logger.getInstance().getLog());

        assertAll("Verify Log is Correct and list is returned",
                () -> assertEquals(4, events.size()),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(events.stream().allMatch(i -> i.getPerformances().stream().anyMatch(EventPerformance::hasAirFiltration)))
                );
    }
    /**
     * Verify ListEventsCommand when a consumer calls the command with a preference of having socialDistancing with only
     * activeEvents being listed too
     *
     * Various events are created and a consumer is created to run the command
     * The Consumer's profile is updated to test the command
     * There is limited events compared to the perceived total as 2 of the events are in the "past"
     *
     * Assert: length of the list of events is 2
     *         log is correct
     *         that all events in list have socialDistancing
     */
    @Test
    void testSocialDistancing() {
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        controller.runCommand(new UpdateConsumerProfileCommand("giantsRverycool",
                "Jane Giantsdottir",
                "jane2@inf.ed.ac.uk",
                "04462187232",
                "giantsRverycool",
                "jane@aol.com",
                new ConsumerPreferences(true,false,false,Integer.MAX_VALUE,Integer.MAX_VALUE)));

        ListEventsCommand cmd = new ListEventsCommand(true,true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();
        System.out.println(Logger.getInstance().getLog());

        assertAll("Verify Log is Correct and list is returned",
                () -> assertEquals(2, events.size()),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(events.stream().allMatch(i -> i.getPerformances().stream().anyMatch(EventPerformance::hasSocialDistancing)))
        );
    }
    /**
     * Verify ListEventsCommand when a consumer calls the command with a preference of having outdoorsOnly with only
     * activeEvents being listed too
     *
     * Various events are created and a consumer is created to run the command
     * The Consumer's profile is updated to test the command
     * There is limited events compared to the perceived total as 2 of the events are in the "past"
     *
     * Assert: length of the list of events is 3
     *         log is correct
     *         that all events in list have outdoorsOnly
     */
    @Test
    void testOutdoorsOnly() {
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        controller.runCommand(new UpdateConsumerProfileCommand("giantsRverycool",
                "Jane Giantsdottir",
                "jane2@inf.ed.ac.uk",
                "04462187232",
                "giantsRverycool",
                "jane@aol.com",
                new ConsumerPreferences(false,false,true, Integer.MAX_VALUE,Integer.MAX_VALUE)));

        ListEventsCommand cmd = new ListEventsCommand(true,true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Log is Correct and list is returned",
                () -> assertEquals(3, events.size()),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(events.stream().allMatch(i -> i.getPerformances().stream().anyMatch(EventPerformance::isOutdoors)))
        );
    }
    /**
     * Verify ListEventsCommand when a consumer calls the command with a preference of having a minimized maxCapacity with only
     * activeEvents being listed too
     *
     * Various events are created and a consumer is created to run the command
     * The Consumer's profile is updated to test the command such that we have a preferred maxCapacity of 100
     * There is limited events compared to the perceived total as 2 of the events are in the "past"
     *
     * Assert: length of the list of events is 1
     *         log is correct
     *         that all events in list have maxCapacity < 100
     */
    @Test
    void testLowCapacity() {
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        controller.runCommand(new UpdateConsumerProfileCommand("giantsRverycool",
                "Jane Giantsdottir",
                "jane2@inf.ed.ac.uk",
                "04462187232",
                "giantsRverycool",
                "jane@aol.com",
                new ConsumerPreferences(false,false,false, 100,Integer.MAX_VALUE)));

        ListEventsCommand cmd = new ListEventsCommand(true,true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Log is Correct and list is returned",
                () -> assertEquals(1, events.size()),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(events.stream().allMatch(i -> i.getPerformances().stream().anyMatch(j -> j.getCapacityLimit() <= 100)))
        );
    }
    /**
     * Verify ListEventsCommand when a consumer calls the command with a preference of having a minimized maxVenueSize with only
     * activeEvents being listed too
     *
     * Various events are created and a consumer is created to run the command
     * The Consumer's profile is updated to test the command such that we have a preferred maxVenueSize of 100
     * There is limited events compared to the perceived total as 2 of the events are in the "past"
     *
     * Assert: length of the list of events is 1
     *         log is correct
     *         that all events in list have maxVenueSize < 100
     */
    @Test
    void testLowVenueSize() {
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        controller.runCommand(new UpdateConsumerProfileCommand("giantsRverycool",
                "Jane Giantsdottir",
                "jane2@inf.ed.ac.uk",
                "04462187232",
                "giantsRverycool",
                "jane@aol.com",
                new ConsumerPreferences(false,false,false, Integer.MAX_VALUE,100)));

        ListEventsCommand cmd = new ListEventsCommand(true,true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Log is Correct and list is returned",
                () -> assertEquals(1, events.size()),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(events.stream().allMatch(i -> i.getPerformances().stream().anyMatch(j -> j.getVenueSize() <= 100)))
        );
    }
    /**
     * Verify ListEventsCommand when a consumer calls the command where some events have been cancelled and we want
     * only active events
     *
     * Various events are created and a consumer is created to run the command
     * One event is cancelled.
     * There is limited events compared to the perceived total as 2 of the events are in the "past"
     *
     *
     * Assert: length of the list of events is 3
     *         log is correct
     *         that all events in list are ACTIVE
     */
    @Test
    void testInactiveEvents() {
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginOlympicsProvider(controller);

        ListEventsCommand cmd1 = new ListEventsCommand(false,true);
        controller.runCommand(cmd1);
        List<Event> priorEvents = cmd1.getResult();

        providerCancelFirstEvent(controller);

        loginConsumer2(controller);

        ListEventsCommand cmd = new ListEventsCommand(false,true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Log is Correct and list is returned",
                () -> assertEquals(3, events.size()),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(events.stream().allMatch(i -> i.getStatus() == EventStatus.ACTIVE)),
                () -> assertEquals(priorEvents.size()-1,events.size())
        );
    }

    /**
     * Verify ListEventsCommand when no user is logged in and the command is called
     *
     * Various events are created
     *
     * Assert: the list of events is null
     *         log is correct
     */
    @Test
    void verifyLogin(){
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        ListEventsCommand cmd = new ListEventsCommand(false,true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Log is Correct and list is returned",
                () -> assertNull(events),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_NOT_LOGGED_IN.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult())
        );
    }
    /**
     * Verify ListEventsCommand when an entertainment provider calls the command where some events have been cancelled and we want
     * only active events
     *
     * Various events are created with the specified entertainment provider creating 2 events
     * One event is cancelled.
     * Entertainment Provider runs the command
     *
     * Assert: length of the list of events is 2
     *         log is correct
     *         the organiser email is correct for the event in the list
     */
    @Test
    void entertainmentProviderListEvents() {
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginOlympicsProvider(controller);

        ListEventsCommand cmd1 = new ListEventsCommand(true,false);
        controller.runCommand(cmd1);
        List<Event> events = cmd1.getResult();

        assertAll("Verify Log is Correct and list is returned",
                () -> assertEquals(2, events.size()),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(events.stream().allMatch(i -> i.getOrganiser().getEmail().equals("anonymous@gmail.com")))
        );
    }
    /**
     * Verify ListEventsCommand when an entertainment provider calls the command where some events have been cancelled and we want
     * only active events
     *
     * Various events are created
     * One event is cancelled.
     * Initially there are 2 events created for the entertainent provider running the command
     *
     *
     * Assert: length of the list of events is 1
     *         log is correct
     *         that all events in list are ACTIVE
     *         the organiser email is correct for the event in the list
     *         verify cancellation occurred
     */
    @Test
    void entertainmentProviderListActiveEvents() {
        Controller controller = new Controller();

        register3Consumers(controller);

        /*loginConsumer2(controller);*/

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginOlympicsProvider(controller);

        ListEventsCommand cmd1 = new ListEventsCommand(true,true);
        controller.runCommand(cmd1);
        List<Event> priorEvents = cmd1.getResult();

        providerCancelFirstEvent(controller);

        ListEventsCommand cmd = new ListEventsCommand(true,true);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Log is Correct and list is returned",
                () -> assertEquals(1, events.size()),
                () -> assertEquals(ListEventsCommand.LogStatus.LIST_USER_EVENTS_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(events.stream().allMatch(i -> i.getOrganiser().getEmail().equals("anonymous@gmail.com"))),
                () -> assertTrue(events.stream().allMatch(i -> i.getStatus() == EventStatus.ACTIVE)),
                () -> assertEquals(priorEvents.size()-1,events.size())
        );
    }
}