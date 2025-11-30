package junitTesting;

import static org.junit.jupiter.api.Assertions.*;
import model.Vehicle;
import model.Colors;
import model.VehicleType;
import org.junit.jupiter.api.Test;

class VehicleTest {

	private static class TestVehicle extends Vehicle {
        @Override
        public double calculateFee(long minutesParked) {
            return 0.0;
        }
    }
	
	@Test
	void settersAndGettersTest() {
		TestVehicle v = new TestVehicle();
		
		//setting vehicle
		v.setPlateNumber("XYZ789");
        v.setBramd("Honda");
        v.setModel("Civic");
        v.setColor(Colors.BLUE);
        v.setType(VehicleType.CAR);
		
        assertEquals("XYZ789", v.getPlateNumber());
        assertEquals("Honda", v.getBrand());
        assertEquals("Civic", v.getModel());
        assertEquals(Colors.BLUE, v.getColor());
        assertEquals(VehicleType.CAR, v.getType());
		
		
	}
	
	
	@Test
	void toStringContainsKeyFields() {
		TestVehicle v = new TestVehicle();
		
		//setting vehicle
		v.setPlateNumber("XYZ789");
        v.setBramd("Honda");
        v.setModel("Civic");
        v.setColor(Colors.BLUE);
        v.setType(VehicleType.CAR);

        String s = v.toString();
        assertTrue(s.contains("plateNumber='XYZ789'"));
        assertTrue(s.contains("brand='Honda'"));
        assertTrue(s.contains("model='Civic'"));
        assertTrue(s.contains("color=BLUE"));
        assertTrue(s.contains("type=CAR"));
		
	}
	
	
<<<<<<< HEAD
=======
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
>>>>>>> 23b811087ec9c7d2f890e3a7b170e7856ea6fa3c

}
