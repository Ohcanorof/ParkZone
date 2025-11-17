package model;


public class Main {

	public static void main(String[] args) {
		/*
		 * where we will run our main functions 
		 */
		//test
		ParkingSystemServer server = new ParkingSystemServer(8080);

		try {
			server.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
