package model;

public interface Actionable {
	
	public String getLabel();
	
	public void execute();
	
	public boolean isAdminOnly();	// is Admin only action

}
