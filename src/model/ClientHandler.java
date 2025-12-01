package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

//the ClientHandler
//will handle connection with a single connected client, runs on its own thread, reads incomming 
//messages, and can send messages back to the client or ask the server to broadcast
//may change later, this and the ParkingSystemServer are barebones rn
public class ClientHandler implements Runnable {
	//attributes
	private final ParkingSystemServer server;//server= parkingSystem on the uml
	private final Socket socket;
	private final String clientId;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private final AtomicBoolean open = new AtomicBoolean(true);
	private boolean loggedIn = false;
	private volatile boolean running = true;
	private final ParkingSystem parkingSystem;
	private final TicketService ticketService;
	//private final ParkingSystem parkingSystem = ParkingSystem.getInstance();
	//to track the logged in user:
	private User currentUser;
	
	//constructor
	public ClientHandler(ParkingSystemServer server, Socket socket, String clientId) {
	    this.server = server;
	    this.socket = socket;
	    this.clientId = clientId;
	    this.parkingSystem = server.getParkingSystem();
	    this.ticketService = server.getTicketService();
	}
	
	//getters
	public String getClientId() {
		return clientId;
	}
	
	//implementing runnable
	@Override
    public void run() {
        System.out.println("[Client " + clientId + "] Connected from: " + socket.getInetAddress());

        try {
            // order matters: out first, then in
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            readLoop();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Client " + clientId + "] Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void readLoop() throws IOException, ClassNotFoundException {
        boolean running = true;

        while (running && open.get()) {
            try {
                Object obj = in.readObject();
                if (!(obj instanceof Message)) {
                    System.err.println("[Client " + clientId + "] Unknown object: " + obj);
                    continue;
                }

                Message message = (Message) obj;
                System.out.println("[Client " + clientId + "] Received: " + message);

                if (!loggedIn) {
                    // must login first
                    if (Message.TYPE_LOGIN.equals(message.getType())) {
                        handleLogin(message);
                    }else if(Message.TYPE_REGISTER.equals(message.getType())){ 
                    	handleRegister(message);
                    }
                    else {
                        Message errorMsg = new Message(message.getType());
                        errorMsg.setStatus("error");
                        errorMsg.setText("Must login first");
                        send(errorMsg);
                    }
                } else {
                	switch (message.getType()) {
                    case Message.TYPE_TEXT -> handleText(message);
                    case Message.TYPE_LOGOUT -> {
                        handleLogout(message);
                        running = false;
                    }
                    case Message.TYPE_GET_SLOTS -> handleGetSlots(message);
                    case Message.TYPE_ADD_SLOTS -> handleAddSlots(message);
                    case Message.TYPE_REMOVE_SLOT -> handleRemoveSlot(message);
                    case Message.TYPE_RESERVE_SLOT -> handleReserveSlot(message);
                    case Message.TYPE_GET_TICKETS -> handleGetTickets(message);
                    default -> System.out.println(
                            "[Client " + clientId + "] Unknown message type: " + message.getType()
                    );
                	}
                }

            } catch (EOFException e) {
                System.out.println("[Client " + clientId + "] Disconnected unexpectedly");
                break;
            }
        }
    }

    
    private void handleRegister(Message message) throws IOException{
    	System.out.println("[Client " + clientId + "] Processing registration...");
    	
    	String payload = message.getText();
    	//we expect : firstName|lastName|email|password|accountType
    	//treating the missing accountType as "CUSTOMER"
    	if(payload == null || payload.isBlank()) {
    		Message resp = new Message(Message.TYPE_REGISTER);
    		resp.setStatus("error");
    		resp.setText("Empty registration payload");
    		send(resp);
    		return;
    	}
    	
    	String[] parts = payload.split("\\|", -1);
    	if(parts.length <4) {
    		Message resp = new Message(Message.TYPE_REGISTER);
    		resp.setStatus("error");
    		resp.setText("Invalid registration payload");
    		send(resp);
    		return;
    	}
    	
    	String firstName   = parts[0];
        String lastName    = parts[1];
        String email       = parts[2];
        String password    = parts[3];
        String accountType = (parts.length >= 5 && !parts[4].isBlank()) ? parts[4] : "CUSTOMER";
    
        
        // very simple duplicate check by email
        for (User u : parkingSystem.getUsers()) {
            if (email.equalsIgnoreCase(u.getEmail())) {
                Message resp = new Message(Message.TYPE_REGISTER);
                resp.setStatus("error");
                resp.setText("Email already in use");
                send(resp);
                return;
            }
        }
        
        // easy ID assignment: size+1
        int newId = parkingSystem.getUsers().size() + 1;
        
        User user;
        if ("ADMIN".equalsIgnoreCase(accountType)) {
            user = new Admin(newId, firstName, lastName, email, password);
        } else {
            user = new Client(newId, firstName, lastName, email, password);
        }
        user.setAccountType(accountType);
        parkingSystem.createAccount(user);
        Message resp = new Message(Message.TYPE_REGISTER);
        resp.setStatus("success");
        resp.setText("Account created");
        send(resp);

        System.out.println("[Client " + clientId + "] Registered new user: " + user);
    }
    
    private void handleLogin(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing login for client " + clientId);

        String payload = message.getText();
        String email = null;
        String password = null;

        if(payload != null) {
        	String[] parts = payload.split("\\|", 2);
        	if(parts.length == 2) {
        		email = parts[0];
        		password = parts[1];
        	}
        }
        
        Message resp = new Message(Message.TYPE_LOGIN);
        
        if(email == null || password == null) {
        	resp.setStatus("error");
        	resp.setText("Invalid login payload");
        	send(resp);
        	return;
        }
        
        User user = parkingSystem.login(email, password);
        
        if(user == null) {
        	resp.setStatus("error");
        	resp.setText("Invalid email or password");
        	send(resp);
        	return;
        }
        
        //save loged in user and mark them as logged in
        this.currentUser = user;
        this.loggedIn = true;
        //might change later
        String accountType = user.getAccountType() != null ? user.getAccountType() : "";
        String data = user.getID() + "|" +user.getFirstName() + "|" +user.getLastName() + "|" +user.getEmail() + "|" +accountType;
        
        resp.setStatus("success");
        resp.setText(data);
        send(resp);
        
        
    }

    private void handleText(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing text: " + message.getText());

        //MIGHT CHANGE THIS
        String capitalizedText = message.getText() == null ? "" : message.getText().toUpperCase();

        Message response = new Message(Message.TYPE_TEXT);
        response.setStatus("success");
        response.setText(capitalizedText);

        send(response);
        System.out.println("[Client " + clientId + "] Sent capitalized text: " + capitalizedText);
    }

    private void handleLogout(Message message) throws IOException {
        System.out.println("[Client " + clientId + "] Processing logout...");

        loggedIn = false;

        Message response = new Message(Message.TYPE_LOGOUT);
        response.setStatus("success");
        response.setText("Logout successful");
        send(response);

        System.out.println("[Client " + clientId + "] Logout successful");
    }

    /** Send a message to this client */
    public synchronized void send(Message message) throws IOException {
        if (out == null) return;
        out.writeObject(message);
        out.flush();
        out.reset();
    }

    /** Used by server to push events (broadcast, sendTo) */
    public void onServerPush(Message message) {
        try {
            send(message);
        } catch (IOException e) {
            System.err.println("[Client " + clientId + "] Error during push: " + e.getMessage());
            close();
        }
    }

    public void close() {
        open.set(false);
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private void cleanup() {
        server.removeClient(clientId);
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {
        }
        try {
            if (out != null) out.close();
        } catch (IOException ignored) {
        }
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException ignored) {
        }

        System.out.println("[Client " + clientId + "] Connection closed");
    }

    //-----------------------------------
    //functions for slots to be visible between clients
    private void handleAddSlots(Message message) throws IOException {
        // only ADMINS can add slots
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getAccountType())) {
            Message resp = new Message(Message.TYPE_ADD_SLOTS);
            resp.setStatus("error");
            resp.setText("Only admins can add slots.");
            send(resp);
            return;
        }

        int count;
        try {
            count = Integer.parseInt(message.getText());
        } catch (Exception e) {
            Message resp = new Message(Message.TYPE_ADD_SLOTS);
            resp.setStatus("error");
            resp.setText("Invalid slot count: " + message.getText());
            send(resp);
            return;
        }

        if (count <= 0) {
            Message resp = new Message(Message.TYPE_ADD_SLOTS);
            resp.setStatus("error");
            resp.setText("Count must be positive.");
            send(resp);
            return;
        }

        int nextId = computeNextSlotIdFromServer();

        for (int i = 0; i < count; i++) {
            // Calculate floor and section for the new slot
            int floor = ParkingSlot.calculateFloor(nextId);
            String section = ParkingSlot.calculateSection(nextId);
            
            // Create slot with floor/section information
            ParkingSlot s = new ParkingSlot(nextId, floor, section);
            s.setOccupied(false);
            s.setVehicle(null);
            parkingSystem.addSlot(s);
            nextId++;
        }

        Message resp = new Message(Message.TYPE_ADD_SLOTS);
        resp.setStatus("success");
        resp.setText("Added " + count + " slots.");
        send(resp);

        System.out.println("[Client " + clientId + "] Admin added " + count + " slots.");
    	//broadcast updated slots list to everyone (should remove the need for a refresh button?!)
        java.util.List<ParkingSlot> allSlots = parkingSystem.getSlotsSnapshot();
        Message update = Message.makeSlotsUpdate(allSlots);
        server.broadcast(update);
    }

    private int computeNextSlotIdFromServer() {
        int max = 0;
        for (ParkingSlot s : parkingSystem.getSlots()) {
            if (s != null && s.getSlotID() > max) {
                max = s.getSlotID();
            }
        }
        return max + 1;
    }
	
    private void handleGetSlots(Message message) throws IOException {
        // Ignore message.getText() filters for now; return all slots snapshot to send actual objects
        java.util.List<ParkingSlot> snapshot = parkingSystem.getSlotsSnapshot();

        Message resp = new Message(Message.TYPE_SLOTS_DATA);
        resp.setStatus("success");
        resp.setSlots(snapshot);
        send(resp);
    }
    
    private void handleGetTickets(Message message) throws IOException {
        Message resp = new Message(Message.TYPE_TICKETS_DATA);

        if (currentUser == null) {
            resp.setStatus("error");
            resp.setText("Must be logged in to view tickets.");
            send(resp);
            return;
        }

        java.util.List<Ticket> snapshot = new java.util.ArrayList<>();

        if (currentUser instanceof Client client) {
            //customers see only their tickets (active + history)
            snapshot.addAll(client.getActiveTickets());
            snapshot.addAll(client.getTicketHistory());
        } else if (currentUser instanceof Admin) {
            //admins see all tickets in the system
            snapshot.addAll(parkingSystem.getTicketsSnapshot());
        } else {
            resp.setStatus("error");
            resp.setText("Unsupported user type for ticket view.");
            send(resp);
            return;
        }

        resp.setStatus("success");
        resp.setTickets(snapshot);
        send(resp);
    }
    
    //emthod for handling the slot removal:
    private void handleRemoveSlot(Message message) throws IOException {
        String text = message.getText();
        int slotId;

        try {
            slotId = Integer.parseInt(text.trim());
        } catch (Exception e) {
            Message resp = new Message(Message.TYPE_REMOVE_SLOT);
            resp.setStatus("error");
            resp.setText("Invalid slot id: " + text);
            send(resp);
            return;
        }

        // Only admins are allowed to remove slots
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getAccountType())) {
            Message resp = new Message(Message.TYPE_REMOVE_SLOT);
            resp.setStatus("error");
            resp.setText("Only admins can remove slots");
            send(resp);
            return;
        }

        boolean removed = parkingSystem.removeSlotById(slotId);

        Message resp = new Message(Message.TYPE_REMOVE_SLOT);
        if (!removed) {
            resp.setStatus("error");
            resp.setText("No slot with id " + slotId);
        } else {
            resp.setStatus("success");
            resp.setText("Slot " + slotId + " removed.");
        	//broadcast updated slots list to everyone (should remove the need for a refresh button?!)
            java.util.List<ParkingSlot> allSlots = parkingSystem.getSlotsSnapshot();
            Message update = Message.makeSlotsUpdate(allSlots);
            server.broadcast(update);
        }
        send(resp);
    }
    
 // helper to find a slot by ID
    private ParkingSlot findSlotById(int slotId) {
        for (ParkingSlot s : parkingSystem.getSlots()) {
            if (s.getSlotID() == slotId) {
                return s;
            }
        }
        return null;
    }

    // handle reservation messages from a client
    private void handleReserveSlot(Message message) throws IOException {
        int slotId = message.getSlotId();
        Message resp = new Message(Message.TYPE_RESERVE_SLOT);

        if (slotId <= 0) {
            resp.setStatus("error");
            resp.setText("Invalid slot id: " + slotId);
            send(resp);
            return;
        }
        
        if (!(currentUser instanceof Client client)) {
            resp.setStatus("error");
            resp.setText("Only logged-in customers can reserve slots.");
            send(resp);
            return;
        }

        ParkingSlot slot = findSlotById(slotId);
        if (slot == null) {
            resp.setStatus("error");
            resp.setText("No slot with id " + slotId);
            send(resp);
            return;
        }

        if (slot.isOccupied() || slot.isOutOfService()) {
            resp.setStatus("error");
            resp.setText("Slot " + slotId + " is not available!");
            send(resp);
            return;
        }
        
        String plateNumber = message.getText();
        if (plateNumber == null || plateNumber.isBlank()) {
            resp.setStatus("error");
            resp.setText("No plate number provided.");
            send(resp);
            return;
        }
        
        // Find or create a Vehicle for this client by plate
        Vehicle vehicle = null;
        for (Vehicle v : client.getRegisteredVehicles()) {
            if (plateNumber.equalsIgnoreCase(v.getPlateNumber())) {
                vehicle = v;
                break;
            }
        }
        if (vehicle == null) {
            // simple default vehicle if not registered
            vehicle = new Car(plateNumber, "Unknown", "Unknown", null, VehicleType.REGULAR);
            client.registerVehicle(vehicle);
        }

        try {
            Ticket ticket = ticketService.createTicket(client, vehicle, slot);
            if (ticket == null) {
                resp.setStatus("error");
                resp.setText("Failed to create ticket.");
                send(resp);
                return;
            }

            //marked the slot as rseserved
            resp.setStatus("success");
            resp.setText("Slot " + slotId + " reserved on server. Ticket #" + ticket.getTicketID());
            send(resp);

            // Broadcast updated slots to all clients
            java.util.List<ParkingSlot> allSlots = parkingSystem.getSlotsSnapshot();
            Message update = Message.makeSlotsUpdate(allSlots);
            server.broadcast(update);

        } catch (Exception ex) {
            resp.setStatus("error");
            resp.setText("Error reserving slot: " + ex.getMessage());
            send(resp);
        }
    }
    
}