package model;

import uiwindows.RolePanel;
import uiwindows.LoginPanel;
import uiwindows.RegisterPanel;
import uiwindows.SlotsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//not complete!
//CHANGES NEEDED
//Role page doesnt work buttons need to redirect to the correct role login
//remove the slots page or add in the actual maps
//
//need to make a 2 login pages, one for admin, one for customer (title should say which is which)
//AAdmin view: should be able to upload a map that you can click on (2 maps, one free spots, one w/ some taken spots)
//should be able to view the reservations made by other, have the ability to remove them (notifies the user of the removal)
//can also prompt user to pay for it (help pay for it)
//Customer view: should be able to have a dashboard, tabs on the left, view tickets, spots, make a reservation, view reservations, and end the reservation
//
/*
 * customer page: buttons on the left, will turn it into a menu thing later
 * weird bug when using back buttons and end up in the welcome page, the login buttons dont work
 * will need to fix that.
 */
//

public class ClientGUI {

	//stuff for swing (GUI)
	private JFrame frame;
	private CardLayout cardLayout;
	private JPanel root;
	
	//the window names
	private static final String RolePage = "ROLE";
	private static final String LoginPage = "LOGIN";
	private static final String RegisterPage = "REGISTER";
	private static final String SlotPage = "SLOTS";
	
	//the windows
	private RolePanel rolep;
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
			rolep = new RolePanel(this);
			lp = new LoginPanel(this);
			rp = new RegisterPanel(this);
			sp = new SlotsPanel(this);
			
			root.add(rolep, RolePage);
			root.add(lp, LoginPage);
			root.add(rp, RegisterPage);
			root.add(sp, SlotPage);
			
			frame.setContentPane(root);
			showRolepage();
			frame.setVisible(true);
		});
	}
	
	public void connect(String host, int port) {
	    this.serverHost = host;
	    this.serverPort = port;

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
		
		if(!connected) {
			return;
		}
		events = null;
		session = null;
		connected = false;
		currentUser = null;
		System.out.println("[ClientGUI] Disconnected");
	}
	
	public boolean login(String email, String password) {
	    if (!connected || events == null) {
	        handleError("Not connected to server.");
	        return false;
	    }

	    try {
	        boolean ok = events.login(email, password);
	        if (ok) {
	            // TODO: when you have real login, fetch actual User from server
	            this.currentUser = null; // placeholder
	            this.session = null;     // or new AuthSession(...)
	        }
	        return ok;
	    } catch (IOException | ClassNotFoundException e) {
	        handleError("Login error: " + e.getMessage());
	        return false;
	    }
	}
	
	public void logout() {
		//TODO: notify the server/ invalidate session
		currentUser = null;
		session = null;
		showLoginPage();
	}
	
	public List<ParkingSlot> refreshSlots(int garageId, String type){
		//TODO: server side SlotCatalog.findAvailable(garageId, type)
		//for now it just rets the current list (if there is one)
		return slots;
	}
	
	public void showSlots(int garageId, String type) {
		selectedGarageId = garageId;
		slots = refreshSlots(garageId, type);
		sp.loadSlots(slots);
		cardLayout.show(root, SlotPage);
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
	
	public void showRolepage() {
		cardLayout.show(root, RolePage);
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
	
	//GUI WINDOWS NOW IN THEIR OWN PACKAGE

	
	//placeholders (might have to add a class or two)
	class AuthSession{
		//addsession toke, userId, expiry, etc
	}
	

	

}
