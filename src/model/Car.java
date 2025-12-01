package model;

import java.time.Duration;

public class Car extends Vehicle {

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

}
