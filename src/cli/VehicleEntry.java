package cli;

import java.util.Scanner;

import model.Actionable;
import model.ParkingLotManager;
import model.ParkingSlot;
import model.Ticket;
import model.User;
import model.Vehicle;
import storage.DataManager;
import util.ConsoleInput;

public class VehicleEntry implements Actionable {

	@Override
	public String getLabel() {
		return "Vehicle Entry";
	}
	
	// vehicle plate num.
	@Override	
	public void execute (Scanner s, User u) {
		String selected;
		String plateNumber;
		do {
			System.out.print("Enter Vehicle Plate Number: ");
			plateNumber = ConsoleInput.readString(s);
			System. out.println("Vehicle Plate Number: "+ plateNumber);
		do {
			System.out.println("Are you sure that you want to continue? (y/n)");
			selected = ConsoleInput.readString(s).toLowerCase();
		} while (!selected.equals("y") && !selected.equals("n"));
	} while (!selected.equals("y"));
	
	Vehicle vehicle = ParkingLotManager.findByPlate(plateNumber);
	
	if (vehicle == null) {
		// if vehicle don't exit then we will add it to our registered vehicle
		vehicle = VehicleHandler.AddNewVehicle(s, u, plateNumber);
	}
	
	ParkingSlot slot = ParkingLotManager.findAvailableSlot(vehicle);
	if (slot == null) {
	 // no available slots
	 System.out.println("Sorry, no available slots");
	 System.out.println("Please try again later");
	 return;
	}
	
	//make the slot occupied and add the vehicle to it
	slot.occupy();
	slot.setVehicle(vehicle);
	
	Ticket ticket = new Ticket(vehicle, slot.getSlotNumber());
	DataManager.activeTickets.add(ticket);
	System. out.println("Vehicle entered successfully");
	System. out.println("--------------------------------");
	System. out.println("Ticket ID: " + ticket.getID());
	System. out.println("Slot Number: " + slot.getSlotNumber());
	System. out.println("Slot Type: " + slot.getType());
	System. out.println("Entry Date: " + ticket.getEntryDate());
	System. out.println("Entry Time: " + ticket.getEntryTimeToString());
	
}

	@Override
	public boolean isAdminOnly() {
		// TODO Auto-generated method stub
		return false;
	}

}
