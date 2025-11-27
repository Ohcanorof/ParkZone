package model;

public class IDGenerator {
	
	private static int nextUserID = 0;
	private static int nextSlotNum = 1;
	private static int nextTicketID = 0;
	
	public static int getNextUserID() { // getter
		return nextUserID++;
	}
	
	public static int setNextUserID() { //setters
		return nextUserID++;
	}
	
	
	public static int getNextSlotNum() {
		return nextSlotNum++;
	}
	
	public static int getNextTicketID() {
		return nextTicketID++;
	}
	
	
	
}
