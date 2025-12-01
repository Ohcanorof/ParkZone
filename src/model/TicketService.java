package model;

import java.time.LocalDateTime;
import java.util.List;

public class TicketService {
    private final ParkingSystem parkingSystem;
    private final PaymentProcessor paymentProcessor;

    public TicketService() {
        this.parkingSystem = ParkingSystem.getInstance();
        this.paymentProcessor = new PaymentProcessorImpl(new PaymentGatewayImpl());
    }

    public synchronized Ticket createTicket(Client client, Vehicle vehicle, ParkingSlot slot) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (slot == null) {
            throw new IllegalArgumentException("Slot cannot be null");
        }
        if (slot.isOccupied()) {
            throw new IllegalStateException("Slot is already occupied");
        }

        Ticket t = parkingSystem.issueTicket(vehicle, slot);
        if(t != null) {
        	client.addActiveTicket(t);
        }
        return t;
    }
    
    //overloaded version
    public Ticket createTicket(Client client, Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (slot == null) {
            throw new IllegalArgumentException("Slot cannot be null");
        }
        if (slot.isOccupied()) {
            throw new IllegalStateException("Slot is already occupied");
        }

        // use the new ParkingSystem overload
        return parkingSystem.issueTicket(vehicle, slot, entryTime);
    }

    //close ticket func
    public synchronized Ticket closeTicket(int ticketID, LocalDateTime exitTime) {
    	Ticket t = findTicketById(ticketID);
    	if (exitTime == null) {
            throw new IllegalArgumentException("Exit time cannot be null");
        }
        Ticket ticket = findTicketById(ticketID);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketID);
        }
        if (!ticket.isActive()) {
            throw new IllegalStateException("Ticket already closed: " + ticketID);
        }
        if (exitTime.isBefore(ticket.getEntryTime())) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }
        t.closeTicket(exitTime);

        // Free the slot
        ParkingSlot slot = t.getSlot();
        if (slot != null) {
            slot.setOccupied(false);
            slot.setVehicle(null);
        }

        return t;
    }

    public List<Ticket> listActiveTickets() {
        return parkingSystem.getActiveTickets();
    }

    public void extendExpiration(int ticketID, int minutes) {
        //can keep this as is or make it have more features later
        if (minutes < 0) {
            throw new IllegalArgumentException("Minutes cannot be negative");
        }
        Ticket ticket = findTicketById(ticketID);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketID);
        }
        if (!ticket.isActive()) {
            throw new IllegalStateException("Cannot extend closed ticket");
        }
        // TODO: implement when Ticket has expiration field
    }

    public void runOverstayChecks() {
        parkingSystem.runOverStayChecks();
    }

    //pay for a ticket in one place
    public synchronized boolean payTicket(int ticketID, String method) {
    	Ticket t = findTicketById(ticketID);
        if (t == null) {
            System.err.println("[TicketService] No ticket with ID " + ticketID);
            return false;
        }

        if (method == null || method.isBlank()) {
            System.err.println("[TicketService] No payment method provided for ticket " + ticketID);
            return false;
        }

        boolean success = paymentProcessor.takePayment(t, method);
        if (!success) {
            System.err.println("[TicketService] Payment failed for ticket " + ticketID);
            return false;
        }

        // If we wanted extra logic (mark ticket as paid, etc.) we’d do it here.

        return true;
    }
    
    public synchronized double getTicketFee(int ticketID) {
        Ticket t = findTicketById(ticketID);
        if (t == null) {
            throw new IllegalArgumentException("No ticket with ID " + ticketID);
        }
        return t.getTotalFee();
    }

    //helper for receipts
    public String generateReceipt(int ticketID) {
        Ticket t = findTicketById(ticketID);
        if (t == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketID);
        }
        return paymentProcessor.generateReceipt(t);
    }

    // ticket finder helper
    private Ticket findTicketById(int ticketID) {
        List<Ticket> allTickets = parkingSystem.getTickets();
        synchronized (allTickets) {
            for (Ticket t : allTickets) {
                if (t.getTicketID() == ticketID) {
                    return t;
                }
            }
        }
        return null;
    }
}
