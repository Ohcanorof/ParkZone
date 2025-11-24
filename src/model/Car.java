package model;

import java.time.Duration;

public class Car extends Vehicle {

	@Override
	public double calculateFee(Duration duration) {
		return 0;
	}

}
