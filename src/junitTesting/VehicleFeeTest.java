package junitTesting;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import model.*;  // make sure your model classes are on the classpath

/**
 * Tests for vehicle parking fees based on VehicleType and duration.
 *
 * Each test focuses on ONE vehicle type and ONE scenario,
 * so we are clear on what's being checked
 * 
 * note: so far everything works
 */
public class VehicleFeeTest {

    // Helper to avoid copy/paste of Duration.ofMinutes(...)
    private double feeForMinutes(Vehicle v, int minutes) {
        Duration d = Duration.ofMinutes(minutes);
        return v.calculateFee(d);
    }

    @Test
    public void testCarFee_90Minutes() {
        Vehicle car = new Car("ABC123", "Toyota", "Camry", Colors.BLUE);
        double fee = feeForMinutes(car, 90); // 1.5 hours

        // CAR: $5 per hour → 1.5 * 5 = 7.5
        assertEquals(7.5, fee, 0.0001);
    }

    @Test
    public void testMotorcycleFee_45Minutes() {
        Vehicle bike = new Bike("MOTO1", "Honda", "CBR", Colors.RED);
        double fee = feeForMinutes(bike, 45); // 0.75 hours

        // MOTORCYCLE: $2 per 30 min → $4 per hour
        // 0.75 * 4 = 3.0
        assertEquals(3.0, fee, 0.0001);
    }

    @Test
    public void testScooterFee_30Minutes() {
        Vehicle scooter = new Scooter("SCOOT1", "Xiaomi", "M365", Colors.BLACK);
        double fee = feeForMinutes(scooter, 30);

        // SCOOTER: $2 per 30 min → 2.0 for 30 minutes
        assertEquals(2.0, fee, 0.0001);
    }

    @Test
    public void testBusFee_FlatVisit() {
        Vehicle bus = new Bus("BUS99", "Mercedes", "Coach", Colors.WHITE);
        double shortVisit = feeForMinutes(bus, 10);
        double longVisit = feeForMinutes(bus, 180);

        // BUS: always $25 per visit
        assertEquals(25.0, shortVisit, 0.0001);
        assertEquals(25.0, longVisit, 0.0001);
    }

    @Test
    public void testTruckFee_2Hours() {
        Vehicle truck = new Truck("TRK1", "Ford", "F150", Colors.GREEN);
        double fee = feeForMinutes(truck, 120);

        // TRUCK: $10 per hour → 2 * 10 = 20
        assertEquals(20.0, fee, 0.0001);
    }

    @Test
    public void testVanFee_45Minutes() {
        Vehicle van = new Van("VAN1", "Honda", "Odyssey", Colors.SILVER);
        double fee = feeForMinutes(van, 45); // 0.75 hours

        // VAN: $7 per hour → 7 * 0.75 = 5.25
        assertEquals(5.25, fee, 0.0001);
    }

    @Test
    public void testCompactFee_3Hours() {
        Vehicle compact = new Compact("CMP1", "Honda", "Civic", Colors.GOLDEN);
        double fee = feeForMinutes(compact, 180);

        // COMPACT: $4 per hour → 4 * 3 = 12
        assertEquals(12.0, fee, 0.0001);
    }

    @Test
    public void testSuvFee_150Minutes() {
        Vehicle suv = new SUV("SUV1", "Toyota", "RAV4", Colors.BLACK);
        double fee = feeForMinutes(suv, 150); // 2.5 hours

        // SUV: $6 per hour → 6 * 2.5 = 15
        assertEquals(15.0, fee, 0.0001);
    }

    @Test
    public void testEvFee_60Minutes() {
        Vehicle ev = new EV("EV1", "Tesla", "Model 3", Colors.WHITE);
        double fee = feeForMinutes(ev, 60);

        // EV: $6 per hour + $2 charging fee → 6*1 + 2 = 8
        assertEquals(8.0, fee, 0.0001);
    }

    @Test
    public void testEvFee_30Minutes() {
        Vehicle ev = new EV("EV2", "Nissan", "Leaf", Colors.BLUE);
        double fee = feeForMinutes(ev, 30);

        // EV: 0.5 hours → 6*0.5 + 2 = 5
        assertEquals(5.0, fee, 0.0001);
    }
}

