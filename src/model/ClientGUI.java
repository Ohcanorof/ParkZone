package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//not complete!

public class ClientGUI {

	//attributes
	private User currentUser;
	private boolean connected;
	private String serverHost;
	private int serverPort;
	
	//server stuff
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	//constructors
	public ClientGUI() {
		this.serverHost = "localhost";
		this.serverPort = 8080;
		this.connected = false;
	}
	
	//methods (not fully complete)
	public void start() {
		try {
			connect(serverHost, serverPort);
			consoleLoop();
		}catch(IOException e) {
			handleError("Failed to start client: " + e.getMessage());
		}finally {
			disconnect();
		}
	}
	
	public void connect(String host, int port) throws IOException {
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		connected = true;
		System.out.println("Connected to server at " + host + ":" + port);
		
		Thread reader = new Thread(() -> { 
			try {
				String line;
				while((line = readLine()) != null) {
					System.out.println("[from server] " + line);
				}	
			}catch(IOException e) {
				if(connected) {
				handleError("Connection lost: " + e.getMessage());
				}
			}
		});
		reader.setDaemon(true);
		reader.start();
	}
	
	public void disconnect() {
		connected = false;
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
			}
		}catch(IOException ignored) {
			
		}
		System.out.println("Client disconected.");
	}
	
	public boolean login(String email, String password) {
		System.out.println("login() not implemented yet, pretent it is....");
		return true;
	}
	
	public void logout() {
		System.out.println("logout() not implemented yet...");
	}
	//need to make these eventually...
	 /* 
    public java.util.List<ParkingSlot> refreshSlots(int garageId, String type) { return null; }
    public void showSlots(int garageId, String type) {}
    public Ticket issueTicket(int vehicleId, int slotId) { return null; }
    public Ticket closeTicket(int ticketId) { return null; }
    public boolean pay(int ticketId, String method) { return false; }
    public void onSpaceUpdate(Object event) {}
    public void updateUI() {}
    public void handleError(String message) { System.err.println("[ClientGUI] " + message); }
    public void selectGarage(int garageId) {}
    public void selectVehicle(int vehicleId) {}
    public void selectSlot(int slotId) {}
    */
	
	//error handler for now
	public void handleError(String message) {
		System.err.println("[ClientGUI]" + message);
	}
	
	//helpers
	
	private void consoleLoop() throws IOException{
		try(BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))){
			System.out.println("Type messages and press Enter. Type 'quit' to exit.");
			String line;
			while((line = userIn.readLine()) != null){
				if("quit".equalsIgnoreCase(line.trim())) {
					break;
				}
				send(line);
			}
		}
	}
	
	private void send(String msg) {
		if(out != null) {
			out.println(msg);
		}
	}
	
	private String readLine() throws IOException{
		if (in == null) return null;
		return in.readLine();
	}
	
	//main here for convenience testing
	public static void main(String[] args) {
		ClientGUI client = new ClientGUI();
		client.start();
	}
	
	
	
	
}
