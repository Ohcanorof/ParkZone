package junitTesting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.ParkingSlot;
import model.ParkingSystem;
import model.SlotCatalog;
import model.VehicleType;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SlotCatalogTest {

    private ParkingSystem system;
    private SlotCatalog catalog;
    private ParkingSlot carSlot;
    private ParkingSlot evSlot;

    @BeforeEach
    void setUp() {
        system = ParkingSystem.getInstance();
        system.getSlots().clear();
        catalog = new SlotCatalog(system);

        carSlot = new ParkingSlot(1, VehicleType.CAR, 2.50);
        evSlot = new ParkingSlot(2, VehicleType.EV, 3.00);

        system.addSlot(carSlot);
        system.addSlot(evSlot);
    }

    @Test
    @DisplayName("findAvailable returns only unoccupied slots of given type")
    void findAvailable_returnsOnlyUnoccupiedSlotsOfGivenType() {
        List<ParkingSlot> carSlots = catalog.findAvailable(VehicleType.CAR);
        assertTrue(carSlots.contains(carSlot));
        assertFalse(carSlots.contains(evSlot));
    }

    @Test
    @DisplayName("findAvailable returns empty list when none available")
    void findAvailable_whenNone_returnsEmptyList() {
        carSlot.setOccupied(true);
        evSlot.setOccupied(true);

        List<ParkingSlot> carSlots = catalog.findAvailable(VehicleType.CAR);
        assertTrue(carSlots.isEmpty());
    }
}

