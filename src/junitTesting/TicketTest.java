package junitTesting;

import model.Ticket;
import model.ParkingSlot;
import model.Vehicle;
import model.Colors;
import model.VehicleType;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private static class TestVehicle extends Vehicle {
        @Override
        public double calculateFee(long minutesParked) {
            return 0;
        }
    }

    private TestVehicle newTestVehicle(String plate) {
        TestVehicle v = new TestVehicle();
        v.setPlateNumber(plate);
        v.setBrand("TestBrand");
        v.setModel("TestModel");
        v.setColor(Colors.BLACK);
        v.setType(VehicleType.CAR);
        return v;
    }

    @Test
    void constructorWithNonNullEntryTime_usesProvidedTime() {
        TestVehicle car = newTestVehicle("ABC123");
        ParkingSlot slot = new ParkingSlot(7);
        LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 10, 0);

        Ticket ticket = new Ticket(42, car, slot, entry);

        assertEquals(42, ticket.getTicketID());
        assertEquals(car, ticket.getVehicle());
        assertEquals(slot, ticket.getSlot());
        assertEquals(entry, ticket.getEntryTime());
        assertTrue(ticket.isActive(), "New ticket should be active");
    }

    @Test
    void constructorWithNullEntryTime_usesCurrentTime() {
        TestVehicle car = newTestVehicle("ABC123");
        ParkingSlot slot = new ParkingSlot(7);

        LocalDateTime before = LocalDateTime.now();
        Ticket ticket = new Ticket(1, car, slot, null);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull(ticket.getEntryTime());
        // loose time window check
        assertFalse(ticket.getEntryTime().isBefore(before));
        assertFalse(ticket.getEntryTime().isAfter(after));
    }

    @Test
    void ticketIdCodeStartsWithPlateNumber() {
        TestVehicle car = newTestVehicle("PLATE42");
        ParkingSlot slot = new ParkingSlot(3);

        Ticket ticket = new Ticket(5, car, slot, LocalDateTime.now());

        String code = ticket.getTicketIDCode();
        assertNotNull(code);
        assertTrue(code.startsWith("PLATE42"),
                "Ticket ID code should start with plate number");
    }

    @Test
    void toStringContainsImportantFields() {
        TestVehicle car = newTestVehicle("AAA111");
        ParkingSlot slot = new ParkingSlot(9);
        LocalDateTime entry = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime exit = LocalDateTime.of(2025, 1, 1, 14, 0);

        Ticket ticket = new Ticket(99, car, slot, entry);
        ticket.setExitTime(exit);
        ticket.setTotalFee(20.0);
        ticket.setActive(false);

        String s = ticket.toString();
        assertTrue(s.contains("ticketID=99"));
        assertTrue(s.contains("vehicle=AAA111"));
        assertTrue(s.contains("slot=9"));
        assertTrue(s.contains("entryTime="));
        assertTrue(s.contains("exitTime="));
        assertTrue(s.contains("totalFee=20.0"));
        assertTrue(s.contains("isActive=false"));
    }
}