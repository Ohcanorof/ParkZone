package model;

import java.time.LocalDateTime;
import java.time.Duration;

public class Ticket {

    private static int nextId = 1;
    private int ticketID;
    private String ticketIDCode;  // ✨ ADD THIS FIELD
    private double totalFee;
    private ParkingSlot slot;
    private boolean isActive;
    private Vehicle vehicle;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double feeOverride = null;  // ✨ ADD FOR ADMIN OVERRIDE

    // Single constructor
    public Ticket(Vehicle vehicle, ParkingSlot slot, LocalDateTime entryTime) {
        this.ticketID = generateNextId();
        this.vehicle = vehicle;
        this.slot = slot;
        this.isActive = true;
        
        // ✨ FIX: Set field, not parameter
        if (entryTime == null) {
            this.entryTime = LocalDateTime.now();
        } else {
            this.entryTime = entryTime;
        }
        
        this.totalFee = 0.0;
        
        // ✨ FIX: Generate human-readable ticket code
        this.ticketIDCode = ParkingSystem.getInstance().generateTicketID();
    }

    // ID generation
    private static synchronized int generateNextId() {
        return nextId++;
    }

    // Ticket closing
    public void closeTicket(LocalDateTime exitTime) {
        // ✨ FIX: Comparison, not assignment
        if (!isActive) {
            return; // ticket already closed
        }
        
        // If exit time is null, set it to current time
        if (exitTime == null) {
            this.exitTime = LocalDateTime.now();
        } else {
            this.exitTime = exitTime;
        }
        
        this.isActive = false;
        this.totalFee = generateFee();
    }

    // Calculate duration of stay
    public int calculateDuration() {
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        Duration duration = Duration.between(entryTime, end);
        return (int) duration.toMinutes();
    }

    public double generateFee() {
        if (vehicle == null) {
            totalFee = 0.0;
            return totalFee;
        }
        
        int durationMinutes = calculateDuration();
        totalFee = vehicle.calculateFee(durationMinutes);
        return totalFee;
    }

    // ✨ NEW: Admin fee override
    public void setFeeOverride(double fee) {
        this.feeOverride = fee;
    }

    // Getters
    public int getTicketID() {
        return ticketID;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    // ✨ FIX: Return override if set, otherwise calculated fee
    public double getTotalFee() {
        if (feeOverride != null) {
            return feeOverride;
        }
        return totalFee;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setSlot(ParkingSlot slot) {
        this.slot = slot;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    // ✨ FIX: Use the stored ticketIDCode instead of generating on-the-fly
    public String getTicketIDCode() {
        return ticketIDCode != null ? ticketIDCode : ("UNKNOWN" + ticketID);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketID=" + ticketID +
                ", compositeCode='" + getTicketIDCode() + '\'' +
                ", vehicle=" + (vehicle != null ? vehicle.getPlateNumber() : "none") +
                ", slot=" + (slot != null ? slot.getSlotID() : -1) +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                ", totalFee=" + getTotalFee() +
                ", isActive=" + isActive +
                '}';
    }
}