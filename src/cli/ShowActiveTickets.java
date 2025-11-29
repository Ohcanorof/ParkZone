package cli;

import java.util.Scanner;

import model.Actionable;
import model.ParkingLotManager;
import model.Ticket;
import model.User;
import storage.DataManager;


public class ShowActiveTickets implements Actionable {

	@Override
	public String getLabel() {
		return "Show Active Ticket";
	}

	@Override
	public void execute(Scanner s, User u) {
		for (int i=0; i < DataManager.activeTickets.size(); i++) {
			Ticket ticket = DataManager.activeTickets.get(i);
			User user = ParkingLotManager.findUserByID(ticket.getVehicle().getOwnerID());

			System.out.println("\tTicket ID: " + ticket.getID());
			System.out.println("\tVehicle Plate Number: " + ticket.getVehicle().getPlateNumber()) ;
			System.out.println("\tVehicle Brand: " + ticket.getVehicle().getBrand());
			System.out.println("\tVehicle Model: " + ticket.getVehicle().getModel());
			System.out.println("\tVehicle Color: " + ticket.getVehicle().getColor()) ;
			System.out.println("\tVehicle Owner: " + user.getFullName());
			System.out.println("\tEntry Date: " + ticket. getEntryDate()) ;
			System.out.println("\tEntry Time: " + ticket.getEntryTimeToString()) ;
			System.out.println("\tSlot Number: "+ ticket. getSlotNumber());
			System.out.println("\t--------------------------------------------------------------------");
		}
		
	}

	@Override
	public boolean isAdminOnly() {
		return false;
	}


}
