package model;

import java.time.Duration;

public class Bike extends Vehicle {

	// bikes will be charged $2 per 30min
	@Override
	public double calculateFee(Duration duration) {
		long minutes = duration.toMinutes();
		return Math.ceil (minutes/30) *2; 
	}

}
