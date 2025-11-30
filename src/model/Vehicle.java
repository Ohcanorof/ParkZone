package model;

import java.time.Duration;
/*
 * Vehicle will be abstract class and every Vehicle type will inherit it
 */
public abstract class Vehicle implements Payable{
	
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
<<<<<<< HEAD
	public double calculateFee(int durationMinutes) {
	    // no time parked = no charge
	    if (durationMinutes <= 0) {
	        return 0.0;
	    }

	    double hours = durationMinutes / 60.0;
	    double amount;

	    // If type is not set, treat like a regular CAR.
	    if (type == null) {
	        amount = 5.0 * hours; // same as CAR
	    } else {
	        switch (type) {
	            case CAR -> {
	                // CAR: $5 per hour
	                amount = 5.0 * hours;
	            }
	            case MOTORCYCLE -> {
	                // MOTORCYCLE: $2 per 30 min -> $4 per hour
	                amount = 4.0 * hours;
	            }
	            case SCOOTER -> {
	                // SCOOTER: $2 per 30 min -> $4 per hour
	                amount = 4.0 * hours;
	            }
	            case BUS -> {
	                // BUS: $25 per visit (flat fee)
	                amount = 25.0;
	            }
	            case TRUCK -> {
	                // TRUCK: $10 per hour
	                amount = 10.0 * hours;
	            }
	            case VAN -> {
	                // VAN: $7 per hour
	                amount = 7.0 * hours;
	            }
	            case COMPACT -> {
	                // COMPACT: reasonable, slightly cheaper than CAR -> $4 per hour
	                amount = 4.0 * hours;
	            }
	            case SUV -> {
	                // SUV: reasonable, a bit more than CAR -> $6 per hour
	                amount = 6.0 * hours;
	            }
	            case EV -> {
	                // EV: $6 per hour + $2 charging fee per visit
	                amount = 6.0 * hours + 2.0;
	            }
	            default -> {
	                // fallback: same as CAR
	                amount = 5.0 * hours;
	            }
	        }
	    }

	    return amount;
	}

=======
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
>>>>>>> 23b811087ec9c7d2f890e3a7b170e7856ea6fa3c
	
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
