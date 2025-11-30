package junitTesting;

import model.Ticket;
import model.ParkingSlot;
import model.Vehicle;
import model.VehicleType;
import model.Colors;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TicketTest {

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

