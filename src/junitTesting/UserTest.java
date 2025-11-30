package junitTesting;

import model.User;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTest {

	//made a sublcass for testing purposes
	private static class TestUser extends User{
		public TestUser() {
			super();
		}
		public TestUser(int id, String firstName, String lastName, String email, String password) {
			super(id, firstName, lastName, email, password);
		}
	}
	
	//testing basic field setting for users
	@Test
	void basicFieldSetting() {
		TestUser user = new TestUser(10, "Alice", "Smith", "alice@gmail.com", "password");
		
		//tests
		assertEquals(10, user.getID());
		assertEquals("Alice", user.getFirstName());
		assertEquals("Smith", user.getLastName());
		assertEquals("alice@gmail.com", user.getEmail());
		assertEquals("password", user.getPassword());
	}
	
	//tests setters and how they update fields
	@Test
	void settersUpdatingFields() {
		TestUser user = new TestUser();
		
		user.setID(99);
		user.setFirstName("Bob");
		user.setLastName("Jones");
		user.setEmail("bob@gmail.com");
		user.setPassword("password!");
		user.setAccountType("Client");
		
		//test
		assertEquals(99, user.getID());
		assertEquals("Bob", user.getFirstName());
		assertEquals("Jones", user.getLastName());
		assertEquals("bob@gmail.com", user.getEmail());
		assertEquals("password!", user.getPassword());
		assertEquals("Client", user.getAccountType());
	}

}
