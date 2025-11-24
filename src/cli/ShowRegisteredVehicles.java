package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;

public class ShowRegisteredVehicles implements Actionable{

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(Scanner s, User u) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAdminOnly() {
		// TODO Auto-generated method stub
		return false;
	}

}
