import command.*;
import controller.Controller;
import logging.Logger;
import model.Event;
import model.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListSponsorshipRequestsSystemTests {
    @BeforeEach
    void printTestName(TestInfo testInfo){
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs(){
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    private static void registerEntertainmentProvider(Controller controller){
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
                true
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

    /**
     * Verifies ListSponsorshipRequestsCommand works correctly
     *
     * 5 events created with 4 requesting sponsorship
     *
     * Assert: length of resulting list is 4
     *
     */
    @Test
    void listSponsorshipRequests(){
        Controller controller = new Controller();

        //registerEntertainmentProvider(controller);

        createCinemaProviderWith3Events(controller);
        createOlympicsProviderWith2Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand sponsorshipRequests = new ListSponsorshipRequestsCommand(false);
        controller.runCommand(sponsorshipRequests);

        System.out.println(Logger.getInstance().getLog());
        assertEquals(4,sponsorshipRequests.getResult().size());
    }

    /**
     * Verifies ListSponsorshipRequestsCommand works correctly with pendingResults set to true
     *
     * 5 events created with 4 requesting sponsorship
     * One event is responded to and is accepted.
     *
     * Assert: length of resulting list is 3
     *
     */
    @Test
    void listPendingSponsorshipRequests(){
        Controller controller = new Controller();

        //registerEntertainmentProvider(controller);

        createCinemaProviderWith3Events(controller);
        createOlympicsProviderWith2Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        controller.runCommand(new RespondSponsorshipCommand(1,10));

        ListSponsorshipRequestsCommand sponsorshipRequests = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(sponsorshipRequests);

        System.out.println(Logger.getInstance().getLog());
        assertEquals(3,sponsorshipRequests.getResult().size());
    }

    /**
     * Attempts ListSponsorshipRequestsCommand when user is not logged in
     *
     * Assert: resulting list is null
     *          log is correct
     */
    @Test
    void verifyUserIsLoggedIn(){
        Controller controller = new Controller();

        createCinemaProviderWith3Events(controller);
        createOlympicsProviderWith2Events(controller);

        ListSponsorshipRequestsCommand sponsorshipRequests = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(sponsorshipRequests);

        System.out.println(Logger.getInstance().getLog());
        assertTrue(
                Logger.getInstance().getLog().stream().anyMatch(log -> log.getResult().equals(
                        ListSponsorshipRequestsCommand.LogStatus.LIST_SPONSORSHIP_REQUESTS_NOT_LOGGED_IN.toString())));
        assertNull(sponsorshipRequests.getResult());
    }

    /**
     * Attempts ListSponsorshipRequestsCommand when user is not a governmnet representative
     *
     * Assert: resulting list is null
     *          log is correct
     */
    @Test
    void verifyUserIsGovernmentRepresentative(){
        Controller controller = new Controller();

        createCinemaProviderWith3Events(controller);
        createOlympicsProviderWith2Events(controller);

        controller.runCommand(new LoginCommand("anonymous@gmail.com", "anonymous"));
        ListSponsorshipRequestsCommand sponsorshipRequests = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(sponsorshipRequests);

        System.out.println(Logger.getInstance().getLog());
        assertTrue(
                Logger.getInstance().getLog().stream().anyMatch(log -> log.getResult().equals(
                        ListSponsorshipRequestsCommand.LogStatus.LIST_SPONSORSHIP_REQUESTS_NOT_GOVERNMENT_REPRESENTATIVE.toString())));
        assertNull(sponsorshipRequests.getResult());
    }
}
