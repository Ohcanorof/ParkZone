package model;

import java.time.Duration;

public class EV extends Vehicle {

	// for electric vehicles lets make it $6 per hour and an extra $2 as charging free
	@Override
	public double calculateFee(Duration duration) {
		long minutes = duration.toMinutes();
		return Math.ceil(minutes/60 * 6) + 2 ;	// $6/h + $2 charing fee
	}

}
