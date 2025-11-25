package model;


public class Main {

	public static void main(String[] args) {
		/*
		 * where we will run our main functions 
		 */
		//test
		int port = 8080;
		ClientGUI gui = new ClientGUI();

		gui.start();
		gui.connect("10.0.0.150", port);
	}

}
