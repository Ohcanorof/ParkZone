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

	@Override
	public double calculateFee(Duration duration) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// Inherits both calculateFee(int) and calculateFee(Duration) from Vehicle
	// No override needed - Vehicle.calculateFee handles MOTORCYCLE type via switch statement
}