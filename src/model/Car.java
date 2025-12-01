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

	public Car(String plateNumber, String brand, String model, Colors color, VehicleType type) {
        super();                    // calls Vehicle()
        setPlateNumber(plateNumber);
        setBrand(brand);
        setModel(model);
        setColor(color);
        setType(type);
    }
	
	@Override
	public double calculateFee(Duration duration) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// Inherits both calculateFee(int) and calculateFee(Duration) from Vehicle
	// No override needed - Vehicle.calculateFee handles CAR type via switch statement
}