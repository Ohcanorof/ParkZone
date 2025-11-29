package model;

import storage.DataManager;

public class ParkingLotManager {
	
	// helper function to manage the system
	public static Vehicle findByPlate(String plate) {
		Vehicle v = null;
		for (Vehicle vehicle : DataManager.registeredVehicles) {
			if (vehicle.getPlateNumber().toLowerCase().equals(plate.toLowerCase())) {
				v = vehicle;
				break;
			}
		}
		return v;
		
	}

	public static Ticket findActiveTicketByPlate(String plate) {
		Ticket ticket = null;
		for (Ticket t : DataManager.activeTickets) {
			if (t.getVehicle().getPlateNumber().toLowerCase().equals(plate.toLowerCase())) {
				ticket = t;
				break;
			}
		}
		return ticket;
	}
	
	public static Ticket findActiveTicketBySlot(int slotNum) {
		Ticket ticket = null;
		for (Ticket t : DataManager.activeTickets) {
			if (t.getSlotNumber() == slotNum) {
				ticket = t;
				break;
			}
		}
		return ticket;
	}
	
	
	public static ParkingSlot findAvailableSlot(Vehicle vehicle) {
	 // if its type is the same type we search for and is available then that's it
	 SlotType[] types;
	 if (vehicle instanceof Car) {
		 types = new SlotType[] {SlotType. COMPACT, SlotType.LARGE};
	 } else if (vehicle instanceof Bike) {
		 types = new SlotType[] {SlotType.BIKE};
	 } else if (vehicle instanceof Scooter) {
		 types = new SlotType[] {SlotType.BIKE};
	 } else if (vehicle instanceof Bicycle) {
		 types = new SlotType[] {SlotType.BICYCLE};
	 } else if (vehicle instanceof EV) {
		 types = new SlotType[] {SlotType.ELECTRIC, SlotType.COMPACT, SlotType.LARGE};
	 } else {
		 types = new SlotType[] {SlotType.LARGE};
	 }
	 
	 ParkingSlot requiredSlot = null;
	 
	 outerLoop:
	 for (int i = 0; i < types.length; i++) {
		 SlotType type = types[i];
		 for (int j = 0; j < DataManager.parkingSlots.size(); j++) {
			 ParkingSlot slot = DataManager.parkingSlots.get(j);
			 if (slot.getType().equals(type) && slot.isAvailable()) {
				 requiredSlot = slot;
				 break outerLoop; // this only break inner loop if we don't add outerloop after break
			 }
		 }
	 }
	 return requiredSlot;
	}

	public static User findUserByID(int ID) {
		User user = null;
		for (User u : DataManager.users) {
			if (u.getID() == ID) {
				user = u;
				break;
			}
		}
		return user;
	
}
}
