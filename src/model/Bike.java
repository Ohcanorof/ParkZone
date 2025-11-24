package model;

import java.time.Duration;

public class Bike extends Vehicle {

	@Override
	public double calculateFee(Duration duration) {
		return 0;
	}

}
