package model;

import java.time.Duration;

/**
 * Bike (Motorcycle) vehicle type
 * Inherits calculateFee logic from Vehicle parent class
 */
public class Bike extends Vehicle {
	
	public Bike() {
		super();
		setType(VehicleType.MOTORCYCLE);
	}
	
	public Bike(String plateNumber, String brand, String model, Colors color) {
		super(plateNumber, brand, model, color, VehicleType.MOTORCYCLE);
	}

    public Bike() {
        super();
        // Treat Bike as a MOTORCYCLE in VehicleType 
        setType(VehicleType.MOTORCYCLE); //note:we might be better if we rename this to motorcycle instead
    }

    public Bike(String plateNumber, String brand, String model, Colors color) {
        super(plateNumber, brand, model, color, VehicleType.MOTORCYCLE);
    }

    @Override
    public double calculateFee(Duration duration) {
        // Use the shared pricing logic in Vehicle
        return super.calculateFee(duration);
    }

}
=======
	
	// Inherits both calculateFee(int) and calculateFee(Duration) from Vehicle
	// No override needed - Vehicle.calculateFee handles MOTORCYCLE type via switch statement
}

