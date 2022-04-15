import command.*;
import controller.Controller;
import logging.Logger;
import model.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.lang.AssertionError;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterConsumerSystemTests {
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
     * Verifies RegisterConsumerCommands works as intended with expected inputs
     *
     * Assert: log is correct so user is registered and logged in
     *         the returned user is a Consumer
     *         the information is in the correct fields in the returned Consumer as intended
     *
     */
    @Test
    void checkUserCreatedCorrectly(){
        Controller controller = new Controller();

        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Mr Pickles",
                "picker345@gmail.com",
                "0983453678",
                "picklesRCool34",
                "picker345@hotmail.com");

        controller.runCommand(cmd);

        Consumer consumer = cmd.getResult();

        assertAll("Check return is instance of Consumer and log is correct",
                () -> assertEquals(RegisterConsumerCommand.LogStatus.REGISTER_CONSUMER_SUCCESS.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-2).getResult()),
                () -> assertEquals(RegisterConsumerCommand.LogStatus.USER_LOGIN_SUCCESS.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()),
                () -> assertTrue(consumer instanceof Consumer));
        assertAll("Check user registered is correct",
                () -> assertEquals("Mr Pickles",consumer.getName()),
                () -> assertEquals("picker345@gmail.com",consumer.getEmail()),
                () -> assertEquals("picker345@hotmail.com",consumer.getPaymentAccountEmail()),
                () -> assertTrue(consumer.checkPasswordMatch("picklesRCool34"))
        );
        //assertAll("Check current user logged in is correct");
    }

    /**
     * Attempts RegisterConsumerCommand with some fields empty
     *
     * Assert: null is returned from the command
     *         log is correct
     */
    @Test
    void checkEmptyFields(){
        Controller controller = new Controller();

        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Mr Pickles",
                "",
                "0983453678",
                "picklesRCool34",
                "picker345@hotmail.com");

        controller.runCommand(cmd);
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(RegisterConsumerCommand.LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));
    }

    /**
     * Attempts RegisterConsumerCommand with some fields null
     *
     * Assert: null is returned from the command
     *         log is correct
     */
    @Test
    void checkNullFields(){
        Controller controller = new Controller();

        RegisterConsumerCommand cmd = new RegisterConsumerCommand("Mr Pickles",
                null,
                "0983453678",
                "picklesRCool34",
                "picker345@hotmail.com");

        controller.runCommand(cmd);
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(RegisterConsumerCommand.LogStatus.USER_REGISTER_FIELDS_CANNOT_BE_NULL.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));

        System.out.println(Logger.getInstance().getLog());
    }

    /**
     * Attempts RegisterConsumerCommand with some an already registered email
     *
     * Registers two consumers with different details aside from the email
     *
     * Assert: null is returned from the command
     *         log is correct
     */
    @Test
    void checkWhereEmailExists(){
        Controller controller = new Controller();

        controller.runCommand(new RegisterConsumerCommand("Mr Pickles",
                "jbiggson1@hotmail.co.uk",
                "0983453678",
                "picklesRCool34",
                "picker345@hotmail.com"));

        RegisterConsumerCommand cmd = new RegisterConsumerCommand("John Biggson",
                "jbiggson1@hotmail.co.uk",
                "077893153480",
                "jbiggson2",
                "jbiggson1@hotmail.co.uk");

        controller.runCommand(cmd);
        assertAll("Should return null",
                () -> assertNull(cmd.getResult()),
                () -> assertEquals(RegisterConsumerCommand.LogStatus.USER_REGISTER_EMAIL_ALREADY_REGISTERED.toString(),Logger.getInstance().getLog().get(Logger.getInstance().getLog().size()-1).getResult()));

        System.out.println(Logger.getInstance().getLog());
    }
}
