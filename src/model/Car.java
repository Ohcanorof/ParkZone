package model;

import java.time.Duration;

public class Car extends Vehicle {

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
}
