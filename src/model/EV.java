package model;

import java.time.Duration;

public class EV extends Vehicle {

	// for EV it'll be $6 per hour + $2 as charging fee
	@Override
	public double calculateFee(Duration duration) {
		long minutes = duration.toMinutes();
		return (Math.ceil (minutes/60)*6) + 2; 
	}

}
