package cli;

import java.util.Scanner;

import model.Actionable;
import model.User;
import util.ConsoleInput;

public class Logout implements Actionable {

	@Override
	public String getLabel() {
		return "Logout";
	}

	@Override
	public void execute(Scanner s, User u) {
		 String selected;
		 do {
			 System.out.println("Are you sure that you want to logout? (y/n)");
			 selected = ConsoleInput.readString(s);
		 }while (!selected.equals("y") && !selected.equals("n"));
		 if (selected. equals("y")) {
			 System.out.println("Thanks for using our system");
			 System.out.println("Have a nice day " + u.getFirstName());
			 NavigationHandler. welcome(s);
		 }else {
			 NavigationHandler. showMenu (s, u);
		 }
		
	}

	@Override
	public boolean isAdminOnly() {
		// TODO Auto-generated method stub
		return false;
	}

}
