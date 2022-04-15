import command.*;
import controller.Controller;
import logging.Logger;
import model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSystemTests {
    @BeforeEach
    void printTestName(TestInfo testInfo){
        System.out.println(testInfo.getDisplayName());
    }

    @AfterEach
    void clearLogs(){
        Logger.getInstance().clearLog();
        System.out.println("---");
    }

    private static HashMap<String,Consumer> register2Consumer(Controller controller){
        HashMap<String,Consumer> consumers = new HashMap<String, Consumer>();
        RegisterConsumerCommand cmd1 = new RegisterConsumerCommand("Mr Pickles",
                "picker345@gmail.com",
                "0983453678",
                "picklesRCool34",
                "picker345@hotmail.com");
        controller.runCommand(cmd1);
        consumers.put(cmd1.getResult().getEmail(),cmd1.getResult());
        controller.runCommand(new LogoutCommand());

        RegisterConsumerCommand cmd2 = new RegisterConsumerCommand("Mr Carrot",
                "orangestick@gmail.com",
                "0184563678",
                "carrotsRCool897",
                "orangestick@hotmail.com");
        controller.runCommand(cmd2);
        consumers.put(cmd2.getResult().getEmail(),cmd2.getResult());
        controller.runCommand(new LogoutCommand());

        return consumers;
    }

    private static HashMap<String, EntertainmentProvider> createEntertainmentProviders(Controller controller) {
        HashMap<String,EntertainmentProvider> entertainmentProviders = new HashMap<String,EntertainmentProvider>();
        RegisterEntertainmentProviderCommand cmd1 = new RegisterEntertainmentProviderCommand(
                "Olympics Committee",
                "Mt. Everest",
                "noreply@gmail.com",
                "Secret Identity",
                "anonymous@gmail.com",
                "anonymous",
                List.of("Unknown Actor", "Spy"),
                List.of("unknown@gmail.com", "spy@gmail.com"));
        controller.runCommand(cmd1);
        entertainmentProviders.put(cmd1.getResult().getEmail(),cmd1.getResult());
        controller.runCommand(new LogoutCommand());

        RegisterEntertainmentProviderCommand cmd2 = new RegisterEntertainmentProviderCommand(
                        "Cinema Conglomerate",
                        "Global Office, International Space Station",
                        "$$$@there'sNoEmailValidation.wahey!",
                        "Mrs Representative",
                        "odeon@cineworld.com",
                        "F!ghT th3 R@Pture",
                        List.of("Dr Strangelove"),
                        List.of("we_dont_get_involved@cineworld.com"));
        controller.runCommand(cmd2);
        entertainmentProviders.put(cmd2.getResult().getEmail(),cmd2.getResult());
        controller.runCommand(new LogoutCommand());
        return entertainmentProviders;
    }

    /**
     * Attempts to login to the government representative account using the pre-registered values
     */
    @Test
    void loginGovernmentRepresentative(){
        Controller controller = new Controller();
        LoginCommand cmd = new LoginCommand(
                "margaret.thatcher@gov.uk", "The Good times  "
        );
        controller.runCommand(cmd);

        /*
        Verifies that the User returned is of the correct class type
         */
        assertTrue(cmd.getResult() instanceof GovernmentRepresentative);
    }
    /**
     * Attempts to login to a consumer account using the accounts defined in Register2Consumers
     *
     * Assert: the log output from the LoginCommand command is correct
     *         that the User returned is of the correct class type
     *         that the User returned matches the User email
     */
    @Test
    void loginConsumer(){
        Controller controller = new Controller();
        HashMap<String,Consumer> consumers = register2Consumer(controller);
        LoginCommand cmd = new LoginCommand(
                "picker345@gmail.com","picklesRCool34"
        );
        controller.runCommand(cmd);
        User consumer = cmd.getResult();

        assertAll("Check log and Consumer is correct",
                () -> assertEquals(LoginCommand.LogStatus.USER_LOGIN_SUCCESS.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(consumer instanceof Consumer),
                () -> assertEquals(consumer,consumers.get("picker345@gmail.com"))
        );

    }
    /**
     * Attempts to login to a entertainment provider account using the accounts defined in createEntertainmentProviders
     *
     * Assert: the log output from the LoginCommand command is correct
     *         that the User returned is of the correct class type
     *         that the User returned matches the User email
     */
    @Test
    void loginEntertainmentProvider(){
        Controller controller = new Controller();
        HashMap<String,EntertainmentProvider> entertainmentProviders = createEntertainmentProviders(controller);

        LoginCommand cmd = new LoginCommand("odeon@cineworld.com",
                "F!ghT th3 R@Pture"
        );
        controller.runCommand(cmd);
        System.out.println(Logger.getInstance().getLog());
        User entertainmentProvider = cmd.getResult();
        /*
        Verifies the log output from the LoginCommand command is correct
        Verifies that the User returned is of the correct class type
        Verifies that the User returned matches the User email
         */
        assertAll("Check log and Consumer is correct",
                () -> assertEquals(LoginCommand.LogStatus.USER_LOGIN_SUCCESS.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(entertainmentProvider instanceof EntertainmentProvider),
                () -> assertEquals(entertainmentProvider,entertainmentProviders.get("odeon@cineworld.com"))
        );
    }
    /**
     * Attempts to login to an account where the inputted email does not match any other email
     *
     * Asserts: that null is returned from LoginCommand
     *          the log output from the LoginCommand command is correct
     */
    @Test
    void loginNoMatchingEmail(){
        Controller controller = new Controller();
        register2Consumer(controller);
        LoginCommand cmd = new LoginCommand(
                "","picklesRCool34"
        );
        controller.runCommand(cmd);

        /*
        Verifies that null is returned from LoginCommand
        Verifies the log output from the LoginCommand command is correct
         */
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(LoginCommand.LogStatus.USER_LOGIN_EMAIL_NOT_REGISTERED.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));

        System.out.println(Logger.getInstance().getLog());

    }
    /**
     * Attempts to login to an account where the inputted email does match another email but
     * the password is incorrect
     *
     * Assert: that null is returned from LoginCommand
     *         the log output from the LoginCommand command is correct
     */
    @Test
    void loginWrongPassword(){
        Controller controller = new Controller();
        register2Consumer(controller);
        LoginCommand cmd = new LoginCommand(
                "picker345@gmail.com","picklesRCool32"
        );
        controller.runCommand(cmd);

        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(LoginCommand.LogStatus.USER_LOGIN_WRONG_PASSWORD.toString(),
                        Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));
    }
}
