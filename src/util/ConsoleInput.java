package util;

import java.util.Scanner;

public class ConsoleInput {

	// for reading string input with spaces for many words
	 public static String readLinestring(Scanner scanner) {
		 String line;
		 do {
			 line = scanner.nextLine();
		 } while (line.isEmpty());
		 return line;
	 }
	 
	 // for read only one word without spaces
	 public static String readString(Scanner scanner) {
		 return scanner. next ();
	 }
	 
	 // only double (string --> double)
	 public static double readDouble(Scanner scanner) {
		 double inputDouble = 0;
		 boolean doubleInput = false;
		 while (!doubleInput) {
			try {
				String input = readString(scanner);
				inputDouble = Double.parseDouble(input); //if parsed successfully
				doubleInput = true;
			} catch (Exception e) {
				System.out.println("Please enter double");
				
			}
		 }
		 return inputDouble;
	}
	 
	 public static int readInt(Scanner scanner) {	// if not
		 int inputInt = 0;
		 boolean intIn = false;
		 while (!intIn) {
			 try {
				 String input = readString(scanner);
				 inputInt = Integer.parseInt(input);
				 intIn = true;
			 } catch (Exception e) {
				 System. out.println("Enter an intiger");
			 }
		 }
		 return inputInt;
		 
	 }

	 
	 
	 	 
	 
}
