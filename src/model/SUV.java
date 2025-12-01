package model;

import java.time.Duration;

public class SUV extends Vehicle {

    public SUV() {
        super();
        setType(VehicleType.SUV);
    }

    public SUV(String plateNumber, String brand, String model, Colors color) {
        super(plateNumber, brand, model, color, VehicleType.SUV);
    }

    @Override
    public double calculateFee(Duration duration) {
        // SUV: $6 per hour (I added just $1 more than CAR, handled in Vehicle)
        return super.calculateFee(duration);
    }
}
