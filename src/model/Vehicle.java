package model;

import java.time.Duration;
import java.io.Serializable;
/*
 * Vehicle will be abstract class and every Vehicle type will inherit it
 */
public abstract class Vehicle implements Payable, Serializable{
	
	private static final long serialVersionUID = 1L;
	private String plateNumber;	// the plate number will be unique ID
	private String brand;
	private String model;
	private Colors color;
	private VehicleType type;
	
	//constructor
	public Vehicle() {
		
	}
	public Vehicle(String plateNumber, String brand, String model, Colors color, VehicleType type) {
		this.plateNumber= plateNumber;
		this.brand = brand;
		this.model = model;
		this.color = color;
		this.type = type;
	}
	
	//methods:
	public  double calculateFee(int durationMinutes) {
		//can adjust later as needed
		double ratePerHour;
		if(type == null) {
			ratePerHour = 2.0;
		}
		else {
			//can add to this later
			switch(type) {
			case MOTORCYCLE:
				ratePerHour = 1.0;
				break;
			case COMPACT:
				ratePerHour = 1.5;
				break;
			case SUV:
				ratePerHour = 2.5;
				break;
			case TRUCK:
				ratePerHour = 3.0;
				break;
			default:
				ratePerHour = 2.0;
				break;
			}
		}
		
		double hours = durationMinutes/ 60.0;
		return ratePerHour * hours;
	}
	
	// Payable interface implementation
    @Override
    public double calculateFee(Duration duration) {
        if (duration == null) {
            return 0.0;
        }
        long minutes = duration.toMinutes();
        return calculateFee((int) minutes);
    }
	
	
	//SETTERS
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
	
	public void setType(VehicleType type) {
		this.type = type;
	}
	
	public void setColor(Colors color) {
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
	
	public VehicleType getType() {
		return type;
	}

	public Colors getColor() {
		return color;
	}
	
	public String getBrand() {
		return brand;
	}
	
	public String getModel() {
		return model;
	}
	
	//vehicle toString
	@Override
	public String toString() {
		return "Vehicle{" +
                "plateNumber='" + plateNumber + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", color=" + color +
                ", type=" + type +
                '}';
	}

}
