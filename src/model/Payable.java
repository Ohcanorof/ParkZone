package model;

import java.time.Duration;

public interface Payable {
	
	public double calculateFee(Duration duration);

}
