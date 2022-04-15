package controller;

import command.ICommand;

/**
 * Controller is the main external interface of this application. It allows executing commands.
 */
public class Controller extends Object {

    private Context context;
    
    /**
     * The Controller constructor creates and keeps a reference to a new Context.
     */
    public Controller() {
        this.context = new Context();
    }

    /**
     * This method runs a given command, by calling its ICommand.execute(Context) method and passing in the
     * Controller's Context.
     * 
     * @param command command to run
     */
    public void runCommand(ICommand command) {
        command.execute(context);
    }

}
