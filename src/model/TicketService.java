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

    public Ticket createTicket(Client client, Vehicle vehicle, ParkingSlot slot) {
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

        return parkingSystem.issueTicket(vehicle, slot);
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

    public void closeTicket(int ticketID, LocalDateTime exitTime) {
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
        parkingSystem.endParking(ticketID);
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
    public boolean payTicket(int ticketID, String cardToken, double slotHourlyRate, String method) {
        Ticket t = findTicketById(ticketID);
        if (t == null) {
            throw new IllegalArgumentException("Ticket not found: " + ticketID);
        }

        if (!t.isActive()) {
            //already closed; may or may not allow paying closed tickets later
            //for now its allowed, but ensure exitTime set
            if (t.getExitTime() == null) {
                t.setExitTime(LocalDateTime.now());
            }
        } else {
            // close it now, then compute fee
            t.closeTicket(LocalDateTime.now());
        }

        // ompute the fee
        paymentProcessor.calculateFee(t, slotHourlyRate);

        //pretend to charge card
        boolean ok = paymentProcessor.takePayment(t, cardToken);
        if (!ok) {
            return false;
        }

        // record payment info on ticket
        t.setPaid(true);
        t.setPaymentMethod(method);

        return true;
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
