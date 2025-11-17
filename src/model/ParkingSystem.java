package model;

public class ParkingSystem {
    private static ParkingSystem instance;  // Only ONE instance
    private List<User> users;
    private List<ParkingSlot> slots;
    private List<Ticket> tickets;
    
    // Private constructor prevents multiple instances
    private ParkingSystem() {
        users = Collections.synchronizedList(new ArrayList<>());
        slots = Collections.synchronizedList(new ArrayList<>());
        tickets = Collections.synchronizedList(new ArrayList<>());
    }
    
    // Thread-safe singleton access
    public static synchronized ParkingSystem getInstance() {
        if (instance == null) {
            instance = new ParkingSystem();
        }
        return instance;
    }
    
    // Thread-safe operations
    public synchronized Ticket issueTicket(Vehicle vehicle, ParkingSlot slot) {
        // Only ONE thread can execute this at a time
        slot.assignVehicle(vehicle);
        Ticket ticket = new Ticket(vehicle, slot, LocalDateTime.now());
        tickets.add(ticket);
        return ticket;
    }
    
    public synchronized List<ParkingSlot> getAvailableSlots() {
        return slots.stream()
            .filter(slot -> !slot.isOccupied())
            .collect(Collectors.toList());
    }
}
