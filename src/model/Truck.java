package model;

import java.time.Duration;

public class Truck extends Vehicle {

    public Truck() {
        super();
        setType(VehicleType.TRUCK);
    }

    public Truck(String plateNumber, String brand, String model, Colors color) {
        super(plateNumber, brand, model, color, VehicleType.TRUCK);
    }

    @Override
    public double calculateFee(Duration duration) {
        // TRUCK: $10 per hour (handled in Vehicle)
        return super.calculateFee(duration);
    }
}
