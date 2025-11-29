package cli;

import java.time.Duration;
import java.util.Scanner;

import model.Actionable;
import model.ParkingLotManager;
import model.Ticket;
import model.User;
import util.ConsoleInput;

public class VehicleExit implements Actionable {

	@Override
	public String getLabel() {
		return "Vehicle Exit";
	}

	@Override
	public void execute(Scanner s, User u) {
		int selected;
		do {
			System.out.println("Select Ticket by:");
			System.out.println("1. Vehicle Plate Number");
			System.out.println("2. Parking Slot Number");
			selected = ConsoleInput.readInt(s);
		} while (selected != 1 && selected != 2);
		
		Ticket ticket = null;
		if (selected == 1) {
			// find ticket by plate number
			System.out.print("Enter Vehicle Plate Number: ");
			String plate = ConsoleInput.readString(s);
			ticket = ParkingLotManager.findActiveTicketByPlate(plate);
			} else if (selected == 2) {
			// find ticket by slot number
			System.out.print("Enter Parking Slot Number: ");
			int slotNum = ConsoleInput.readInt(s);
			ticket = ParkingLotManager.findActiveTicketBySlot(slotNum);
			}
		if (ticket == null) {
			System.out.println("Invalid input");
			return;
		}else if (ticket.getExitTime() != null){
			System.out.println("Vehicle exited before");
			return;
		}
		ticket.exitVehicle();
		System.out.println("Vehicle exited ");
		System.out.println("\t-----------------------------");
		System.out.println("\tEntry Date: "+ticket. getEntryDate()) ;
		System.out.println("\tEntry Time: "+ticket.getEntryTimeToString()) ;
		System.out.println("\tExit Date: "+ticket.getExitDate());
		System.out.println("\tExit Time: "+ticket.getExitTimeToString());
		System.out.println("\tDuration in Minutes: " + 
				Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toMinutes());
		System.out.println("\tTotal Fee " + "$" + ticket.getTotalFee());		
	}
		
		
		
		
	@Override
	public boolean isAdminOnly() {
		return false;
	}

}
