package Server;
import java.io.*;
import java.net.Socket;

/**
 * ClientHandler - Thread class to handle individual client connections
 * Each client gets its own handler thread for concurrent processing
 * Implements the login -> text/logout protocol
 */
public class ClientHandler extends Thread {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isLoggedIn;
    private int clientId;
    
    public ClientHandler(Socket socket, int clientId) {
        this.clientSocket = socket;
        this.clientId = clientId;
        this.isLoggedIn = false;
    }
    
    @Override
    public void run() {
        System.out.println("[Client " + clientId + "] Connected from: " + clientSocket.getInetAddress());
        
        try {
            // Set up streams - order matters! OutputStream first, then InputStream
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();  // Important: flush the header
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            // Main message processing loop
            boolean running = true;
            while (running) {
                try {
                    // Read incoming message from client
                    Message message = (Message) in.readObject();
                    System.out.println("[Client " + clientId + "] Received: " + message);
                    
                    // Process message based on type and login state
                    if (!isLoggedIn) {
                        // Client must login first before any other operations
                        if (Message.TYPE_LOGIN.equals(message.getType())) {
                            handleLogin(message);
                        } else {
                            // Reject non-login messages if not logged in
                            Message errorMsg = new Message(message.getType());
                            errorMsg.setStatus("error");
                            errorMsg.setText("Must login first");
                            sendMessage(errorMsg);
                        }
                    } else {
                        // Client is logged in, process their request
                        switch (message.getType()) {
                            case Message.TYPE_TEXT:
                                handleText(message);
                                break;
                            case Message.TYPE_LOGOUT:
                                handleLogout(message);
                                running = false;  // Exit loop after logout
                                break;
                            default:
                                System.out.println("[Client " + clientId + "] Unknown message type: " + message.getType());
                        }
                    }
                } catch (EOFException e) {
                    // Client disconnected unexpectedly
                    System.out.println("[Client " + clientId + "] Disconnected unexpectedly");
                    running = false;
                }
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Client " + clientId + "] Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    
    /**
     * Handle login message - set login state and send success response
     */
    private void handleLogin(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing login...");
        isLoggedIn = true;
        
        // Create success response
        Message response = new Message(Message.TYPE_LOGIN);
        response.setStatus("success");
        response.setText("Login successful");
        
        sendMessage(response);
        System.out.println("[Client " + clientId + "] Login successful");
    }
    
    /**
     * Handle text message - capitalize the text and send back
     */
    private void handleText(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing text: " + message.getText());
        
        // Capitalize the text
        String capitalizedText = message.getText().toUpperCase();
        
        // Create response with capitalized text
        Message response = new Message(Message.TYPE_TEXT);
        response.setText(capitalizedText);
        response.setStatus("success");
        
        sendMessage(response);
        System.out.println("[Client " + clientId + "] Sent capitalized text: " + capitalizedText);
    }
    
    /**
     * Handle logout message - send confirmation and prepare to close connection
     */
    private void handleLogout(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing logout...");
        
        // Create logout confirmation
        Message response = new Message(Message.TYPE_LOGOUT);
        response.setStatus("success");
        response.setText("Logout successful");
        
        sendMessage(response);
        System.out.println("[Client " + clientId + "] Logout successful");
    }
    
    /**
     * Send a message to the client
     */
    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
        out.reset();  // Prevent caching issues with ObjectOutputStream
    }
    
    /**
     * Clean up resources and close connection
     */
    private void cleanup() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            System.out.println("[Client " + clientId + "] Connection closed");
        } catch (IOException e) {
            System.err.println("[Client " + clientId + "] Error during cleanup: " + e.getMessage());
        }
    }
}