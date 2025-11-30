package model;

import java.time.Duration;

public class EV extends Vehicle {

    public EV() {
        super();
        setType(VehicleType.EV);
    }

    public EV(String plateNumber, String brand, String model, Colors color) {
        super(plateNumber, brand, model, color, VehicleType.EV);
    }

    @Override
    public double calculateFee(Duration duration) {
        // EV: $6 per hour + $2 charging fee (handled in Vehicle)
        return super.calculateFee(duration);
    }
}

