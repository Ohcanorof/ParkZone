package model;

import uiwindows.LoginPanel;
import uiwindows.RegisterPanel;
import uiwindows.SlotsPanel;
import model.ParkingSlot;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import model.Ticket;
import java.util.Collections;
import model.Vehicle;
import model.Message;
import java.lang.ClassNotFoundException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//not complete!
// CHANGES NEEDED
// Removed the RolePanel.java, seemed redundant
// clients are real now, and they can see their account info (role is hardcoded rn, chage later)
//
// hooking up the class functions with the gui, so now clients are real (they werent before, just a demo)

// needed updates:
//
// Admin view: should be able to upload a map that you can click on (2 maps, one free spots, one w/ some taken spots)
// should be able to view the reservations made by other, have the ability to remove them (notifies the user of the removal)
// can also prompt user to pay for it (help pay for it)
//
// Customer view: should be able to have a dashboard, tabs on the left, view tickets, spots, make a reservation, view reservations, and end the reservation
//
/* what im working on right now:
 * Clients should be able to see the color change in slots when another customer reserves a spot.
 * Admins should be able to set slots with certain types view all current reservations in the system,
 * might let admins click on the slots themselves to allow for type setting.
 * 
 * Slot map should change colors when they are available or not, slots should show the type when cliking on the slot to reserve it,
 *  Filter slots doesnt work yet,
 * 
 * 
 * 
 * Working content: 
 * customers can login register vechilces, view the slots, register a vehicle, reserve a spot, \
 * veiw their reservations, and pay them
 * Admins can add/remove parking slots live, set pricing on certain spots.
 */
//

public class ClientGUI {

	//stuff for swing (GUI)
	private JFrame frame;
	private CardLayout cardLayout;
	private JPanel root;
	
	//the window names
	private static final String LoginPage = "LOGIN";
	private static final String RegisterPage = "REGISTER";
	private static final String SlotPage = "SLOTS";
	
	//the windows
	private LoginPanel lp;
	private RegisterPanel rp;
	private SlotsPanel sp;

	
	//------------------------------------
	//atributes
	private EventStreamClient events;
	private AuthSession session;
	private User currentUser;
	private boolean connected;
	private String serverHost;
	private int serverPort;
	public int selectedGarageId;
	private int selectedVehicleId;
	private int selectedSlotId;
	private String Role;
	
	public List<ParkingSlot> slots = new ArrayList<>();
	
	//-----------------------------
	//methods
	
	public void start() {
		SwingUtilities.invokeLater(() ->{
			frame = new JFrame("ParkZone Client");
			frame.setSize(900, 600);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			
			//closing
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					disconnect();
					frame.dispose();
				}
			});
			
			cardLayout = new CardLayout();
			root = new JPanel(cardLayout);
			lp = new LoginPanel(this);
			rp = new RegisterPanel(this);
			sp = new SlotsPanel(this);
			
			root.add(lp, LoginPage);
			root.add(rp, RegisterPage);
			root.add(sp, SlotPage);
			
			frame.setContentPane(root);
			frame.setVisible(true);
		});
	}
	
	public void connect(String host, int port) {
	    this.serverHost = host;
	    this.serverPort = port;

	    //dont open a socket if you are already connected
	    if(connected && events != null) {
	    	return;
	    }
	    
	    try {
	        this.events = new EventStreamClient(host, port);
	        this.connected = true;
	    } catch (IOException e) {
	        this.connected = false;
	        handleError("Could not connect to server: " + e.getMessage());
	    }
	}
	
	public void disconnect() {
		//close socket/event stream, cleanup session (needs to test, not done)
		
		if(!connected && events == null) {
			return;
		}
		try {
			if(events != null) {
				events.close();//closing the socket and streams
			}
		}catch(Exception e) {
			System.err.println("[ClientGUI] Error while closing connection: "+ e.getMessage());
		}
		
		
		events = null;
		session = null;
		connected = false;
		currentUser = null;
		System.out.println("[ClientGUI] Disconnected");
	}
	
	public boolean login(String email, String password) {
	    //make sure there is a connection
		if (!connected || events == null) {
	    	
	    	if(serverHost == null || serverPort == 0) {
	    		handleError("Not connected to server.");
	        return false;
	    	}
	        
	    	connect(serverHost, serverPort);
	    	if(!connected || events == null) {
	    		//connect showed the error so ret false
	    		return false;
	    	}
	    }

	    try {
	    	//ask server to log in and ret a real user
	        User u = events.login(email, password);
	        if (u != null) {
	        	//assign currentUser first
	            this.currentUser = u;
	            //fill in account type from role if its needed
	            if (currentUser.getAccountType() == null && Role != null) {
	            	currentUser.setAccountType(Role);
	            }
	            this.session = null;//this is a place holder for the future AuthSession
	            return true;
	        }else {
	        	//login failed on the SERVER side
	        	return false;
	        }
	    } catch (IOException | ClassNotFoundException e) {
	        handleError("Login error: " + e.getMessage());
	        return false;
	    }
	}
	
	public void logout() {
		//TODO: notify the server/ invalidate session
		disconnect();
		showLoginPage();
	}
	
	public List<ParkingSlot> refreshSlots(int garageId, String type){
		 if (!connected || events == null) {
		        if (serverHost == null || serverPort == 0) {
		            handleError("Not connected to server.");
		            return slots;
		        }
		        connect(serverHost, serverPort);
		        if (!connected || events == null) {
		            return slots;
		        }
		    }

		    try {
		        List<ParkingSlot> fromServer = events.fetchSlotsFromServer(garageId, type);
		        this.slots = new ArrayList<>(fromServer);
		        return this.slots;
		    } catch (IOException | ClassNotFoundException e) {
		        handleError("Error fetching slots: " + e.getMessage());
		        return slots;
		    }
	}
	
	public void showSlots(int garageId, String type) {
		selectedGarageId = garageId;
		slots = refreshSlots(garageId, type);
		sp.loadSlots(slots);
		sp.updateRoleUI();
		cardLayout.show(root, SlotPage);
	}
	
	public List<Ticket> fetchTicketsFromServer() {
	    // if we're not connected, just give an empty list
	    if (!connected || events == null) {
	        return Collections.emptyList();
	    }
	    if (getCurrentUser() == null) {
	        return Collections.emptyList();
	    }
	    
	    try {
	        return events.fetchTicketsFromServer();
	    } catch (IOException | ClassNotFoundException ex) {
	        ex.printStackTrace();
	        // might also add thiis popup later
	        // JOptionPane.showMessageDialog(null,
	        //         "Failed to load tickets from server:\n" + ex.getMessage(),
	        //         "Network Error",
	        //         JOptionPane.ERROR_MESSAGE);
	        return Collections.emptyList();
	    }
	}
	
	public void openAdminSlotConfigDialog(ParkingSlot slot) {
	    if (sp != null && slot != null) {
	        sp.openAdminSlotConfigDialog(slot);
	    }
	}
	
	public Ticket issueTicket(int vehicleId, int slotId) {
		//needs to send an request to the server (not done)
		//TODO: (server work)
		// Vehicle v = currentUser.getRegisteredVehicles().findById(vehicleId)
        // ParkingSlot ps = slots.findById(slotId)
        // return ParkingSystem.issueTicket(v, ps) OR TicketService.createTicket(...)
		selectedVehicleId = vehicleId;
		selectedSlotId = slotId;
		return null;
	}
	
	public void openReservationDialog(ParkingSlot slot) {
		if(sp != null && slot != null) {
			sp.openReservationDialog(slot);
		}
	}
	
	public Ticket closeTicket(int ticketId) {
		//needs to send an request to the server (not done)
		// TODO (server): TicketService.closeTicket(ticketId, LocalDateTime.now())
		return null;
	}
	
	public boolean pay(int ticketId, String method) {
		// TODO (server): PaymentProcessor.takePayment(...)
		return false;
	}
	
	public void onSpaceUpdate(Object event) {
		// TODO: called by EventStreamClient when server broadcasts updates
		updateUI();
	}
	
	public void updateUI() {
		//refresh current view if needed
		if(sp != null) {
			sp.loadSlots(slots);
		}
	}
	
	public void setRole(String role) {
		this.Role = role;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}
	
	public String getRole() {
		return Role;
	}
	
	public void handleError(String message) {
		JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void selectGarage(int garageId) {
		selectedGarageId = garageId;
	}
	
	public void selectVehicle(int vehicleId) {
		selectedVehicleId = vehicleId;
	}
	
	public void selectSlot(int slotId) {
		selectedSlotId = slotId;
	}
	
	
	//-------------------------------------
	//nav helpers
	public void showLoginPage() {
		cardLayout.show(root, LoginPage);
	}
	
	public void showRegisterPage() {
		cardLayout.show(root, RegisterPage);
	}
	
	
	
	public void startAdminLogin() {
		Role = "ADMIN";
		lp.setRole(Role);
		showLoginPage();
	}
	
	public void startCustomerLogin() {
		Role = "CUSTOMER";
		lp.setRole(Role);
		showLoginPage();
	}
	public int getSelectedGarageId() {
		return selectedGarageId;
	}
	
	// Reservation helper function
	public void notifyServerSlotReserved(int slotId, Vehicle vehicle) {
	    String plate = (vehicle != null) ? vehicle.getPlateNumber() : null;

	    try {
	        boolean ok = events.reserveSlotOnServer(slotId, plate);
	        if (!ok) {
	            System.out.println("[ClientGUI] Server rejected reservation for slot " + slotId);
	        }
	    } catch (IOException | ClassNotFoundException ex) {
	        ex.printStackTrace();
	        // could also do:
	        // JOptionPane.showMessageDialog(null,"Failed to notify server about reservation:\n" + ex.getMessage(),"Network Error",JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	//placeholders (might have to add a class or two)
	class AuthSession{
		//addsession toke, userId, expiry, etc
	}
	
	public boolean createAccount(String firstName, String lastName, String email, String password, String accountType) {
		if(!connected || events == null) {
			if(serverHost == null || serverPort == 0) {
				handleError("Not connected to server.");
				return false;
			}
			connect(serverHost, serverPort);
			if(!connected || events == null) {
				return false;
			}
		}
		
		try {
			return events.registerAccount(firstName, lastName, email, password, accountType);
		}catch (IOException | ClassNotFoundException e) {
			handleError("Account creation error: " + e.getMessage());
			return false;
		}
		
		
	}
	//helper func for slots
	public boolean addSlotsOnServer(int count) {
	    if (!connected || events == null) {
	        if (serverHost == null || serverPort == 0) {
	            handleError("Not connected to server.");
	            return false;
	        }
	        connect(serverHost, serverPort);
	        if (!connected || events == null) {
	            return false;
	        }
	    }

	    try {
	        return events.addSlotsOnServer(count);
	    } catch (IOException | ClassNotFoundException e) {
	        handleError("Error adding slots: " + e.getMessage());
	        return false;
	    }
	}
	
	//gui func for removing slots
	public boolean removeSlot(int slotId) {
	    if (!connected || events == null) {
	        if (serverHost == null || serverPort == 0) {
	            handleError("Not connected to server.");
	            return false;
	        }
	        connect(serverHost, serverPort);
	        if (!connected || events == null) {
	            return false;
	        }
	    }

	    try {
	        return events.removeSlot(slotId);
	    } catch (IOException | ClassNotFoundException e) {
	        handleError("Remove slot error: " + e.getMessage());
	        return false;
	    }
	}

}
