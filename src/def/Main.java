package def;

import java.util.ArrayList;
import java.util.Scanner;

import cli.NavigationHandler;
import storage.DataManager;

public class Main {

	public static void main(String[] args) {
		/*
		 * where we will run our main functions 
		 */
		
		//testing delete after
		DataManager.activeTickets = new ArrayList<>();
		DataManager.parkingSlots = new ArrayList<> ();
		DataManager.registeredVehicles = new ArrayList<>();
		DataManager.ticketHistory = new ArrayList<>();
		DataManager.users = new ArrayList<>();
		NavigationHandler. welcome (new Scanner (System. in)) ;
	}

}

