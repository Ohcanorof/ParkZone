package junitTesting;

import model.Client;
import model.Ticket;
import model.ParkingSlot;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClientTest {

	//testing registeredvehicle as null, it should NOT affect the list of vehicles
	@Test
	void registerVehicleNull() {
		Client client = new Client();
		
		assertTrue(client.getRegisteredVehicles().isEmpty());
		client.registerVehicle(null);//should be ignored since its null
		assertTrue(client.getRegisteredVehicles().isEmpty(), "Null vehicle should not be added to registeredVehicles");
	}
	
	//tests that gettign registeredVehicles is unomdifiable
	@Test
	void getRegisteredVehicles() {
		Client client = new Client();
		
		List<?> vehicles = client.getRegisteredVehicles();
		assertThrows(UnsupportedOperationException.class, () -> {
			vehicles.add(null);
		});
	}
	
	//testing requestParking with a null slot
	@Test
	void requestParkingWithNullSlot() {
		Client client = new Client();
		
		assertThrows(IllegalArgumentException.class, () ->{
			client.requestParking((ParkingSlot) null);
		});
	}
	
	//tests adding to ticket history as null, should do nothing
	@Test
	void addToTicketHistoryNull () {
		Client client = new Client();
		
		assertTrue(client.getActiveTickets().isEmpty());
		assertTrue(client.getTicketHistory().isEmpty());
		
		client.addToTicketHistory((Ticket) null);
		assertTrue(client.getActiveTickets().isEmpty());
		assertTrue(client.getTicketHistory().isEmpty());
		
	}

}
