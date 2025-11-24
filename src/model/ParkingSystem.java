package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSystem {
    private static ParkingSystem instance;  // Only ONE instance
    private final List<User> users;
    private final List<ParkingSlot> slots;
    private final List<Ticket> tickets;
    
    // Private constructor prevents multiple instances
    private ParkingSystem() {
        this.users = Collections.synchronizedList(new ArrayList<>());
        this.slots = Collections.synchronizedList(new ArrayList<>());
        this.tickets = Collections.synchronizedList(new ArrayList<>());
    }
    

    //methods
    //functions for accoutns/authentication
    public void createAccount(User user) {
    	if(user != null) {
    		addUser(user);
    	}
    }
    
    public User login(String email, String password) {
    	if(email == null || password == null) {
    		return null;
    	}
    	synchronized (users) {
    		for(User u : users) {
    			if(email.equals(u.getEmail()) && password.equals(u.getPassword())) {
    				return u;
    			}
    		}
    	}
    	return null;
    }
    
    //registering vehicle functions
    public void registerVehicle(Client user, Vehicle vehicle) {
    	if(user == null || vehicle == null) {
    		return;
    	}
    	user.registerVehicle(vehicle);
    }

    //Ticket functions
    public Ticket issueTicket(Vehicle vehicle, ParkingSlot slot) {
    	if(vehicle == null || slot == null|| slot.isOccupied()) {
    		return null;
    	}
    	//assign the vehicle to a slot
    	slot.assignVehicle(vehicle);
    	//make the ticket, and Ticket will handle the ticketID gen
    	Ticket ticket = new Ticket(vehicle, slot, LocalDateTime.now());
    	
    	addTicket(ticket);
    	return ticket;
    }
    
    //this function will end the parking for a ticket with its ticketID, it closes it
    //and then it calculates the fee
    public void endParking(int ticketID) {
    	Ticket ticket = findTicketById(ticketID);
    	if(ticket != null && ticket.isActive()) {
    		//close now
    		ticket.closeTicket(LocalDateTime.now());
    		
    		//free up the slot
    		ParkingSlot slot = ticket.getSlot();
    		if (slot != null) {
    			slot.removeVehicle();
    			slot.setOccupied(false);
    		}
    	}
    }
    
    public List<Ticket> getActiveTickets(){
    	List<Ticket> active = new ArrayList<>();
    	synchronized (tickets) {
    		for (Ticket t : tickets) {
    			if(t.isActive()) {
    				active.add(t);
    			}
    		}
    	}
    	return active;
    }
    
    public List<Ticket> getTicketHistory(){
    	List<Ticket> history = new ArrayList<>();
    	synchronized (tickets) {
    		for (Ticket t : tickets) {
    			if(!t.isActive()) {
    				history.add(t);
    			}
    		}
    	}
    	return history;
    }
    
    //accessors
    public List<User> getUsers(){
    	return users;
    }
    
    public List<ParkingSlot> getSlots(){
    	return slots;
    }
    
    public List<Ticket> getTickets(){
    	return tickets;
    }
    
    public static synchronized ParkingSystem getInstance() {
        if (instance == null) {
            instance = new ParkingSystem();
        }
        return instance;
    }
    
    //adders
    public void addUser(User u) {
    	if(u != null) {
    		synchronized (users) {
    			users.add(u);
    		}
    	}
    }
    
    public void addSlot(ParkingSlot s) {
    	if(s != null) {
    		synchronized (slots) {
    			slots.add(s);
    		}
    	}
    }
    
    public void addTicket(Ticket t) {
    	if(t != null) {
    		synchronized (tickets) {
    			tickets.add(t);
    		}
    	}
    }
    
    //system level ops
    public void persistSystemState() {
    	//implement later
    }
    
    public void generateWeeklyReports() {
    	//implement later
    }
    
    public void runOverStayChecks() {
    	//implement later
    }
    
    public void broadcastSpaceUpdates() {
    	//implement later
    }
    
    //helper function for ticketID
    private Ticket findTicketById(int ticketID) {
    	synchronized(tickets){
    		for(Ticket t: tickets) {
    			if(t.getTicketID() == ticketID) {
    				return t;
    			}
    		}
    	}return null;
    }
    
    
}
