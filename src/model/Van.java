package model;

import java.time.Duration;

public class Van extends Vehicle{

	@Override
	public double calculateFee(Duration duration) {
		return 0;
	}

}
