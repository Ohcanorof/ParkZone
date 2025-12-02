package model;

import java.io.Serializable;

/**
 * ParkingSlot - Enhanced with multi-floor, multi-section support
 * Maintains ALL original functionality while adding floor/section architecture
 * 
 * Global ID: 1-100 (for display)
 * Composite ID: "1A-01" format (for backend logic)
 */
public class ParkingSlot implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Original fields
    private int SlotID;
    private boolean isOccupied;
    private Vehicle vehicle;
    private VehicleType allowedType;
    private double hourlyRate;
    private boolean outOfService; // for admin control
    
    // NEW: Floor/section fields
    private int floor;        // Floor number: 1-6
    private String section;   // Section letter: A-E
    
    //==========================================================================
    // CONSTRUCTORS
    //==========================================================================
    
    // No-arg constructor (original)
    public ParkingSlot() {
        this.floor = 1;
        this.section = "A";
    }
    
    // Single-arg constructor (original)
    public ParkingSlot(int slotID) {
        this.SlotID = slotID;
        this.isOccupied = false;
        this.vehicle = null;
        this.allowedType = null;   // ANY type by default
        this.hourlyRate = 0.0;
        // NEW: Auto-calculate floor/section
        this.floor = calculateFloor(slotID);
        this.section = calculateSection(slotID);
    }
    
    // NEW: Constructor with floor/section
    public ParkingSlot(int slotID, int floor, String section) {
        this.SlotID = slotID;
        this.isOccupied = false;
        this.vehicle = null;
        this.allowedType = null;
        this.hourlyRate = 0.0;
        this.floor = floor;
        this.section = section;
    }
    
    //==========================================================================
    // ORIGINAL VEHICLE MANAGEMENT METHODS
    //==========================================================================
    
    public void assignVehicle(Vehicle v) {
        this.vehicle = v;
        this.isOccupied = (vehicle != null);
    }
    
    // Removes vehicle from the slot and marks it as unoccupied
    public void removeVehicle() {
        this.vehicle = null;
        this.isOccupied = false;
    }
    
    //==========================================================================
    // ORIGINAL GETTERS
    //==========================================================================
    
    public int getSlotID() {
        return SlotID;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public VehicleType getAllowedType() {
        return allowedType;
    }
    
    public double getHourlyRate() {
        return hourlyRate;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public boolean isOutOfService() {
        return outOfService;
    }
    
    //==========================================================================
    // NEW: FLOOR/SECTION GETTERS
    //==========================================================================
    
    public int getFloor() {
        return floor;
    }
    
    public String getSection() {
        return section;
    }
    
    /**
     * Get slot number within its section (1-20)
     * Example: Global ID 25 → Slot 5 in Section B
     */
    public int getSlotNumberInSection() {
        int slotInSection = ((SlotID - 1) % 20) + 1;
        return slotInSection;
    }
    
    /**
     * Get composite ID in format "1A-01"
     * Format: [Floor][Section]-[SlotInSection]
     */
    public String getCompositeID() {
        return String.format("%d%s-%02d", floor, section, getSlotNumberInSection());
    }
    
    //==========================================================================
    // ORIGINAL SETTERS
    //==========================================================================
    
    public void setSlotID(int id) {
        this.SlotID = id;
    }
    
    public void setVehicle(Vehicle v) {
        this.vehicle = v;
        // Helps keep isOccupied in sync with the vehicle
        this.isOccupied = (v != null);
    }
    
    public void setOccupied(boolean b) {
        this.isOccupied = b;
    }
    
    public void setAllowedType(VehicleType allowedType) {
        this.allowedType = allowedType;
    }
    
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public void setOutOfService(boolean outOfService) {
        this.outOfService = outOfService;
    }
    
    //==========================================================================
    // NEW: FLOOR/SECTION SETTERS
    //==========================================================================
    
    public void setFloor(int floor) {
        this.floor = floor;
    }
    
    public void setSection(String section) {
        this.section = section;
    }
    
    //==========================================================================
    // NEW: STATIC HELPER METHODS (PUBLIC for ClientHandler)
    //==========================================================================
    
    /**
     * Calculate floor from global slot ID (1-600)
     * Slots 1-100 = Floor 1, 101-200 = Floor 2, etc.
     */
    public static int calculateFloor(int slotID) {
        return ((slotID - 1) / 100) + 1;
    }
    
    /**
     * Calculate section from global slot ID (1-600)
     * Each floor has 5 sections: A=x01-x20, B=x21-x40, C=x41-x60, D=x61-x80, E=x81-x00
     */
    public static String calculateSection(int slotID) {
        int sectionIndex = ((slotID - 1) % 100) / 20;
        return String.valueOf((char)('A' + sectionIndex));
    }
    
    //==========================================================================
    // HELPER METHODS
    //==========================================================================
    
    /**
     * Check if a vehicle type is allowed in this slot
     */
    public boolean isVehicleTypeAllowed(VehicleType vehicleType) {
        if (allowedType == null) {
            return true; // Any type allowed
        }
        return allowedType == vehicleType;
    }
    
    @Override
    public String toString() {
        return "ParkingSlot{" +
                "slotID=" + SlotID +
                ", compositeID=" + getCompositeID() +
                ", floor=" + floor +
                ", section=" + section +
                ", isOccupied=" + isOccupied +
                ", allowedType=" + (allowedType != null ? allowedType : "ANY") +
                ", hourlyRate=" + hourlyRate +
                ", vehicle=" + (vehicle != null ? vehicle.getPlateNumber() : "none") +
                ", outOfService=" + outOfService +
                '}';
    }
}