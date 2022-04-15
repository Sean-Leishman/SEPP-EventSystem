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

public class LogoutSystemTests {
    @BeforeEach
    void printTestName(TestInfo testInfo){
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs(){
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    @Test
    void logoutConsumer(){
        Controller controller = new Controller();

        controller.runCommand(new RegisterConsumerCommand("Mr Pickles",
                "picker345@gmail.com",
                "0983453678",
                "picklesRCool34",
                "picker345@hotmail.com"));
        LogoutCommand cmd = new LogoutCommand();
        controller.runCommand(cmd);

        System.out.println(Logger.getInstance().getLog());
        assertAll("Verify null is returned and log is correct",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(LogoutCommand.LogStatus.USER_LOGOUT_SUCCESS.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult())
        );

    }
    @Test
    void logoutEntertainmentProvider(){
        Controller controller = new Controller();

        RegisterEntertainmentProviderCommand cmd1 = new RegisterEntertainmentProviderCommand(
                "Cinema Conglomerate",
                "Global Office, International Space Station",
                "$$$@there'sNoEmailValidation.wahey!",
                "Mrs Representative",
                "odeon@cineworld.com",
                "F!ghT th3 R@Pture",
                List.of("Dr Strangelove"),
                List.of("we_dont_get_involved@cineworld.com"));
        controller.runCommand(cmd1);
        LogoutCommand cmd2 = new LogoutCommand();
        controller.runCommand(cmd2);

        System.out.println(Logger.getInstance().getLog());
        assertAll("Verify null is returned and log is correct",
                () -> assertNull(cmd2.getResult()),
                () -> assertEquals(LogoutCommand.LogStatus.USER_LOGOUT_SUCCESS.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult())
        );

    }
    @Test
    void logoutGovernmentRep(){
        Controller controller = new Controller();

        LoginCommand cmd1 = new LoginCommand(
                "margaret.thatcher@gov.uk", "The Good times  "
        );
        controller.runCommand(cmd1);
        LogoutCommand cmd2 = new LogoutCommand();
        controller.runCommand(cmd2);

        System.out.println(Logger.getInstance().getLog());
        assertAll("Verify null is returned and log is correct",
                () -> assertNull(cmd2.getResult()),
                () -> assertEquals(LogoutCommand.LogStatus.USER_LOGOUT_SUCCESS.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult())
        );

    }
    @Test
    void logoutNull(){
        Controller controller = new Controller();

        LogoutCommand cmd = new LogoutCommand();
        controller.runCommand(cmd);

        System.out.println(Logger.getInstance().getLog());
        assertAll("Verify null is returned and log is correct",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(LogoutCommand.LogStatus.USER_LOGOUT_NOT_LOGGED_IN.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult())
        );

    }
}
