package cli;

import java.util.Scanner;

import model.Actionable;
import model.ParkingLotManager;

import model.User;
import model.Vehicle;
import storage.DataManager;

public class ShowRegisteredVehicles implements Actionable{

	@Override
	public String getLabel() {
		return "Show Registered Vehicles";
	}

	@Override
	public void execute(Scanner s, User u) {
		for (int i=0; i < DataManager.registeredVehicles.size(); i++) {
			Vehicle v = DataManager.registeredVehicles.get(i);
			User user = ParkingLotManager.findUserByID(v.getOwnerID());
			System.out.println("\tVehicle Plate Number: " + v.getPlateNumber());
			System.out.println("\tVehicle Brand: " + v.getBrand());
			System.out.println("\tVehicle Model: " + v.getModel());
			System.out.println("\tVehicle Color: " + v.getColor());
			System.out.println("\tVehicle Owner: " + user.getFullName());
			System.out.println("\t--------------------------------------------------------------------");
		}
		
	}

	@Override
	public boolean isAdminOnly() {
		// TODO Auto-generated method stub
		return false;
	}

}
