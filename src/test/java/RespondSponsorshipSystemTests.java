import command.*;
import controller.Controller;
import logging.Logger;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RespondSponsorshipSystemTests {
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
                true
        );
        controller.runCommand(eventCmd1);
        long eventNumber1 = eventCmd1.getResult();
        controller.runCommand(new AddEventPerformanceCommand(
                eventNumber1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now(),
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

    private static void loginCinemaProvider(Controller controller) {
        controller.runCommand(new LoginCommand("odeon@cineworld.com", "F!ghT th3 R@Pture"));
    }

    private static SponsorshipRequest findRequestFromRequestNumber(long requestNo, List<SponsorshipRequest> sponsorshipRequests){
        for (SponsorshipRequest sponsorshipRequest: sponsorshipRequests){
            if (sponsorshipRequest.getRequestNumber() == requestNo){
                return sponsorshipRequest;
            }
        }
        return null;
    }

    /**
     * Verifies RespondSponsorshipCommand when accepting a sponsorship request with expected inputs
     *
     * Register three 3 consumers with one entertainment provider with 3 events all of which requested sponsorship aside
     *      from one which was unticketed
     * Login as Government Representative
     * Before and after running the command we gather the list of sponsorshipRequests with only pending requests
     * The RespondSponsorshipCommand is called with requestNumber 1 belonging to eventNumber 1 and with percentToSponsor 10
     *
     * Assert: Command returns true
     *         Log is correct
     *         Size of list of pending sponsorship requests reduces by 1
     *         SponsorshipRequest status changes to ACCEPTED
     *         The details of the SponsorshipRequest are validated
     *         Check discountedEventTicketPrice is correct
     *         Check event is now listed as being sponsored
     *         Check payment has occured between Government and Entertainment Provider
     */
    @Test
    void acceptSponsorshipTest(){
        Controller controller = new Controller();
        register3Consumers(controller);
        createCinemaProviderWith3Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand priorSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(priorSponsorshipsCmd);
        List <SponsorshipRequest> priorSponsorships = priorSponsorshipsCmd.getResult();

        RespondSponsorshipCommand respondSponsorshipCmd = new RespondSponsorshipCommand(1,10);
        controller.runCommand(respondSponsorshipCmd);
        String respondSponsorshipLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        ListSponsorshipRequestsCommand postSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(postSponsorshipsCmd);
        List <SponsorshipRequest> postSponsorships = postSponsorshipsCmd.getResult();

        SponsorshipRequest sponsorshipRequest = findRequestFromRequestNumber(1,priorSponsorships);
        TicketedEvent event = sponsorshipRequest.getEvent();

        assertAll("Verify sponsorship response",
                () -> assertTrue(respondSponsorshipCmd.getResult()),
                () -> assertEquals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_APPROVE.toString(),respondSponsorshipLog),
                () -> assertEquals(priorSponsorships.size()-1,postSponsorships.size()),
                () -> assertEquals(SponsorshipStatus.ACCEPTED,sponsorshipRequest.getStatus()),
                () -> assertEquals(10,sponsorshipRequest.getSponsoredPricePercent()),
                () -> assertEquals(0.9 * event.getOriginalTicketPrice(), event.getDiscountedTicketPrice()),
                () -> assertTrue(event.isSponsored()),
                () -> assertTrue(Logger.getInstance().getLog().stream().anyMatch(
                        i -> i.getResult().equals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_PAYMENT_SUCCESS.toString())))
                );
    }
    /**
     * Verifies RespondSponsorshipCommand when rejecting a sponsorship request with expected inputs
     *
     * Register three 3 consumers with one entertainment provider with 3 events all of which requested sponsorship aside
     *      from one which was unticketed
     * Login as Government Representative
     * Before and after running the command we gather the list of sponsorshipRequests with only pending requests
     * The RespondSponsorshipCommand is called with requestNumber 1 belonging to eventNumber 1 and with percentToSponsor 0
     *      - indicating a rejection
     *
     * Assert: Command returns true
     *         Log is correct
     *         Size of list of pending sponsorship requests remains the same
     *         SponsorshipRequest status changes to REJECTED
     *         The details of the SponsorshipRequest are validated
     *         Check event is now listed as being not sponsored
     */
    @Test
    void rejectSponsorshipTest(){
        Controller controller = new Controller();
        register3Consumers(controller);
        createCinemaProviderWith3Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand priorSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(priorSponsorshipsCmd);
        List <SponsorshipRequest> priorSponsorships = priorSponsorshipsCmd.getResult();

        RespondSponsorshipCommand respondSponsorshipCmd = new RespondSponsorshipCommand(1,0);
        controller.runCommand(respondSponsorshipCmd);
        String repondSponsorshipLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        ListSponsorshipRequestsCommand postSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(postSponsorshipsCmd);
        List <SponsorshipRequest> postSponsorships = postSponsorshipsCmd.getResult();

        SponsorshipRequest sponsorshipRequest = findRequestFromRequestNumber(1,priorSponsorships);
        TicketedEvent event = sponsorshipRequest.getEvent();

        assertAll("Verify Sponsorship response",
                () -> assertTrue(respondSponsorshipCmd.getResult()),
                () -> assertEquals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_REJECT.toString(),repondSponsorshipLog),
                () -> assertEquals(priorSponsorships.size()-1,postSponsorships.size()),
                () -> assertEquals(SponsorshipStatus.REJECTED,sponsorshipRequest.getStatus()),
                () -> assertNull(sponsorshipRequest.getSponsoredPricePercent()),
                () -> assertFalse(event.isSponsored())
        );
    }

    /**
     * Attempts RespondSponsorshipCommand when no user is logged in
     *
     * Register three 3 consumers with one entertainment provider with 3 events all of which requested sponsorship aside
     *      from one which was unticketed
     * Before and after running the command we gather the list of sponsorshipRequests with only pending requests
     * Logout current user
     * The RespondSponsorshipCommand is called with requestNumber 1 belonging to eventNumber 1 and with percentToSponsor 0
     *      - indicating a rejection
     *
     * Assert: Command returns false
     *         Log is correct
     *         Size of list of pending sponsorship requests remains the same
     *         SponsorshipRequest status remains PENDING
     *         The details of the SponsorshipRequest are null
     *         Check event is listed as being not sponsored
     */
    @Test
    void verifyUserIsLoggedIn(){
        Controller controller = new Controller();
        register3Consumers(controller);
        createCinemaProviderWith3Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand priorSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(priorSponsorshipsCmd);
        List <SponsorshipRequest> priorSponsorships = priorSponsorshipsCmd.getResult();
        //int priorSponsorshipsSize = p

        controller.runCommand(new LogoutCommand());

        RespondSponsorshipCommand respondSponsorshipCmd = new RespondSponsorshipCommand(1,0);
        controller.runCommand(respondSponsorshipCmd);
        String repondSponsorshipLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand postSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(postSponsorshipsCmd);
        List <SponsorshipRequest> postSponsorships = postSponsorshipsCmd.getResult();

        SponsorshipRequest sponsorshipRequest = findRequestFromRequestNumber(1,priorSponsorships);
        TicketedEvent event = sponsorshipRequest.getEvent();

        assertAll("Verify returns False and log is correct",
                () -> assertFalse(respondSponsorshipCmd.getResult()),
                () -> assertEquals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_USER_NOT_LOGGED_IN.toString(),repondSponsorshipLog),
                () -> assertEquals(priorSponsorships.size(),postSponsorships.size()),
                () -> assertEquals(SponsorshipStatus.PENDING,sponsorshipRequest.getStatus()),
                () -> assertNull(sponsorshipRequest.getSponsoredPricePercent()),
                () -> assertFalse(event.isSponsored())
        );
    }
    /**
     * Attempts RespondSponsorshipCommand when the user logged in is not a Government Representative
     *
     * Register three 3 consumers with one entertainment provider with 3 events all of which requested sponsorship aside
     *      from one which was unticketed
     * Before and after running the command we gather the list of sponsorshipRequests with only pending requests
     * Login as a consumer
     * The RespondSponsorshipCommand is called with requestNumber 1 belonging to eventNumber 1 and with percentToSponsor 0
     *      - indicating a rejection
     *
     * Assert: Command returns false
     *         Log is correct
     *         Size of list of pending sponsorship requests remains the same
     *         SponsorshipRequest status remains PENDING
     *         The details of the SponsorshipRequest are null
     *         Check event is listed as being not sponsored
     */
    @Test
    void verifyUserIsGovernmentRepresentative(){
        Controller controller = new Controller();
        register3Consumers(controller);
        createCinemaProviderWith3Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand priorSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(priorSponsorshipsCmd);
        List <SponsorshipRequest> priorSponsorships = priorSponsorshipsCmd.getResult();
        //int priorSponsorshipsSize = p

        loginConsumer2(controller);

        RespondSponsorshipCommand respondSponsorshipCmd = new RespondSponsorshipCommand(1,0);
        controller.runCommand(respondSponsorshipCmd);
        String repondSponsorshipLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand postSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(postSponsorshipsCmd);
        List <SponsorshipRequest> postSponsorships = postSponsorshipsCmd.getResult();

        SponsorshipRequest sponsorshipRequest = findRequestFromRequestNumber(1,priorSponsorships);
        TicketedEvent event = sponsorshipRequest.getEvent();

        assertAll("Verify returns False and log is correct",
                () -> assertFalse(respondSponsorshipCmd.getResult()),
                () -> assertEquals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_USER_NOT_GOVERNMENT_REPRESENTATIVE.toString(),repondSponsorshipLog),
                () -> assertEquals(priorSponsorships.size(),postSponsorships.size()),
                () -> assertEquals(SponsorshipStatus.PENDING,sponsorshipRequest.getStatus()),
                () -> assertNull(sponsorshipRequest.getSponsoredPricePercent()),
                () -> assertFalse(event.isSponsored())
        );
    }

    /**
     * Attempts RespondSponsorshipCommand when the percentage specified is out of range
     *
     * Register three 3 consumers with one entertainment provider with 3 events all of which requested sponsorship aside
     *      from one which was unticketed
     * Login as a Government Representative
     * Before and after running the command we gather the list of sponsorshipRequests with only pending requests
     * The RespondSponsorshipCommand is called with requestNumber 1 belonging to eventNumber 1 and with percentToSponsor 101
     *      - percentToSponsor is too large
     *
     * Assert: Command returns false
     *         Log is correct
     *         Size of list of pending sponsorship requests remains the same
     *         SponsorshipRequest status remains PENDING
     *         The details of the SponsorshipRequest are null
     *         Check event is listed as being not sponsored
     */
    @Test
    void verifyPercentageInRange1(){
        Controller controller = new Controller();
        register3Consumers(controller);
        createCinemaProviderWith3Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand priorSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(priorSponsorshipsCmd);
        List <SponsorshipRequest> priorSponsorships = priorSponsorshipsCmd.getResult();
        //int priorSponsorshipsSize = p


        RespondSponsorshipCommand respondSponsorshipCmd = new RespondSponsorshipCommand(1,101);
        controller.runCommand(respondSponsorshipCmd);
        String repondSponsorshipLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();


        ListSponsorshipRequestsCommand postSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(postSponsorshipsCmd);
        List <SponsorshipRequest> postSponsorships = postSponsorshipsCmd.getResult();

        SponsorshipRequest sponsorshipRequest = findRequestFromRequestNumber(1,priorSponsorships);
        TicketedEvent event = sponsorshipRequest.getEvent();

        assertAll("Verify returns False and log is correct",
                () -> assertFalse(respondSponsorshipCmd.getResult()),
                () -> assertEquals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_INVALID_PERCENTAGE.toString(),repondSponsorshipLog),
                () -> assertEquals(priorSponsorships.size(),postSponsorships.size()),
                () -> assertEquals(SponsorshipStatus.PENDING,sponsorshipRequest.getStatus()),
                () -> assertNull(sponsorshipRequest.getSponsoredPricePercent()),
                () -> assertFalse(event.isSponsored())
        );
    }
    /**
     * Attempts RespondSponsorshipCommand when the percentage specified is out of range
     *
     * Register three 3 consumers with one entertainment provider with 3 events all of which requested sponsorship aside
     *      from one which was unticketed
     * Login as a Government Representative
     * Before and after running the command we gather the list of sponsorshipRequests with only pending requests
     * The RespondSponsorshipCommand is called with requestNumber 1 belonging to eventNumber 1 and with percentToSponsor -1
     *      - percentToSponsor is too low
     *
     * Assert: Command returns false
     *         Log is correct
     *         Size of list of pending sponsorship requests remains the same
     *         SponsorshipRequest status remains PENDING
     *         The details of the SponsorshipRequest are null
     *         Check event is listed as being not sponsored
     */
    @Test
    void verifyPercentageInRange2(){
        Controller controller = new Controller();
        register3Consumers(controller);
        createCinemaProviderWith3Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand priorSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(priorSponsorshipsCmd);
        List <SponsorshipRequest> priorSponsorships = priorSponsorshipsCmd.getResult();
        //int priorSponsorshipsSize = p


        RespondSponsorshipCommand respondSponsorshipCmd = new RespondSponsorshipCommand(1,-1);
        controller.runCommand(respondSponsorshipCmd);
        String repondSponsorshipLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();


        ListSponsorshipRequestsCommand postSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(postSponsorshipsCmd);
        List <SponsorshipRequest> postSponsorships = postSponsorshipsCmd.getResult();

        SponsorshipRequest sponsorshipRequest = findRequestFromRequestNumber(1,priorSponsorships);
        TicketedEvent event = sponsorshipRequest.getEvent();

        assertAll("Verify returns False and log is correct",
                () -> assertFalse(respondSponsorshipCmd.getResult()),
                () -> assertEquals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_INVALID_PERCENTAGE.toString(),repondSponsorshipLog),
                () -> assertEquals(priorSponsorships.size(),postSponsorships.size()),
                () -> assertEquals(SponsorshipStatus.PENDING,sponsorshipRequest.getStatus()),
                () -> assertNull(sponsorshipRequest.getSponsoredPricePercent()),
                () -> assertFalse(event.isSponsored())
        );
    }
    /**
     * Attempts RespondSponsorshipCommand when the requestNumber does not belong to a Request
     *
     * Register three 3 consumers with one entertainment provider with 3 events all of which requested sponsorship aside
     *      from one which was unticketed
     * Login as a Government Representative
     * Before and after running the command we gather the list of sponsorshipRequests with only pending requests
     * The RespondSponsorshipCommand is called with requestNumber 1000 belonging to eventNumber 1 and with percentToSponsor 10
     *      - requestNumber is a known invalid number
     *
     * Assert: Command returns false
     *         Log is correct
     *         Size of list of pending sponsorship requests remains the same
     *         SponsorshipRequest status remains PENDING
     *         The details of the SponsorshipRequest are null
     *         Check event is listed as being not sponsored
     */
    @Test
    void verifyRequestValid(){
        Controller controller = new Controller();
        register3Consumers(controller);
        createCinemaProviderWith3Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand priorSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(priorSponsorshipsCmd);
        List <SponsorshipRequest> priorSponsorships = priorSponsorshipsCmd.getResult();


        RespondSponsorshipCommand respondSponsorshipCmd = new RespondSponsorshipCommand(1000,10);
        controller.runCommand(respondSponsorshipCmd);
        String repondSponsorshipLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();


        ListSponsorshipRequestsCommand postSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(postSponsorshipsCmd);
        List <SponsorshipRequest> postSponsorships = postSponsorshipsCmd.getResult();

        SponsorshipRequest sponsorshipRequest = findRequestFromRequestNumber(1,priorSponsorships);
        TicketedEvent event = sponsorshipRequest.getEvent();

        assertAll("Verify returns False and log is correct",
                () -> assertFalse(respondSponsorshipCmd.getResult()),
                () -> assertEquals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_REQUEST_NOT_FOUND.toString(),repondSponsorshipLog),
                () -> assertEquals(priorSponsorships.size(),postSponsorships.size()),
                () -> assertEquals(SponsorshipStatus.PENDING,sponsorshipRequest.getStatus()),
                () -> assertNull(sponsorshipRequest.getSponsoredPricePercent()),
                () -> assertFalse(event.isSponsored())
        );
    }

    /**
     * Attempts RespondSponsorshipCommand when the percentage specified is out of range
     *
     * Register three 3 consumers with one entertainment provider with 3 events all of which requested sponsorship aside
     *      from one which was unticketed
     * Login as a Government Representative
     * Before and after running the command we gather the list of sponsorshipRequests with only pending requests
     * The RespondSponsorshipCommand is called with requestNumber 1 belonging to eventNumber 1 and with percentToSponsor -1
     *      - percentToSponsor is too low
     *
     * Assert: Command returns false
     *         Log is correct
     *         Size of list of pending sponsorship requests remains the same
     *         SponsorshipRequest status remains PENDING
     *         The details of the SponsorshipRequest are null
     *         Check event is listed as being not sponsored
     */
    @Test
    void verifyRequestIsPending(){
        Controller controller = new Controller();
        register3Consumers(controller);
        createCinemaProviderWith3Events(controller);

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        ListSponsorshipRequestsCommand priorSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(priorSponsorshipsCmd);
        List <SponsorshipRequest> priorSponsorships = priorSponsorshipsCmd.getResult();

        RespondSponsorshipCommand respondSponsorshipCmd1 = new RespondSponsorshipCommand(1,10);
        controller.runCommand(respondSponsorshipCmd1);
        //int priorSponsorshipsSize = p

        RespondSponsorshipCommand respondSponsorshipCmd = new RespondSponsorshipCommand(1,10);
        controller.runCommand(respondSponsorshipCmd);
        String repondSponsorshipLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size() - 1).getResult();


        ListSponsorshipRequestsCommand postSponsorshipsCmd = new ListSponsorshipRequestsCommand(true);
        controller.runCommand(postSponsorshipsCmd);
        List <SponsorshipRequest> postSponsorships = postSponsorshipsCmd.getResult();

        SponsorshipRequest sponsorshipRequest = findRequestFromRequestNumber(1,priorSponsorships);
        TicketedEvent event = sponsorshipRequest.getEvent();

        assertAll("Verify returns False and log is correct",
                () -> assertFalse(respondSponsorshipCmd.getResult()),
                () -> assertEquals(RespondSponsorshipCommand.LogStatus.RESPOND_SPONSORSHIP_REQUEST_NOT_PENDING.toString(),repondSponsorshipLog),
                () -> assertEquals(priorSponsorships.size()-1,postSponsorships.size()),
                () -> assertEquals(SponsorshipStatus.ACCEPTED,sponsorshipRequest.getStatus()),
                () -> assertEquals(10,sponsorshipRequest.getSponsoredPricePercent())
        );
    }

}
