package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;
public class Admin extends User implements Serializable{
	// Slots this admin manages (can be a subset of all slots in ParkingSystem)
	private static final long serialVersionUID = 1L;
	private final List<ParkingSlot> managedSlots = new ArrayList<>();
	
	public Admin() {
		super();
		super.actions = new Actionable[] {
			//will add the admin actions here	
		};
		setAccountType("ADMIN");
	}
	
	
	public Admin(int ID, String firstName, String lastName, String email, String password) {
		super(ID, firstName, lastName, email, password);
		super.actions = new Actionable[] {
				//actions here
		};
		setAccountType("ADMIN");
	}
	
	
	//----------------------------------------------------------
	//admins managed slots
	
	//function to add a physical slot in the admins managed list
	public void addParkingSlot(ParkingSlot slot) {
		if (slot == null) return;
	    ParkingSystem ps = ParkingSystem.getInstance();
	    ps.addSlot(slot);          // add to system
		addManagedSlots(slot); //trakc the slot
	}
	
	//remove a slot from this admins managed list by ID 
	public void removeParkingSlot(int slotID) {
		ParkingSystem ps = ParkingSystem.getInstance();
	    ps.removeSlotById(slotID); // remove from global system
		removeManagedSlot(slotID);
	}
	
	public List<ParkingSlot> getManagedSlots(){
		return Collections.unmodifiableList(managedSlots);
	}
	
	public void addManagedSlots(ParkingSlot s) {
		if(s == null) {
			return;
		}
		if(!managedSlots.contains(s)) {
			managedSlots.add(s);
		}
	}
	
	public boolean removeManagedSlot(int id) {
		for(int i = 0; i < managedSlots.size(); i++) {
			if(managedSlots.get(i).getSlotID() == id) {
				managedSlots.remove(i);
				return true;
			}
		}
		return false;
	}
	
	//--------------------------------------
	//tickets and history  functions
	//view of all the active tickets in the system 
	public void viewAllActiveTickets() {
		ParkingSystem ps = ParkingSystem.getInstance();
		System.out.println("[Admin] Active tickets:");
        for (Ticket t : ps.getActiveTickets()) {
            System.out.println("  " + t);
        }
	}
	
	/** Console view of ticket history for a specific vehicle plate */
	public void viewVehicleHistory(String vehiclePlate) {
        if (vehiclePlate == null || vehiclePlate.isBlank()) {
            System.out.println("[Admin] Vehicle plate is blank.");
            return;
        }

        ParkingSystem ps = ParkingSystem.getInstance();
        System.out.println("[Admin] Ticket history for plate " + vehiclePlate + ":");
        for (Ticket t : ps.getTicketHistory()) {
            Vehicle v = t.getVehicle();
            if (v != null && vehiclePlate.equalsIgnoreCase(v.getPlateNumber())) {
                System.out.println("  " + t);
            }
        }
	}
	
	
     // --------------------------------------------------
        // Slot / pricing management (basic stubs for now)

        /**
         * Set hourly rate for the system or a default garage. (might just outright ignore garages)
         */
        public void setHourlyRate(double rate) {
            // TODO: connect this to ParkingGarage or system-wide config
            System.out.println("[Admin] setHourlyRate called with rate=" + rate + " (stub)");
        }
	
        /**
         * Mark a slot out of service.
         * For now it is marked as occupied with no vehicle,
         * so the slot shows up as "unavailable".
         */
        public void markSpaceOutOfService(int slotID) {
            ParkingSlot slot = findManagedSlotById(slotID);
            if (slot == null) {
                System.out.println("[Admin] Slot " + slotID + " not found in managed slots.");
                return;
            }
            slot.setOccupied(true);
            slot.setVehicle(null);
            System.out.println("[Admin] Slot " + slotID + " marked out of service.");
        }

        // Return an out-of-service space back into service (available)
        public void returnSpaceToService(int slotID) {
            ParkingSlot slot = findManagedSlotById(slotID);
            if (slot == null) {
                System.out.println("[Admin] Slot " + slotID + " not found in managed slots.");
                return;
            }
            slot.setOccupied(false);
            slot.setVehicle(null);
            System.out.println("[Admin] Slot " + slotID + " returned to service.");
        }

        // --------------------------------------------------
        // Helper

        private ParkingSlot findManagedSlotById(int slotID) {
            for (ParkingSlot s : managedSlots) {
                if (s.getSlotID() == slotID) {
                    return s;
                }
            }
            return null;
        }
	
	
}
