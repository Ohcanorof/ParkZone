package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
	
	private int ID;
	private Vehicle vehicle;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;
	private double totalFee;
	private int slotNumber;
	
	public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // "day/month/year" eg. 12/11/2025
	public static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm"); 		// "hour:min" eg. 1:15

	
	
	public Ticket() {
		
	}
	
	// this constructor will be used on vehicle entry
	// the ID should be from IDGenerator
	
	public Ticket(Vehicle vehicle, int slotNumber) {
		this.ID = IDGenerator.getNextTicketID();
		this.vehicle = vehicle;
		this.entryTime = LocalDateTime.now(); // will display current time
		exitTime = null;
		totalFee = 0;
		this.slotNumber = slotNumber;
	 	
	}
	
	

	public Ticket(int ID, Vehicle vehicle, LocalDateTime entryTime, LocalDateTime exitTime, double totalFee, int slotNumber) {
		this.ID = ID;
		this.vehicle = vehicle;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.totalFee = totalFee;
		this.slotNumber = slotNumber;
		
	}
	
	//Getters
	public int getID() {
		return ID;
	}
	
	public Vehicle getVehicle() {
		return vehicle;
	}
	
	public LocalDateTime getEntryTime() {
		return entryTime;
	}

	public LocalDateTime getExitTime() {
		return exitTime;
	}
	
	public String getEntryDate() {
		return dateFormat.format(entryTime);
	}
	
	public String getEntryTimeToString() {
		return timeFormat.format(entryTime);
	}
	
	public String getExitDate() {
		return dateFormat.format(exitTime);
	}
	
	public String getExitTimeToString() {
		return timeFormat.format(exitTime);
	}
	
	public double getTotalFee() {
		return totalFee;
	}
	
	public int getSlotNumber() {
		return slotNumber;
	}
	
	//Setters
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
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
	
	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;    
	}
	
	public void exitVehicle () {
		this.exitTime = LocalDateTime.now();
		totalFee = vehicle.calculateFee(Duration.between(entryTime, exitTime));
	}
	
	@Override
	public String toString() {
		String output = "/tTicket ID: " + getID() + "\n"
		+ "\tVehicle Plate Number: " + getVehicle().getPlateNumber() + "\n"
		+ "\tVehicle Brand: " + getVehicle().getBrand() + "\n"
		+ "\tVehicle Model: " + getVehicle().getModel()+"\n"
		+ "\tVehicle Color: " + getVehicle().getColor()+"\n"
		+ "\tVehicle Owner: " + ParkingLotManager.findUserByID(getVehicle().getOwnerID()).getFullName() + "\n"
		+ "\tEntry Date: " + getEntryDate()+"\n"
		+ "\tEntry Time: " + getEntryTimeToString()+"\n"
		+ "\tSlot Number: "+ getSlotNumber() + "\n"; //if it's active print only this

		
		if (exitTime == null) {
			return output + "/t------------------------------------------------";
		}else {
			return output //else history
			+ "\tExit Date: "+ getExitDate()+"\n"
			+ "\tExit Time: "+ getExitTimeToString()+"\n"
			+ "\tDuration in Minutes: " +
					Duration.between(getEntryTime(), getExitTime()).toMinutes()+"\n"
			+ "\tTotal Fee " + "$" + getTotalFee() + "\n"
			+ "/t------------------------------------------------";
		}
	}
	
	
	
	
}
