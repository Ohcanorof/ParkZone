package model;

public class Client extends User {
	
	public Client() {
		super.actions = new Actionable[] {
				
		};
	}
	
	public Client(int ID, String firstName, String lastName, String email, String password) {
		super(ID, firstName, lastName, email, password);
		super.actions = new Actionable[] {
				
		};
	
	}

}
