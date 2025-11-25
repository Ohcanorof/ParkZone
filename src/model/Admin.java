package model;

public class Admin extends User {
	
	public Admin() {
		super.actions = new Actionable[] {
				
		};
	}
	
	public Admin(int ID, String firstName, String lastName, String email, String password) {
		super(ID, firstName, lastName, email, password);
		super.actions = new Actionable[] {
				
		};
	}

}
