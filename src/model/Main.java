package model;


public class Main {

	public static void main(String[] args) {
		/*
		 * where we will run our main functions 
		 */
		//test
		int port = 8080;
		ParkingSystemServer server = new ParkingSystemServer(port);

		try {
			server.start();
		}catch(Exception e) {
			e.printStackTrace();
			server.stop();
		}
	}

}
