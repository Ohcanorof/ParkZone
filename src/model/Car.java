package model;

import java.time.Duration;

public class Car extends Vehicle {

	// for cars lets make it $5 per hour
	@Override
	public double calculateFee(Duration duration) {
		long minutes = duration.toMinutes();
		return Math.ceil(minutes/60) * 5 ;	
		
	}

}
