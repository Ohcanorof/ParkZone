package model;

import java.time.Duration;

public class Van extends Vehicle{

	// Van will be charged $7 per hour
	@Override
	public double calculateFee(Duration duration) {
		long minutes = duration.toMinutes();
		return Math.ceil (minutes/60) * 7; 
	}

}
