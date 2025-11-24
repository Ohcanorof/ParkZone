package cli;

import java.util.Scanner;

import model.User;
import util.ConsoleInput;

public class NavigationHandler {
	
	public static void welcome (Scanner scanner) {
		System.out.println("Welcome to our Smart Parking System");
		System.out.println("1. Login");
		System.out.println("2. Create new Account");
		System.out.println("3. Exit");
		int input = ConsoleInput.readInt(scanner);
		switch (input) {
		case 1:
			AuthHandler.login(scanner); 
			break;
		case 2:
			AuthHandler.createNewAccount(scanner);
			break;
		case 3:
			exit();
			break;
			default:
				welcome(scanner);
		}
	}
	
	public static void exit() {
		System.out.println("Thanks for using our smart parking system");
		System.out.println("Have a great day :) ");
	}
	
	public static void showMenu(Scanner s, User user) {
		System.out.println("================================");
		for (int i = 0; i < user.getActions().length; i++) {
			System.out.println((i+1)+ ". " + user.getActions()[i].getLabel());
		} 
		System.out.println("================================"); 
		int selected = ConsoleInput.readInt(s); 
		selected--;
		if (selected < 0|| selected >= user.getActions().length) {
			System.out.println("Invalid input"); 
			showMenu (s, user);
		} else {
			user.getActions()[selected].execute(s, user);; 
			showMenu(s,user);
		}
	}
	
	
	
	
}
