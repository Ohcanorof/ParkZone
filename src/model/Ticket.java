package model;

public class Ticket {
  private int ticketID;
  private double totalFee;
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
  }

  //class functions
  public void closeTicket(LocalDateTime exitTime) {
    if (isActive = false){
      return;
    }
    else{
      this.exitTime 
      this.isActive = false;
      this.totalFee = proc.calculateFee(this, hourlyRate);//might change later after testing
    }
  }
  
    
  public double generateFee(double ratePerHour){
    int minutes = calculateDuration();
    int roundedHours = (minutes + 59) / 60;
    return Math.max(0, hoursRoundedUp * ratePerHour);
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
    this.active = active;
  }
  
  
}











