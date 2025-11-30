package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * EventStreamClient
 * - wraps Socket + Object streams
 * - provides login/text/logout methods used by ClientGUI
 */
public class EventStreamClient {

    private final String host;
    private final int port;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public EventStreamClient(String host, int port) throws IOException {
    	this.host = host;
    	this.port = port;
    	connect();
    }

    private void connect() throws IOException {
        System.out.println("Connecting to server at " + host + ":" + port + "...");
        socket = new Socket(host, port);
        System.out.println("Connected to server!");

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void send(Message msg) throws IOException{
    	System.out.println("[Client->Server] " + msg);
    	out.writeObject(msg);
    	out.flush();
    	out.reset();
    }

    public Message receive() throws IOException, ClassNotFoundException{
    	Message resp = (Message) in.readObject();
    	System.out.println("[Server->Client] " + resp);
    	return resp;
    }
    
    private Message sendAndReceive(Message msg) throws IOException, ClassNotFoundException{
    	send(msg);
    	return receive();
    }
    //login w/ email and password (might change to username and password instead
    public User login(String email, String password) throws IOException, ClassNotFoundException {
    	// send: type=login, text="email|password"
    	String payload = email + "|" + password;
        Message loginMsg = new Message(Message.TYPE_LOGIN, payload);
        send(loginMsg);
        
        Message resp = receive();
        
        if (!"success".equalsIgnoreCase(resp.getStatus())) {
            System.out.println("[Client] Login failed: " + resp.getText());
            return null;
        }
        
        String info = resp.getText(); // "ID|firstName|lastName|email|accountType"
        if (info == null || info.isEmpty()) {
            System.out.println("[Client] Login success but no user info in response.");
            return null;
        }
        
        String[] parts = info.split("\\|", -1);
        if (parts.length < 5) {
            System.out.println("[Client] Unexpected user info format: " + info);
            return null;
        }
        
        int id;
        try {
            id = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            System.out.println("[Client] Invalid user ID in response: " + parts[0]);
            return null;
        }
        String firstName   = parts[1];
        String lastName    = parts[2];
        String userEmail   = parts[3];
        String accountType = parts[4];
        
        //differentiate between admin and customer
        User user;
        if ("ADMIN".equalsIgnoreCase(accountType)) {
            user = new Admin(id, firstName, lastName, userEmail, password);
        } else {
            // default to Client for CUSTOMER or other account types (might add the operator?)
            user = new Client(id, firstName, lastName, userEmail, password);
        }
        user.setAccountType(accountType);

        return user;
    }

    public String sendText(String text) throws IOException, ClassNotFoundException {
        Message textMsg = new Message(Message.TYPE_TEXT, text);
        Message response = sendAndReceive(textMsg);

        if ("success".equals(response.getStatus())) {
            return response.getText();
        } else {
            throw new IOException("Text failed: " + response.getText());
        }
    }

    public boolean logout() throws IOException, ClassNotFoundException {
        System.out.println("\n=== Logging out ===");
        Message logoutMsg = new Message(Message.TYPE_LOGOUT);
        Message response = sendAndReceive(logoutMsg);
        return "success".equals(response.getStatus());
    }

    public void close() {
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {
        }
        try {
            if (out != null) out.close();
        } catch (IOException ignored) {
        }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {
        }
        System.out.println("[EventStreamClient] Connection closed.");
    }
    
    public boolean registerAccount(String firstName, String lastName, String email, String password, String accountType) throws IOException, ClassNotFoundException{
    	// firstName|lastName|email|password|accountType
    	//MIGHT CHANGE THIS
        String payload = String.join("|", firstName, lastName, email, password, accountType != null ? accountType : "CUSTOMER");
    	
    	Message registerMsg = new Message(Message.TYPE_REGISTER, payload);
    	Message resp = sendAndReceive(registerMsg);
    	
    	if(!"success".equalsIgnoreCase(resp.getStatus())){
    		System.out.println("[Client] Registration failed: " + resp.getText());
    		System.out.println("[Client] Registration succeeded for email: " + email);
    		return false;
    	}
    	return true;
    }
    
    
}