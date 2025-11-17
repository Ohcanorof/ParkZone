package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

//the ClientHandler
//will handle connection with a single connected client, runs on its own thread, reads incomming 
//messages, and can send messages back to the client or ask the server to broadcast
//may change later, this and the ParkingSystemServer are barebones rn
public class ClientHandler implements Runnable {
	//attributes
	private final ParkingSystemServer parkingSystem;//server= parkingSystem on the uml
	private final Socket socket;
	private final String clientId;
	private BufferedReader in;
	private PrintWriter out;
	
	//constructor
	public ClientHandler(ParkingSystemServer parkingSystem, Socket socket) {
		this.parkingSystem = parkingSystem;
		this.socket = socket;
		this.clientId = UUID.randomUUID().toString();
	}
	
	//getters
	public String getClientId() {
		return clientId;
	}
	
	//implementing runnable
	@Override
	public void run() {
		try {
			initStreams();
			readLoop();
		}
		catch(IOException e) {
			System.err.println("[server] Connection error with " + clientId+ ": "+ e.getMessage());
		}
		finally {
			cleanup();
		}
	}
	
	//operations
	//send a single line of text to the client
	public void send(String message) {
		if(out != null) {
			out.println(message);
		}
	}
	
	//helperfuncst
	private void initStreams() throws IOException{
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);//this auto flushes
	}
	
	//the main loop that reads messages from the client
	//since its barebones right now, it can only echo the message back and broad casts the message
	//to all the other clients through the server
	private void readLoop() throws IOException{
		String line;
			
		while((line = in.readLine()) != null) {
			System.out.println("[server] From " + clientId + ": " + line);
			
			//echo it back
			send("you said: " + line);
			
			//broadcast it
			parkingSystem.broadcast("[broadcast from " + clientId + "]: " + line);
		}
	}
	
	private void cleanup() {
		parkingSystem.removeClient(clientId);
		try {
			socket.close();
		}
		catch(IOException ignored) {
			
		}
	}
	

	
}