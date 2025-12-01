package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSystem implements Serializable  {
	private static final long serialVersionUID = 1L;
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
    //persistence of the system
    private static final Path DEFAULT_STATE_FILE = Path.of("parkzone.dat");
    
    public static synchronized ParkingSystem loadOrCreate(Path path) {
        if (instance != null) {
            return instance;
        }

        if (path != null && Files.exists(path)) {
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
                Object obj = in.readObject();
                if (obj instanceof ParkingSystem ps) {
                    instance = ps;
                    return instance;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[ParkingSystem] Failed to load state from " + path + ": " + e.getMessage());
            }
        }

        // fallback: new instance
        instance = new ParkingSystem();
        return instance;
    }

    public static synchronized ParkingSystem loadOrCreateDefault() {
        return loadOrCreate(DEFAULT_STATE_FILE);
    }
    
    public synchronized void saveToFile(Path path) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(this);
            out.flush();
        } catch (IOException e) {
            System.err.println("[ParkingSystem] Failed to save state to " + path + ": " + e.getMessage());
        }
    }
    
    //--------------------------------------------------
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
    
    //-----------------------------------------------
    //registering vehicle functions
    public void registerVehicle(Client user, Vehicle vehicle) {
    	if(user == null || vehicle == null) {
    		return;
    	}
    	user.registerVehicle(vehicle);
    }

    //--------------------------------------------------------
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
    
    public Ticket issueTicket(Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime) {
        if (vehicle == null || slot == null || slot.isOccupied()) {
            return null;
        }
        // assign the vehicle to the slot
        slot.assignVehicle(vehicle);
        // create ticket with the chosen entryTime
        Ticket ticket = new Ticket(vehicle, slot, entryTime);
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
    
    //going to try this function instead of the endParking func for testing
    public Ticket closeTicket(int ticketID) {
        Ticket t = findTicketById(ticketID);
        if (t == null || !t.isActive()) {
            return null;
        }
        t.closeTicket(LocalDateTime.now());
        return t;
    }
    
    public List<Ticket> getActiveTickets(){
    	synchronized (tickets) {
            return tickets.stream()
                    .filter(Ticket::isActive)
                    .collect(Collectors.toList());
        }
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
    
    //--------------------------------------------------------
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
    
    //Safe copy for sending over the wire
    public List<ParkingSlot> getSlotsSnapshot() {
        synchronized (slots) {
            return new ArrayList<>(slots);
        }
    }

    //Safe copy for reporting
    public List<Ticket> getTicketsSnapshot() {
        synchronized (tickets) {
            return new ArrayList<>(tickets);
        }
    }
    
    public static synchronized ParkingSystem getInstance() {
        if (instance == null) {
            instance = new ParkingSystem();
        }
        return instance;
    }
    
    //------------------------------------------------
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
    
    //--------------------------------------------------------------------------
    //system level operations
    public void persistSystemState() {
    	saveToFile(DEFAULT_STATE_FILE);
    }
    
    public void generateWeeklyReports() {
    	//implement later
    }
    
    public void runOverStayChecks() {
    	//implement later
    }
    
    public void broadcastSpaceUpdates() {
    	//implement later, should be left to ParkingSystemServer using the broadcast() instead
    }
    
    //--------------------------------------------------------------
    //helper functions
    //ticket ID
    private Ticket findTicketById(int ticketID) {
    	synchronized(tickets){
    		for(Ticket t: tickets) {
    			if(t.getTicketID() == ticketID) {
    				return t;
    			}
    		}
    	}return null;
    }
    //remove slots
    public boolean removeSlotById(int slotID) {
        synchronized (slots) {
            for (int i = 0; i < slots.size(); i++) {
                ParkingSlot s = slots.get(i);
                if (s != null && s.getSlotID() == slotID) {
                    slots.remove(i);
                    return true;
                }
            }
        }
        return false;
    }
    
    
}
