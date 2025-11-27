package model;

import java.time.Duration;

public class Bicycle extends Vehicle {

	// free of charge for bicycles
	@Override
	public double calculateFee(Duration duration) {
		return 0;
	}

}
