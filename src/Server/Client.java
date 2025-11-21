package Server;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client for connecting to the multi-threaded server
 * Implements the login -> text messages -> logout protocol
 */
public class Client {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Scanner scanner;
    
    public Client(String host, int port) throws IOException {
        System.out.println("Connecting to server at " + host + ":" + port + "...");
        
        // Connect to server
        socket = new Socket(host, port);
        System.out.println("Connected to server!");
        
        // Set up streams - order matters! OutputStream first, then InputStream
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();  // Important: flush the header
        in = new ObjectInputStream(socket.getInputStream());
        
        scanner = new Scanner(System.in);
    }
    
    /**
     * Send a message to the server and wait for response
     */
    private Message sendAndReceive(Message message) throws IOException, ClassNotFoundException {
        System.out.println("\n[Sending] " + message);
        out.writeObject(message);
        out.flush();
        out.reset();  // Prevent caching issues
        
        Message response = (Message) in.readObject();
        System.out.println("[Received] " + response);
        return response;
    }
    
    /**
     * Run the client session
     */
    public void run() {
        try {
            // Step 1: Login
            if (!login()) {
                System.out.println("Login failed. Exiting...");
                return;
            }
            
            // Step 2: Main interaction loop - send text messages
            boolean continueRunning = true;
            while (continueRunning) {
                System.out.print("\nEnter text to send (or 'logout' to quit): ");
                String userInput = scanner.nextLine().trim();
                
                if (userInput.equalsIgnoreCase("logout")) {
                    logout();
                    continueRunning = false;
                } else if (!userInput.isEmpty()) {
                    sendTextMessage(userInput);
                }
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during communication: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    
    /**
     * Perform login handshake with server
     */
    private boolean login() throws IOException, ClassNotFoundException {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Performing login...");
        System.out.println("=".repeat(40));
        
        // Create and send login message
        Message loginMsg = new Message(Message.TYPE_LOGIN);
        Message response = sendAndReceive(loginMsg);
        
        // Check if login was successful
        if ("success".equals(response.getStatus())) {
            System.out.println("✓ Login successful!");
            return true;
        } else {
            System.out.println("✗ Login failed: " + response.getText());
            return false;
        }
    }
    
    /**
     * Send a text message to the server
     */
    private void sendTextMessage(String text) throws IOException, ClassNotFoundException {
        // Create and send text message
        Message textMsg = new Message(Message.TYPE_TEXT, text);
        Message response = sendAndReceive(textMsg);
        
        // Display the server's response (capitalized text)
        if ("success".equals(response.getStatus())) {
            System.out.println("\n>>> Server response: " + response.getText());
        } else {
            System.out.println("Error: " + response.getText());
        }
    }
    
    /**
     * Perform logout from server
     */
    private void logout() throws IOException, ClassNotFoundException {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("Logging out...");
        System.out.println("=".repeat(40));
        
        // Create and send logout message
        Message logoutMsg = new Message(Message.TYPE_LOGOUT);
        Message response = sendAndReceive(logoutMsg);
        
        if ("success".equals(response.getStatus())) {
            System.out.println("✓ Logout successful!");
        } else {
            System.out.println("✗ Logout failed: " + response.getText());
        }
    }
    
    /**
     * Clean up resources
     */
    private void cleanup() {
        try {
            if (scanner != null) scanner.close();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("\nConnection closed. Goodbye!");
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Main method - entry point for the client
     */
    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        // Parse command-line arguments if provided
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number, using default: " + DEFAULT_PORT);
            }
        }
        
        try {
            // Create and run client
            Client client = new Client(host, port);
            client.run();
            
        } catch (IOException e) {
            System.err.println("Could not connect to server at " + host + ":" + port);
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nMake sure the server is running first!");
        }
    }
}