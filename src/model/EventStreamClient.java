package model;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import model.ParkingSlot;
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
    
    private synchronized Message sendAndReceive(Message req) throws IOException, ClassNotFoundException {

        //send the request first
        out.writeObject(req);
        out.flush();

        final String reqType = req.getType();

        while (true) {
            Object raw = in.readObject();
            if (!(raw instanceof Message resp)) {
                System.out.println("[Client] Unknown object from server: " + raw);
                continue;  //keep reading until we see a Message
            }

            String respType = resp.getType();

            //Match reply based on what we sent

            //sent get_slots -> expect slots_data OR slots_update
            if (Message.TYPE_GET_SLOTS.equals(reqType)) {
                if (Message.TYPE_SLOTS_DATA.equals(respType)
                        || Message.TYPE_SLOTS_UPDATE.equals(respType)) {
                    return resp;
                }
            }
            //sent get_tickets -> expect tickets_data
            else if (Message.TYPE_GET_TICKETS.equals(reqType)) {
                if (Message.TYPE_TICKETS_DATA.equals(respType)) {
                    return resp;
                }
            }
            //sent reserve_slot -> expect reserve_slot (ack)
            else if (Message.TYPE_RESERVE_SLOT.equals(reqType)) {
                if (Message.TYPE_RESERVE_SLOT.equals(respType)) {
                    return resp;
                }
            }
            //sent add_slots / remove_slot -> expect same type back
            else if (Message.TYPE_ADD_SLOTS.equals(reqType)
                  || Message.TYPE_REMOVE_SLOT.equals(reqType)) {
                if (respType.equals(reqType)) {
                    return resp;
                }
            }
            //a fallback: for any other simple 1-to-1 command, just return first Message
            else {
                return resp;
            }

            //For now, we just log and ignore them; the caller who cares will make
            //their own call and get a fresh response.
            System.out.println("[Client] Skipping message not for this request: " + resp);
        }
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
    
    //functions for slots
    // tells server: "Admin wants to add N slots"
    public boolean addSlotsOnServer(int count) throws IOException, ClassNotFoundException {
        Message msg = new Message(Message.TYPE_ADD_SLOTS, String.valueOf(count));
        Message resp = sendAndReceive(msg);
        return "success".equalsIgnoreCase(resp.getStatus());
    }
    
    //tells server: "Admin wants to remove slots by the id"
    public boolean removeSlot(int slotId) throws IOException, ClassNotFoundException {
        Message req = new Message(Message.TYPE_REMOVE_SLOT, String.valueOf(slotId));
        Message resp = sendAndReceive(req);

        if (!"success".equalsIgnoreCase(resp.getStatus())) {
            System.out.println("[Client] Remove slot failed: " + resp.getText());
            return false;
        }
        return true;
    }

    // ask server for current slots, parse into a List<ParkingSlot>
    public List<ParkingSlot> fetchSlotsFromServer(int garageId, String type) throws IOException, ClassNotFoundException {
    	Message req = new Message(Message.TYPE_GET_SLOTS, "");
        Message resp = sendAndReceive(req);

        //resp is guaranteed to be slots_data or slots_update (should be)
        List<ParkingSlot> fromServer = resp.getSlots();
        if (fromServer == null) {
            return java.util.Collections.emptyList();
        }
        return new java.util.ArrayList<>(fromServer);
    }
    
    public List<Ticket> fetchTicketsFromServer() throws IOException, ClassNotFoundException {
    	//asking server for tickets
    	Message req = new Message(Message.TYPE_GET_TICKETS, "");
        Message resp = sendAndReceive(req);

        //resp is guaranteed to be tickets_data (should be)
        List<Ticket> list = resp.getTickets();
        if (list == null) {
            return java.util.Collections.emptyList();
        }
        return new java.util.ArrayList<>(list);
    }
    
    public boolean reserveSlotOnServer(int slotId, String plateNumber)
            throws IOException, ClassNotFoundException {

        Message msg = new Message(Message.TYPE_RESERVE_SLOT);
        msg.setSlotId(slotId);
        if (plateNumber != null) {
            msg.setText(plateNumber);
        }

        Message resp = sendAndReceive(msg);

        if (!"success".equalsIgnoreCase(resp.getStatus())) {
            System.out.println("[Client] Reserve slot failed: " + resp.getText());
            return false;
        }

        return true;
    }
}