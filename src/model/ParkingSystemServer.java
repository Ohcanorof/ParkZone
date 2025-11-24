package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParkingSystemServer {
	//attributes
	private final int port;
	private ServerSocket serverSocket;
	private ExecutorService clientPool;
	private final Map<String, ClientHandler> clientsById = new ConcurrentHashMap<>();
	private final AtomicBoolean running = new AtomicBoolean(false);	
    private final ParkingSystem parkingSystem = ParkingSystem.getInstance();

	
	
	//constructor
	public ParkingSystemServer(int port) {
		this.port = port;
	}
	
	//operations
	//start the server and accept client connections
	public void start() throws IOException {
        if (!running.compareAndSet(false, true)) {
            System.out.println("[server] Already running");
            return;
        }

        serverSocket = new ServerSocket(port);
        clientPool = Executors.newCachedThreadPool();

        System.out.println("=".repeat(50));
        System.out.println("[server] ParkingSystemServer starting...");
        System.out.println("[server] Port: " + port);
        System.out.println("=".repeat(50));
        System.out.println("[server] Listening on port " + port);
        System.out.println("[server] Waiting for client connections...\n");

        Thread acceptThread = new Thread(this::acceptLoop, "ParkingSystemServer-Acceptor");
        acceptThread.start();
    }

	//stop the server and close all 
	public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }

        System.out.println("[server] Stopping...");

        // close all clients
        clientsById.values().forEach(ClientHandler::close);
        clientsById.clear();

        if (clientPool != null) {
            clientPool.shutdownNow();
        }

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }

        System.out.println("[server] Stopped.");
    }
	
	//broadcast message to all clients that are currently connected
	public void broadcast(Message msg) {
        for (ClientHandler handler : clientsById.values()) {
            handler.onServerPush(msg);
        }
    }
	
	//this is called by the client handler when the client disconnects
	public void removeClient(String id) {
        clientsById.remove(id);
        System.out.println("[server] Client removed: " + id);
    }
	
	//helper funcs
	
	//accept loop, waits for the clients and moves them to the thread pool
	private void acceptLoop() {
        try {
            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    String clientId = UUID.randomUUID().toString();
                    ClientHandler handler = newClientHandler(clientSocket, clientId);
                    clientsById.put(clientId, handler);

                    clientPool.execute(handler);
                    System.out.println("[server] Client connected: " + clientId);

                } catch (IOException e) {
                    if (running.get()) {
                        System.err.println("[server] Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } finally {
            stop();
        }
    }
	
	private ClientHandler newClientHandler(Socket socket, String clientId) {
        return new ClientHandler(this, socket, clientId);
    }
	
	/** Send a message to one specific client id */
    public void sendTo(String id, Message msg) {
        ClientHandler handler = clientsById.get(id);
        if (handler != null) {
            handler.onServerPush(msg);
        }
    }
	
	//main function, server starter:
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
