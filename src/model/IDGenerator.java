package model;

public class IDGenerator {
	
	private static int nextUserID = 0;
	private static int nextSlotNum = 1;
	
	public static int getNextUserID() { // getter
		return nextUserID++;
	}
	
	/* public static void setNextUserID(int nextUserID) { //setters
		IDGenerator.nextUserID = nextUserID;
	}
	*/ //redundent
	
	public static int getNextSlotNum() {
		return nextSlotNum++;
	}
	
	
	
	
}
