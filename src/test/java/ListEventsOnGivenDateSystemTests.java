import command.*;
import controller.Controller;
import logging.Logger;
import model.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListEventsOnGivenDateSystemTests {
    @BeforeEach
    void printTestName(TestInfo testInfo){
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs(){
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    private static void register2Consumer(Controller controller){

        controller.runCommand(new RegisterConsumerCommand("Mr Pickles",
                "picker345@gmail.com",
                "0983453678",
                "picklesRCool34",
                "picker345@hotmail.com"));
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new RegisterConsumerCommand("Mr Carrot",
                "orangestick@gmail.com",
                "0184563678",
                "carrotsRCool897",
                "orangestick@hotmail.com"));
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
    }

    private static void createTicketedEvent(Controller controller){
        CreateTicketedEventCommand cmd = new CreateTicketedEventCommand(
                "The LEGO Movie",
                EventType.Movie,
                50,
                15.75,
                false
        );
        controller.runCommand(cmd);

        controller.runCommand(new AddEventPerformanceCommand(1,
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
    }

    private static long createNonTicketedEvent(Controller controller){
        CreateNonTicketedEventCommand cmd = new CreateNonTicketedEventCommand(
                "The LEGO Movie",
                EventType.Movie
        );
        controller.runCommand(cmd);
        controller.runCommand(new AddEventPerformanceCommand(2,
                "You know it",
                LocalDateTime.now().plusDays(10).plusHours(1),
                LocalDateTime.now().plusDays(10).plusHours(2),
                List.of("The usual"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));
        return cmd.getResult();
    }

    private static long createNonTicketedEvent2(Controller controller){
        CreateNonTicketedEventCommand cmd = new CreateNonTicketedEventCommand(
                "The LEGO Movie 2",
                EventType.Movie
        );
        controller.runCommand(cmd);
        controller.runCommand(new AddEventPerformanceCommand(3,
                "You know it",
                LocalDateTime.now().plusDays(10).minusHours(10),
                LocalDateTime.now().plusDays(10).minusHours(8),
                List.of("The usual"),
                true,
                true,
                true,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE
        ));
        return cmd.getResult();
    }

    /**
     * Verifies ListEventsOnGivenDateCommand works
     *
     * Creates ticketed event that is in 2030 and two non-ticketed events both within 1 day away
     * We use the searchdatetime with input equal to at the present moment plus 10 days.
     *
     * Assert: returned list is of size 2
     */
    @Test
    void listEvents(){
        Controller controller = new Controller();
        registerEntertainmentProvider(controller);
        createTicketedEvent(controller);
        createNonTicketedEvent(controller);
        createNonTicketedEvent2(controller);

        ListEventsCommand cmd = new ListEventsOnGivenDateCommand(false,false, LocalDateTime.now().plusDays(10));
        controller.runCommand(cmd);
        assertEquals(2,cmd.getResult().size());
    }

    /**
     * Verifies ListEventsOnGivenDateCommand works
     *
     * Creates ticketed event that is in 2030 and two non-ticketed events both within 1 day away
     * We use the searchdatetime with input equal to at the present moment plus 10 days.
     *
     * Assert: returned list is of size 2
     */
    @Test
    void listEventsActive(){
        Controller controller = new Controller();
        registerEntertainmentProvider(controller);
        createTicketedEvent(controller);
        long ev1 = createNonTicketedEvent(controller);
        createNonTicketedEvent2(controller);

        controller.runCommand(new CancelEventCommand(ev1,"dijas"));
        ListEventsCommand cmd = new ListEventsOnGivenDateCommand(false,true, LocalDateTime.now().plusDays(10));
        controller.runCommand(cmd);
        assertEquals(1,cmd.getResult().size());
    }

    /**
     * Verifies ListEventsOnGivenDateCommand works
     *
     * Creates ticketed event that is in 2030 and two non-ticketed events both within 1 day away
     * We use the searchdatetime with input equal to at the present moment plus 10 days.
     *
     * Assert: returned list is of size 2
     */
    @Test
    void listEntProviderEvents(){
        Controller controller = new Controller();
        registerEntertainmentProvider(controller);
        createTicketedEvent(controller);
        long ev1 = createNonTicketedEvent(controller);
        createNonTicketedEvent2(controller);

        ListEventsCommand cmd = new ListEventsOnGivenDateCommand(true,false, LocalDateTime.now().plusDays(10));
        controller.runCommand(cmd);
        assertEquals(2,cmd.getResult().size());
    }

    /**
     * Verifies ListEventsOnGivenDateCommand works when consumer is user and userEventsOnly is set to True
     *
     * Creates ticketed event that is in 2030 and two non-ticketed events both within 1 day away
     * We use the searchdatetime with input equal to at the present moment plus 10 days.
     *
     * Assert: returned list is of size 2
     */
    @Test
    void listConsumerEvents(){
        Controller controller = new Controller();
        registerEntertainmentProvider(controller);
        createTicketedEvent(controller);
        long ev1 = createNonTicketedEvent(controller);
        createNonTicketedEvent2(controller);

        controller.runCommand(new LoginCommand("orangestick@gmail.com","carrotsRCool897"));

        ListEventsCommand cmd = new ListEventsOnGivenDateCommand(true,false, LocalDateTime.now().plusDays(10));
        controller.runCommand(cmd);
        assertEquals(2,cmd.getResult().size());
    }

    /**
     * Verifies ListEventsOnGivenDateCommand works when consumer is the user and activeEvents is set to trye
     *
     * Creates ticketed event that is in 2030 and two non-ticketed events both within 1 day away
     * We use the searchdatetime with input equal to at the present moment plus 10 days as we
     * can only list events that have performances in the future.
     * We cancel an event to see if it is working
     *
     * Assert: returned list is of size 1
     */
    @Test
    void listConsumerActiveEvents(){
        Controller controller = new Controller();
        registerEntertainmentProvider(controller);
        createTicketedEvent(controller);
        long ev1 = createNonTicketedEvent(controller);
        createNonTicketedEvent2(controller);

        controller.runCommand(new CancelEventCommand(ev1,"dijas"));

        controller.runCommand(new LoginCommand("orangestick@gmail.com","carrotsRCool897"));

        ListEventsCommand cmd = new ListEventsOnGivenDateCommand(false,true, LocalDateTime.now().plusDays(10));
        controller.runCommand(cmd);
        assertEquals(1,cmd.getResult().size());
    }

    /**
     * Verifies ListEventsOnGivenDateCommand works when consumer is the user and when we have set
     * userEvents and activeEvents to true
     *
     * Creates ticketed event that is in 2030 and two non-ticketed events both within 1 day away
     * We use the searchdatetime with input equal to at the present moment.
     *
     * Assert: returned list is of size 1
     */
    @Test
    void listConsumerUserActiveEvents(){
        Controller controller = new Controller();
        registerEntertainmentProvider(controller);
        createTicketedEvent(controller);
        long ev1 = createNonTicketedEvent(controller);
        createNonTicketedEvent2(controller);

        controller.runCommand(new CancelEventCommand(ev1,"dijas"));

        controller.runCommand(new LoginCommand("orangestick@gmail.com","carrotsRCool897"));

        ListEventsCommand cmd = new ListEventsOnGivenDateCommand(true,true, LocalDateTime.now().plusDays(10));
        controller.runCommand(cmd);
        assertEquals(1,cmd.getResult().size());
    }
}
