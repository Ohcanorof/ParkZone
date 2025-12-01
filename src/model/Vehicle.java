package model;

import java.time.Duration;
import java.io.Serializable;

public abstract class Vehicle implements Payable, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String plateNumber;
	private String brand;
	private String model;
	private Colors color;
	private VehicleType type;
	
	public Vehicle() {
		
	}
	
	public Vehicle(String plateNumber, String brand, String model, Colors color, VehicleType type) {
		this.plateNumber = plateNumber;
		this.brand = brand;
		this.model = model;
		this.color = color;
		this.type = type;
	}
	
	// Calculate fee based on duration in minutes
	public double calculateFee(int durationMinutes) {
	    if (durationMinutes <= 0) {
	        return 0.0;
	    }

	    double hours = durationMinutes / 60.0;
	    double amount;

	    if (type == null) {
	        amount = 5.0 * hours;
	    } else {
	        switch (type) {
	            case CAR:
	                amount = 5.0 * hours;
	                break;
	            case MOTORCYCLE:
	                amount = 4.0 * hours;
	                break;
	            case SCOOTER:
	                amount = 4.0 * hours;
	                break;
	            case BUS:
	                amount = 25.0;
	                break;
	            case TRUCK:
	                amount = 10.0 * hours;
	                break;
	            case VAN:
	                amount = 7.0 * hours;
	                break;
	            case COMPACT:
	                amount = 4.0 * hours;
	                break;
	            case SUV:
	                amount = 6.0 * hours;
	                break;
	            case EV:
	                amount = 6.0 * hours + 2.0;
	                break;
	            default:
	                amount = 5.0 * hours;
	                break;
	        }
	    }

	    return amount;
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
	
	// SETTERS
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
	
	// GETTERS
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