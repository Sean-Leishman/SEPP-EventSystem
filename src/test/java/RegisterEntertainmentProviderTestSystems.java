import command.*;
import controller.Controller;
import external.EntertainmentProviderSystem;
import logging.Logger;
import model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class RegisterEntertainmentProviderTestSystems {
    @BeforeEach
    void printTestName(TestInfo testInfo){
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs(){
        Logger.getInstance().clearLog();
        System.out.println("---");
    }
    /**
     * Verifies RegisterEntertainmentProviderCommand works as intended with expected inputs
     *
     * Assert: log is correct so user is registered and logged in
     *         the returned user is an EntertainmentProvider
     *         the information is in the correct fields in the returned Consumer as intended
     *
     */
    @Test
    void checkEntertainmentProviderRegisteredCorrectly(){
        Controller controller = new Controller();

        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com")
        );

        controller.runCommand(cmd);
        EntertainmentProvider entProvider = cmd.getResult();
        System.out.println(Logger.getInstance().getLog());
        assertAll("Should return EntertainmentProvider with the correct logs",
                () -> assertEquals(RegisterEntertainmentProviderCommand.LogStatus.REGISTER_ENTERTAINMENT_PROVIDER_SUCCESS.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-2).getResult()),
                () -> assertEquals(RegisterEntertainmentProviderCommand.LogStatus.USER_LOGIN_SUCCESS.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(entProvider instanceof EntertainmentProvider),
                () -> assertEquals("Olympics Committee",entProvider.getOrgName()),
                () -> assertEquals("Mt. Everest",entProvider.getOrgAddress()),
                () -> assertEquals("noreply@gmail.com",entProvider.getPaymentAccountEmail()),
                () -> assertEquals("anonymous@gmail.com",entProvider.getEmail()),
                () -> assertTrue(entProvider.checkPasswordMatch("anonymous")),
                () -> assertTrue(entProvider.getProviderSystem() instanceof EntertainmentProviderSystem)
                );
    }
    /**
     * Attempts RegisterEntertainmentProviderCommand with some fields empty
     *
     * Assert: null is returned from the command
     *         log is correct
     */
    @Test
    void checkEmptyFields(){
        Controller controller = new Controller();

        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com")
        );
        controller.runCommand(cmd);
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(RegisterEntertainmentProviderCommand.LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));

        System.out.println(Logger.getInstance().getLog());
    }
    /**
     * Attempts RegisterEntertainmentProviderCommand with some lists empty
     *
     * Assert: null is returned from the command
     *         log is correct
     */
    @Test
    void checkEmptyLists(){
        Controller controller = new Controller();

        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "",
                "anonymous@gmail.com",
                "anonymous",
                new ArrayList<>(),
                List.of("unknown@gmail.com", "spy@gmail.com")
        );
        controller.runCommand(cmd);
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(RegisterEntertainmentProviderCommand.LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));

        System.out.println(Logger.getInstance().getLog());
    }
    /**
     * Attempts RegisterEntertainmentProviderCommand with some fields null
     *
     * Assert: null is returned from the command
     *         log is correct
     */
    @Test
    void checkNullFields(){
        Controller controller = new Controller();

        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                null,
                "",
                "anonymous@gmail.com",
                "anonymous",
                new ArrayList<>(),
                List.of("unknown@gmail.com", "spy@gmail.com")
        );
        controller.runCommand(cmd);
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(RegisterEntertainmentProviderCommand.LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));

        System.out.println(Logger.getInstance().getLog());
    }
    /**
     * Attempts RegisterEntertainmentProviderCommand with some an already registered email
     *
     * Registers two consumers with different details aside from the email
     *
     * Assert: null is returned from the command
     *         log is correct
     */
    @Test
    void checkEmailExists(){
        Controller controller = new Controller();
        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "hifhs",
                "fs",
                "anonymous@gmail.com",
                "anonymous",
                List.of("unknown@gmail.com", "spy@gmail.com"),
                List.of("unknown@gmail.com", "spy@gmail.com")
        ));

        controller.runCommand(new LogoutCommand());

        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "wfef",
                "fefes",
                "gdfgd",
                "fesfes",
                "anonymous@gmail.com",
                "fseesfes",
                List.of("unknown@gmail.com", "spy@gmail.com"),
                List.of("unknown@gmail.com", "spy@gmail.com")
        );
        controller.runCommand(cmd);
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(RegisterEntertainmentProviderCommand.LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));

        System.out.println(Logger.getInstance().getLog());
    }
    /**
     * Attempts RegisterEntertainmentProviderCommand with an orgName and orgAddress which already belongs to another
     *      EntertainmentProvider
     *
     * Registers two consumers with different details aside from the orgName and orgAddress
     *
     * Assert: null is returned from the command
     *         log is correct
     */
    @Test
    void checkNameAndOrgExists(){
        Controller controller = new Controller();

        controller.runCommand(new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "hifhs",
                "fs",
                "anonymous@gmail.com",
                "anonymous",
                List.of("unknown@gmail.com", "spy@gmail.com"),
                List.of("unknown@gmail.com", "spy@gmail.com")
        ));

        controller.runCommand(new LogoutCommand());

        RegisterEntertainmentProviderCommand cmd = new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "gdfgd",
                "fesfes",
                "anonymdadsaous@gmail.com",
                "fseesfes",
                List.of("unknown@gmail.com", "spy@gmail.com"),
                List.of("unknown@gmail.com", "spy@gmail.com")
        );

        controller.runCommand(cmd);
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(RegisterEntertainmentProviderCommand.LogStatus.USER_REGISTER_ORG_ALREADY_REGISTERED.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));

        System.out.println(Logger.getInstance().getLog());
    }
}
