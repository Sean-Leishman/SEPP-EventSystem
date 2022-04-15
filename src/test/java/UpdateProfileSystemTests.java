import command.*;
import controller.Controller;
import logging.Logger;
import model.ConsumerPreferences;
import model.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateProfileSystemTests {
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

    @Test
    void updateConsumerProfile(){
        Controller controller = new Controller();
        register2Consumer(controller);

        UpdateConsumerProfileCommand cmd = new UpdateConsumerProfileCommand("carrotsRCool897",
                "John Biggson",
                "jbiggson1@hotmail.co.uk",
                "077893153480",
                "jbiggson2",
                "jbiggson1@hotmail.co.uk",
                new ConsumerPreferences(true,false,false,1000,1000));
        controller.runCommand(cmd);
        assertTrue(cmd.getResult());
    }

    @Test
    void updateEntertainmentProviderProfile(){
        Controller controller = new Controller();
        registerEntertainmentProvider(controller);

        UpdateEntertainmentProviderProfileCommand cmd = new UpdateEntertainmentProviderProfileCommand("F!ghT th3 R@Pture",
                "No org",
                "Leith Walk",
                "a hat on the ground",
                "the best musicican ever",
                "busk@every.day",
                "When they say 'you can't do this': Ding Dong! You are wrong!",
                List.of("Dr Strangelosdsve"),
                List.of("we_dont_get_insdsvolved@cineworld.com")
        );
        controller.runCommand(cmd);
        assertTrue(cmd.getResult());

    }

    @Test
    void verifyUser(){
        Controller controller = new Controller();
        registerEntertainmentProvider(controller);

        UpdateEntertainmentProviderProfileCommand cmd = new UpdateEntertainmentProviderProfileCommand("F!ghT th3 R@Pture",
                "No org",
                "Leith Walk",
                "a hat on the ground",
                "the best musicican ever",
                "busk@every.day",
                "When they say 'you can't do this': Ding Dong! You are wrong!",
                List.of("Dr Strangelosdsve"),
                List.of("we_dont_get_insdsvolved@cineworld.com")
        );
        controller.runCommand(cmd);
        assertTrue(cmd.getResult());

    }
}
