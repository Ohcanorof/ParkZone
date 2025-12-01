package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkingSystem {
    private static ParkingSystem instance;
    private final List<User> users;
    private final List<ParkingSlot> slots;
    private final List<Ticket> tickets;
    private int dailyTicketCounter = 0;  // ✨ MOVED TO TOP
    
    // Private constructor prevents multiple instances
    private ParkingSystem() {
        this.users = Collections.synchronizedList(new ArrayList<>());
        this.slots = Collections.synchronizedList(new ArrayList<>());
        this.tickets = Collections.synchronizedList(new ArrayList<>());
    }
    
    // ✨ SINGLETON INSTANCE
    public static synchronized ParkingSystem getInstance() {
        if (instance == null) {
            instance = new ParkingSystem();
        }
        return instance;
    }

    // ✨ TICKET ID GENERATION
    public synchronized String generateTicketID() {
        dailyTicketCounter++;
        
        java.time.LocalDate today = java.time.LocalDate.now();
        String dateStr = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Format: PZ-YYYYMMDD-####
        return String.format("PZ-%s-%04d", dateStr, dailyTicketCounter);
    }
    
    // ACCOUNT FUNCTIONS
    public void createAccount(User user) {
        if (user != null) {
            addUser(user);
        }
    }
    
    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        synchronized (users) {
            for (User u : users) {
                if (email.equals(u.getEmail()) && password.equals(u.getPassword())) {
                    return u;
                }
            }
        }
        return null;
    }
    
    // ✨ FIX: VEHICLE REGISTRATION
    public void registerVehicle(Client client, Vehicle vehicle) {
        if (client == null || vehicle == null) {
            return;
        }
        client.registerVehicle(vehicle);  // ✨ FIXED: Use 'client' parameter
        System.out.println("[ParkingSystem] Registered vehicle: " + 
            vehicle.getPlateNumber() + " for client " + client.getID());
    }

    // TICKET FUNCTIONS
    public Ticket issueTicket(Vehicle vehicle, ParkingSlot slot) {
        if (vehicle == null || slot == null || slot.isOccupied()) {
            return null;
        }
        
        // Assign vehicle to slot
        slot.assignVehicle(vehicle);
        
        // Create ticket
        Ticket ticket = new Ticket(vehicle, slot, LocalDateTime.now());
        
        addTicket(ticket);
        System.out.println("[ParkingSystem] Ticket issued: " + ticket.getTicketIDCode());
        return ticket;
    }
    
    // End parking session
    public void endParking(int ticketID) {
        Ticket ticket = findTicketById(ticketID);
        if (ticket != null && ticket.isActive()) {
            // Close ticket and calculate fee
            ticket.closeTicket(LocalDateTime.now());
            
            // Free up the slot
            ParkingSlot slot = ticket.getSlot();
            if (slot != null) {
                slot.removeVehicle();
                slot.setOccupied(false);
            }
            
            System.out.println("[ParkingSystem] Parking ended for ticket: " + 
                ticket.getTicketIDCode() + " | Fee: $" + ticket.getTotalFee());
        }
    }
    
    public List<Ticket> getActiveTickets() {
        List<Ticket> active = new ArrayList<>();
        synchronized (tickets) {
            for (Ticket t : tickets) {
                if (t.isActive()) {
                    active.add(t);
                }
            }
        }
        return active;
    }
    
    public List<Ticket> getTicketHistory() {
        List<Ticket> history = new ArrayList<>();
        synchronized (tickets) {
            for (Ticket t : tickets) {
                if (!t.isActive()) {
                    history.add(t);
                }
            }
        }
        return history;
    }
    
    // ACCESSORS
    public List<User> getUsers() {
        return users;
    }
    
    public List<ParkingSlot> getSlots() {
        return slots;
    }
    
    public List<Ticket> getTickets() {
        return tickets;
    }
    
    // ADDERS
    public void addUser(User u) {
        if (u != null) {
            synchronized (users) {
                users.add(u);
            }
        }
    }
    
    public void addSlot(ParkingSlot s) {
        if (s != null) {
            synchronized (slots) {
                slots.add(s);
            }
        }
    }
    
    public void addTicket(Ticket t) {
        if (t != null) {
            synchronized (tickets) {
                tickets.add(t);
            }
        }
    }
    
    // SYSTEM LEVEL OPS (TODO)
    public void persistSystemState() {
        // Implement later
    }
    
    public void generateWeeklyReports() {
        // Implement later
    }
    
    public void runOverStayChecks() {
        // Implement later
    }
    
    public void broadcastSpaceUpdates() {
        // Implement later
    }
    
    // HELPER FUNCTION
    private Ticket findTicketById(int ticketID) {
        synchronized (tickets) {
            for (Ticket t : tickets) {
                if (t.getTicketID() == ticketID) {
                    return t;
                }
            }
        }
        return null;
    }
}