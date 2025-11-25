package model;

import java.time.Duration;

/**
 * Car vehicle type
 * Inherits calculateFee logic from Vehicle parent class
 */
public class Car extends Vehicle {
	
	public Car() {
		super();
		setType(VehicleType.CAR);
	}
	
	public Car(String plateNumber, String brand, String model, Color color) {
		super(plateNumber, brand, model, color, VehicleType.CAR);
	}

	@Override
	public double calculateFee(Duration duration) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// Inherits both calculateFee(int) and calculateFee(Duration) from Vehicle
	// No override needed - Vehicle.calculateFee handles CAR type via switch statement
}