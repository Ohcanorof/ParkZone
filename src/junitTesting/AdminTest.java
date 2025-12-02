package junitTesting;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.Admin;
import model.ParkingSlot;
import model.VehicleType;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    private Admin admin;
    private ParkingSlot slot;

    @BeforeEach
    void setUp() {
        admin = new Admin(/* your constructor args here */);
        slot = new ParkingSlot(1, VehicleType.CAR, 2.50);
    }

    @Test
    @DisplayName("addParkingSlot adds slot to managed list")
    void addParkingSlot_addsToManaged() {
        admin.addParkingSlot(slot);

        assertTrue(admin.getManagedSlots().contains(slot));
    }

    @Test
    @DisplayName("removeParkingSlot removes existing slot and returns true")
    void removeParkingSlot_existingId_removesFromManaged() {
        admin.addParkingSlot(slot);
        boolean removed = admin.removeParkingSlot(slot.getSlotID());

        assertTrue(removed);
        assertFalse(admin.getManagedSlots().contains(slot));
    }

    @Test
    @DisplayName("markSpaceOutOfService sets flag on slot")
    void markSpaceOutOfService_setsFlagOnSlot() {
        admin.addParkingSlot(slot);

        admin.markSpaceOutOfService(slot.getSlotID());

        assertTrue(slot.isOutOfService());
    }

    @Test
    @DisplayName("returnSpaceToService clears out-of-service flag")
    void returnSpaceToService_clearsFlag() {
        admin.addParkingSlot(slot);
        admin.markSpaceOutOfService(slot.getSlotID());

        admin.returnSpaceToService(slot.getSlotID());

        assertFalse(slot.isOutOfService());
    }
}

