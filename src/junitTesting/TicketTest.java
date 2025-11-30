package junitTesting;

import model.Ticket;
import model.ParkingSlot;
import model.Vehicle;
import model.VehicleType;
import model.Colors;
<<<<<<< HEAD

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

=======
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
>>>>>>> 23b811087ec9c7d2f890e3a7b170e7856ea6fa3c
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TicketTest {
<<<<<<< HEAD

    // simple vehicle for testing; relies on Vehicle's pricing
    private static class TestVehicle extends Vehicle {
        public TestVehicle() {
            super();
        }

        public TestVehicle(String plate) {
            super();
            setPlateNumber(plate);
            setBrand("TestBrand");
            setModel("TestModel");
            setColor(Colors.BLACK);
            setType(VehicleType.CAR);
        }
    }

    private TestVehicle newTestVehicle(String plate) {
        return new TestVehicle(plate);
    }

    // tests that constructor sets fields and defaults entryTime when null
    @Test
    void constructorSetsFieldsAndDefaultsEntryTime() {
        Vehicle vehicle = newTestVehicle("TEST123");
        ParkingSlot slot = new ParkingSlot(7);

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // use the 3-arg constructor: Ticket(Vehicle, ParkingSlot, LocalDateTime)
        Ticket ticket = new Ticket(vehicle, slot, null);

        // ID is auto-generated, just assert it's positive
        assertTrue(ticket.getTicketID() > 0);
        assertSame(vehicle, ticket.getVehicle());
        assertSame(slot, ticket.getSlot());
        assertTrue(ticket.isActive());
        assertNotNull(ticket.getEntryTime(), "entryTime should default to current time when null is passed");

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        // entryTime should be roughly "now"
        assertFalse(ticket.getEntryTime().isBefore(before));
        assertFalse(ticket.getEntryTime().isAfter(after));
        assertEquals(0.0, ticket.getTotalFee(), 0.0001);
    }

    @Test
    void calculateDurationReturnsDifferenceInMinutes() {
        Vehicle v = newTestVehicle("X1");
        Ticket ticket = new Ticket(v, null, null);

        // year, month, day, hour, min
        LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 11, 0);
        LocalDateTime exit  = LocalDateTime.of(2025, 1, 1, 11, 30); // 30 minutes

        ticket.setEntryTime(entry);
        ticket.setExitTime(exit);

        int minutes = ticket.calculateDuration();
        assertEquals(30, minutes);
    }

    // tests generateFee using vehicle's rate:
    // CAR = $5/hour, so 30 minutes = 2.5
    @Test
    void generateFeeUsesVehicleRate() {
        Vehicle v = newTestVehicle("CAR123");
        Ticket ticket = new Ticket(v, null, null);

        LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 11, 0);
        LocalDateTime exit  = LocalDateTime.of(2025, 1, 1, 11, 30); // 0.5 hour

        ticket.setEntryTime(entry);
        ticket.setExitTime(exit);

        double fee = ticket.generateFee();

        // 0.5h * $5/h = 2.5
        assertEquals(2.5, fee, 0.0001);
        assertEquals(2.5, ticket.getTotalFee(), 0.0001);
    }

    // tests closeTicket sets exit time, marks inactive, and fee is non-negative
    @Test
    void closeTicketSetsExitTimeInactiveAndComputesFee() throws InterruptedException {
        Vehicle v = newTestVehicle("CAR123");
        Ticket ticket = new Ticket(v, null, null);

        // make sure exitTime is after entryTime
        LocalDateTime beforeClose = LocalDateTime.now();
        Thread.sleep(5); // tiny delay
        ticket.closeTicket(null);
        LocalDateTime afterClose = LocalDateTime.now();

        assertFalse(ticket.isActive(), "Ticket should be inactive after closeTicket");
        assertNotNull(ticket.getExitTime(), "exitTime should be set when closing ticket");

        // exitTime should be between beforeClose and afterClose
        assertFalse(ticket.getExitTime().isBefore(beforeClose));
        assertFalse(ticket.getExitTime().isAfter(afterClose));

        double fee = ticket.getTotalFee();
        assertTrue(fee >= 0.0, "Fee should not be negative");
    }

    @Test
    void settersAndGetters() {
        Vehicle v = newTestVehicle("ABC999");
        Ticket ticket = new Ticket(v, null, null);

        // setTicketID
        ticket.setTicketID(12345);
        assertEquals(12345, ticket.getTicketID());

        // setVehicle
        ticket.setVehicle(null);
        assertNull(ticket.getVehicle());

        // setSlot
        ParkingSlot slot = new ParkingSlot(3);
        ticket.setSlot(slot);
        assertSame(slot, ticket.getSlot());

        // setEntry/ExitTime
        LocalDateTime entry = LocalDateTime.now().minus(2, ChronoUnit.HOURS);
        LocalDateTime exit  = LocalDateTime.now();
        ticket.setEntryTime(entry);
        ticket.setExitTime(exit);

        assertEquals(entry, ticket.getEntryTime());
        assertEquals(exit, ticket.getExitTime());

        // setTotalFee
        ticket.setTotalFee(42.5);
        assertEquals(42.5, ticket.getTotalFee(), 0.0001);

        // setActive
        ticket.setActive(false);
        assertFalse(ticket.isActive());
    }
}

=======
	//vehicle for testing
	private static class TestVehicle extends Vehicle {
        @Override
        public double calculateFee(long minutesParked) {
            return 0;
        }
    }
	
	private TestVehicle newTestVehicle(String plate) {
		TestVehicle v = new TestVehicle();
        v.setPlateNumber(plate);
        v.setBramd("TestBrand");
        v.setModel("TestModel");
        v.setColor(Colors.BLACK);
        v.setType(VehicleType.CAR);;
        return v;
	}
	
	//this tests the constructor set fields and defaults the entry time when null
	@Test
	void constructorFieldsTime() {
		int id = 42;
		Vehicle vehicle = null;
		ParkingSlot slot = null;
		LocalDateTime before = LocalDateTime.now().minusSeconds(1);
		
		Ticket ticket = new Ticket(id, vehicle, null, slot);
		
		assertEquals(id, ticket.getTicketID());
		assertSame(vehicle, ticket.getVehicle());
		assertSame(slot, ticket.getSlot());
		assertTrue(ticket.isActive());
		assertNotNull(ticket.getEntryTime(), "entryTime should default to curr time when passed null");
		
		
		LocalDateTime after = LocalDateTime.now().plusSeconds(1);
		assertTrue(!ticket.getEntryTime().isBefore(before) && !ticket.getEntryTime().isAfter(after), "entryTime should be roughly current time (now)");
		assertEquals(0.0, ticket.getTotalFee(), 0.0001);
	}

	//ests the calculateDuration, it should Return 0 if the times are NULL
	@Test
	void calculatingDurationRetZeroWhenNull() {
		Ticket ticket = new Ticket(1, null, null, null);
		//entryTime and exitTime are not set
		assertEquals(0, ticket.calculateDuration());
	}
	
	@Test
	void calculateDurationReturnDiffInMinutes() {
		Ticket ticket = new Ticket(1, null, LocalDateTime.now(), null);
											//year, month, day, hour, min (check again)
		LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 11, 0);
		LocalDateTime exit = LocalDateTime.of(2025, 1, 1, 11, 30); //30 min passed
		
		ticket.setEntryTime(entry);
		ticket.setExitTime(exit);
		int min = ticket.calculateDuration();
		assertEquals(30, min); //duration needs to be 30 for this ticket
	}
	
	//tests the generateFee, will use rounded hours tha the set rate per hour, which is 5 right now (can be adjusted later)
	@Test
	void generateFeeRoundHoursAndRatePerHour() {
		Ticket ticket = new Ticket(1, null, LocalDateTime.now(), null);
		
		//30 min will be rounded up to 1 hour  year, month, day, hour, min (check again)
		LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 11, 0);
		LocalDateTime exit = LocalDateTime.of(2025, 1, 1, 11, 30);
		
		ticket.setEntryTime(entry);
		ticket.setExitTime(exit);
		double fee= ticket.generateFee();
		
		//ratePerHour = 5.0, and 30 min rounded up to 1 hour, should be 5.0 as a result
		assertEquals(5.0, fee, 0.0001);
		assertEquals(5.0, ticket.getTotalFee(), 0.0001);
	}
	
	//tests the set exit time, inacitve ticket, and computing fee
	@Test
	void closeTicketSetExitTimeInactiveCompFee() {
		Ticket ticket = new Ticket(1, null, LocalDateTime.now(), null);
		
		//need to make suer exitTime is greater than entryTime
		LocalDateTime beforeClose = LocalDateTime.now();
		Thread.sleep(5);
		ticket.closeTicket(null);
		LocalDateTime afterClose = LocalDateTime.now();
		
		assertFalse(ticket.isActive(), "Ticket should be inactive after closeTicket");
		assertNotNull(ticket.getExitTime(), "exitTime should be set when closing ticket");
		
		//exit time needs to be btwn beforeClose and afterClose
		assertTrue(!ticket.getExitTime().isBefore(beforeClose) && !ticket.getExitTime().isAfter(afterClose));
		
		//totalFee should be >= 0 and equal to generateFee() result
		double Fee = ticket.getTotalFee();
		assertTrue(Fee >= 0);
	}
	
	@Test
	void settersGetters() {
		//setTicketID
		Ticket ticket = new Ticket(1, null, LocalDateTime.now(), null);
		ticket.setTicketID(12345);
		assertEquals(12345, ticket.getTicketID());
		
		//setVehicle
		//verify the setter/getter with null with Vehicle/ParkingSlot (Classes not complete, so test is incomplete)\
		ticket.setVehicle(null);
		assertNull(ticket.getVehicle());
		
		//setSlot
		ticket.setSlot(null);
		assertNull(ticket.getSlot());
		
		//setEntry/exitTime
		LocalDateTime entry = LocalDateTime.now().minus(2, ChronoUnit.HOURS);
		LocalDateTime exit = LocalDateTime.now();
		ticket.setEntryTime(entry);
		ticket.setExitTime(exit);
		
		assertEquals(entry, ticket.getEntryTime());
		assertEquals(exit, ticket.getExitTime());
		
		//setTotalFee
		ticket.setTotalFee(42.5);
		assertEquals(42.5, ticket.getTotalFee(), 0.0001);
		
		//setActive
		ticket.setActive(false);
		assertFalse(ticket.isActive());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
>>>>>>> 23b811087ec9c7d2f890e3a7b170e7856ea6fa3c
