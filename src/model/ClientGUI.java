package model;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//not complete!

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
	private int selectedGarageId;
	private int selectedVehicleId;
	private int selectedSlotId;
	private List<ParkingSlot> slots = new ArrayList<>();
	
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
			lp = new LoginPanel();
			rp = new RegisterPanel();
			sp = new SlotsPanel();
			
			root.add(lp, LoginPage);
			root.add(rp, RegisterPage);
			root.add(sp, SlotPage);
			
			frame.setContentPane(root);
			showLoginPage();
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
	    // Get all slots from the singleton ParkingSystem
	    ParkingSystem ps = ParkingSystem.getInstance();
	    this.slots = new ArrayList<>(ps.getSlots());
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
	private void showLoginPage() {
		cardLayout.show(root, LoginPage);
	}
	
	private void showRegisterPage() {
		cardLayout.show(root, RegisterPage);
	}
	
	
	//---------------------------------------
	//login window
	private class LoginPanel extends JPanel{
		private JTextField emailField;
		private JPasswordField passwordField;
		
		public LoginPanel() {
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(20, 20, 20, 20));
			
			JLabel title = new JLabel("ParkZone Login", SwingConstants.CENTER);
			title.setFont(new Font("Arial", Font.BOLD, 26));
			add(title, BorderLayout.NORTH);
			
			JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
			form.setBorder(new EmptyBorder(40, 220, 40, 220));
			
			emailField = new JTextField();
			passwordField = new JPasswordField();
			
			form.add(new JLabel("Email:"));
			form.add(emailField);
			
			form.add(new JLabel("Password:"));
			form.add(passwordField);
			
			add(form, BorderLayout.CENTER);
			
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
			
			JButton loginBtn = new JButton("Login");
			JButton registerBtn= new JButton("Create Account");
			
			loginBtn.addActionListener(e ->{
				String email = emailField.getText();
				String password = new String(passwordField.getPassword());
				
				if(login(email, password)) {
					//show slots list after success
					showSlots(0, "ALL");//default for now
				}
				else {
					handleError("Login failed. Check your credentials.");
				}
			});
			
			registerBtn.addActionListener(e -> showRegisterPage());
			
			buttons.add(loginBtn);
			buttons.add(registerBtn);
			add(buttons, BorderLayout.SOUTH);
			
			
		}
	}
	
	//------------------------------------------
	//register window
	private class RegisterPanel extends JPanel{
		private JTextField firstNameField;
		private JTextField lastNameField;
		private JTextField usernameField;
		private JTextField emailField;
		private JPasswordField passwordField;//might change this to JTextField
		
		public RegisterPanel() {
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(20,20,20,20));
			
			JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
			title.setFont(new Font("Arial", Font.BOLD, 26));
			add(title, BorderLayout.NORTH);
			
			JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
			form.setBorder(new EmptyBorder(20, 220, 20, 220));
			
			firstNameField = new JTextField();
			lastNameField = new JTextField();
			usernameField = new JTextField();
			emailField = new JTextField();
			passwordField = new JPasswordField();
			
			form.add(new JLabel("First Name:"));
			form.add(firstNameField);
			
			form.add(new JLabel("Last Name:"));
			form.add(lastNameField);
			
			form.add(new JLabel("Username:"));
			form.add(usernameField);
			
			form.add(new JLabel("Email:"));
			form.add(emailField);
			
			form.add(new JLabel("Password:"));
			form.add(passwordField);
			
			add(form, BorderLayout.CENTER);
			
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
			JButton createBtn = new JButton("Create Account");
			JButton backBtn = new JButton("Back to Login");
			
			createBtn.addActionListener(e ->{
				//TODO: (server): ParkingSystem.createAccount(new Client(...)) send create acc req to the server
				//simulating success if the fields arent empty
				if (firstNameField.getText().isBlank() ||
	                    lastNameField.getText().isBlank() ||
	                    usernameField.getText().isBlank() ||
	                    emailField.getText().isBlank() ||
	                    new String(passwordField.getPassword()).isBlank()) {

					handleError("Please fill in all fields.");
	                return;
				}
				
				//pretending the acc creation worked
				JOptionPane.showMessageDialog(frame, "Account created! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
				showLoginPage();
				
			});
			
			backBtn.addActionListener(e -> showLoginPage());
			buttons.add(createBtn);
			buttons.add(backBtn);
			add(buttons, BorderLayout.SOUTH);

		}
	}
	
	
	//-----------------------------------------------------
	//window for parking slots
	private class SlotsPanel extends JPanel{
		private DefaultListModel<String> slotModel;
		private JList<String> slotList;
		
		public SlotsPanel() {
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(20,20,20,20));
			
			JLabel title = new JLabel("Available Parking Slots", SwingConstants.CENTER);
			title.setFont(new Font("Arial", Font.BOLD, 24));
			add(title, BorderLayout.NORTH);
			
			slotModel = new DefaultListModel<>();
			slotList = new JList<>(slotModel);
			
			add(new JScrollPane(slotList), BorderLayout.CENTER);
			
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
			JButton refreshBtn = new JButton("Refresh");
			
			JButton entryBtn = new JButton("Vehicle Entry");  // ✨ NEW
			JButton exitBtn = new JButton("Vehicle Exit");    // ✨ NEW
			JButton logoutBtn = new JButton("Logout");
			
			refreshBtn.addActionListener(e -> {
				slots = refreshSlots(selectedGarageId, "ALL");
				loadSlots(slots);
			});
			entryBtn.addActionListener(e -> showEntryDialog());
			exitBtn.addActionListener(e -> showExitDialog());
			
			logoutBtn.addActionListener(e -> logout());
			
			buttons.add(refreshBtn);
			buttons.add(logoutBtn);
			buttons.add(entryBtn);   // ✨ NEW
			buttons.add(exitBtn);    // ✨ NEW
			add(buttons, BorderLayout.SOUTH);

			}

		//function to load the slot list into the GUI (temp, will be changed later!)
		public void loadSlots(List<ParkingSlot> slots) {
			slotModel.clear();
			
			if(slots == null || slots.isEmpty()) {
				slotModel.addElement("(No slots available)");
				return;
			}
			for(ParkingSlot s : slots) {
				String label = "Slot #" + s.getSlotID() + " | Occupied: " + s.isOccupied();
				slotModel.addElement(label);
			}
		}

		private void showEntryDialog() {
		    ParkingSystem ps = ParkingSystem.getInstance();
		    
		    String plate = JOptionPane.showInputDialog(frame, 
		        "Enter vehicle plate number:\n(Try: ABC123, XYZ789, or BIKE01)", 
		        "Vehicle Entry", 
		        JOptionPane.PLAIN_MESSAGE);
		    
		    if (plate == null || plate.trim().isEmpty()) {
		        return;
		    }
		    
		    plate = plate.trim().toUpperCase();
		    
		    // Find vehicle
		    Vehicle vehicle = null;
		    for (User u : ps.getUsers()) {
		        if (u instanceof Client) {
		            Client c = (Client) u;
		            for (Vehicle v : c.getRegisteredVehicles()) {
		                if (v.getPlateNumber().equalsIgnoreCase(plate)) {
		                    vehicle = v;
		                    break;
		                }
		            }
		            if (vehicle != null) break;
		        }
		    }
		    
		    if (vehicle == null) {
		        handleError("Vehicle not found: " + plate + "\nPlease use ABC123, XYZ789, or BIKE01");
		        return;
		    }
		    
		    // Find available slot
		    ParkingSlot availableSlot = null;
		    for (ParkingSlot slot : ps.getSlots()) {
		        if (!slot.isOccupied()) {
		            availableSlot = slot;
		            break;
		        }
		    }
		    
		    if (availableSlot == null) {
		        handleError("No available parking slots!");
		        return;
		    }
		    
		    // Issue ticket
		    try {
		        Ticket ticket = ps.issueTicket(vehicle, availableSlot);
		        
		        String message = String.format(
		            "✓ Vehicle Parked Successfully!\n\n" +
		            "Ticket ID: %s\n" +
		            "Plate: %s\n" +
		            "Vehicle: %s %s\n" +
		            "Slot: #%d\n" +
		            "Entry Time: %s",
		            ticket.getTicketIDCode(),
		            vehicle.getPlateNumber(),
		            vehicle.getBrand(),
		            vehicle.getModel(),
		            availableSlot.getSlotID(),
		            ticket.getEntryTime().toString()
		        );
		        
		        JOptionPane.showMessageDialog(frame, message, "Parking Confirmed", JOptionPane.INFORMATION_MESSAGE);
		        
		        // Refresh
		        slots = refreshSlots(selectedGarageId, "ALL");
		        loadSlots(slots);
		        
		    } catch (Exception e) {
		        handleError("Error parking vehicle: " + e.getMessage());
		        e.printStackTrace();
		    }
		}

		private void showExitDialog() {
		    ParkingSystem ps = ParkingSystem.getInstance();
		    
		    String input = JOptionPane.showInputDialog(frame,
		        "Enter ticket ID or plate number:",
		        "Vehicle Exit",
		        JOptionPane.PLAIN_MESSAGE);
		    
		    if (input == null || input.trim().isEmpty()) {
		        return;
		    }
		    
		    input = input.trim();
		    Ticket ticket = null;
		    
		    // Find by ticket ID
		    for (Ticket t : ps.getTickets()) {
		        if (t.isActive() && t.getTicketIDCode().equalsIgnoreCase(input)) {
		            ticket = t;
		            break;
		        }
		    }
		    
		    // If not found, search by plate
		    if (ticket == null) {
		        for (Ticket t : ps.getTickets()) {
		            if (t.isActive() && 
		                t.getVehicle().getPlateNumber().equalsIgnoreCase(input)) {
		                ticket = t;
		                break;
		            }
		        }
		    }
		    
		    if (ticket == null) {
		        handleError("No active parking session found for: " + input);
		        return;
		    }
		    
		    // End parking
		    try {
		        ps.endParking(ticket.getTicketID());
		        
		        int duration = ticket.calculateDuration();
		        double fee = ticket.getTotalFee();
		        
		        String message = String.format(
		            "✓ Vehicle Exit Processed\n\n" +
		            "Ticket ID: %s\n" +
		            "Plate: %s\n" +
		            "Vehicle: %s %s\n" +
		            "Slot: #%d\n\n" +
		            "Entry: %s\n" +
		            "Exit: %s\n" +
		            "Duration: %d minutes\n\n" +
		            "═══════════════════\n" +
		            "TOTAL FEE: $%.2f\n" +
		            "═══════════════════",
		            ticket.getTicketIDCode(),
		            ticket.getVehicle().getPlateNumber(),
		            ticket.getVehicle().getBrand(),
		            ticket.getVehicle().getModel(),
		            ticket.getSlot().getSlotID(),
		            ticket.getEntryTime().toString(),
		            ticket.getExitTime().toString(),
		            duration,
		            fee
		        );
		        
		        JOptionPane.showMessageDialog(frame, message, "Payment Due", JOptionPane.INFORMATION_MESSAGE);
		        
		        // Refresh
		        slots = refreshSlots(selectedGarageId, "ALL");
		        loadSlots(slots);
		        
		    } catch (Exception e) {
		        handleError("Error processing exit: " + e.getMessage());
		        e.printStackTrace();
		    }
		}
	}
	
	//placeholders (might have to add a class or two)
	class AuthSession{
		//addsession toke, userId, expiry, etc
	}
	

	

}
