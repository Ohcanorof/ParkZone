package model;

import storage.DataManager;

public class ParkingLotManager {
	
	// helper function to manage the system
	public Vehicle findByPlate(String plate) {
		Vehicle v = null;
		for (Vehicle vehicle : DataManager.registeredVehicles) {
			if (vehicle.getPlateNumber().toLowerCase().equals(plate.toLowerCase())) {
				v = vehicle;
				break;
			}
		}
		return v;
		
	}

	public Ticket findActiveTicketByPlate(String plate) {
		Ticket ticket = null;
		for (Ticket t : DataManager.activeTickets) {
			if (t.getVehicle().getPlateNumber().toLowerCase().equals(plate.toLowerCase())) {
				ticket = t;
				break;
			}
		}
		return ticket;
	}
	
	public ParkingSlot findAvailableSlot(Vehicle vehicle) {
		SlotType[] carTypes = new SlotType[] {SlotType.COMPACT, SlotType.LARGE};
		SlotType[] bikeTypes = new SlotType[] {SlotType.BIKE};
		SlotType[] schooterTypes = new SlotType[] {SlotType.BIKE};
		SlotType[] bicycleTypes = new SlotType[] {SlotType.BICYCLE};
		SlotType[] vanTypes = new SlotType[] {SlotType.LARGE};
		SlotType[] truckTypes = new SlotType[] {SlotType.LARGE};
		SlotType[] busTypes = new SlotType[] {SlotType.LARGE};
		SlotType[] evTypes = new SlotType[] {SlotType.ELECTRIC, SlotType.COMPACT, SlotType.LARGE};

	}
	
	
	
	
	
}
