package model;

import java.io.Serializable;
public class ParkingSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int SlotID;
	private boolean isOccupied;
	private Vehicle vehicle;
	private VehicleType allowedType;
	private double hourlyRate;
	private boolean outOfService; //for admin control

	//constructor
	public ParkingSlot(){
	}

	public ParkingSlot(int slotID) {
		this.SlotID = slotID;
        this.isOccupied = false;
        this.vehicle = null;
        this.allowedType = null;   // ANY typw by default
        this.hourlyRate = 0.0;
	}

	//---------------------------------------------------
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
	
	//------------------------------------------------
	//getters
	public int getSlotID() {
		return SlotID;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}
	
	public VehicleType getAllowedType() {
		 return allowedType; 
	}
	 
	public double getHourlyRate() {
		return hourlyRate;
	}

	public boolean isOccupied() {
		return isOccupied;
	}
	
	public boolean isOutOfService() {
        return outOfService;
    }
	
	//-------------------------------------------------------
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

	public void setAllowedType(VehicleType allowedType) {
        this.allowedType = allowedType;
    }
	
	public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
	
	    public void setOutOfService(boolean outOfService) {
        this.outOfService = outOfService;
    }
	    
	//-----------------------------------------------------
	//helper
	//tostring()
	@Override
	public String toString() {
		return "ParkingSlot{" +
                "slotID=" + SlotID +
                ", isOccupied=" + isOccupied +
                ", allowedType=" + (allowedType != null ? allowedType : "ANY") +
                ", hourlyRate=" + hourlyRate +
                ", vehicle=" + (vehicle != null ? vehicle.getPlateNumber() : "none") +
                '}';
	}
}




