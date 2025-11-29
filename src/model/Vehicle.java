package model;

/*
 * Vehicle will be abstract class and every Vehicle type will inherit it
 */
public abstract class Vehicle implements Payable{
	
	private String plateNumber;	// The plate number will be unique ID
	private int ownerID;
	private Color color;
	private String brand;
	private String model;
	
	
	//SETTERS
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
	
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	//GETTERS
	public String getPlateNumber() {
		return plateNumber;
	}
	
	public int getOwnerID() {
		return ownerID;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public String getModel() {
		return model;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
