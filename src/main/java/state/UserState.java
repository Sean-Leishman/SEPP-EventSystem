package state;

import model.GovernmentRepresentative;
import model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * UserState is a concrete implementation of IUserState.
 */
public class UserState extends Object implements IUserState {

    Map<String, User> users;
    User currentUser;

    /**
     * Create a new UserState with an empty collection of users and the currently logged-in user set to null.
     * Then add pre-registered government representative accounts to the users list upon state creation.
     * There is no functionality to register government representatives during runtime.
     *
     * Important note: DO NOT DO STORE SECRETS LIKE THIS IN A REAL APPLICATION.
     * Why? Because anyone with the right tools can reverse engineer the code and see these login details in plain text.
     * What to do instead? Load the appropriate details from environment variables or get them from an external server.
     * Why did we not do this? It makes testing more difficult for you and security is not our main goal here - this
     * will be the topic of next year's Computer Security course.
     */

    public UserState() {
        this.users = new HashMap<String,User>();
        this.currentUser = null;
        this.registerGovernmentRepresentatives();
    }

    /**
     * Copy constructor to create a deep copy of another UserState instance
     * 
     * @param other instance to copy
     */
    public UserState(IUserState other) {
        this.users = new HashMap<>();
        Map<String,User> usersToCopy = other.getAllUsers();
        for (String string : usersToCopy.keySet()){
            this.users.put(string, usersToCopy.get(string));
        }
        this.currentUser = other.getCurrentUser();
    }

    /**
     * @param user user
     * {@inheritDoc}
     */
    public void addUser(User user) {
        this.users.put(user.getEmail(),user);
    }

    /**
     * @return A collection of all registered users
     */
    public Map<String, User> getAllUsers() {
        return this.users;
    }

    /**
     * @return The currently logged-in user if there is one, or null otherwise
     */
    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * @param user user
     * {@inheritDoc}
     */
    public void setCurrentUser(User user) {
        //assert this.getAllUsers().containsKey(user.getEmail()) : "Trying to set current user to non registered user";
        this.currentUser = user;
    }

    private void registerGovernmentRepresentatives(){
        this.users.put("margaret.thatcher@gov.uk",new GovernmentRepresentative("margaret.thatcher@gov.uk","The Good times  ","government@email.com"));
    }

}
