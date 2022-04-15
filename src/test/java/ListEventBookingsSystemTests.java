
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

public class ListEventBookingsSystemTests {
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

    private static long consumerBookNthTicketedEvent(Controller controller, int n) {
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
                return event.getEventNumber();
            }
        }
        assert false;
        return 0;
    }

    /**
     * Verifies output of ListEventBookingsCommand
     *
     * Registers 3 consumers
     * Registers 3 entertainment providers with 6 events total
     *
     * Second consumer logs in and books an event
     * We run the ListEventBookings command for this same event. At this point the entertainmentProvider is
     *      logged in
     *
     * Assert: bookings is of size 1
     *         bookings contains the correct Consumer
     *         log is correct
     *
     */
    @Test
    void testListBookingEvents() {
        Controller controller = new Controller();

        register3Consumers(controller);

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        long eventNo = consumerBookNthTicketedEvent(controller,1);
        controller.runCommand(new LogoutCommand());
        loginOlympicsProvider(controller);

        ListEventBookingsCommand cmd = new ListEventBookingsCommand(eventNo);
        controller.runCommand(cmd);
        List<Booking> bookings = cmd.getResult();

        assertAll("Verify bookings return values and log",
                () -> assertEquals(1, bookings.size()),
                () -> assertTrue(bookings.stream().anyMatch(booking -> booking.getBooker().getName().equals("Jane Giantsdottir"))),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        log -> log.getResult().equals(ListEventBookingsCommand.LogStatus.LIST_EVENT_BOOKINGS_SUCCESS.toString())
                ))
                );
    }

    /**
     * Verifies output of ListEventBookingsCommand when no user is logged in
     *
     * Registers 3 consumers
     * Registers 3 entertainment providers with 6 events total
     *
     * Second consumer logs in and books an event
     * We run the ListEventBookings command for this same event. At this point no user is logged in
     *
     * Assert: bookings is null
     *         log is correct
     *
     */
    @Test
    void verifyUserLoggedIn() {
        Controller controller = new Controller();

        register3Consumers(controller);

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        long eventNo = consumerBookNthTicketedEvent(controller,1);
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new LogoutCommand());

        ListEventBookingsCommand cmd = new ListEventBookingsCommand(eventNo);
        controller.runCommand(cmd);
        List<Booking> bookings = cmd.getResult();

        assertAll("Verify bookings return values and log",
                () -> assertNull(bookings),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        log -> log.getResult().equals(ListEventBookingsCommand.LogStatus.LIST_EVENT_BOOKINGS_USER_NOT_LOGGED_IN.toString())
                ))
        );
    }

    /**
     * Verifies output of ListEventBookingsCommand when eventNumber does not correspond to an Event
     *
     * Registers 3 consumers
     * Registers 3 entertainment providers with 6 events total
     *
     * Second consumer logs in and books an event
     * We run the ListEventBookings command for this same event. At this the entertainment provider is logged in but
     *      the eventNumber is invalid
     *
     * Assert: bookings is null
     *         log is correct
     *
     */
    @Test
    void verifyEventExists() {
        Controller controller = new Controller();

        register3Consumers(controller);

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        long eventNo = consumerBookNthTicketedEvent(controller,1);
        controller.runCommand(new LogoutCommand());

        loginOlympicsProvider(controller);
        ListEventBookingsCommand cmd = new ListEventBookingsCommand(10000);
        controller.runCommand(cmd);
        List<Booking> bookings = cmd.getResult();

        assertAll("Verify bookings return values and log",
                () -> assertNull(bookings),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        log -> log.getResult().equals(ListEventBookingsCommand.LogStatus.LIST_EVENT_BOOKINGS_EVENT_NOT_FOUND.toString())
                ))
        );
    }

    /**
     * Verifies output of ListEventBookingsCommand when the event is not ticketed
     *
     * Registers 3 consumers
     * Registers 3 entertainment providers with 6 events total
     *
     * Second consumer logs in and books an event
     * We run the ListEventBookings command for this same event. At this the entertainment provider is logged in but
     *      the event is not ticketed
     *
     * Assert: bookings is null
     *         log is correct
     *
     */
    @Test
    void verifyEventIsTicketed() {
        Controller controller = new Controller();

        register3Consumers(controller);

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        long eventNo = consumerBookNthTicketedEvent(controller,1);

        ListEventBookingsCommand cmd = new ListEventBookingsCommand(5);
        controller.runCommand(cmd);
        List<Booking> bookings = cmd.getResult();

        assertAll("Verify bookings return values and log",
                () -> assertNull(bookings),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        log -> log.getResult().equals(ListEventBookingsCommand.LogStatus.LIST_EVENT_BOOKINGS_EVENT_NOT_TICKETED.toString())
                ))
        );
    }

    /**
     * Verifies output of ListEventBookingsCommand when the user is not a government representative nor an entertainment
     * provider
     *
     * Registers 3 consumers
     * Registers 3 entertainment providers with 6 events total
     *
     * Second consumer logs in and books an event
     * We run the ListEventBookings command for this same event. At this point the consumer is logged in
     *
     * Assert: bookings is null
     *         log is correct
     *
     */
    @Test
    void verifyUserIsGovRepOrEntProvider() {
        Controller controller = new Controller();

        register3Consumers(controller);

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        long eventNo = consumerBookNthTicketedEvent(controller,1);

        ListEventBookingsCommand cmd = new ListEventBookingsCommand(eventNo);
        controller.runCommand(cmd);
        List<Booking> bookings = cmd.getResult();

        assertAll("Verify bookings return values and log",
                () -> assertNull(bookings),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        log -> log.getResult().equals(ListEventBookingsCommand.LogStatus.LIST_EVENT_BOOKINGS_USER_NOT_ORGANISER_NOR_GOV.toString())
                ))
        );
    }
    /**
     * Verifies output of ListEventBookingsCommand when the user is not the organiser of the event
     * provider
     *
     * Registers 3 consumers
     * Registers 3 entertainment providers with 6 events total
     *
     * Second consumer logs in and books an event
     * We run the ListEventBookings command for this same event. At this the entertainment provider is logged in but
     *      the eventNumber is invalid
     *
     * Assert: bookings is null
     *         log is correct
     *
     */
    @Test
    void verifyUserIsCorrectEntProvider() {
        Controller controller = new Controller();

        register3Consumers(controller);

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);

        loginConsumer2(controller);
        long eventNo = consumerBookNthTicketedEvent(controller,1);

        controller.runCommand(new LoginCommand("busk@every.day",
                "When they say 'you can't do this': Ding Dong! You are wrong!"));
        ListEventBookingsCommand cmd = new ListEventBookingsCommand(eventNo);
        controller.runCommand(cmd);
        List<Booking> bookings = cmd.getResult();

        assertAll("Verify bookings return values and log",
                () -> assertNull(bookings),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        log -> log.getResult().equals(ListEventBookingsCommand.LogStatus.LIST_EVENT_BOOKINGS_USER_NOT_ORGANISER_NOR_GOV.toString())
                ))
        );
    }
}