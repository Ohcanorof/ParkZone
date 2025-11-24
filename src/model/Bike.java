package model;

import java.time.Duration;

public class Bike extends Vehicle {

	
	// for bikes and scooters we will be charging them $2 per 30 min parking time
	@Override
	public double calculateFee(Duration duration) {
		long minutes = duration.toMinutes();
		return Math.ceil(minutes/30) * 2; // # of min/30min then multiple by 2
	}

}
