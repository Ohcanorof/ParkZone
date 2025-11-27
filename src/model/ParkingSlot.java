 package model;

public class ParkingSlot {
	
	private int slotNumber;
	private boolean isOccupied;
	private Vehicle vehicle;
	private SlotType type;
	
	public ParkingSlot() {
		
	}
	
	 public ParkingSlot(SlotType type) {
		// new constructor while adding new slot
		this.slotNumber = IDGenerator.getNextSlotNum(); // gets it from IDGenerator by default
		this.isOccupied = false; // is not occupied by default
		this.vehicle = null; // not occupied so vehicle is null by default
		this.type = type;
	 }
	
	 //getters
	public ParkingSlot(int slotNumber, boolean isOccupied, Vehicle vehicle, SlotType type) {
		this.slotNumber = slotNumber;
		this.isOccupied = isOccupied;
		this.vehicle = vehicle;
		this.type = type;
	}
	
	public boolean isAvailable() {
		return !isOccupied;		//NOT occupied
	}
	
	public int getSlotNumber() {
		return slotNumber;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}
	
	public SlotType getType() {
		return type;
	}
	
	//setters
	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}
	
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	
	public void occupy() {
		this.isOccupied = true;
	}
	
	public void free() {
		this.isOccupied = false;
	}
	
	
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	public void setSlotType(SlotType slot) {
		this.type = slot;
	}
	
	
}
