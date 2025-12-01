package model;

import java.time.Duration;

public class Van extends Vehicle {

    public Van() {
        super();
        setType(VehicleType.VAN);
    }

    public Van(String plateNumber, String brand, String model, Colors color) {
        super(plateNumber, brand, model, color, VehicleType.VAN);
    }

    @Override
    public double calculateFee(Duration duration) {
        // VAN: $7 per hour (handled in Vehicle)
        return super.calculateFee(duration);
    }
}
