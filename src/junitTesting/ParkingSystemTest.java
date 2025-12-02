package junitTesting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Admin;
import model.Car;
import model.Client;
import model.Colors;
import model.ParkingSlot;
import model.ParkingSystem;
import model.Ticket;
import model.VehicleType;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class ParkingSystemTest {

    private ParkingSystem system;
    private Client client;
    private Car car;
    private ParkingSlot slot;

    @BeforeEach
    void setUp() {
        system = ParkingSystem.getInstance();
        // You might want a helper to clear data between tests if your singleton keeps state.
        system.getUsers().clear();
        system.getSlots().clear();
        system.getTickets().clear();

        client = new Client(/* your constructor args */);
        car = new Car("ABC123", "Toyota", "Corolla", Colors.BLUE, VehicleType.CAR);
        slot = new ParkingSlot(1, VehicleType.CAR, 2.50);
        system.addUser(client);
        system.addSlot(slot);
    }

    @Test
    @DisplayName("createAccount adds user to system")
    void createAccount_addsUserToList() {
        int before = system.getUsers().size();
        Admin admin = new Admin(/* args */);

        system.createAccount(admin);

        assertEquals(before + 1, system.getUsers().size());
        assertTrue(system.getUsers().contains(admin));
    }

    @Test
    @DisplayName("issueTicket assigns slot and adds ticket to system")
    void issueTicket_assignsSlotAndAddsTicket() {
        Ticket ticket = system.issueTicket(car, slot);

        assertNotNull(ticket);
        assertTrue(system.getTickets().contains(ticket));
        assertTrue(slot.isOccupied());
        assertEquals(car, slot.getVehicle());
    }

    @Test
    @DisplayName("findTicketById returns ticket if it exists")
    void findTicketById_existing_returnsTicket() {
        Ticket ticket = system.issueTicket(car, slot);

        Ticket found = system.findTicketById(ticket.getTicketID());

        assertSame(ticket, found);
    }

    @Test
    @DisplayName("findTicketById returns null if missing")
    void findTicketById_missing_returnsNull() {
        Ticket found = system.findTicketById(99999);
        assertNull(found);
    }
}

