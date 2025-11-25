package model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for ticket operations
 * Wraps ParkingSystem ticket functionality for cleaner API
 * 
 * @author Group 7
 */
public class TicketService {
	private final ParkingSystem parkingSystem;
	
	public TicketService() {
		this.parkingSystem = ParkingSystem.getInstance();
	}
	
	/**
	 * Create a new parking ticket
	 * @param client The client requesting parking
	 * @param vehicle The vehicle to park
	 * @param slot The parking slot
	 * @return The created ticket
	 * @throws IllegalArgumentException if any parameter is null
	 * @throws IllegalStateException if slot is already occupied
	 */
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
	
	/**
	 * Close a ticket and release the parking slot
	 * @param ticketID The ticket ID to close
	 * @param exitTime The exit time
	 * @throws IllegalArgumentException if ticket not found or already closed
	 */
	public void closeTicket(int ticketID, LocalDateTime exitTime) {
		if (exitTime == null) {
			throw new IllegalArgumentException("Exit time cannot be null");
		}
		
		// Find ticket
		Ticket ticket = findTicketById(ticketID);
		if (ticket == null) {
			throw new IllegalArgumentException("Ticket not found: " + ticketID);
		}
		if (!ticket.isActive()) {
			throw new IllegalStateException("Ticket already closed: " + ticketID);
		}
		
		// Validate exit time
		if (exitTime.isBefore(ticket.getEntryTime())) {
			throw new IllegalArgumentException("Exit time cannot be before entry time");
		}
		
		parkingSystem.endParking(ticketID);
	}
	
	/**
	 * Get all currently active tickets
	 * @return List of active tickets
	 */
	public List<Ticket> listActiveTickets() {
		return parkingSystem.getActiveTickets();
	}
	
	/**
	 * Extend the expiration time of a ticket
	 * @param ticketID The ticket ID
	 * @param minutes Number of minutes to extend
	 * @throws IllegalArgumentException if ticket not found or minutes negative
	 * @throws IllegalStateException if ticket is closed
	 */
	public void extendExpiration(int ticketID, int minutes) {
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
		
		// TODO: Implement expiration extension logic when Ticket has expirationTime field
		// For now, just validate parameters
	}
	
	/**
	 * Run overstay checks on all active tickets
	 */
	public void runOverstayChecks() {
		parkingSystem.runOverStayChecks();
	}
	
	/**
	 * Helper method to find ticket by ID
	 */
	private Ticket findTicketById(int ticketID) {
		List<Ticket> allTickets = parkingSystem.getTickets();
		synchronized(allTickets) {
			for (Ticket t : allTickets) {
				if (t.getTicketID() == ticketID) {
					return t;
				}
			}
		}
		return null;
	}
}