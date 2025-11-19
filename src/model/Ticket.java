package model;

import java.time.LocalDateTime;
import java.time.Duration;


public class Ticket {

	private static int nextId = 1;
	private int ticketID;
	private double totalFee;
	private ParkingSlot slot;
	private boolean isActive;
	private Vehicle vehicle;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;


	//constructor
	public Ticket(Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime){
		this.ticketID = generateNextId();
		this.vehicle = vehicle;
		this.slot = slot;
		this.isActive = true;
		if (entryTime == null){
			entryTime = LocalDateTime.now();
		}
		else{
			this.entryTime = entryTime;
		}
		this.totalFee = 0.0;
	}
	
	//constructor so that entryTime is defualted to curr time (now)
	public Ticket(Vehicle vehicle, ParkingSlot slot) {
		this(vehicle, slot, LocalDateTime.now());
	}

	// class functions
	//id generation
	private static synchronized int generateNextId() {
		return nextId++;
	}
	
	//function for ticket closing
	public void closeTicket(LocalDateTime exitTime) {
		if (isActive = false) {
			return;// ticket closed
		}
		//if an exit time is null, set it to current time, then we close
		if (exitTime == null) {
			exitTime = LocalDateTime.now();
		}
		this.exitTime = exitTime;
		this.isActive = false;
		this.totalFee = generateFee();
	}

	//calculating duration of stay 
	public int calculateDuration() {
		//change this!
		LocalDateTime end;
		if(exitTime != null) {
			end = exitTime;
		}
		else {
			end = LocalDateTime.now();
		}
		
		Duration duration = Duration.between(entryTime, end);
		return (int) duration.toMinutes();
	}

	public double generateFee() {
		// rate for hour adjust later?
		if(vehicle == null) {
			totalFee = 0.0;
			return totalFee;
		}
		int durationMinutes = calculateDuration();

		totalFee = vehicle.calculateFee(durationMinutes);
		return totalFee;
	}

	// getters
	public int getTicketID() {
		return ticketID;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public ParkingSlot getSlot() {
		return slot;
	}

	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}

	public double getTotalFee() {
		return totalFee;
	}

	public boolean isActive() {
		return isActive;
	}

	// setters
	public void setTicketID(int ticketID) {
		this.ticketID = ticketID;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public void setSlot(ParkingSlot slot) {
		this.slot = slot;
	}

	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}

	public void setExitTime(LocalDateTime exitTime) {
		this.exitTime = exitTime;
	}

	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}

	public void setActive(boolean active) {
		this.isActive = active;
	}
	
	//helper function for the plate+id ticket id format
	//returns the ticketID as plate+ ticket id, ex: plate# is 112ad12 
	///and the ticketID (ticket number, each time a ticket is made, it increments by one, so each ticket is different, even if it has the same plate number)
	// is 9, the complete TicketId would be 112ad129, 9 being the ticket number.
	public String getTicketIDCode() {
		String plate;
		if(vehicle != null && vehicle.getPlateNumber() != null) {
			plate = vehicle.getPlateNumber();
		}
		else {
			plate = "UNKNOWN";
		}
		
		return plate + ticketID;
	}

	@Override
	public String toString() {
		return "Ticket{" +
                "ticketID=" + ticketID +
                ", compositeCode='" + getTicketIDCode() + '\'' +
                ", vehicle=" + (vehicle != null ? vehicle.getPlateNumber() : "none") +
                ", slot=" + (slot != null ? slot.getSlotID() : -1) +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                ", totalFee=" + totalFee +
                ", isActive=" + isActive +
                '}';
	}
}
