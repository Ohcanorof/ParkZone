package model;

public class ParkingSlot {

  private int SlotID;
  private boolean isOccupied;
  private Vehicle vehicle;

  //constructor
  public ParkingSlot(){
  }

  //class methods
  public void assignVehicle(Vehicle v){
    this.vehicle = v;
    this.isOccupied = true;
  }

  public void removeVehicle(){
    this.vehicle = null;
    this.isOccupied = false;
  }

  //getters

  public int getSlotID() {
    return slotID;
  }

  public int getVehicle() {
    return vehicle;
  }

  public boolean isOccupied() {
    return isOccupied;
  }
  
  //setters

  public void setSlotID(int id) {
    this.slotID = id;
  }

  public void setVehicle (Vehicle v) {
    this.vehicle = v;
  }

  public void setOccupied(boolean b) {
    this.isOccupied = b;
  }

}


  
}
