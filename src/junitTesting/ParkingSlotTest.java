package junitTesting;

import static org.junit.jupiter.api.Assertions.*;

import model.Vehicle;
import model.Colors;
import model.VehicleType;
import org.junit.jupiter.api.Test;

class ParkingSlotTest {

    /**
     * Simple concrete Vehicle subclass for testing.
     * We do NOT override calculateFee, we just use Vehicle's own implementation.
     */
    private static class TestVehicle extends Vehicle {
        // no extra behavior; uses Vehicle's calculateFee methods
    }

    @Test
    void settersAndGettersTest() {
        TestVehicle v = new TestVehicle();

        // setting vehicle fields
        v.setPlateNumber("XYZ789");
        v.setBrand("Honda");
        v.setModel("Civic");
        v.setColor(Colors.BLUE);
        v.setType(VehicleType.CAR);

        // verify getters
        assertEquals("XYZ789", v.getPlateNumber());
        assertEquals("Honda", v.getBrand());
        assertEquals("Civic", v.getModel());
        assertEquals(Colors.BLUE, v.getColor());
        assertEquals(VehicleType.CAR, v.getType());
    }

    @Test
    void toStringContainsKeyFields() {
        TestVehicle v = new TestVehicle();

        v.setPlateNumber("XYZ789");
        v.setBrand("Honda");
        v.setModel("Civic");
        v.setColor(Colors.BLUE);
        v.setType(VehicleType.CAR);

        String s = v.toString();

        assertTrue(s.contains("plateNumber='XYZ789'"));
        assertTrue(s.contains("brand='Honda'"));
        assertTrue(s.contains("model='Civic'"));
        assertTrue(s.contains("color=BLUE"));
        assertTrue(s.contains("type=CAR"));
    }
}

