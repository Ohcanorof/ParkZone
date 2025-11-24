package cli;

import java.util.Scanner;

import model.Admin;
import model.Client;
import model.IDGenerator;
import model.User;
import storage.DataManager;
import util.ConsoleInput;

public class AuthHandler {
	
	public static void login (Scanner s) { 
			System.out.print("Email: ");
			String email = ConsoleInput.readString(s);
			System.out.print("Password: ");
			String password = ConsoleInput.readString(s);
	
			User user = null;
			for (User u : DataManager.users) {
				if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
					user = u;
					break;
				}
			}
			
			if (user != null) {
				System.out.println("Logged in successfull");
				System.out.println("Welcome back " + user.getFirstName());
				NavigationHandler.showMenu (s, user);
			} else {
				System.out.println("Incorrect email or password");
				System.out.println("Please try again");
				login(s); 
			}
	}
	
	public static void createNewAccount(Scanner s) {
			 System.out.print("First Name: ");
			 String firstName = ConsoleInput.readString(s);
			 System.out.print("Last Name: ");
			 String lastName = ConsoleInput.readString(s);
			 System.out.print("Email: ");
			 String email = ConsoleInput.readString(s);
			 String password, confirmPassword;
			 do {
			 System.out.print("Password: ");
			 password = ConsoleInput.readString(s);
			 System.out.print("Confirm Password: ");
			 confirmPassword = ConsoleInput.readString(s);
			 } while(!password.equals(confirmPassword));
			 int accType;
			 do {
			 System.out.println("Account Type: ");
			 System.out.println("1. Client");
			 System.out.println("2. Admin");
			 accType = ConsoleInput.readInt(s);
			 } 
			 while (accType < 1 || accType > 2);
			 User user;
			 if (accType == 2) {
				 user = new Admin(IDGenerator.getNextUserID(), firstName, lastName, email, password);
			 }
			 else {
				 user = new Client(IDGenerator.getNextUserID(), firstName, lastName, email, password);
			 }
			DataManager.users.add (user);
			NavigationHandler. showMenu(s, user);
	}
	
	
	
			 
}

