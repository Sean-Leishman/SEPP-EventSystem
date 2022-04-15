import command.*;
import controller.Controller;
import logging.Logger;
import model.Event;
import model.EventPerformance;
import model.EventType;
import model.NonTicketedEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddEventPerformanceSystemTests {
    private static Long registerEntertainmentProviderWithOneEvent(Controller controller){
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
        return eventCmd1.getResult();
    }
    private static Event findEventbyEventNo(List<Event> events,Long eventNo){
        for (Event e:events){
            if (e.getEventNumber() == eventNo){
                return e;
            }
        }
        return null;
    }

    /**
     * Test verifies AddEventPerformanceCommand works correctly when given valid inputs
     * The test uses the AddEventPerformanceCommand 4 times for various both ticketed and
     * non-ticketed events:
     * 2 - for eventNumber1
     * 1 - for eventNumber2
     * 1 - for eventNumber3
     * Then we verify the number of performances for each event is as above
     * Then we check some performance to see if it has been created correctly
     * The number of ADD_PERFORMANCE_SUCCESS logs are also checked to match the number of performances created
     */
    @Test
    void addEventPerformance(){
        Controller controller = new Controller();
        long eventNumber1 = registerEntertainmentProviderWithOneEvent(controller);
        AddEventPerformanceCommand performanceCmd1 = new AddEventPerformanceCommand(
                eventNumber1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                49,
                50
        );
        controller.runCommand(performanceCmd1);
        AddEventPerformanceCommand performanceCmd2 = new AddEventPerformanceCommand(
                eventNumber1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                1,
                1
        );
        controller.runCommand(performanceCmd2);
        CreateTicketedEventCommand eventCmd2 = new CreateTicketedEventCommand(
                "Frozen Ballet",
                EventType.Dance,
                50,
                35,
                true
        );
        controller.runCommand(eventCmd2);
        long eventNumber2 = eventCmd2.getResult();
        AddEventPerformanceCommand performanceCmd3 = new AddEventPerformanceCommand(
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
        );
        controller.runCommand(performanceCmd3);
        CreateNonTicketedEventCommand eventCmd3 = new CreateNonTicketedEventCommand(
                "The Shining at the Meadows (Free Screening) (Live Action)",
                EventType.Sports
        );
        controller.runCommand(eventCmd3);
        long eventNumber3 = eventCmd3.getResult();
        AddEventPerformanceCommand performanceCmd4 = new AddEventPerformanceCommand(
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
        );
        controller.runCommand(performanceCmd4);

        ListEventsCommand cmd = new ListEventsCommand(false,false);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        EventPerformance examplePerformance = findEventbyEventNo(events,eventNumber1).getPerformanceByNumber(performanceCmd1.getResult().getPerformanceNumber());
        assertAll("Verify performances created and stored in each events performers attribute",
                () -> assertEquals(2,findEventbyEventNo(events,eventNumber1).getPerformances().size()),
                () -> assertEquals(1,findEventbyEventNo(events,eventNumber2).getPerformances().size()),
                () -> assertEquals(1,findEventbyEventNo(events,eventNumber3).getPerformances().size()),
                // Ensures performance was added and logged 4 times in total
                () -> assertEquals(4, Logger.getInstance().getLog().stream().filter(
                        i-> i.getResult().equals(AddEventPerformanceCommand.LogStatus.ADD_PERFORMANCE_SUCCESS.toString())).count()),
                () -> assertAll("Verify some performance information",
                        () -> assertEquals(eventNumber1, examplePerformance.getEvent().getEventNumber()),
                        () -> assertEquals(49,examplePerformance.getCapacityLimit()),
                        () -> assertEquals(50,examplePerformance.getVenueSize()),
                        () -> assertEquals(false,examplePerformance.hasSocialDistancing()),
                        () -> assertEquals(true,examplePerformance.hasAirFiltration()),
                        () -> assertEquals(false,examplePerformance.isOutdoors()))
        );

    }

    /**
     * Attempts AddEventPerformanceCommand when performance start time is after the performance endtime
     *
     * Register one entertainment provider with one event. An event performance is then added to this event
     * with a start time after the end time.
     * We also get a list of all events after the attempted AddEventPerformanceCommand
     *
     * Verifies: Null is returned from AddEventPerformanceCommand
     *            Log message from AddEventPerformanceCommand is correct
     *            No events are listed after AddEventPerformanceCommand as the event in question has no
     *            performances in the future and so is not listed by the ListEventsCommand
     */
    @Test
    void verifyStartTimeAfterEndTime(){
        Controller controller = new Controller();
        long eventNumber1 = registerEntertainmentProviderWithOneEvent(controller);
        AddEventPerformanceCommand performanceCmd1 = new AddEventPerformanceCommand(
                eventNumber1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                49,
                50
        );
        controller.runCommand(performanceCmd1);

        String addEventPerformanceLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();
        ListEventsCommand cmd = new ListEventsCommand(false,false);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Performance not created",
                () -> assertNull(performanceCmd1.getResult()),
                () -> assertEquals(AddEventPerformanceCommand.LogStatus.ADD_PERFORMANCE_START_AFTER_END.toString(),
                        addEventPerformanceLog),
                () -> assertNull(findEventbyEventNo(events,eventNumber1))
        );
    }
    /**
     * Attempts AddEventPerformanceCommand when capacity is lower than 1
     *
     * Register one entertainment provider with one event. An event performance is then added to this event with
     * a capacity set to 0
     * We also get a list of all events after the attempted AddEventPerformanceCommand
     *
     * Verifies: Null is returned from AddEventPerformanceCommand
     *            Log message from AddEventPerformanceCommand is correct
     *            No events are listed after AddEventPerformanceCommand as the event in question has no
     *            performances in the future and so is not listed by the ListEventsCommand
     */
    @Test
    void verifyCapacityLimitLessThan1(){
        Controller controller = new Controller();
        long eventNumber1 = registerEntertainmentProviderWithOneEvent(controller);
        AddEventPerformanceCommand performanceCmd1 = new AddEventPerformanceCommand(
                eventNumber1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                0,
                50
        );
        controller.runCommand(performanceCmd1);

        String addEventPerformanceLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();
        ListEventsCommand cmd = new ListEventsCommand(false,false);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Performance not created",
                () -> assertNull(performanceCmd1.getResult()),
                () -> assertEquals(AddEventPerformanceCommand.LogStatus.ADD_PERFORMANCE_CAPACITY_LESS_THAN_1.toString(),
                        addEventPerformanceLog),
                () -> assertNull(findEventbyEventNo(events,eventNumber1))
        );
    }
    /**
     * Attempts AddEventPerformanceCommand when venuesize is lower than 1
     * Register one entertainment provider with one event. An event performance is then added to this event with
     * a venueSize set to 0
     * We also get a list of all events after the attempted AddEventPerformanceCommand
     *
     * Verifies: Null is returned from AddEventPerformanceCommand
     *            Log message from AddEventPerformanceCommand is correct
     *            No events are listed after AddEventPerformanceCommand as the event in question has no
     *            performances in the future and so is not listed by the ListEventsCommand
     */
    @Test
    void verifyVenueSizeLessThan1(){
        Controller controller = new Controller();
        long eventNumber1 = registerEntertainmentProviderWithOneEvent(controller);
        AddEventPerformanceCommand performanceCmd1 = new AddEventPerformanceCommand(
                eventNumber1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                0
        );
        controller.runCommand(performanceCmd1);
        String addEventPerformanceLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();
        ListEventsCommand cmd = new ListEventsCommand(false,false);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Performance not created",
                () -> assertNull(performanceCmd1.getResult()),
                () -> assertEquals(AddEventPerformanceCommand.LogStatus.ADD_PERFORMANCE_VENUE_SIZE_LESS_THAN_1.toString(),
                        addEventPerformanceLog),
                () -> assertNull(findEventbyEventNo(events,eventNumber1))
        );
    }
    /**
     * Attempts AddEventPerformanceCommand when logged in user is the government representative
     * Register one entertainment provider with one event. An event performance is then added to this event while
     * Government Representative is logged in.
     * We also get a list of all events after the attempted AddEventPerformanceCommand
     *
     * Verifies: Null is returned from AddEventPerformanceCommand
     *            Log message from AddEventPerformanceCommand is correct
     *            No events are listed after AddEventPerformanceCommand as the event in question has no
     *            performances in the future and so is not listed by the ListEventsCommand
     */
    @Test
    void verifyUserIsNotEntertainmentProvider(){
        Controller controller = new Controller();
        long eventNumber1 = registerEntertainmentProviderWithOneEvent(controller);
        controller.runCommand(new LogoutCommand());

        controller.runCommand(new LoginCommand("margaret.thatcher@gov.uk", "The Good times  "));

        AddEventPerformanceCommand performanceCmd1 = new AddEventPerformanceCommand(
                eventNumber1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        );
        controller.runCommand(performanceCmd1);

        String addEventPerformanceLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();
        ListEventsCommand cmd = new ListEventsCommand(false,false);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Performance not created",
                () -> assertNull(performanceCmd1.getResult()),
                () -> assertEquals(AddEventPerformanceCommand.LogStatus.ADD_PERFORMANCE_USER_NOT_ENTERTAINMENT_PROVIDER.toString(),
                        addEventPerformanceLog),
                () -> assertNull(findEventbyEventNo(events,eventNumber1))
        );
    }
    /**
     * Attempts AddEventPerformanceCommand when eventNumber is a random number
     * Register one entertainment provider with one event. An event performance is then added to this event with
     * the eventNumber set to 20 with no Event having an EventNumber = 20.
     * We also get a list of all events after the attempted AddEventPerformanceCommand
     *
     * Verifies: Null is returned from AddEventPerformanceCommand
     *            Log message from AddEventPerformanceCommand is correct
     *            No events are listed after AddEventPerformanceCommand as the event in question has no
     *            performances in the future and so is not listed by the ListEventsCommand
     */
    @Test
    void verifyEventDoesNotExist(){
        Controller controller = new Controller();
        long eventNumber1 = registerEntertainmentProviderWithOneEvent(controller);

        AddEventPerformanceCommand performanceCmd1 = new AddEventPerformanceCommand(
                20,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        );
        controller.runCommand(performanceCmd1);

        String addEventPerformanceLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();
        ListEventsCommand cmd = new ListEventsCommand(false,false);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Performance not created",
                () -> assertNull(performanceCmd1.getResult()),
                () -> assertEquals(AddEventPerformanceCommand.LogStatus.ADD_PERFORMANCE_EVENT_NOT_FOUND.toString(),
                        addEventPerformanceLog),
                () -> assertNull(findEventbyEventNo(events,eventNumber1))
        );
    }
    /**
     * Attempts AddEventPerformanceCommand when user is an entertainment provider but not the one who created the event
     * Register one entertainment provider with one event. An event performance is then added to this event while the
     * logged in user is an entertainment provider but not the one who created the event.
     * We also get a list of all events after the attempted AddEventPerformanceCommand
     *
     * Verifies: Null is returned from AddEventPerformanceCommand
     *            Log message from AddEventPerformanceCommand is correct
     *            No events are listed after AddEventPerformanceCommand as the event in question has no
     *            performances in the future and so is not listed by the ListEventsCommand
     */
    @Test
    void verifyCurrentUserIsNotEventOrganiser(){
        Controller controller = new Controller();
        long eventNumber1 = registerEntertainmentProviderWithOneEvent(controller);
        controller.runCommand(new LogoutCommand());

        /*
         * Switches currentUser to this newly registered Entertainment Provider, thus this entertainment provider
         * events do not contain the other created event
         */
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

        AddEventPerformanceCommand performanceCmd1 = new AddEventPerformanceCommand(
                1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.now().plusWeeks(1),
                LocalDateTime.now().plusWeeks(1).plusHours(2),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        );
        controller.runCommand(performanceCmd1);
        String addEventPerformanceLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        controller.runCommand(new LoginCommand("odeon@cineworld.com",
                "F!ghT th3 R@Pture"));
        ListEventsCommand cmd = new ListEventsCommand(false,false);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        assertAll("Verify Performance not created",
                () -> assertNull(performanceCmd1.getResult()),
                () -> assertEquals(AddEventPerformanceCommand.LogStatus.ADD_PERFORMANCE_USER_NOT_EVENT_ORGANISER.toString(),
                        addEventPerformanceLog),
                () -> assertNull(findEventbyEventNo(events,eventNumber1))
        );
    }

    /**
     * Attempts AddEventPerformanceCommand when an event has been created prior with the same title and with a
     * performance containing the same times with the test run of AddEventPerformanceCommand
     *
     * Register one entertainment provider with one event. An event performance is then added to this event
     * We also get a list of all events after the attempted AddEventPerformanceCommand
     *
     * Verifies: Null is returned from AddEventPerformanceCommand
     *          Log message from AddEventPerformanceCommand is correct
     *          Verify event and performance created first exists
     *          No events are listed after AddEventPerformanceCommand as the event in question (eventNumber2) has no
     *          performances in the future and so is not listed by the ListEventsCommand
     *
     */
    @Test
    void verifySameTitleWithSameTimesExists(){
        Controller controller = new Controller();
        // Creates first event with title "The LEGO Movie"
        long eventNumber1 = registerEntertainmentProviderWithOneEvent(controller);
        /*
         * Adds first event performance of times: 2030/3/21 - 04:20
         * and 2030/3/21 - 07:00
         */
        AddEventPerformanceCommand performanceCmd1 = new AddEventPerformanceCommand(
                1,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        );
        controller.runCommand(performanceCmd1);

        /*
         * Gets events in the future after first performance and event created
         */
        ListEventsCommand cmd = new ListEventsCommand(false,false);
        controller.runCommand(cmd);
        List<Event> events = cmd.getResult();

        /*
         * Creates second event with title "The LEGO Movie" but different type
         * Adds EventPerformance with times: 2030/3/21 - 04:20
         *      and 2030/3/21 - 07:00
         */
        CreateNonTicketedEventCommand createNonTicketedEventCommand = new CreateNonTicketedEventCommand("The LEGO Movie",EventType.Dance);
        controller.runCommand(createNonTicketedEventCommand);
        long eventNumber2 = createNonTicketedEventCommand.getResult();
        AddEventPerformanceCommand performanceCmd2 = new AddEventPerformanceCommand(
                2,
                "You know how much it hurts when you step on a Lego piece?!?!",
                LocalDateTime.of(2030, 3, 21, 4, 20),
                LocalDateTime.of(2030, 3, 21, 7, 0),
                Collections.emptyList(),
                false,
                true,
                false,
                50,
                50
        );
        controller.runCommand(performanceCmd2);
        String addEventPerformanceLog = Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult();

        /*
         * Gets events in the future after second attempt of creation of event and performance
         */
        ListEventsCommand cmd2 = new ListEventsCommand(false,false);
        controller.runCommand(cmd2);
        List<Event> events2 = cmd2.getResult();

        assertAll("Verify Performance not created",
                () -> assertNull(performanceCmd2.getResult()),
                () -> assertEquals(AddEventPerformanceCommand.LogStatus.ADD_PERFORMANCE_EVENTS_WITH_SAME_TITLE_CLASH.toString(),
                        addEventPerformanceLog),
                () -> assertTrue(findEventbyEventNo(events,eventNumber1).getPerformances().size() == 1),
                () -> assertNull(findEventbyEventNo(events,eventNumber2))
        );
    }
}
