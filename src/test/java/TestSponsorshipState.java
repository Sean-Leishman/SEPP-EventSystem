import model.*;
import org.junit.jupiter.api.Test;
import state.SponsorshipState;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class TestSponsorshipState {

    /**
     * Test whether we can successfully clone sponsorshipState
     */
    @Test
    void TestDeepCopyUnchanged(){
        SponsorshipState sponsorshipState = new SponsorshipState();

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

        TicketedEvent ticketedEvent = new TicketedEvent(
                1,
                entertainmentProvider,
                "This is a test",
                EventType.Dance,
                10,
                100
        );

        // In practice, we would have max one sponsorship request per ticketed event however
        // since we are just testing Sponsorship State deep copying, we can ignore this for now

        sponsorshipState.addSponsorshipRequest(ticketedEvent);
        sponsorshipState.addSponsorshipRequest(ticketedEvent);
        sponsorshipState.addSponsorshipRequest(ticketedEvent);

        SponsorshipState copyState = new SponsorshipState(sponsorshipState);
        assertEquals(sponsorshipState.getAllSponsorshipRequests(), copyState.getAllSponsorshipRequests(),
                "Not all Sponsorship Requests copied");

    }

    /**
     * Test to confirm that our copy is a deep copy of sponsorship state
     * Depth of copy is just list of sponsorship requests - not deep copy of each sponsorship request
     * From Piazza @248
     */
    @Test
    void testIsDeepCopy(){
        SponsorshipState sponsorshipState = new SponsorshipState();

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

        TicketedEvent ticketedEvent1 = new TicketedEvent(
                1,
                entertainmentProvider,
                "This is a test",
                EventType.Dance,
                10,
                100
        );

        TicketedEvent ticketedEvent2 = new TicketedEvent(
                2,
                entertainmentProvider,
                "This is a test",
                EventType.Music,
                100,
                10
        );

        sponsorshipState.addSponsorshipRequest(ticketedEvent1);

        SponsorshipState copyState = new SponsorshipState(sponsorshipState);

        sponsorshipState.addSponsorshipRequest(ticketedEvent2);

        assertNotEquals(sponsorshipState.getAllSponsorshipRequests(), copyState.getAllSponsorshipRequests(),
                "Shallow copy - Sponsorship Request Lists are the same");
    }

    @Test
    void TestCanAddSponsorship(){
        SponsorshipState sponsorshipState = new SponsorshipState();

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

        TicketedEvent ticketedEvent1 = new TicketedEvent(
                1,
                entertainmentProvider,
                "This is a test",
                EventType.Dance,
                10,
                100
        );


        Integer originalSize = sponsorshipState.getAllSponsorshipRequests().size();

        assertAll("Sponsorship request can be added",
                () -> assertEquals(sponsorshipState.addSponsorshipRequest(ticketedEvent1).getClass().getName(),"model.SponsorshipRequest",
                        "Method doesn't return correct type"),
                () -> assertEquals(sponsorshipState.getAllSponsorshipRequests().size(), originalSize+1,
                        "Sponsorship Request hasn't been added")
        );
    }

    /**
     * Test that adding a sponsorship doesn't distort the sponsorship request
     */
    @Test
    void TestSponsorshipAddsCorrectly(){
        SponsorshipState sponsorshipState = new SponsorshipState();

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

        TicketedEvent ticketedEvent1 = new TicketedEvent(
                1,
                entertainmentProvider,
                "This is a test",
                EventType.Dance,
                10,
                100
        );
        SponsorshipRequest sponsorshipRequest = sponsorshipState.addSponsorshipRequest(ticketedEvent1);

        assertAll("Sponsorship status is pending at first",
                () -> assertEquals(sponsorshipRequest.getStatus(), SponsorshipStatus.PENDING, "Sponsorship Status isn't set as Pending"),
                () -> assertEquals(sponsorshipRequest.getEvent(), ticketedEvent1, "Event hasn't copied correctly")
        );
        ;
    }

    /**
     * Test that get all requests functions properly and returns the correct amount of requests
     */
    @Test
    void testGetAllRequests(){
        SponsorshipState sponsorshipState = new SponsorshipState();

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

        TicketedEvent ticketedEvent1 = new TicketedEvent(
                1,
                entertainmentProvider,
                "This is a test",
                EventType.Dance,
                10,
                100
        );

        TicketedEvent ticketedEvent2 = new TicketedEvent(
                2,
                entertainmentProvider,
                "This is a test",
                EventType.Music,
                100,
                10
        );

        assertAll("Sponsorship requests can be added successfully",
                () -> assertEquals(sponsorshipState.getAllSponsorshipRequests().size(), 0, "One or more sponsorship requests are present upon initialisation"),
                () -> {
                    sponsorshipState.addSponsorshipRequest(ticketedEvent1);
                    sponsorshipState.addSponsorshipRequest(ticketedEvent2);

                    assertEquals(sponsorshipState.getAllSponsorshipRequests().size(), 2,
                            "Correct amount of Requests (2) not present");
                }
        );
    }

    /**
     * Tests that getPendingRequests return has size less than or equal to getAllRequests and
     * that rejecting a sponsorship request reduces the amount of requests getPendingRequests returns
     */
    @Test
    void testPendingRequestSizes(){
        SponsorshipState sponsorshipState = new SponsorshipState();

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

        TicketedEvent ticketedEvent1 = new TicketedEvent(
                1,
                entertainmentProvider,
                "This is a test",
                EventType.Dance,
                10,
                100
        );

        TicketedEvent ticketedEvent2 = new TicketedEvent(
                2,
                entertainmentProvider,
                "This is a test",
                EventType.Music,
                100,
                10
        );

        assertAll("Test that sponsorship request counts are lawful (also upon rejecting)",
                () -> assertTrue(sponsorshipState.getAllSponsorshipRequests().size() >= sponsorshipState.getPendingSponsorshipRequests().size()
                        , "There are more pending requests than total sponsorship requests"),
                () -> {
                    sponsorshipState.addSponsorshipRequest(ticketedEvent1);
                    sponsorshipState.addSponsorshipRequest(ticketedEvent2);

                    assertAll("Test that adding some requests or rejecting stay lawful",
                            () -> assertTrue(sponsorshipState.getAllSponsorshipRequests().size() >= sponsorshipState.getPendingSponsorshipRequests().size(),
                                    "There are more pending requests than total sponsorship requests"),
                            () -> {
                                Integer originalSize = sponsorshipState.getPendingSponsorshipRequests().size();
                                sponsorshipState.findRequestByNumber(1).reject();
                                // we have rejected one of the two requests to get pending requests should only return one request
                                assertEquals(sponsorshipState.getPendingSponsorshipRequests().size(), originalSize - 1,
                                        "Sponsorship Status has not been changed from PENDING");
                            }
                    );
                }
        );
    }

    /**
     * Tests that getPendingRequests returns only requests that have a pending status
     */
    @Test
    void testPendingRequestStatus(){
        SponsorshipState sponsorshipState = new SponsorshipState();

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

        TicketedEvent ticketedEvent1 = new TicketedEvent(
                1,
                entertainmentProvider,
                "This is a test",
                EventType.Dance,
                10,
                100
        );

        TicketedEvent ticketedEvent2 = new TicketedEvent(
                2,
                entertainmentProvider,
                "This is a test",
                EventType.Music,
                100,
                10
        );

        sponsorshipState.addSponsorshipRequest(ticketedEvent1);
        sponsorshipState.addSponsorshipRequest(ticketedEvent2);
        sponsorshipState.addSponsorshipRequest(ticketedEvent1);
        sponsorshipState.addSponsorshipRequest(ticketedEvent2);
        sponsorshipState.addSponsorshipRequest(ticketedEvent1);
        sponsorshipState.addSponsorshipRequest(ticketedEvent2);

        // we will now reject two of these sponsorship requests
        sponsorshipState.findRequestByNumber(3).reject();
        sponsorshipState.findRequestByNumber(4).reject();

        assertTrue(sponsorshipState.getPendingSponsorshipRequests().stream().allMatch(x -> x.getStatus().equals(SponsorshipStatus.PENDING)),
                "Some sponsorship requests listed as Pending do not have Pending status");

    }

    /**
     * Tests that getPendingRequests returns only requests that have a pending status
     */
    @Test
    void testFindByNumber(){
        SponsorshipState sponsorshipState = new SponsorshipState();

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

        TicketedEvent ticketedEvent1 = new TicketedEvent(
                1,
                entertainmentProvider,
                "This is a test",
                EventType.Dance,
                10,
                100
        );

        TicketedEvent ticketedEvent2 = new TicketedEvent(
                2,
                entertainmentProvider,
                "This is a test",
                EventType.Music,
                100,
                10
        );

        sponsorshipState.addSponsorshipRequest(ticketedEvent1);
        sponsorshipState.addSponsorshipRequest(ticketedEvent2);
        sponsorshipState.addSponsorshipRequest(ticketedEvent1);
        sponsorshipState.addSponsorshipRequest(ticketedEvent2);
        sponsorshipState.addSponsorshipRequest(ticketedEvent1);
        sponsorshipState.addSponsorshipRequest(ticketedEvent2);

        assertAll("Test that findRequestByNumber behaves fine",
                () -> assertNull(sponsorshipState.findRequestByNumber(-1),
                        "Returns a request when there isn't a valid request number"),
                () -> assertNull(sponsorshipState.findRequestByNumber(0),
                        "Returns a request when there isn't a valid request number"),
                () -> assertNull(sponsorshipState.findRequestByNumber(10),
                        "Returns a request when there isn't a valid request number"),
                () -> assertTrue(sponsorshipState.getAllSponsorshipRequests().stream().allMatch(x -> sponsorshipState.findRequestByNumber(x.getRequestNumber()).equals(x)),
                        "Some sponsorship request were not returned when they should exist")
        );
    }
}
