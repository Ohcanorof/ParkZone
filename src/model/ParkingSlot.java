package model;

public class ParkingSlot {

	private int SlotID;
	private boolean isOccupied;
	private Vehicle vehicle;

	//constructor
	public ParkingSlot(){
	}

	public ParkingSlot(int slotID) {
		this.SlotID = slotID;
		this.isOccupied = false;
		this.vehicle = null;
	}

	//class methods
	public void assignVehicle(Vehicle v){
		this.vehicle = v;
		this.isOccupied = (vehicle != null);
	}

	//removes vechile from the slot and marks it as unocupied
	public void removeVehicle(){
		this.vehicle = null;
		this.isOccupied = false;
	}

	//getters
	public int getSlotID() {
		return SlotID;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public boolean isOccupied() {
		return isOccupied;
	}

	//setters

	public void setSlotID(int id) {
		this.SlotID = id;
	}

	public void setVehicle (Vehicle v) {
		this.vehicle = v;
		//helps keep isOccupied in sync with the vehicle
		this.isOccupied = (v != null);
	}

	public void setOccupied(boolean b) {
		this.isOccupied = b;
	}

	@Override
	public String toString() {
		return "ParkingSlot{" +
                "slotID=" + SlotID +
                ", isOccupied=" + isOccupied +
                ", vehicle=" + (vehicle != null ? vehicle.getPlateNumber() : "none") +
                '}';
	}
}




