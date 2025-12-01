package model;

import java.time.Duration;

public class Scooter extends Vehicle {

    public Scooter() {
        super();
        setType(VehicleType.SCOOTER);
    }

    public Scooter(String plateNumber, String brand, String model, Colors color) {
        super(plateNumber, brand, model, color, VehicleType.SCOOTER);
    }

    @Override
    public double calculateFee(Duration duration) {
        // SCOOTER: $2 per 30 min → $4 per hour (handled in Vehicle)
        return super.calculateFee(duration);
    }
}

