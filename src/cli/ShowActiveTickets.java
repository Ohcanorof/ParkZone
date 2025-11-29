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
			// moved the function toString 
			System.out.println(ticket.toString());
			
		}
		
	}

	@Override
	public boolean isAdminOnly() {
		return false;
	}


}
