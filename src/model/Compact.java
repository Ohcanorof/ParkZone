package model;

import java.time.Duration;

public class Compact extends Vehicle { // should we remove this since we have Van already it feels redundant

    public Compact() {
        super();
        setType(VehicleType.COMPACT);
    }

    public Compact(String plateNumber, String brand, String model, Colors color) {
        super(plateNumber, brand, model, color, VehicleType.COMPACT);
    }

    @Override
    public double calculateFee(Duration duration) {
        // COMPACT: $4 per hour (slightly cheaper than CAR, handled in Vehicle)
        return super.calculateFee(duration);
    }
}

