package model;

public class IDGenerator {
	
	private static int nextUserID = 0;
	
	public static int getNextUserID() { // getter
		return nextUserID++;
	}
	
	public static void setNextUserID(int nextUserID) { //setters
		IDGenerator.nextUserID = nextUserID;
	}
	
	
}
