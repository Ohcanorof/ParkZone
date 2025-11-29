package model;

import cli.AddNewParkingSlot;
import cli.Logout;
import cli.ShowActiveTickets;
import cli.ShowRegisteredVehicles;
import cli.ShowSlotsStatus;
import cli.ShowTicketsBySlotNumber;
import cli.ShowTicketsByVehiclePlate;
import cli.ShowTicketsHistory;

public class Admin extends User {
	
	public Admin() {
		super.actions = new Actionable[] {
				new AddNewParkingSlot(),
				new ShowSlotsStatus (),
				new ShowActiveTickets(),
				new ShowRegisteredVehicles(),
				new ShowTicketsHistory(),
				new ShowTicketsBySlotNumber(),
				new ShowTicketsByVehiclePlate(),
				new Logout ()
				
		};
	}
	
	public Admin(int ID, String firstName, String lastName, String email, String password) {
		super(ID, firstName, lastName, email, password);
		super.actions = new Actionable[] {
				new AddNewParkingSlot(),
				new ShowSlotsStatus (),
				new ShowActiveTickets(),
				new ShowRegisteredVehicles(),
				new ShowTicketsHistory(),
				new ShowTicketsBySlotNumber(),
				new ShowTicketsByVehiclePlate(),
				new Logout ()
				
		};
	}

}
