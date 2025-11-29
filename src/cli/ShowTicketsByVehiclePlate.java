package cli;

import java.util.Scanner;

import model.Actionable;
import model.Ticket;
import model.User;
import storage.DataManager;
import util.ConsoleInput;

public class ShowTicketsByVehiclePlate implements Actionable {

	@Override
	public String getLabel() {
		return "Show Tickets By Vehicle Plate";
	}

	@Override
	public void execute(Scanner s, User u) {
		System.out.println("Enter vehicle plate number:");
		String plate = ConsoleInput.readString(s);
		for (Ticket t: DataManager.activeTickets) {
			if (t.getVehicle().getPlateNumber().equals(plate)) {
				System.out.println(t.toString());
			}
		}
		
		for (Ticket t: DataManager.ticketsHistory) {
			if (t.getVehicle().getPlateNumber().equals(plate)) {
				System.out.println(t.toString());
			}
		}
		
	}

	@Override
	public boolean isAdminOnly() {
		return true;
	}

}
