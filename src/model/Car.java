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
	
	public Car(String plateNumber, String brand, String model, Colors color) {
		super(plateNumber, brand, model, color, VehicleType.CAR);
	}
	
	@Override
	public double calculateFee(Duration duration) {
		// Use the shared pricing logic in Vehicle
		return super.calculateFee(duration);
	}
	
	// Inherits both calculateFee(int) and calculateFee(Duration) from Vehicle
}
