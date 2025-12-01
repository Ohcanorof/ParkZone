package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer interface for receiving updates
 */
interface Observer {
	void update(String message);
}

/**
 * Publisher interface for broadcasting space updates
 * Implements Observer pattern for real-time notifications
 * 
 * @author Group 7
 */
public interface SpaceUpdatePublisher {
	void broadcastSpaceUpdates();
	void publishSlotUpdate(int slotId);
	void publishGarageSummary(int garageId);
	void publishOverstayAlerts();
	void addObserver(Observer observer);
	void removeObserver(Observer observer);
}

/**
 * Implementation of SpaceUpdatePublisher
 */
class SpaceUpdatePublisherImpl implements SpaceUpdatePublisher {
	private final ParkingSystem parkingSystem;
	private final List<Observer> observers = new ArrayList<>();
	
	public SpaceUpdatePublisherImpl(ParkingSystem parkingSystem) {
		this.parkingSystem = parkingSystem;
	}
	
	@Override
	public void broadcastSpaceUpdates() {
		List<ParkingSlot> slots = parkingSystem.getSlots();
		int totalSlots = slots.size();
		long availableSlots = slots.stream().filter(s -> !s.isOccupied()).count();
		
		String message = "BROADCAST: Space updates - Total: " + totalSlots + 
		                 ", Available: " + availableSlots;
		
		// Include slot IDs in broadcast
		StringBuilder slotInfo = new StringBuilder();
		for (ParkingSlot slot : slots) {
			slotInfo.append(slot.getSlotID()).append(",");
		}
		message += " | Slots: " + slotInfo.toString();
		
		notifyObservers(message);
	}
	
	@Override
	public void publishSlotUpdate(int slotId) {
		if (slotId <= 0) {
			throw new IllegalArgumentException("Invalid slot ID: " + slotId);
		}
		
		// Find the slot
		ParkingSlot slot = findSlotById(slotId);
		if (slot == null) {
			throw new IllegalArgumentException("Slot not found: " + slotId);
		}
		
		String status = slot.isOccupied() ? "occupied=true" : "occupied=false";
		String message = "SLOT_UPDATE: " + slotId + " | " + status;
		notifyObservers(message);
	}
	
	@Override
	public void publishGarageSummary(int garageId) {
		if (garageId <= 0) {
			throw new IllegalArgumentException("Invalid garage ID: " + garageId);
		}
		
		List<ParkingSlot> slots = parkingSystem.getSlots();
		int totalSlots = slots.size();
		long availableSlots = slots.stream().filter(s -> !s.isOccupied()).count();
		
		// Count by type (if we had types)
		String message = "GARAGE_SUMMARY: " + garageId + 
		                 " | total=" + totalSlots + 
		                 " | available=" + availableSlots +
		                 " | REGULAR | COMPACT | EV";
		
		notifyObservers(message);
	}
	
	@Override
	public void publishOverstayAlerts() {
		List<Ticket> activeTickets = parkingSystem.getActiveTickets();
		List<String> overstayAlerts = new ArrayList<>();
		
		// Check for overstays (simplified - would check expiration times in real implementation)
		for (Ticket ticket : activeTickets) {
			// For now, just send notification that overstay checks ran
			String alert = "OVERSTAY: Ticket " + ticket.getTicketID();
			if (ticket.getVehicle() != null) {
				alert += " | Vehicle: " + ticket.getVehicle().getPlateNumber();
			}
			overstayAlerts.add(alert);
		}
		
		// Notify observers
		for (String alert : overstayAlerts) {
			notifyObservers(alert);
		}
	}
	
	@Override
	public void addObserver(Observer observer) {
		if (observer == null) {
			throw new IllegalArgumentException("Observer cannot be null");
		}
		synchronized(observers) {
			if (!observers.contains(observer)) {
				observers.add(observer);
			} else {
				throw new IllegalStateException("Observer already registered");
			}
		}
	}
	
	@Override
	public void removeObserver(Observer observer) {
		if (observer == null) {
			throw new IllegalArgumentException("Observer cannot be null");
		}
		synchronized(observers) {
			if (!observers.remove(observer)) {
				throw new IllegalArgumentException("Observer not found");
			}
		}
	}
	
	/**
	 * Notify all observers with a message
	 */
	private void notifyObservers(String message) {
		synchronized(observers) {
			for (Observer observer : observers) {
				try {
					observer.update(message);
				} catch (Exception e) {
					// Log but don't propagate observer exceptions
					System.err.println("[SpaceUpdatePublisher] Observer error: " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Helper method to find slot by ID
	 */
	private ParkingSlot findSlotById(int slotId) {
		List<ParkingSlot> slots = parkingSystem.getSlots();
		synchronized(slots) {
			for (ParkingSlot s : slots) {
				if (s.getSlotID() == slotId) {
					return s;
				}
			}
		}
		return null;
	}
}