package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Multi-threaded Server
 * Listens for incoming client connections and spawns a new ClientHandler thread
 * for each connection to enable concurrent client processing
 * 
 * This demonstrates the Client-Server Design Pattern with multi-threading:
 * - Single server object
 * - Many client objects
 * - Server spawns threads to service clients concurrently
 */
public class Server {
    private static final int PORT = 8080;  // Default port (matches ParkZone requirement)
    private static int clientCounter = 0;   // Counter for assigning client IDs
    
    public static void main(String[] args) {
        int port = PORT;
        
        // Allow port to be specified as command-line argument
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number, using default: " + PORT);
                port = PORT;
            }
        }
        
        System.out.println("=".repeat(50));
        System.out.println("Multi-threaded Server Starting...");
        System.out.println("Port: " + port);
        System.out.println("=".repeat(50));
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            System.out.println("Waiting for client connections...\n");
            
            // Main server loop - accept connections indefinitely
            while (true) {
                try {
                    // Block until a client connects
                    Socket clientSocket = serverSocket.accept();
                    
                    // Assign unique ID to this client
                    int clientId = ++clientCounter;
                    
                    System.out.println("\n[Server] New connection accepted! Assigned Client ID: " + clientId);
                    
                    // Create and start a new thread to handle this client
                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientId);
                    clientHandler.start();
                    
                    System.out.println("[Server] ClientHandler thread started for Client " + clientId);
                    System.out.println("[Server] Server ready for more connections...\n");
                    
                } catch (IOException e) {
                    System.err.println("[Server] Error accepting client connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
        } catch (IOException e) {
            System.err.println("[Server] Could not start server on port " + port);
            System.err.println("[Server] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}