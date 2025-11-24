package model;

import java.util.Scanner;

public interface Actionable {
	
	public String getLabel();
	
	public void execute(Scanner s, User u);
	
	public boolean isAdminOnly();	// is Admin only action

}
