package command;

import controller.Context;

/**
 * ICommand is the interface implemented by all commands. This common interface allows the
 * Controller to run any command using a single common method
 * Controller.runCommand(ICommand).
 */
public interface ICommand {
    /**
     * This method should not be called directly outside of testing.
     * Normal usage is to create a command object and execute it by passing to
     * Controller.runCommand(ICommand) instead.
     * 
     * @param context object that provides access to global application state
     */
    void execute(Context context);

    /**
     * Get the result from the latest run of the command.
     * 
     * @return The command result (type varies by command) if successful and null or false otherwise.
     */
    Object getResult();

}
