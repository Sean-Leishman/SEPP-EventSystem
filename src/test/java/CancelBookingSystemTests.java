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

public class CancelBookingSystemTests {
    @BeforeEach
    void printTestName(TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs() {
        Logger.getInstance().clearLog();
        System.out.println("---");
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
                BookEventCommand bookCmd = new BookEventCommand(
                        event.getEventNumber(),
                        performances.iterator().next().getPerformanceNumber(),
                        1
                );
                controller.runCommand(bookCmd);
                return bookCmd.getResult();
            }
        }
        assert false;
        return 0;
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
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
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
                LocalDateTime.now().plusHours(1),
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

    private static void loginCinemaProvider(Controller controller) {
        controller.runCommand(new LoginCommand("odeon@cineworld.com", "F!ghT th3 R@Pture"));
    }

    private static Booking getBookingwithBookingNo(long bookingNo, List<Booking> bookings){
        for (Booking b: bookings){
            if (b.getBookingNumber() == bookingNo){
                return b;
            }
        }
        return null;
    }

    /**
     * Verifies that CancelBookingCommand() works correctly when given expected inputs
     *
     * EntertainmentProvider registered and creates 3 events
     * Three Consumers registered with the third one becoming the logged in user
     * The consumer books the Event with eventNumber 2
     * Before and after the CancelBookingCommand we gather the list/details
     *      of all consumer bookings for the logged-in consumer
     * Call the CancelBookingCommand
     *
     * Assert: booking status has changed to CancelledByConsumer
     *         outputted log entry for CancelBookingCommand() is correct
     *         number of active bookings for the consumer has been reduced by 1
     *         CancelBookingCommand returns true
     */
    @Test
    void cancelBooking(){
        Controller controller = new Controller();
        createCinemaProviderWith3Events(controller);
        register3Consumers(controller);

        loginConsumer3(controller);

        long bookingNo = consumerBookNthTicketedEvent(controller,2);

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer before attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd);
        BookingStatus status1 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        // Only count bookings which have not been cancelled
        long preBookingsSize = listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        CancelBookingCommand cmd = new CancelBookingCommand(bookingNo);
        controller.runCommand(cmd);

        String cancelBookingLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer after attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd2 = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd2);
        BookingStatus status2 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        long postBookingsSize =  listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        /*
        Verifies booking status has changed to CancelledByConsumer
        Verifies last log entry is correct
        Verifies number of active bookings for the consumer has been reduced by 1
        Verifies CancelBookingCommand returns true
         */
        assertAll("Verify cancel event",
                () -> assertEquals(BookingStatus.CancelledByConsumer,status2),
                () -> assertEquals(CancelBookingCommand.LogStatus.CANCEL_BOOKING_SUCCESS.toString(), cancelBookingLog),
                () -> assertEquals(preBookingsSize-1,postBookingsSize),
                () -> assertTrue(cmd.getResult()));
    }
    /**
     * Attempts CancelBookingCommand() when the logged in user is not a consumer
     *
     * EntertainmentProvider registered and creates 3 events
     * Three Consumers are registered
     * The consumer books the Event with eventNumber 2
     * Before and after the CancelBookingCommand we gather the list/details
     *      of all consumer bookings for the logged-in consumer
     * An entertainment provider than logs in and performs the CancelBookingCommand
     *
     * Assert: booking status remains active
     *         correct log has been outputted
     *         number of active bookings remains unchanged
     *         CancelBookingCommand returns false
     */
    @Test
    void verifyLoggedInUserIsConsumer(){
        Controller controller = new Controller();
        createCinemaProviderWith3Events(controller);
        register3Consumers(controller);

        loginConsumer3(controller);

        long bookingNo = consumerBookNthTicketedEvent(controller,2);

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer before attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd);
        BookingStatus status1 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        // Only count bookings which have not been cancelled
        long preBookingsSize = listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        loginCinemaProvider(controller);
        CancelBookingCommand cmd = new CancelBookingCommand(bookingNo);
        controller.runCommand(cmd);

        String cancelBookingLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        ListConsumerBookingsCommand listBookingsCmd2 = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd2);
        BookingStatus status2 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        long postBookingsSize =  listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        assertAll("Verify return of False with appropriate logs",
                () -> assertEquals(BookingStatus.Active,status2),
                () -> assertEquals(CancelBookingCommand.LogStatus.CANCEL_BOOKING_USER_NOT_CONSUMER.toString(), cancelBookingLog),
                () -> assertEquals(preBookingsSize,postBookingsSize),
                () -> assertFalse(cmd.getResult()));
    }
    /**
     * Attempts CancelBookingCommand() when the bookingNumber used is incorrect
     *
     * EntertainmentProvider registered and creates 3 events
     * Three Consumers are registered
     * The consumer books the Event with eventNumber 1
     * Before and after the CancelBookingCommand we gather the list/details
     *      of all consumer bookings for the logged-in consumer
     * The CancelBookingCommand is called with a known invalid bookingNumber e.g. 1000
     *
     * Assert: booking status remains active
     *         correct log has been outputted
     *         number of active bookings remains unchanged
     *         CancelBookingCommand returns false
     */
    @Test
    void verifyBookingNumberExists(){
        Controller controller = new Controller();
        createCinemaProviderWith3Events(controller);
        register3Consumers(controller);

        loginConsumer3(controller);

        long bookingNo = consumerBookNthTicketedEvent(controller,1);

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer before attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd);
        BookingStatus status1 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        // Only count bookings which have not been cancelled
        long preBookingsSize = listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        CancelBookingCommand cmd = new CancelBookingCommand(100000);
        controller.runCommand(cmd);

        String cancelBookingLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer after attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd2 = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd2);
        BookingStatus status2 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        long postBookingsSize =  listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        assertAll("Verify return of False with appropriate logs",
                () -> assertEquals(BookingStatus.Active,status2),
                () -> assertEquals(CancelBookingCommand.LogStatus.CANCEL_BOOKING_BOOKING_NOT_FOUND.toString(), cancelBookingLog),
                () -> assertEquals(preBookingsSize,postBookingsSize),
                () -> assertFalse(cmd.getResult()));
    }
    /**
     * Attempts CancelBookingCommand() when the logged in consumer is not the booking owner for the specified
     * booking number
     *
     * EntertainmentProvider registered and creates 3 events
     * Three Consumers are registered
     * The consumer books the Event with eventNumber 1
     * Before and after the CancelBookingCommand we gather the list/details
     *      of all consumer bookings for the logged-in consumer
     * Wrong consumer logs in
     * The CancelBookingCommand is called with an invalid consumer
     *
     * Assert: booking status remains active
     *         correct log has been outputted
     *         number of active bookings remains unchanged
     *         CancelBookingCommand returns false
     */
    @Test
    void verifyUserIsBookingOwner(){
        Controller controller = new Controller();
        createCinemaProviderWith3Events(controller);
        register3Consumers(controller);

        loginConsumer3(controller);

        long bookingNo = consumerBookNthTicketedEvent(controller,1);

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer before attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd);
        //BookingStatus status1 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        // Only count bookings which have not been cancelled
        long preBookingsSize = listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        loginConsumer2(controller);
        CancelBookingCommand cmd = new CancelBookingCommand(bookingNo);
        controller.runCommand(cmd);

        String cancelBookingLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer after attempting
        cancellation
         */
        loginConsumer3(controller);
        ListConsumerBookingsCommand listBookingsCmd2 = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd2);
        BookingStatus status2 = getBookingwithBookingNo(bookingNo,listBookingsCmd2.getResult()).getStatus();
        long postBookingsSize =  listBookingsCmd2.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        assertAll("Verify return of False with appropriate logs",
                () -> assertEquals(BookingStatus.Active,status2),
                () -> assertEquals(CancelBookingCommand.LogStatus.CANCEL_BOOKING_USER_IS_NOT_BOOKER.toString(), cancelBookingLog),
                () -> assertEquals(preBookingsSize,postBookingsSize),
                () -> assertFalse(cmd.getResult()));
    }
    /**
     * Attempts CancelBookingCommand() when the booking referenced is not active
     *
     * EntertainmentProvider registered and creates 3 events
     * Three Consumers are registered
     * The consumer books the Event with eventNumber 2
     * Before and after the CancelBookingCommand we gather the list/details
     *      of all consumer bookings for the logged-in consumer
     * The CancelBookingCommand is called with a bookingNumber belonging to an already cancelled booking
     *
     * Assert: booking status remains Cancelled
     *         correct log has been outputted
     *         number of active bookings remains unchanged
     *         CancelBookingCommand returns false
     */
    @Test
    void verifyBookingIsActive(){
        Controller controller = new Controller();
        createCinemaProviderWith3Events(controller);
        register3Consumers(controller);

        loginConsumer3(controller);

        long bookingNo = consumerBookNthTicketedEvent(controller,2);

        CancelBookingCommand cmd1 = new CancelBookingCommand(bookingNo);
        controller.runCommand(cmd1);

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer before attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd);
        BookingStatus status1 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        // Only count bookings which have not been cancelled
        long preBookingsSize = listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        CancelBookingCommand cmd = new CancelBookingCommand(bookingNo);
        controller.runCommand(cmd);

        String cancelBookingLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer after attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd2 = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd2);
        BookingStatus status2 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        long postBookingsSize =  listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        /*
        Verifies booking status is unchanged
        Verifies correct log has been outputted
        Verifies number of active bookings remains unchanged
        Verifies CancelBookingCommand returns false
         */
        assertAll("Verify return of False with appropriate logs",
                () -> assertEquals(BookingStatus.CancelledByConsumer,status2),
                () -> assertEquals(CancelBookingCommand.LogStatus.CANCEL_BOOKING_BOOKING_NOT_ACTIVE.toString(), cancelBookingLog),
                () -> assertEquals(preBookingsSize,postBookingsSize),
                () -> assertFalse(cmd.getResult()));
    }
    /**
     * Attempts CancelBookingCommand() when the performance start time is within 24 hours.
     *
     * EntertainmentProvider registered and creates 3 events
     * Three Consumers are registered
     * The consumer books the Event with eventNumber 1
     * Before and after the CancelBookingCommand we gather the list/details
     *      of all consumer bookings for the logged-in consumer
     * The CancelBookingCommand is called with a bookingNumber that references a Booking which
     *      is for a performance that starts within 24 hours.
     *
     * Assert: booking status remains active
     *         correct log has been outputted
     *         number of active bookings remains unchanged
     *         CancelBookingCommand returns false
     */
    @Test
    void verifyPerformanceIsFarEnoughAway(){
        Controller controller = new Controller();
        createCinemaProviderWith3Events(controller);
        register3Consumers(controller);

        loginConsumer3(controller);

        long bookingNo = consumerBookNthTicketedEvent(controller,1);

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer before attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd);
        BookingStatus status1 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        // Only count bookings which have not been cancelled
        long preBookingsSize = listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        CancelBookingCommand cmd = new CancelBookingCommand(bookingNo);
        controller.runCommand(cmd);

        String cancelBookingLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        /*
        Gets details of booking and list of active bookings belonging to the logged in consumer after attempting
        cancellation
         */
        ListConsumerBookingsCommand listBookingsCmd2 = new ListConsumerBookingsCommand();
        controller.runCommand(listBookingsCmd2);
        BookingStatus status2 = getBookingwithBookingNo(bookingNo,listBookingsCmd.getResult()).getStatus();
        long postBookingsSize =  listBookingsCmd.getResult().stream().filter(x -> x.getStatus() == BookingStatus.Active).count();

        assertAll("Verify return of False with appropriate logs",
                () -> assertEquals(BookingStatus.Active,status2),
                () -> assertEquals(CancelBookingCommand.LogStatus.CANCEL_BOOKING_NO_CANCELLATIONS_WITHIN_24H.toString(), cancelBookingLog),
                () -> assertEquals(preBookingsSize,postBookingsSize),
                () -> assertFalse(cmd.getResult()));
    }
}
