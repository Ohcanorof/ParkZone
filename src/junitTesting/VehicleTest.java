package junitTesting;

import model.*;
import static org.junit.jupiter.api.Assertions.*;
<<<<<<< HEAD
=======
import model.Vehicle;
import model.Color;
import model.VehicleType;
>>>>>>> main
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.Duration;

class VehicleTest {
<<<<<<< HEAD
	
	// Test subclass since Vehicle is abstract
	private static class TestVehicle extends Vehicle {
		public TestVehicle() {
			super();
		}
		
		public TestVehicle(String plateNumber, String brand, String model, Color color, VehicleType type) {
			super(plateNumber, brand, model, color, type);
		}
	}
	
	private Vehicle vehicle;
	
	@BeforeEach
	void setUp() {
		vehicle = new TestVehicle();
	}
	
	// ========== Constructor Tests ==========
	
	@Test
	void testDefaultConstructor() {
		Vehicle v = new TestVehicle();
		assertNotNull(v);
		assertNull(v.getPlateNumber());
		assertNull(v.getBrand());
		assertNull(v.getModel());
		assertNull(v.getColor());
		assertNull(v.getType());
	}
	
	@Test
	void testParameterizedConstructor() {
		Vehicle v = new TestVehicle("ABC123", "Toyota", "Camry", Color.BLUE, VehicleType.CAR);
		
		assertEquals("ABC123", v.getPlateNumber());
		assertEquals("Toyota", v.getBrand());
		assertEquals("Camry", v.getModel());
		assertEquals(Color.BLUE, v.getColor());
		assertEquals(VehicleType.CAR, v.getType());
	}
	
	// ========== Getter/Setter Tests ==========
	
	@Test
	void testSetGetPlateNumber() {
		vehicle.setPlateNumber("XYZ789");
		assertEquals("XYZ789", vehicle.getPlateNumber());
	}
	
	@Test
	void testSetGetBrand() {
		vehicle.setBrand("Honda");
		assertEquals("Honda", vehicle.getBrand());
	}
	
	@Test
	void testSetGetModel() {
		vehicle.setModel("Civic");
		assertEquals("Civic", vehicle.getModel());
	}
	
	@Test
	void testSetGetColor() {
		vehicle.setColor(Color.RED);
		assertEquals(Color.RED, vehicle.getColor());
	}
	
	@Test
	void testSetGetType() {
		vehicle.setType(VehicleType.SUV);
		assertEquals(VehicleType.SUV, vehicle.getType());
	}
	
	// ========== calculateFee(int) Tests - Jose's Method ==========
	
	@Test
	void testCalculateFeeWithNullType() {
		vehicle.setType(null);
		double fee = vehicle.calculateFee(60); // 1 hour
		assertEquals(2.0, fee, 0.001); // Default rate $2/hour
	}
	
	@Test
	void testCalculateFeeMotorcycle() {
		vehicle.setType(VehicleType.MOTORCYCLE);
		double fee = vehicle.calculateFee(60); // 1 hour
		assertEquals(1.0, fee, 0.001); // $1/hour for motorcycle
	}
	
	@Test
	void testCalculateFeeCompact() {
		vehicle.setType(VehicleType.COMPACT);
		double fee = vehicle.calculateFee(120); // 2 hours
		assertEquals(3.0, fee, 0.001); // $1.50/hour * 2 hours
	}
	
	@Test
	void testCalculateFeeSUV() {
		vehicle.setType(VehicleType.SUV);
		double fee = vehicle.calculateFee(60); // 1 hour
		assertEquals(2.5, fee, 0.001); // $2.50/hour
	}
	
	@Test
	void testCalculateFeeTruck() {
		vehicle.setType(VehicleType.TRUCK);
		double fee = vehicle.calculateFee(60); // 1 hour
		assertEquals(3.0, fee, 0.001); // $3/hour
	}
	
	@Test
	void testCalculateFeeCarDefault() {
		vehicle.setType(VehicleType.CAR);
		double fee = vehicle.calculateFee(60); // 1 hour
		assertEquals(2.0, fee, 0.001); // Default $2/hour
	}
	
	@Test
	void testCalculateFeeZeroMinutes() {
		vehicle.setType(VehicleType.CAR);
		double fee = vehicle.calculateFee(0);
		assertEquals(0.0, fee, 0.001);
	}
	
	@Test
	void testCalculateFeeFractionalHour() {
		vehicle.setType(VehicleType.CAR);
		double fee = vehicle.calculateFee(30); // 30 minutes = 0.5 hours
		assertEquals(1.0, fee, 0.001); // $2/hour * 0.5 hours
	}
	
	@Test
	void testCalculateFeeLongDuration() {
		vehicle.setType(VehicleType.CAR);
		double fee = vehicle.calculateFee(600); // 10 hours
		assertEquals(20.0, fee, 0.001); // $2/hour * 10 hours
	}
	
	// ========== calculateFee(Duration) Tests - Interface Method ==========
	
	@Test
	void testCalculateFeeDurationOneHour() {
		vehicle.setType(VehicleType.CAR);
		Duration duration = Duration.ofHours(1);
		double fee = vehicle.calculateFee(duration);
		assertEquals(2.0, fee, 0.001);
	}
	
	@Test
	void testCalculateFeeDurationMinutes() {
		vehicle.setType(VehicleType.CAR);
		Duration duration = Duration.ofMinutes(30);
		double fee = vehicle.calculateFee(duration);
		assertEquals(1.0, fee, 0.001);
	}
	
	@Test
	void testCalculateFeeDurationNull() {
		vehicle.setType(VehicleType.CAR);
		double fee = vehicle.calculateFee((Duration) null);
		assertEquals(0.0, fee, 0.001);
	}
	
	@Test
	void testCalculateFeeDurationZero() {
		vehicle.setType(VehicleType.CAR);
		Duration duration = Duration.ZERO;
		double fee = vehicle.calculateFee(duration);
		assertEquals(0.0, fee, 0.001);
	}
	
	@Test
	void testCalculateFeeDurationMultipleHours() {
		vehicle.setType(VehicleType.TRUCK);
		Duration duration = Duration.ofHours(3);
		double fee = vehicle.calculateFee(duration);
		assertEquals(9.0, fee, 0.001); // $3/hour * 3 hours
	}
	
	@Test
	void testCalculateFeeDurationMotorcycle() {
		vehicle.setType(VehicleType.MOTORCYCLE);
		Duration duration = Duration.ofHours(2);
		double fee = vehicle.calculateFee(duration);
		assertEquals(2.0, fee, 0.001); // $1/hour * 2 hours
	}
	
	// ========== toString Tests ==========
	
	@Test
	void testToString() {
		vehicle.setPlateNumber("TEST123");
		vehicle.setBrand("Toyota");
		vehicle.setModel("Corolla");
		vehicle.setColor(Color.WHITE);
		vehicle.setType(VehicleType.CAR);
		
		String result = vehicle.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("TEST123"));
		assertTrue(result.contains("Toyota"));
		assertTrue(result.contains("Corolla"));
		assertTrue(result.contains("WHITE"));
		assertTrue(result.contains("CAR"));
	}
	
	@Test
	void testToStringWithNulls() {
		String result = vehicle.toString();
		assertNotNull(result);
		assertTrue(result.contains("Vehicle"));
	}
	
	// ========== Car Subclass Tests ==========
	
	@Test
	void testCarDefaultConstructor() {
		Car car = new Car();
		assertNotNull(car);
		assertEquals(VehicleType.CAR, car.getType());
	}
	
	@Test
	void testCarParameterizedConstructor() {
		Car car = new Car("CAR123", "Honda", "Accord", Color.BLACK);
		
		assertEquals("CAR123", car.getPlateNumber());
		assertEquals("Honda", car.getBrand());
		assertEquals("Accord", car.getModel());
		assertEquals(Color.BLACK, car.getColor());
		assertEquals(VehicleType.CAR, car.getType());
	}
	
	@Test
	void testCarCalculateFee() {
		Car car = new Car();
		double fee = car.calculateFee(60);
		assertEquals(2.0, fee, 0.001);
	}
	
	// ========== Bike Subclass Tests ==========
	
	@Test
	void testBikeDefaultConstructor() {
		Bike bike = new Bike();
		assertNotNull(bike);
		assertEquals(VehicleType.MOTORCYCLE, bike.getType());
	}
	
	@Test
	void testBikeParameterizedConstructor() {
		Bike bike = new Bike("BIKE123", "Harley", "Sportster", Color.RED);
		
		assertEquals("BIKE123", bike.getPlateNumber());
		assertEquals("Harley", bike.getBrand());
		assertEquals("Sportster", bike.getModel());
		assertEquals(Color.RED, bike.getColor());
		assertEquals(VehicleType.MOTORCYCLE, bike.getType());
	}
	
	@Test
	void testBikeCalculateFee() {
		Bike bike = new Bike();
		double fee = bike.calculateFee(60);
		assertEquals(1.0, fee, 0.001); // Motorcycle rate
	}
	
	// ========== Edge Cases ==========
	
	@Test
	void testSettersWithNull() {
		vehicle.setPlateNumber(null);
		vehicle.setBrand(null);
		vehicle.setModel(null);
		vehicle.setColor(null);
		vehicle.setType(null);
		
		assertNull(vehicle.getPlateNumber());
		assertNull(vehicle.getBrand());
		assertNull(vehicle.getModel());
		assertNull(vehicle.getColor());
		assertNull(vehicle.getType());
	}
	
	@Test
	void testAllVehicleTypes() {
		VehicleType[] types = {
			VehicleType.CAR,
			VehicleType.MOTORCYCLE,
			VehicleType.BUS,
			VehicleType.TRUCK,
			VehicleType.VAN,
			VehicleType.SCOOTER,
			VehicleType.COMPACT,
			VehicleType.SUV,
			VehicleType.EV
		};
		
		for (VehicleType type : types) {
			vehicle.setType(type);
			double fee = vehicle.calculateFee(60);
			assertTrue(fee >= 0, "Fee should be non-negative for " + type);
		}
	}
}
=======

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
        v.setColor(Color.BLUE);
        v.setType(VehicleType.CAR);
		
        assertEquals("XYZ789", v.getPlateNumber());
        assertEquals("Honda", v.getBrand());
        assertEquals("Civic", v.getModel());
        assertEquals(Color.BLUE, v.getColor());
        assertEquals(VehicleType.CAR, v.getType());
		
		
	}
	
	
	@Test
	void toStringContainsKeyFields() {
		TestVehicle v = new TestVehicle();
		
		//setting vehicle
		v.setPlateNumber("XYZ789");
        v.setBramd("Honda");
        v.setModel("Civic");
        v.setColor(Color.BLUE);
        v.setType(VehicleType.CAR);

        String s = v.toString();
        assertTrue(s.contains("plateNumber='XYZ789'"));
        assertTrue(s.contains("brand='Honda'"));
        assertTrue(s.contains("model='Civic'"));
        assertTrue(s.contains("color=BLUE"));
        assertTrue(s.contains("type=CAR"));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
>>>>>>> main
