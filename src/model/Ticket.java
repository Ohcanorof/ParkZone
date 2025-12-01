package model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.io.Serializable;

public class Ticket implements Serializable {
	private static final long serialVersionUID = 1L;
	private static int nextId = 1;
	private int ticketID;
	private double totalFee;
	private ParkingSlot slot;
	private boolean isActive;
	private Vehicle vehicle;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;
	//reservation attributes
	private int reservedMinutes;
	private double estimatedFee;
	//payment attributes:
	private boolean paid;
	private String paymentMethod;


	//constructor
	public Ticket(int ticketID, Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTim){
		this.ticketID = generateNextId();
	    this.vehicle = vehicle;
	    this.slot = slot;
	    this.isActive = true;
	    //might change this
	    this.entryTime = (entryTime != null) ? entryTime : LocalDateTime.now();
	    this.totalFee = 0.0;
	    this.reservedMinutes = 0;
	    this.estimatedFee = 0.0;
	    this.paid = false;
	    this.paymentMethod = null;
	}
	
	//constructor so that entryTime is defualted to curr time (now)
	public Ticket(Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime) {
		this.ticketID = generateNextId();
	    this.vehicle = vehicle;
	    this.slot = slot;
	    this.isActive = true;
	    this.entryTime = (entryTime != null) ? entryTime : LocalDateTime.now();
	    this.totalFee = 0.0;
	    this.reservedMinutes = 0;
	    this.estimatedFee = 0.0;
	    this.paid = false;
	    this.paymentMethod = null;
	}

	// class functions
	//id generation
	private static synchronized int generateNextId() {
		return nextId++;
	}
	
	//function for ticket closing
	public void closeTicket(LocalDateTime exitTime) {
		if (!isActive) {
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
	
	public int getReservedMinutes() {
		return reservedMinutes;
	}
	
	public double getEstimatedFee() {
		return estimatedFee;
	}
	
	public boolean isPaid() {
		return paid;
	}
	
	public String getPaymentMethod() {
		return paymentMethod;
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
	
	public void setReservedMinutes(int reservedMinutes) {
	    this.reservedMinutes = Math.max(reservedMinutes, 0);
	}

	public void setEstimatedFee(double estimatedFee) {
	    this.estimatedFee = Math.max(estimatedFee, 0.0);
	}

	public void setPaid(boolean paid) {
	    this.paid = paid;
	}

	public void setPaymentMethod(String paymentMethod) {
	    this.paymentMethod = paymentMethod;
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

	//helper function for payments
	public void pay(String method) {
	    // if still active and no exit time, treat payment as "leaving now"
	    if (isActive) {
	        closeTicket(LocalDateTime.now());
	    } else if (totalFee == 0.0) {
	        // if we somehow closed without computing fee
	        totalFee = generateFee();
	    }
	    this.paid = true;
	    this.paymentMethod = method;
	}
	
	//ticket toString
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
                ", estimatedFee=" + estimatedFee +
                ", isActive=" + isActive +
                ", paid=" + paid +
                ", paymentMethod=" + paymentMethod +
                '}';
	}
}
