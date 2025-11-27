package model;

import java.time.Duration;

public class Bus extends Vehicle {

	// buses will be charged per visit
	// $25 per visit
	@Override
	public double calculateFee(Duration duration) {
		return 25;
	}

}
