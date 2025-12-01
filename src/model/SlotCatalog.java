package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for parking slot operations
 * Manages finding available slots and reserving/releasing them
 * 
 * @author Group 7
 */
public class SlotCatalog {
	private final ParkingSystem parkingSystem;
	
	public SlotCatalog() {
		this.parkingSystem = ParkingSystem.getInstance();
	}
	
	/**
	 * Add a slot to the catalog
	 * @param slot The slot to add
	 */
	public void addSlot(ParkingSlot slot) {
		parkingSystem.addSlot(slot);
	}
	
	/**
	 * Find available parking slots
	 * @param garageID The garage ID to search (currently not filtered)
	 * @param typeHint The vehicle type hint (currently not filtered)
	 * @return List of available slots
	 */
	public List<ParkingSlot> findAvailable(int garageID, String typeHint) {
		List<ParkingSlot> allSlots = parkingSystem.getSlots();
		
		synchronized(allSlots) {
			return allSlots.stream()
				.filter(s -> !s.isOccupied())
				.filter(s -> {
					// TODO: Filter by garageID when ParkingSlot has garageID field
					return true;
				})
				.filter(s -> {
					// Filter by type if specified
					if (typeHint == null || typeHint.isEmpty() || 
					    typeHint.equalsIgnoreCase("ALL") || typeHint.equalsIgnoreCase("REGULAR")) {
						return true;
					}
					// TODO: Filter by type when ParkingSlot has type field
					return true;
				})
				.collect(Collectors.toList());
		}
	}
	
	/**
	 * Reserve a parking slot
	 * @param slotID The slot ID to reserve
	 * @throws IllegalArgumentException if slot not found
	 * @throws IllegalStateException if slot already occupied
	 */
	public void reserve(int slotID) {
		if (slotID <= 0) {
			throw new IllegalArgumentException("Invalid slot ID: " + slotID);
		}
		
		ParkingSlot slot = findSlotById(slotID);
		if (slot == null) {
			throw new IllegalArgumentException("Slot not found: " + slotID);
		}
		if (slot.isOccupied()) {
			throw new IllegalStateException("Slot already occupied: " + slotID);
		}
		
		slot.setOccupied(true);
	}
	
	/**
	 * Release a parking slot
	 * @param slotID The slot ID to release
	 * @throws IllegalArgumentException if slot not found
	 * @throws IllegalStateException if slot not occupied
	 */
	public void release(int slotID) {
		if (slotID <= 0) {
			throw new IllegalArgumentException("Invalid slot ID: " + slotID);
		}
		
		ParkingSlot slot = findSlotById(slotID);
		if (slot == null) {
			throw new IllegalArgumentException("Slot not found: " + slotID);
		}
		if (!slot.isOccupied()) {
			throw new IllegalStateException("Slot not occupied: " + slotID);
		}
		
		slot.removeVehicle();
		slot.setOccupied(false);
	}
	
	/**
	 * Helper method to find slot by ID
	 */
	private ParkingSlot findSlotById(int slotID) {
		List<ParkingSlot> slots = parkingSystem.getSlots();
		synchronized(slots) {
			for (ParkingSlot s : slots) {
				if (s.getSlotID() == slotID) {
					return s;
				}
			}
		}
		return null;
	}
}