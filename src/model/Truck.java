package model;

import java.time.Duration;

public class Truck extends Vehicle{

	// trucks will pay $10 per hour
	@Override
	public double calculateFee(Duration duration) {
		long minutes = duration.toMinutes();
		return Math.ceil (minutes/60) * 10; 
	}

}
