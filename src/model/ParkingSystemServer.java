package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParkingSystemServer {
	//attributes
	private final int port;
	private ServerSocket serverSocket;
	private ExecutorService clientPool;
	private final Map<String, ClientHandler> clientsById = new ConcurrentHashMap<>();
	private volatile boolean running; //might change to atomic as listed in the UML
	
	//constructor
	public ParkingSystemServer(int port) {
		this.port = port;
	}
	
	//operations
	//start the server and accept client connections
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		clientPool = Executors.newCachedThreadPool();
		running = true;
		
		System.out.println("Server is listening on port " + port);
		acceptLoop();
	}

	//stop the server and close all 
	public void stop() {
		running = false;
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		}catch (IOException ignored) {
			
		}
		
		if(clientPool != null) {
			clientPool.shutdownNow();
		}
		System.out.println("Server stopped!");
	}
	
	//broadcast message to all clients that are currently connected
	public void broadcast(String msg) {
		for (ClientHandler handler : clientsById.values()) {
			handler.send(msg);
		}
	}
	
	//this is called by the client handler when the client disconnects
	void removeClient(String clientId) {
		clientsById.remove(clientId);
		System.out.println("[server] A client has disconnected. Client ID: " + clientId);
	}
	
	//helper funcs
	
	//accept loop, waits for the clients and moves them to the thread pool
	private void acceptLoop() {
		while(running) {
			try {
				Socket clientSocket = serverSocket.accept();
				ClientHandler handler = new ClientHandler(this, clientSocket);
				clientsById.put(handler.getClientId(), handler);
				clientPool.execute(handler);
				
				System.out.println("[server] Client connected: " + handler.getClientId());
			}
			catch(IOException e){
				if(running) {
					System.err.println("[server] There has been an error accepting a connection! " + e.getMessage());
				}
			}
		}
	}
	
	
}
