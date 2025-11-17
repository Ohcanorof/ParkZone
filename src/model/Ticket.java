package model;

import java.time.LocalDateTime;
import java.time.Duration;


public class Ticket {
  private int ticketID;
  private double totalFee;
  private ParkingSlot slot;
  private boolean isActive;
  private Vehicle vehicle;
  private LocalDateTime entryTime;
  private LocalDateTime exitTime;


  //constructor
  public Ticket(int ticketID, Vehicle vehicle, LocalDateTime entryTime, ParkingSlot slot){
    this.ticketID = ticketID;
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

  //class functions
  public void closeTicket(LocalDateTime exitTime) {
    if (isActive = false){
      return;//ticket closed
    }
    
    if(exitTime == null) {
    	exitTime = LocalDateTime.now();
    }
    this.exitTime= exitTime;
    this.isActive = false;
    this.totalFee = generateFee();
  }
  
    

  public int calculateDuration() {
	  if(entryTime== null || exitTime == null) {
		  return 0;
	  }
	  long minutes = Duration.between(entryTime, exitTime).toMinutes();
	  return (int) Math.max(0, minutes);
  }
  
  public double generateFee() {
	  //rate for hour adjust later?
	  double ratePerHour = 5.0;
	  int minutes = calculateDuration();
	  int roundedHours = (minutes + 59)/60; //round up
	  
	  totalFee = Math.max(0, roundedHours * ratePerHour);
	  return totalFee;
  }
  
  //getters
  public int getTicketID(){
    return ticketID;
  }
  
  public Vehicle getVehicle(){
    return vehicle;
  }
  
  public ParkingSlot getSlot(){
    return slot;
  }
  
  public LocalDateTime getEntryTime(){
    return entryTime;
  }
  
  public LocalDateTime getExitTime(){
    return exitTime;
  }

  public double getTotalFee(){
    return totalFee;
  }

  public boolean isActive(){
    return isActive;
  }
  

  
  //setters
  public void setTicketID(int ticketID){
    this.ticketID = ticketID;
  }

  public void setVehicle(Vehicle vehicle) {
    this.vehicle = vehicle;
  }

  public void setSlot (ParkingSlot slot) {
    this.slot = slot;
  }

  public void setEntryTime (LocalDateTime entryTime) {
    this.entryTime = entryTime;
  }

  public void setExitTime (LocalDateTime exitTime) {
    this.exitTime = exitTime;
  }

  public void setTotalFee (double totalFee) {
    this.totalFee = totalFee;
  }

  public void setActive (boolean active) {
    this.isActive = active;
  }
  
  
}











