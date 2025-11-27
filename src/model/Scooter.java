package model;

import java.time.Duration;

public class Scooter extends Vehicle {

	// scooter is $2 per 30 min
	@Override
	public double calculateFee(Duration duration) {
		long minutes = duration.toMinutes();
		return Math.ceil (minutes/30) *2; 
	}
 
}
