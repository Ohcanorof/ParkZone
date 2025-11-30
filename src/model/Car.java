package model;

import java.time.Duration;

public class Car extends Vehicle {

<<<<<<< HEAD
    public Car() {
        super();
        // Mark this vehicle as a CAR type
        setType(VehicleType.CAR);
    }

    public Car(String plateNumber, String brand, String model, Colors color) {
        // Pass the type directly to the Vehicle constructor
        super(plateNumber, brand, model, color, VehicleType.CAR);
    }

    @Override
    public double calculateFee(Duration duration) {
        // Use the shared pricing logic in Vehicle
        return super.calculateFee(duration);
    }
=======
	@Override
	public double calculateFee(Duration duration) {
		// TODO Auto-generated method stub
		return 0;
	}

>>>>>>> 23b811087ec9c7d2f890e3a7b170e7856ea6fa3c
}
