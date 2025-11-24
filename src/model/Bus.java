package model;

import java.time.Duration;

public class Bus extends Vehicle {

	// let's make bus stastic so they will pay $25 per visit
	@Override
	public double calculateFee(Duration duration) {
		return 25;
	}

}
