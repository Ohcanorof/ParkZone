package junitTesting;

import model.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class ParkingSlotTest {
	
	private ParkingSlot slot;
	private Vehicle testVehicle;
	
	@BeforeEach
	void setUp() {
		slot = new ParkingSlot();
		testVehicle = new Car("TEST123", "Toyota", "Camry", Color.BLUE);
	}
	
	// ========== Constructor Tests ==========
	
	@Test
	void testDefaultConstructor() {
		ParkingSlot s = new ParkingSlot();
		assertNotNull(s);
		assertFalse(s.isOccupied());
		assertNull(s.getVehicle());
	}
	
	@Test
	void testParameterizedConstructor() {
		ParkingSlot s = new ParkingSlot(101);
		
		assertEquals(101, s.getSlotID());
		assertFalse(s.isOccupied());
		assertNull(s.getVehicle());
	}
	
	// ========== Getter/Setter Tests ==========
	
	@Test
	void testSetGetSlotID() {
		slot.setSlotID(42);
		assertEquals(42, slot.getSlotID());
	}
	
	@Test
	void testSetGetSlotIDZero() {
		slot.setSlotID(0);
		assertEquals(0, slot.getSlotID());
	}
	
	@Test
	void testSetGetSlotIDNegative() {
		slot.setSlotID(-5);
		assertEquals(-5, slot.getSlotID());
	}
	
	@Test
	void testSetGetSlotIDLarge() {
		slot.setSlotID(999999);
		assertEquals(999999, slot.getSlotID());
	}
	
	@Test
	void testIsOccupiedInitiallyFalse() {
		assertFalse(slot.isOccupied());
	}
	
	@Test
	void testSetOccupiedTrue() {
		slot.setOccupied(true);
		assertTrue(slot.isOccupied());
	}
	
	@Test
	void testSetOccupiedFalse() {
		slot.setOccupied(true);
		slot.setOccupied(false);
		assertFalse(slot.isOccupied());
	}
	
	@Test
	void testGetVehicleInitiallyNull() {
		assertNull(slot.getVehicle());
	}
	
	// ========== assignVehicle Tests ==========
	
	@Test
	void testAssignVehicle() {
		slot.assignVehicle(testVehicle);
		
		assertEquals(testVehicle, slot.getVehicle());
		assertTrue(slot.isOccupied());
	}
	
	@Test
	void testAssignVehicleNull() {
		slot.assignVehicle(null);
		
		assertNull(slot.getVehicle());
		assertFalse(slot.isOccupied());
	}
	
	@Test
	void testAssignVehicleTwice() {
		Vehicle vehicle1 = new Car("CAR1", "Honda", "Civic", Color.RED);
		Vehicle vehicle2 = new Car("CAR2", "Toyota", "Corolla", Color.BLUE);
		
		slot.assignVehicle(vehicle1);
		assertEquals(vehicle1, slot.getVehicle());
		
		slot.assignVehicle(vehicle2);
		assertEquals(vehicle2, slot.getVehicle());
		assertTrue(slot.isOccupied());
	}
	
	@Test
	void testAssignVehicleAfterNull() {
		slot.assignVehicle(null);
		assertFalse(slot.isOccupied());
		
		slot.assignVehicle(testVehicle);
		assertTrue(slot.isOccupied());
		assertEquals(testVehicle, slot.getVehicle());
	}
	
	// ========== removeVehicle Tests ==========
	
	@Test
	void testRemoveVehicle() {
		slot.assignVehicle(testVehicle);
		assertTrue(slot.isOccupied());
		
		slot.removeVehicle();
		
		assertNull(slot.getVehicle());
		assertFalse(slot.isOccupied());
	}
	
	@Test
	void testRemoveVehicleWhenEmpty() {
		assertFalse(slot.isOccupied());
		
		slot.removeVehicle();
		
		assertNull(slot.getVehicle());
		assertFalse(slot.isOccupied());
	}
	
	@Test
	void testRemoveVehicleTwice() {
		slot.assignVehicle(testVehicle);
		slot.removeVehicle();
		slot.removeVehicle();
		
		assertNull(slot.getVehicle());
		assertFalse(slot.isOccupied());
	}
	
	// ========== setVehicle Tests ==========
	
	@Test
	void testSetVehicle() {
		slot.setVehicle(testVehicle);
		
		assertEquals(testVehicle, slot.getVehicle());
		assertTrue(slot.isOccupied());
	}
	
	@Test
	void testSetVehicleNull() {
		slot.setVehicle(testVehicle);
		assertTrue(slot.isOccupied());
		
		slot.setVehicle(null);
		
		assertNull(slot.getVehicle());
		assertFalse(slot.isOccupied());
	}
	
	@Test
	void testSetVehicleSyncsOccupancy() {
		// Setting vehicle should automatically set occupied to true
		slot.setVehicle(testVehicle);
		assertTrue(slot.isOccupied());
		
		// Setting vehicle to null should set occupied to false
		slot.setVehicle(null);
		assertFalse(slot.isOccupied());
	}
	
	// ========== toString Tests ==========
	
	@Test
	void testToStringEmpty() {
		slot.setSlotID(101);
		String result = slot.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("101"));
		assertTrue(result.contains("false") || result.contains("isOccupied=false"));
		assertTrue(result.contains("none") || result.contains("null"));
	}
	
	@Test
	void testToStringWithVehicle() {
		slot.setSlotID(202);
		slot.assignVehicle(testVehicle);
		
		String result = slot.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("202"));
		assertTrue(result.contains("true") || result.contains("isOccupied=true"));
		assertTrue(result.contains("TEST123"));
	}
	
	@Test
	void testToStringNotNull() {
		String result = slot.toString();
		assertNotNull(result);
		assertTrue(result.contains("ParkingSlot"));
	}
	
	// ========== Integration/Workflow Tests ==========
	
	@Test
	void testCompleteAssignRemoveCycle() {
		slot.setSlotID(303);
		
		// Initial state
		assertFalse(slot.isOccupied());
		assertNull(slot.getVehicle());
		
		// Assign vehicle
		slot.assignVehicle(testVehicle);
		assertTrue(slot.isOccupied());
		assertEquals(testVehicle, slot.getVehicle());
		
		// Remove vehicle
		slot.removeVehicle();
		assertFalse(slot.isOccupied());
		assertNull(slot.getVehicle());
	}
	
	@Test
	void testMultipleCycles() {
		for (int i = 0; i < 5; i++) {
			slot.assignVehicle(testVehicle);
			assertTrue(slot.isOccupied());
			
			slot.removeVehicle();
			assertFalse(slot.isOccupied());
		}
	}
	
	@Test
	void testAssignDifferentVehicles() {
		Vehicle car = new Car("CAR1", "Honda", "Civic", Color.RED);
		Vehicle bike = new Bike("BIKE1", "Harley", "Sportster", Color.BLACK);
		
		slot.assignVehicle(car);
		assertEquals(car, slot.getVehicle());
		
		slot.removeVehicle();
		assertNull(slot.getVehicle());
		
		slot.assignVehicle(bike);
		assertEquals(bike, slot.getVehicle());
	}
	
	// ========== Edge Cases ==========
	
	@Test
	void testOccupancyStateConsistency() {
		// Manual occupancy flag should stay in sync with vehicle presence
		slot.setVehicle(testVehicle);
		assertTrue(slot.isOccupied());
		
		// Even if we manually set occupied to false, vehicle should still be there
		// (this tests the implementation behavior)
		slot.setOccupied(false);
		assertNotNull(slot.getVehicle()); // Vehicle still assigned
		assertFalse(slot.isOccupied()); // But occupancy flag is false
	}
	
	@Test
	void testSetOccupiedWithoutVehicle() {
		// Can set occupied flag even without vehicle (edge case behavior)
		slot.setOccupied(true);
		assertTrue(slot.isOccupied());
		assertNull(slot.getVehicle());
	}
	
	@Test
	void testMultipleSlotsDontInterfere() {
		ParkingSlot slot1 = new ParkingSlot(1);
		ParkingSlot slot2 = new ParkingSlot(2);
		
		Vehicle vehicle1 = new Car("CAR1", "Honda", "Civic", Color.RED);
		Vehicle vehicle2 = new Car("CAR2", "Toyota", "Camry", Color.BLUE);
		
		slot1.assignVehicle(vehicle1);
		slot2.assignVehicle(vehicle2);
		
		assertEquals(vehicle1, slot1.getVehicle());
		assertEquals(vehicle2, slot2.getVehicle());
		
		slot1.removeVehicle();
		assertFalse(slot1.isOccupied());
		assertTrue(slot2.isOccupied());
	}
	
	@Test
	void testSlotIDCanBeChanged() {
		slot.setSlotID(100);
		assertEquals(100, slot.getSlotID());
		
		slot.setSlotID(200);
		assertEquals(200, slot.getSlotID());
	}
	
	@Test
	void testAssignRemoveWithDifferentVehicleTypes() {
		Car car = new Car("CAR123", "Honda", "Accord", Color.WHITE);
		Bike bike = new Bike("BIKE456", "Yamaha", "R1", Color.BLUE);
		
		// Assign car
		slot.assignVehicle(car);
		assertTrue(slot.isOccupied());
		assertEquals(VehicleType.CAR, slot.getVehicle().getType());
		
		// Remove car
		slot.removeVehicle();
		assertFalse(slot.isOccupied());
		
		// Assign bike
		slot.assignVehicle(bike);
		assertTrue(slot.isOccupied());
		assertEquals(VehicleType.MOTORCYCLE, slot.getVehicle().getType());
	}
}