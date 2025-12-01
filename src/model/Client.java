package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public class Client extends User implements Serializable{
	
	//client
	//represents a registered user of the parking system. they can register vehicles
	//request parking, and view/manage tickets.
	private static final long serialVersionUID = 1L;
	private final List<Vehicle> registeredVehicles = new ArrayList<>();
	private final List<Ticket> activeTickets = new ArrayList<>();
	private final List<Ticket> ticketHistory = new ArrayList<>();
	
	
	public Client() {
		super();
		super.actions = new Actionable[] {
				//fill with the client actions
				
		};
	}
	
	public Client(int ID, String firstName, String lastName, String email, String password) {
		super(ID, firstName, lastName, email, password);
		super.actions = new Actionable[] {
				//fill with client actions
		};
	
	}
	
	//methods
	public void registerVehicle(Vehicle vehicle) {
		addRegisteredVehicles(vehicle);
	}
	
	public void addRegisteredVehicles(Vehicle v) {
		if (v == null) {
			return;
		}
		if(!registeredVehicles.contains(v)) {
			registeredVehicles.add(v);
		}
	}
	
	public Ticket requestParking(ParkingSlot slot) {
		if(slot == null) {
			throw new IllegalArgumentException("Parking Slot cannot be null");
		}
		
		if(registeredVehicles.isEmpty()) {
			System.err.println("[client] No registered vehicles for client ID" + getID());
			return null;
		}
		
		//basic ID generation
		int nextTicketId = activeTickets.size() + ticketHistory.size() + 1;
		Vehicle  vehicle = registeredVehicles.get(0);//the primary vehicle
		
		Ticket ticket = new Ticket(nextTicketId, vehicle, slot, LocalDateTime.now());
		activeTickets.add(ticket);
		return ticket;
	}
	
	public void viewActiveTickets() {
		System.out.println("Active Tickets for client ID " + getID() + ":");
		if(activeTickets.isEmpty()) {
			System.out.println(" (none)");
			return;
		}
		
		for(Ticket t: activeTickets) {
			System.out.println(" " + t);
		}
	}
	
	public void viewTicketHistory() {
		System.out.println("Ticket history for client ID " + getID() + ":");
		if(ticketHistory.isEmpty()) {
			System.out.println(" (none)");
			return;
		}
		
		for(Ticket t: activeTickets) {
			System.out.println(" " + t);
		}
	}
	
	public List<Vehicle> getRegisteredVehicles() {
		return Collections.unmodifiableList(registeredVehicles);
	}
	
	public boolean removeRegisteredVehicle(String plate) {
		if (plate == null) {
			return false;
		}
		
		for(int i = 0; i < registeredVehicles.size(); i++) {
			Vehicle v = registeredVehicles.get(i);
			if(plate.equalsIgnoreCase(v.getPlateNumber())) {
				registeredVehicles.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public List<Ticket> getActiveTickets(){
		return Collections.unmodifiableList(activeTickets);
	}
	
	public List<Ticket> getTicketHistory(){
		return Collections.unmodifiableList(ticketHistory);
	}
	
	public void addToTicketHistory(Ticket t) {
		if(t == null) {
			return;
		}
		activeTickets.remove(t);
		if(!ticketHistory.contains(t)) {
			ticketHistory.add(t);
		}
	}
	
	public void addActiveTicket(Ticket t) {
        if (t == null) {
            return;
        }
        if (!activeTickets.contains(t)) {
            activeTickets.add(t);
        }
    }
	

}
