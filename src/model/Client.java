package model;

import cli.Logout;
import cli.ShowSlotsStatus;
import cli.VehicleEntry;
import cli.VehicleExit;

public class Client extends User {
	
	public Client() {
		super.actions = new Actionable[] {
				new VehicleEntry(),
				new VehicleExit(),
				new ShowSlotsStatus (),
				new Logout ()
		};
	}
	
	public Client(int ID, String firstName, String lastName, String email, String password) {
		super(ID, firstName, lastName, email, password);
		super.actions = new Actionable[] {
				new VehicleEntry(),
				new VehicleExit(),
				new ShowSlotsStatus (),
				new Logout ()
				
		};
	
	}

}
