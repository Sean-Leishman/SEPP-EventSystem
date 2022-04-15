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

public class BookEventSystemTests {
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

    private static Consumer registerConsumer(Controller controller) {
        RegisterConsumerCommand registerConsumerCommand = new RegisterConsumerCommand(
                "John Biggson",
                "jbiggson1@hotmail.co.uk",
                "077893153480",
                "jbiggson2",
                "jbiggson1@hotmail.co.uk"
        );
        controller.runCommand(registerConsumerCommand);
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
                LocalDateTime.of(2020, 3, 21, 4, 20),
                LocalDateTime.now().minusMinutes(1),
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
                LocalDateTime.of(2020, 3, 21, 4, 20),
                LocalDateTime.now().minusMinutes(1),
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

    /**
     * Verifies BookEventCommand works correctly when the event is sponsored
     *
     * Register 3 entertainment providers with various events as detailed in the functions above
     * A consumer is registered
     * Before and after the BookEventCommand we get a list of consumer bookings
     * The government representative also logins in and accepts all sponsership requests
     *
     * Assert : bookingNo is the first one created
     *          number of total consumer bookings increases
     *          log is correct
     *          details of booking is correct such that booker is correct, booking ticket price is correct
     *              event belonging to booking is correct
     *
     */
    @Test
    void bookEventIsSponsored() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        // Ensures the event in question has been accepted for sponsorship thus discountPrice should be paid
        governmentAcceptAllSponsorships(controller);
        loginConsumer1(controller);

        /*
        Event has name "London Summer Olympics", it is sponsored with an original ticketprice of 25
         */
        int numTicketsRequested = 2;
        BookEventCommand bookEventCmd = new BookEventCommand(preEvents.get(0).getEventNumber(),
                preEvents.get(0).getPerformances().get(0).getPerformanceNumber(), numTicketsRequested);
        controller.runCommand(bookEventCmd);

        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();
        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        Booking booking = postBookings.stream().filter(i -> i.getBookingNumber() == bookEventCmd.getResult()).findFirst().get();
        TicketedEvent event = (TicketedEvent) preEvents.stream().filter(i -> i.getEventNumber() == preEvents.get(0).getEventNumber()).findFirst().get();

        assertAll("Verify booking occurs",
                () -> assertEquals(1, bookEventCmd.getResult()),
                () -> assertEquals(sizePreBookings + 1, sizePostBookings),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_SUCCESS.toString(), bookEventLog),
                () -> assertEquals(0, Logger.getInstance().getLog().stream().filter(
                        i -> i.getResult().equals(BookEventCommand.LogStatus.BOOK_EVENT_PAYMENT_FAILED)).count()),
                () -> assertAll("Verify details of booking",
                        () -> assertEquals(consumer, booking.getBooker()),
                        () -> assertEquals(event.getDiscountedTicketPrice() * numTicketsRequested, booking.getAmountPaid()),
                        () -> assertEquals(event, booking.getEventPerformance().getEvent()))
        );

    }

    /**
     * Attempts BookEventCommand when logged in user is an Entertainment Provider with the same event as used
     * in the BookEventIsSponsoredTest
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * We login as an Entertainment Provider before attempting BookEventCommand
     * Before and after the BookEventCommand we get a list of consumer bookings
     *
     * Assert: null is returned after attempting BookEventCommand
     *         correct log is outputted after BookEventCommand
     *         number of bookings remains unchanged for the consumer
     */
    @Test
    void verifyUserIsConsumer() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        // Wrong user logs in
        loginOlympicsProvider(controller);

        // Attempts book event
        BookEventCommand bookEventCmd = new BookEventCommand(preEvents.get(0).getEventNumber(), preEvents.get(0).getPerformances().get(0).getPerformanceNumber(), 2);
        controller.runCommand(bookEventCmd);

        // Gets output log for BookEventCommand
        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        loginConsumer1(controller);
        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        /*
         * Verifies null is returned after attempting BookEventCommand
         * Verifies correct log is outputted after BookEventCommand
         * Verifies number of bookings remains unchanged for the consumer
         */
        assertAll("Verify Null is returned and log is correct ",
                () -> assertNull(bookEventCmd.getResult()),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_USER_NOT_CONSUMER.toString(),
                        bookEventLog),
                () -> assertEquals(sizePreBookings,sizePostBookings)
        );
    }

    /**
     * Attempts BookEventCommand using an Event Number which does not belong to an event
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * We login as a consumer
     * Before and after the BookEventCommand we get a list of consumer bookings
     * Call BookEventCommand with a known invalid EventNumber e.g. 100
     *
     * Assert: null is returned after attempting BookEventCommand
     *         correct log is outputted after BookEventCommand
     *         number of bookings remains unchanged for the consumer
     */
    @Test
    void verifyEventNoIsValid() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        // Attempts BookEventCommand with invalid EventNumber 100
        BookEventCommand bookEventCmd = new BookEventCommand(100, preEvents.get(0).getPerformances().get(0).getPerformanceNumber(), 2);
        controller.runCommand(bookEventCmd);
        // Gets output log for BookEventCommand
        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        assertAll("Verify Null is returned and log is correct ",
                () -> assertNull(bookEventCmd.getResult()),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_EVENT_NOT_FOUND.toString(),
                        bookEventLog),
                () -> assertEquals(sizePreBookings,sizePostBookings)
        );
    }

    /**
     * Attempts BookEventCommand where the Event selected is NonTicketed
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * We login as a consumer
     * Before and after the BookEventCommand we get a list of consumer bookings
     * Call BookEventCommand with a known EventNumber belonging to a NonTicketedEvent e.g. 6
     *
     * Assert: null is returned after attempting BookEventCommand
     *         correct log is outputted after BookEventCommand
     *         number of bookings remains unchanged for the consumer
     */
    @Test
    void verifyEventIsTicketed() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        BookEventCommand bookEventCmd = new BookEventCommand(6, preEvents.get(0).getPerformances().get(0).getPerformanceNumber(), 2);
        controller.runCommand(bookEventCmd);
        // Gets output log for BookEventCommand
        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        assertAll("Verify Null is returned and log is correct ",
                () -> assertNull(bookEventCmd.getResult()),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_NOT_A_TICKETED_EVENT.toString(),
                        bookEventLog),
                () -> assertEquals(sizePreBookings,sizePostBookings)
        );
    }

    /**
     * Attempts BookEventCommand where the number of tickets requested is invalid
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * We login as a consumer
     * Before and after the BookEventCommand we get a list of consumer bookings
     * Call BookEventCommand with number of requested tickets being invalid e.g. 0
     *
     * Assert: null is returned after attempting BookEventCommand
     *         correct log is outputted after BookEventCommand
     *         number of bookings remains unchanged for the consumer
     */
    @Test
    void verifyRequestedTicketsIsNotLessThan1() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        BookEventCommand bookEventCmd = new BookEventCommand(1, preEvents.get(0).getPerformances().get(0).getPerformanceNumber(), 0);
        controller.runCommand(bookEventCmd);
        // Gets output log for BookEventCommand
        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        assertAll("Verify Null is returned and log is correct ",
                () -> assertNull(bookEventCmd.getResult()),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_INVALID_NUM_TICKETS.toString(),
                        bookEventLog),
                () -> assertEquals(sizePreBookings,sizePostBookings)
        );
    }
    /**
     * Attempts BookEventCommand where the Performance of the Event selected does not exist for that event
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * We login as a consumer
     * Before and after the BookEventCommand we get a list of consumer bookings
     * Call BookEventCommand with a known invalid PerformanceNumber for the specific Event e.g. 10 for EventNumber = 1
     *
     * Assert: null is returned after attempting BookEventCommand
     *         correct log is outputted after BookEventCommand
     *         number of bookings remains unchanged for the consumer
     */
    @Test
    void verifyPerformanceExists() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        BookEventCommand bookEventCmd = new BookEventCommand(1, 10, 2);
        controller.runCommand(bookEventCmd);

        // Gets output log for BookEventCommand
        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        assertAll("Verify Null is returned and log is correct ",
                () -> assertNull(bookEventCmd.getResult()),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_PERFORMANCE_NOT_FOUND.toString(),
                        bookEventLog),
                () -> assertEquals(sizePreBookings,sizePostBookings)
        );
    }
    /**
     * Attempts BookEventCommand where the Performance of the Event selected has already ended
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * We login as a consumer
     * Before and after the BookEventCommand we get a list of consumer bookings
     * Call BookEventCommand with a known PerformanceNumber that belongs to a Performance which has finished
     *  e.g. performanceNumber 6 for eventNumber 3
     *
     * Assert: null is returned after attempting BookEventCommand
     *         correct log is outputted after BookEventCommand
     *         number of bookings remains unchanged for the consumer
     */
    @Test
    void verifyPerformanceHasNotFinished() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        BookEventCommand bookEventCmd = new BookEventCommand(3, 6, 2);
        controller.runCommand(bookEventCmd);

        // Gets output log for BookEventCommand
        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        assertAll("Verify Null is returned and log is correct ",
                () -> assertNull(bookEventCmd.getResult()),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_ALREADY_OVER.toString(),
                        bookEventLog),
                () -> assertEquals(sizePreBookings,sizePostBookings)
        );
    }

    /**
     * Attempts BookEventCommand where the number of tickets requested is larger than the number of tickets available
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * We login as a consumer
     * Before and after the BookEventCommand we get a list of consumer bookings
     * Call BookEventCommand with a numberOfTicketsRequested that is greater than the specific numTickets for
     *      an event e.g. 500 tickets for eventNumber 4
     *
     * Assert: null is returned after attempting BookEventCommand
     *         correct log is outputted after BookEventCommand
     *         number of bookings remains unchanged for the consumer
     */
    @Test
    void verifyNumberOfTicketsIsAvailable() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        BookEventCommand bookEventCmd = new BookEventCommand(4, 7, 500);
        controller.runCommand(bookEventCmd);

        // Gets output log for BookEventCommand
        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        /*
         * Verifies null is returned after attempting BookEventCommand
         * Verifies correct log is outputted after BookEventCommand
         * Verifies number of bookings remains unchanged for the consumer
         */
        assertAll("Verify Null is returned and log is correct ",
                () -> assertNull(bookEventCmd.getResult()),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_NOT_ENOUGH_TICKETS_LEFT.toString(),
                        bookEventLog),
                () -> assertEquals(sizePreBookings,sizePostBookings)
        );
    }

    /**
     * Verifies BookEventCommand works correctly when the event is not sponsored
     *
     * Register 3 entertainment providers with various events as detailed in the functions above e.g. createOlympicsProviderWith2Events()
     * We login as a consumer
     * Before and after the BookEventCommand we get a list of consumer bookings
     * Call BookEventCommand
     *
     * Assert: bookingNo is the first one created
     *          number of total consumer bookings increases
     *          log is correct
     *          details of booking is correct such that booker is correct, booking ticket price is correct
     *              and event belonging to booking is correct
     */
    @Test
    void bookEventIsNotSponsored() {
        Controller controller = new Controller();

        createOlympicsProviderWith2Events(controller);
        createCinemaProviderWith3Events(controller);
        createBuskingProviderWith1Event(controller);
        Consumer consumer = registerConsumer(controller);

        loginConsumer1(controller);
        /*
          Gets all events available for booking that match the users consumer preferences
         */
        ListEventsCommand preListEventCmd = new ListEventsCommand(true, true);
        controller.runCommand(preListEventCmd);
        List<Event> preEvents = preListEventCmd.getResult();

        /*
          Gets all events booked by the consumer prior to new booking
         */
        ListConsumerBookingsCommand preListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(preListBookingsCmd);
        List<Booking> preBookings = preListBookingsCmd.getResult();
        int sizePreBookings = preBookings.size();

        /*
        Event has name "London Summer Olympics", it is sponsored with an original ticketprice of 25
         */
        int numTicketsRequested = 2;
        BookEventCommand bookEventCmd = new BookEventCommand(preEvents.get(0).getEventNumber(),
                preEvents.get(0).getPerformances().get(0).getPerformanceNumber(), numTicketsRequested);
        controller.runCommand(bookEventCmd);

        String bookEventLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();
        /*
          Gets all events booked by the consumer after new booking
         */
        ListConsumerBookingsCommand postListBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(postListBookingsCmd);
        List<Booking> postBookings = postListBookingsCmd.getResult();
        int sizePostBookings = postBookings.size();

        Booking booking = postBookings.stream().filter(i -> i.getBookingNumber() == bookEventCmd.getResult()).findFirst().get();
        TicketedEvent event = (TicketedEvent) preEvents.stream().filter(i -> i.getEventNumber() == preEvents.get(0).getEventNumber()).findFirst().get();

        /*
        Verify bookingNo is the first one created
        Verify number of total consumer bookings increases
        Verify log is correct
        Verify details of booking is correct such that booker is correct, booking ticket price is correct
        and event belonging to booking is correct
         */
        assertAll("Verify booking occurs",
                () -> assertEquals(1, bookEventCmd.getResult()),
                () -> assertEquals(sizePreBookings + 1, sizePostBookings),
                () -> assertEquals(BookEventCommand.LogStatus.BOOK_EVENT_SUCCESS.toString(), bookEventLog),
                () -> assertEquals(0, Logger.getInstance().getLog().stream().filter(
                        i -> i.getResult().equals(BookEventCommand.LogStatus.BOOK_EVENT_PAYMENT_FAILED)).count()),
                () -> assertAll("Verify details of booking",
                        () -> assertEquals(consumer, booking.getBooker()),
                        () -> assertEquals(event.getOriginalTicketPrice() * numTicketsRequested, booking.getAmountPaid()),
                        () -> assertEquals(event, booking.getEventPerformance().getEvent()))
        );

    }
}