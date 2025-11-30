package model;

import java.time.Duration;

public class Bus extends Vehicle {

    public Bus() {
        super();
        setType(VehicleType.BUS);
    }

    public Bus(String plateNumber, String brand, String model, Colors color) {
        super(plateNumber, brand, model, color, VehicleType.BUS);
    }

    @Override
    public double calculateFee(Duration duration) {
        // BUS: flat $25 per visit (already handled in Vehicle)
        return super.calculateFee(duration);
    }
}

