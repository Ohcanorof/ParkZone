package junitTesting;

import model.ParkingSlot;
import model.Vehicle;
import model.Color;
import model.VehicleType;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


class ParkingSlotTest {

	//vehicle for testing
	private static class TestVehicle extends Vehicle {
		@Override
		public double calculateFee(long minutesParked) {
			return 0;
		}
	}
	
	@Test
	void noArgConstructorCreatesEmptySlot() {
		ParkingSlot slot = new ParkingSlot();
		
		//test
		assertEquals(0, slot.getSlotID(), "Default SlotID should be 0");
		assertFalse(slot.isOccupied(), "New slot should not be occupied");
		assertNull(slot.getVehicle(), "New slot should not have a vehicle");
	}
	
	@Test 
	void idConstructor_setsIdAndLeavesSlotEmpty(){
		ParkingSlot slot = new ParkingSlot(5);
		
		assertEquals(5, slot.getSlotID());
		assertFalse(slot.isOccupied());
		assertNull(slot.getVehicle());
	}
	
	@Test
	void setVehicle_marksSlotAsOccupiedAndStoresVehicle() {
		ParkingSlot slot = new ParkingSlot(10);
		
		//made a car
		TestVehicle car = new TestVehicle();
		car.setPlateNumber("ABC123");
		car.setBramd("Toyota");
		car.setModel("Camry");
		car.setColor(Color.BLACK);
		car.setType(VehicleType.CAR);
		
		//put it in slot
		slot.setVehicle(car);
		slot.setOccupied(true);
		
		assertTrue(slot.isOccupied());
		assertNotNull(slot.getVehicle());
		assertEquals("ABC123", slot.getVehicle().getPlateNumber());
		
		String s= slot.toString();
		assertTrue(s.contains("slotID=10"));
		assertTrue(s.contains("vehicle=ABD123"));
		
	}
	
	@Test
	void clearingSlotResetsVehicleAndOccupiedFlag() {
		ParkingSlot slot = new ParkingSlot(1);
		TestVehicle car = new TestVehicle();
		
		slot.setVehicle(car);
		slot.setOccupied(true);
		//use methods for it
		slot.
	}
	
	
	
	
	
	

}
