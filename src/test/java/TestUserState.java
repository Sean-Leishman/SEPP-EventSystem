import model.Consumer;
import model.EntertainmentProvider;
import model.User;
import org.junit.jupiter.api.Test;
import state.UserState;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserState {
    /**
     * Test whether we can successfully deep clone userState
     * Sets up a userState and adds two users and sets a currentUser
     * Then creates a deep copy of the userState and checks every attribute has copied
     */
    @Test
    void testDeepCloneUnchanged(){
        UserState userState = new UserState();

        Consumer consumer = new Consumer(
                "John Doe",
                "johndoe@hotmail.co.uk",
                "077777777777",
                "i am generic",
                "johndoe@hotmail.co.uk"
        );
        EntertainmentProvider entertainmentProvider = new EntertainmentProvider(
                "Shop",
                "Just down the road",
                "shop@hotmail.com",
                "Shopkeeper",
                "shopkeeper@hotmail.com",
                "this is a shop",
                Collections.emptyList(),
                Collections.emptyList()
        );
        userState.addUser(consumer);
        userState.addUser(entertainmentProvider);
        userState.setCurrentUser(consumer);

        UserState copyUserState = new UserState(userState);

        assertAll("Verify number of users in EventState across copy and original",
            () -> assertEquals(userState.getAllUsers(), copyUserState.getAllUsers(), "List of Users hasn't copied"),
            () -> assertEquals(userState.getCurrentUser(), copyUserState.getCurrentUser(), "Current User hasn't copied")
        );
    }

    /**
     * Test that we have a deep copy and not a shallow one
     * Sets up a userState and adds two users and sets a currentUser, then creates a deep copy of this
     * The original userState is modified and we test that the copy doesn't get modified by these changes too
     */
    @Test
    void testIsDeepCopy(){
        UserState userState = new UserState();

        Consumer consumer1 = new Consumer(
                "John Doe",
                "johndoe@hotmail.co.uk",
                "077777777777",
                "i am generic",
                "johndoe@hotmail.co.uk"
        );

        Consumer consumer2 = new Consumer(
                "Jane Doe",
                "janedoe@hotmail.co.uk",
                "077777777777",
                "i am generic",
                "janedoe@hotmail.co.uk"
        );

        EntertainmentProvider entertainmentProvider = new EntertainmentProvider(
                "Shop",
                "Just down the road",
                "shop@hotmail.com",
                "Shopkeeper",
                "shopkeeper@hotmail.com",
                "this is a shop",
                Collections.emptyList(),
                Collections.emptyList()
        );
        userState.addUser(consumer1);
        userState.addUser(entertainmentProvider);
        userState.setCurrentUser(consumer1);

        UserState copyUserState = new UserState(userState);

        userState.addUser(consumer2);
        userState.setCurrentUser(consumer2);

        assertAll("Verify that the two UserStates maintain different lists",
                () -> assertNotSame(userState, copyUserState, "Same Reference"),
                () -> assertNotEquals(userState.getCurrentUser(), copyUserState.getCurrentUser(), "Current Users are the same, copy wasn't deep"),
                () -> assertNotEquals(userState.getAllUsers(), copyUserState.getAllUsers(), "List of Users are the same, copy wasn't deep")
        );


    }

    /**
     * Test that we can add a consumer to a userState
     * Sets up a userState and adds a consumer
     */
    @Test
    void testAddValidConsumer(){
        UserState userState = new UserState();
        Integer originalSize = userState.getAllUsers().size();

        Consumer consumer1 = new Consumer(
                "John Doe",
                "johndoe@hotmail.co.uk",
                "077777777777",
                "i am generic",
                "johndoe@hotmail.co.uk"
        );

        userState.addUser(consumer1);
        assertAll("Verify that a consumer can be added into the UserState",
                () -> assertEquals(userState.getAllUsers().size(), originalSize + 1, "User has not been successfully added"),
                () -> assertEquals(userState.getAllUsers().get(consumer1.getEmail()), consumer1, "User has not been successfully added")
        );

    }

    /**
     * Test that we can add an EntertainmentProvider to a userState
     * Sets up a userState and adds an EntertainmentProvider
     */
    @Test
    void testAddValidEntertainmentProvider(){
        UserState userState = new UserState();
        Integer originalSize = userState.getAllUsers().size();

        EntertainmentProvider entertainmentProvider = new EntertainmentProvider(
                "Shop",
                "Just down the road",
                "shop@hotmail.com",
                "Shopkeeper",
                "shopkeeper@hotmail.com",
                "this is a shop",
                Collections.emptyList(),
                Collections.emptyList()
        );

        userState.addUser(entertainmentProvider);

        assertAll("Verify that an entertainment provider can be added into the UserState",
                () -> assertEquals(userState.getAllUsers().size(), originalSize + 1, "User has not been successfully added"),
                () -> assertEquals(userState.getAllUsers().get(entertainmentProvider.getEmail()), entertainmentProvider, "User has not been successfully added")
        );
    }

    @Test
    /**
     * Test that we can't add a User with the same email to a userState twice
     */
    void testAddDuplicateEntertainmentProvider() {
        UserState userState = new UserState();

        EntertainmentProvider entertainmentProvider = new EntertainmentProvider(
                "Shop",
                "Just down the road",
                "shop@hotmail.com",
                "Shopkeeper",
                "shopkeeper@hotmail.com",
                "this is a shop",
                Collections.emptyList(),
                Collections.emptyList()
        );


        userState.addUser(entertainmentProvider);
        Integer previousSize = userState.getAllUsers().size();
        userState.addUser(entertainmentProvider);
        assertAll("Verify that an a user cannot be added twice",
                () -> assertEquals(userState.getAllUsers().size(), previousSize, "User has been added twice")
        );
    }

    /**
     * Test that we can add numerous users successfully
     */
    @Test
    void testGetAllUsers(){
        UserState userState = new UserState();
        Integer originalSize = userState.getAllUsers().size();

        Consumer consumer1 = new Consumer(
                "John Doe",
                "johndoe@hotmail.co.uk",
                "077777777777",
                "i am generic",
                "johndoe@hotmail.co.uk"
        );

        Consumer consumer2 = new Consumer(
                "Jane Doe",
                "janedoe@hotmail.co.uk",
                "077777777777",
                "i am generic",
                "janedoe@hotmail.co.uk"
        );

        EntertainmentProvider entertainmentProvider = new EntertainmentProvider(
                "Shop",
                "Just down the road",
                "shop@hotmail.com",
                "Shopkeeper",
                "shopkeeper@hotmail.com",
                "this is a shop",
                Collections.emptyList(),
                Collections.emptyList()
        );

        ArrayList<User> usersToAdd = new ArrayList<>();
        usersToAdd.add(consumer1);
        usersToAdd.add(consumer2);
        usersToAdd.add(entertainmentProvider);

        for (User user : usersToAdd){
            userState.addUser(user);
        }

        assertAll("Verify that we can add numerous users successfully",
                () -> assertEquals(userState.getAllUsers().size(), usersToAdd.size() + originalSize, "A User has not been added to userState"),
                () -> assertTrue(userState.getAllUsers().values().containsAll(usersToAdd), "Users not added to userState")
        );
    }

    /**
     * Test that userState getters and setters are working properly
     */
    @Test
    void testGettersSetters(){
        UserState userState = new UserState();

        Consumer consumer = new Consumer(
                "John Doe",
                "johndoe@hotmail.co.uk",
                "077777777777",
                "i am generic",
                "johndoe@hotmail.co.uk"
        );
        EntertainmentProvider entertainmentProvider = new EntertainmentProvider(
                "Shop",
                "Just down the road",
                "shop@hotmail.com",
                "Shopkeeper",
                "shopkeeper@hotmail.com",
                "this is a shop",
                Collections.emptyList(),
                Collections.emptyList()
        );
        userState.addUser(consumer);
        userState.addUser(entertainmentProvider);
        userState.setCurrentUser(consumer);

        assertEquals(userState.getCurrentUser(), consumer, "Current User not set properly");
    }

    /**
     * Test that userState currentUser field is initially Null when created
     */
    @Test
    void testInitialNullCurrentUser(){
        UserState userState = new UserState();
        assertNull(userState.getCurrentUser(), "Current User not initialised to Null");
    }
}
