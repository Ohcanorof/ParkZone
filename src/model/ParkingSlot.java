package model;

public class ParkingSlot {
	
	private int slotNumber;
	private boolean isOccupied;
	private Vehicle vehicle;
	private SlotType type;
	
	public ParkingSlot() {
		
	}
	
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
	
	
	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}
	
	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	public void setSlotType(SlotType slot) {
		this.type = slot;
	}
	
	
}
//
