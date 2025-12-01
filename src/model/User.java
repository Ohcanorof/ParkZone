package model;

import java.io.Serializable;

public abstract class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private int ID;
	private String firstName;
	private String lastName;
	private String email;
	private String password; //we can also add phone number if we want --> if we do we also need to make getters and setters for them too
	private String AccountType;// user can be Admin or Customer
	protected Actionable[] actions;
	
	//class methods
	
	public void login(String email, String password) {
		
	}
	
	public void logout() {
		
	}
	
	
	//constructors
	public User() {
		
	}
	public User(int ID, String firstName, String lastName, String email, String password) {
		this.ID = ID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.AccountType = null; 
	}
	
	//Setters
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public void setAccountType (String accountType) {
		this.AccountType = accountType;
	}
	
	public void setActions(Actionable[] actions) {
		this.actions = actions;
	}
	
	
	//Getters
	public int getID() {
		return ID;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	//user full name
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}

	public String getAccountType(){
		return AccountType;
	}
	
	public Actionable[] getActions() {
		return actions;
	}
	
	//extra function to check passwords
	public boolean checkPassword(String inputPassword) {
		//passwords cant be null and MUST equal the inputed password.
		return password != null && password.equals(inputPassword);
	}
	
	//extra function for user info
	@Override
	public String toString() {
		return "User{" + "ID=" + ID + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", email='" + email + '\'' + ", accountType='" + AccountType + '\'' + '}';
	}
	

	
}
