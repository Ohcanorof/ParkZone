package uiwindows;

import model.Client;
import model.Vehicle;
import model.Car;
import model.Bike;
import model.ClientGUI;
import model.ParkingSlot;
import model.User;
import model.Admin;
import model.VehicleType;
import model.Colors;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import model.Ticket;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//might remove these:
import java.util.function.Consumer;

//this class was for the slots only, but added the menu buttons here as well, like 

//-----------------------------------------------------
//window for parking slots
public class SlotsPanel extends JPanel{
    private final ClientGUI gui;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    private JTextArea accountInfoArea;
    
    //names for the cards
    private static final String CARD_SLOTS = "SLOTS";
    private static final String CARD_VEHICLEREG = "REGISTER_VEHICLE";
    private static final String CARD_ACCOUNT = "ACCOUNT";
    private static final String CARD_RESERVATIONS = "RESERVATIONS";
    private static final String CARD_ADMIN_SLOTS = "ADMIN_SLOTS";
    
    // side-menu buttons
    private JButton viewSlotsBtn;
    private JButton vehicleRegOrAddSlotsBtn;
    private JButton viewAccountBtn;
    private JButton viewReservationsBtn;
    private JButton logoutBtn;
    
    //grid and the filter for slots(actually filters by vehicle type, since admin can set slots for certain types)
    private JPanel gridPanel;
    private JComboBox<Object> filterCombo;
    private List<ParkingSlot> currentSlots = new ArrayList<>();
    
    //the reservation list
	private DefaultListModel<String> reservationsModel;
	private JList<String> reservationsList;
	private final List<Ticket> myReservations = new ArrayList<>();
	
	//tickets table
	private JTable ticketsTable;
	//admin button
	
	public SlotsPanel(ClientGUI gui) {
        this.gui = gui;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(20,20,20,20));
		
		//window title
		JLabel title = new JLabel("Parking Map", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 24));
		add(title, BorderLayout.NORTH);
		
		//side menu
		//title for the menu on the left
		JPanel menuPanel = new JPanel(new GridLayout(0, 1 ,10,10));
		menuPanel.setBorder(new EmptyBorder(10,10,10,10));
		
		//side menu buttons
		viewSlotsBtn = new JButton("View Slots");
		vehicleRegOrAddSlotsBtn = new JButton("Register Vehicle");
		viewAccountBtn = new JButton("View Account");
		viewReservationsBtn = new JButton("View Reservations");
		logoutBtn = new JButton("Logout");
		
		//this gives the box look for the buttons
		Dimension menuButtonSize = new Dimension(160, 40);
		for (JButton b : new JButton[] {
		        viewSlotsBtn,
		        vehicleRegOrAddSlotsBtn,
		        viewAccountBtn,
		        viewReservationsBtn,
		        logoutBtn
		}) {
		    b.setPreferredSize(menuButtonSize);
		    menuPanel.add(b);
		}
		add(menuPanel, BorderLayout.WEST);
		
		//box to the right (cards)
		contentLayout = new CardLayout();
		contentPanel = new JPanel(contentLayout);
		contentPanel.setBorder(new EmptyBorder(0, 10,0, 0));
		
		//the individual cards
		JPanel slotsCard = buildSlotsCard();
		JPanel vehicleRegCard = buildRegisterVehicleCard(); //this is visible for customers
		JPanel adminSlotsCard = buildAdminSlotsCard();//This is only visible for admins
		JPanel accountCard = buildAccountCard();
		JPanel reservationCard = buildReservationCard();
		
		contentPanel.add(slotsCard, CARD_SLOTS);
		contentPanel.add(vehicleRegCard, CARD_VEHICLEREG);
		contentPanel.add(adminSlotsCard,   CARD_ADMIN_SLOTS);
		contentPanel.add(accountCard, CARD_ACCOUNT);
		contentPanel.add(reservationCard, CARD_RESERVATIONS);

		add(contentPanel, BorderLayout.CENTER);
		
		//bottom button (might remove or move somewhere else)
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		JButton refreshBtn = new JButton("Refresh");

		refreshBtn.addActionListener(e -> {
			List<ParkingSlot> refreshed = gui.refreshSlots(gui.selectedGarageId, "ALL");
			loadSlots(refreshed);
		});
		buttons.add(refreshBtn);
		add(buttons, BorderLayout.SOUTH);
		
		//connectivity for side menu buttons
		viewSlotsBtn.addActionListener(e->{
			refreshGrid();
			contentLayout.show(contentPanel, CARD_SLOTS);
		});
		
		vehicleRegOrAddSlotsBtn.addActionListener(e->{
			//if admin client, then you get add slots, if customer, then vehicle reg.
			if (isAdmin()) {
		        contentLayout.show(contentPanel, CARD_ADMIN_SLOTS);
		    } else {
		        contentLayout.show(contentPanel, CARD_VEHICLEREG);
		    }
		});
		
		viewAccountBtn.addActionListener(e->{
			refreshAccountInfo();
			contentLayout.show(contentPanel, CARD_ACCOUNT);
		});
		
		viewReservationsBtn.addActionListener(e->{
			refreshReservationsList();
			contentLayout.show(contentPanel, CARD_RESERVATIONS);
		});
		
		logoutBtn.addActionListener(e -> gui.logout());
		
		//THE DEFAULT
		contentLayout.show(contentPanel,  CARD_SLOTS);
		
		}
	
	public void updateRoleUI() {
		if (vehicleRegOrAddSlotsBtn == null) {
	        return; // not yet constructed
	    }

	    User user = gui.getCurrentUser();
	    String role = gui.getRole();

	    boolean isAdmin = false;

	    if (user != null && user.getAccountType() != null) {
	        isAdmin = "ADMIN".equalsIgnoreCase(user.getAccountType());
	    } else if (role != null) {
	        isAdmin = "ADMIN".equalsIgnoreCase(role);
	    }

	    if (isAdmin) {
	        vehicleRegOrAddSlotsBtn.setText("Add/Remove Slots");
	        vehicleRegOrAddSlotsBtn.setToolTipText("Add or configure parking slots");
	    } else {
	        vehicleRegOrAddSlotsBtn.setText("Register Vehicle");
	        vehicleRegOrAddSlotsBtn.setToolTipText("Register a new vehicle");
	    }
	}
	
	private boolean isAdmin() {
		User user = gui.getCurrentUser();
		String role = gui.getRole();
		// user account type from server
	    if (user != null && user.getAccountType() != null) {
	        return "ADMIN".equalsIgnoreCase(user.getAccountType());
	    }

	    // just incase
	    return role != null && role.equalsIgnoreCase("ADMIN");
	}

	//account info refresh func
	public void refreshAccountInfo() {
		if(accountInfoArea != null) {
			accountInfoArea.setText(buildAccountInfoText());
		}
	}
	
	//---------------=-------------------------------------
	//functions for the cards
	
	//this function is for the interactive grid map to simulate a parking map spot selector, kind of janky idea i got
	private JPanel buildSlotsCard() {
		JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // header + filter bar
        JLabel header = new JLabel("Parking Map", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel north = new JPanel(new BorderLayout());
        north.add(header, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.add(new JLabel("Filter by vehicle type:"));

        filterCombo = new JComboBox<>();
        filterCombo.addItem("All");
        for (VehicleType type : VehicleType.values()) {
            filterCombo.addItem(type);
        }
        filterCombo.addActionListener(e -> refreshGrid());
        filterPanel.add(filterCombo);

        north.add(filterPanel, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);

        // grid panel in the center
        gridPanel = new JPanel();
        gridPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        return panel;
	}
	
	//register vehicle card (prbably going to be changed
	private JPanel buildRegisterVehicleCard() {
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.setBorder(new EmptyBorder(10, 20, 10, 20));

	    JLabel header = new JLabel("Register Vehicle", SwingConstants.CENTER);
	    header.setFont(new Font("Arial", Font.BOLD, 18));
	    panel.add(header, BorderLayout.NORTH);

	    JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));

	    JTextField plateNumber  = new JTextField();
	    JTextField brandField  = new JTextField();
	    JTextField modelField  = new JTextField();
	    JTextField colorField  = new JTextField();

	    // vehicle type select
	    JComboBox<String> typeCombo = new JComboBox<>(new String[] {
	            "CAR", "MOTORCYCLE", "BUS", "TRUCK", "VAN", "SCOOTER", "COMPACT", "SUV", "EV"
	    });

	    form.add(new JLabel("Plate Number:"));
	    form.add(plateNumber);
	    form.add(new JLabel("Brand:"));
	    form.add(brandField);
	    form.add(new JLabel("Model:"));
	    form.add(modelField);
	    form.add(new JLabel("Color (e.g., RED, BLUE):"));
	    form.add(colorField);
	    form.add(new JLabel("Vehicle Type:"));
	    form.add(typeCombo);

	    panel.add(form, BorderLayout.CENTER);

	    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
	    JButton submitBtn = new JButton("Register Vehicle");

	    submitBtn.addActionListener(e -> {
	        // basic validation stuff
	        String plate = plateNumber.getText().trim();
	        String brand = brandField.getText().trim();
	        String model = modelField.getText().trim();
	        String colorText = colorField.getText().trim();

	        if (plate.isEmpty() || brand.isEmpty() || model.isEmpty() || colorText.isEmpty()) {
	            JOptionPane.showMessageDialog(
	                    panel,
	                    "Please fill in all fields.",
	                    "Validation",
	                    JOptionPane.WARNING_MESSAGE
	            );
	            return;
	        }

	        // must have a logged-in user and it MUST be a Client
	        User user = gui.getCurrentUser();
	        if (!(user instanceof Client client)) {
	            JOptionPane.showMessageDialog(
	                    panel,
	                    "You must be logged in as a customer to register a vehicle.",
	                    "Error",
	                    JOptionPane.ERROR_MESSAGE
	            );
	            return;
	        }

	        // parse vehicle type from combo box
	        String typeStr = (String) typeCombo.getSelectedItem();
	        VehicleType vType;
	        try {
	            vType = VehicleType.valueOf(typeStr);
	        } catch (Exception ex) {
	            vType = VehicleType.CAR; // default
	        }

	        // parse color from enum
	        Colors vColor;
	        try {
	            vColor = Colors.valueOf(colorText.toUpperCase());
	        } catch (IllegalArgumentException ex) {
	            JOptionPane.showMessageDialog(
	                    panel,
	                    "Unknown color '" + colorText + "'. Using default BLACK.",
	                    "Info",
	                    JOptionPane.INFORMATION_MESSAGE
	            );
	            vColor = Colors.BLACK;
	        }

	        // create a Vehicle instance, using Car/Bike depending on type, else Car as default
	        Vehicle vehicle;
	        switch (vType) {
	            case MOTORCYCLE -> vehicle = new Bike();
	            default        -> vehicle = new Car();
	        }
	        
	        vehicle.setPlateNumber(plate);
	        vehicle.setBrand(brand); 
	        vehicle.setModel(model);
	        vehicle.setColor(vColor);
	        vehicle.setType(vType);

	        // attach to the client
	        client.addRegisteredVehicles(vehicle);
	        // update the account info card so the new vehicle appears there
	        refreshAccountInfo();

	        JOptionPane.showMessageDialog(
	                panel,
	                "Vehicle registered successfully!",
	                "Success",
	                JOptionPane.INFORMATION_MESSAGE
	        );

	        // clear fields
	        plateNumber.setText("");
	        brandField.setText("");
	        modelField.setText("");
	        colorField.setText("");
	        typeCombo.setSelectedIndex(0);
	    });

	    bottom.add(submitBtn);
	    panel.add(bottom, BorderLayout.SOUTH);

	    return panel;
	}
	
	//could use some work to make it look nice
	private JPanel buildAccountCard() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel header = new JLabel("Account Information", SwingConstants.CENTER);

        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);
		
        accountInfoArea = new JTextArea();
        accountInfoArea.setEditable(false);
        accountInfoArea.setFont(new Font("Arial", Font.PLAIN, 13));
		
        //some initial text
        accountInfoArea.setText(buildAccountInfoText());
		panel.add(new JScrollPane(accountInfoArea), BorderLayout.CENTER);
		return panel;
	}
	
	//Should be complete!?
	//allows for clients to pay for tickets, well see if when Admin clients are able to
	//see tickets, if payment logic works.
	private JPanel buildReservationCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel header = new JLabel("Active Reservations", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);

        reservationsModel = new DefaultListModel<>();
        reservationsList = new JList<>(reservationsModel);

        panel.add(new JScrollPane(reservationsList), BorderLayout.CENTER);

        //Pay button at the bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton payBtn = new JButton("Pay Selected");
        payBtn.addActionListener(e -> paySelectedReservation());
        bottom.add(payBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        refreshReservationsList();

        return panel;
    }
	
	//helper for the reservations
	private void addReservation(Ticket ticket) {
	    if (ticket == null) return;
	    myReservations.add(ticket);
	    refreshReservationsList();
	    
	    //connecting it to the server, reservations should be visible to all clients now
	    ParkingSlot slot = ticket.getSlot();
	    if (slot != null) {
	        try {
	            gui.notifyServerSlotReserved(slot.getSlotID(), ticket.getVehicle());
	        } catch (Exception ex) {
	            //log or show a soft error right now
	            System.err.println("[SlotsPanel] Failed to notify server: " + ex.getMessage());
	        }
	    }
	}
	
	//this function will open a dialog when a single slot is clicked for reservations
	public void openReservationDialog(ParkingSlot slot) {
	    if (slot == null) return;

	    User user = gui.getCurrentUser();
	    if (!(user instanceof Client client)) {
	        JOptionPane.showMessageDialog(
	                this,
	                "You must be logged in as a customer to make a reservation.",
	                "Error",
	                JOptionPane.ERROR_MESSAGE
	        );
	        return;
	    }

	    //get owning window for dialog parent
	    java.awt.Window owner = SwingUtilities.getWindowAncestor(this);

	    ReservationDialog dialog = new ReservationDialog(SwingUtilities.getWindowAncestor(this), gui, client, slot, createdTicket -> { /* optional hook */ });
	    dialog.setLocationRelativeTo(this);
	    dialog.setVisible(true);
	}
	

	private void refreshReservationsList() {
		if (reservationsModel == null) return;

	    reservationsModel.clear();
	    myReservations.clear();

	    // Get tickets from server (for customers: their tickets; for admins: all tickets)
	    java.util.List<Ticket> tickets = gui.fetchTicketsFromServer();

	    if (tickets == null || tickets.isEmpty()) {
	        reservationsModel.addElement("(no reservations yet)");
	        return;
	    }

	    // We only want *active* reservations in this view
	    for (Ticket t : tickets) {
	        if (!t.isActive()) {
	            continue;  // skip closed/paid tickets
	        }
	        myReservations.add(t);
	    }

	    if (myReservations.isEmpty()) {
	        reservationsModel.addElement("(no reservations yet)");
	        return;
	    }

	    java.time.format.DateTimeFormatter fmt =
	            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	    for (Ticket t : myReservations) {
	        int slotId = (t.getSlot() != null) ? t.getSlot().getSlotID() : -1;
	        String when = (t.getEntryTime() != null)
	                      ? t.getEntryTime().format(fmt)
	                      : "(unknown time)";
	        String plate = (t.getVehicle() != null && t.getVehicle().getPlateNumber() != null)
	                       ? t.getVehicle().getPlateNumber()
	                       : "(no plate)";
	        double fee = t.getTotalFee();

	        String line = String.format(
	                "Ticket #%d | Slot %d | %s | Vehicle %s | $%.2f",
	                t.getTicketID(), slotId, when, plate, fee
	        );
	        reservationsModel.addElement(line);
	    }
    }
	
	//pay for reservations
	private void paySelectedReservation() {
        if (myReservations.isEmpty()) {
            JOptionPane.showMessageDialog( this, "You have no reservations to pay.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int idx = reservationsList.getSelectedIndex();
        if (idx < 0 || idx >= myReservations.size()) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to pay.", "Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Ticket ticket = myReservations.get(idx);
        if (!ticket.isActive()) {
            JOptionPane.showMessageDialog(this,  "This ticket is already closed/paid.","Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, String.format("Pay for Ticket #%d now?", ticket.getTicketID()), "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        //Close ticket now; this should also compute the final fee via generateFee()
        ticket.closeTicket(LocalDateTime.now());
        JOptionPane.showMessageDialog(this,String.format("Payment complete.\nTotal: $%.2f", ticket.getTotalFee()), "Payment",JOptionPane.INFORMATION_MESSAGE);
        refreshReservationsList();
    }
	
	//the admins add / remove slot ability
	private JPanel buildAdminSlotsCard() {
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.setBorder(new EmptyBorder(10, 20, 10, 20));

	    JLabel header = new JLabel("Manage Parking Slots", SwingConstants.CENTER);
	    header.setFont(new Font("Arial", Font.BOLD, 18));
	    panel.add(header, BorderLayout.NORTH);

	    JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));

	    //Add slots section
	    JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));
	    form.add(new JLabel("Number of new slots to create:"));
	    form.add(countSpinner);

	    //Remove slot section
	    JSpinner removeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
	    form.add(new JLabel("Slot ID to remove:"));
	    form.add(removeSpinner);

	    panel.add(form, BorderLayout.CENTER);

	    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
	    JButton addBtn = new JButton("Add Slots");
	    JButton removeBtn = new JButton("Remove Slot");

	    // ADD SLOTS
	    addBtn.addActionListener(e -> {
	        int count = (Integer) countSpinner.getValue();
	        if (count <= 0) {
	            JOptionPane.showMessageDialog(panel,"Count must be positive.", "Validation", JOptionPane.WARNING_MESSAGE);
	            return;
	        }
	        //Only admins should add slots
	        User user = gui.getCurrentUser();
	        if (!(user instanceof Admin)) {
	        	JOptionPane.showMessageDialog(panel, "Only admins can add slots.", "Error",JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        boolean ok = gui.addSlotsOnServer(count);
	        if (ok) {
	            //Re-fetch from server so everyone sees the same state
	            java.util.List<ParkingSlot> refreshed =
	                    gui.refreshSlots(gui.getSelectedGarageId(), "ALL");
	            loadSlots(refreshed);

	            JOptionPane.showMessageDialog(panel, "Added " + count + " slots.", "Success",JOptionPane.INFORMATION_MESSAGE);
	        } else {
	            JOptionPane.showMessageDialog(panel, "Failed to add slots on server.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    // REMOVE SLOT
	    removeBtn.addActionListener(e -> {
	        int slotId = (Integer) removeSpinner.getValue();
	        if (slotId <= 0) {
	            JOptionPane.showMessageDialog(panel, "Slot ID must be positive.", "Validation", JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        User user = gui.getCurrentUser();
	        if (!(user instanceof Admin)) {
	            JOptionPane.showMessageDialog(panel, "Only admins can remove slots.", "Error",  JOptionPane.ERROR_MESSAGE);
	            return;
	        }

	        boolean ok = gui.removeSlot(slotId);
	        if (ok) {
	            //fetch from server so everyone sees the same state
	            java.util.List<ParkingSlot> refreshed =
	                    gui.refreshSlots(gui.getSelectedGarageId(), "ALL");
	            loadSlots(refreshed);

	            JOptionPane.showMessageDialog(panel, "Slot " + slotId + " removed.", "Success",JOptionPane.INFORMATION_MESSAGE);
	        } else {
	            JOptionPane.showMessageDialog(panel, "Failed to remove slot " + slotId + ".", "Error",JOptionPane.ERROR_MESSAGE);
	        }
	    });

	    bottom.add(addBtn);
	    bottom.add(removeBtn);
	    panel.add(bottom, BorderLayout.SOUTH);
	    return panel;
	}
	
	//loads the slots into the grid map , called by the ClientGUI.showSlots, cool
	public void loadSlots(List<ParkingSlot> slots) {
		if(slots == null || slots.isEmpty()) {
			currentSlots = Collections.emptyList();
		}
		else {
			currentSlots = new ArrayList<>(slots);
		}
		refreshGrid();
	}
	
	//function to refresh the grid
	private void refreshGrid() {
		if (gridPanel == null) {
			return;
		}
		gridPanel.removeAll();
		
		if(currentSlots == null || currentSlots.isEmpty()) {
			gridPanel.setLayout(new BorderLayout());
			JLabel lbl = new JLabel("(No slots available... yet)", SwingConstants.CENTER);
			lbl.setFont(new Font("Arial", Font.ITALIC, 14));
			gridPanel.add(lbl, BorderLayout.CENTER);
		}else {
			int count = currentSlots.size();
			int cols = 10; //collums can be changed, 10 set for now
			int rows = (int) Math.ceil(count/ (double) cols);//very interesting...
			if(rows <= 0) {
				rows = 1;
			}
			gridPanel.setLayout(new GridLayout(rows, cols, 6, 6));
			
			VehicleType filterType = getSelectedFilterType();
			
			for(ParkingSlot slot : currentSlots) {
				SlotCell cell = new SlotCell(gui, slot, filterType);
				gridPanel.add(cell);
			}
			
			///fill in the remaining cells to make it rectangular
			int totalCells = rows * cols;
			for(int i = count; i < totalCells; i++) {
				JPanel filler = new JPanel();
				filler.setBackground(new Color(0xF0F0F0));
				filler.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
				gridPanel.add(filler);
			}
		}
		gridPanel.revalidate();
		gridPanel.repaint();
	}
	
	//function for the filter type
	private VehicleType getSelectedFilterType() {
		if(filterCombo == null) {
			return null;
		}
		Object sel = filterCombo.getSelectedItem();
		if(sel instanceof VehicleType) {
			return (VehicleType) sel;
		}
		return null; //all is selected in the filter, or nothing is, so show all.
	}
	
	private String buildAccountInfoText() {
		StringBuilder sb = new StringBuilder();

	    User user = gui.getCurrentUser();
	    if (user == null) {
	        sb.append("Not logged in.\n");
	        return sb.toString();
	    }

	    // --- Basic account info ---
	    sb.append("Account Information\n");
	    sb.append("-------------------\n");
	    sb.append("Name:  ")
	      .append(user.getFirstName())
	      .append(" ")
	      .append(user.getLastName())
	      .append("\n");
	    sb.append("Email: ")
	      .append(user.getEmail())
	      .append("\n");
	    sb.append("Role:  ")
	      .append(user.getAccountType())
	      .append("\n\n");

	    // --- Vehicles (for customers) ---
	    if (user instanceof Client client) {
	        sb.append("Registered Vehicles:\n");
	        List<Vehicle> vehicles = client.getRegisteredVehicles();
	        if (vehicles == null || vehicles.isEmpty()) {
	            sb.append("  (none)\n");
	        } else {
	            for (Vehicle v : vehicles) {
	                sb.append("  - ")
	                  .append(v.getPlateNumber())
	                  .append(" (")
	                  .append(v.getType())
	                  .append(")\n");
	            }
	        }
	        sb.append("\n");
	    }

	    // --- Tickets / Reservations from the server ---
	    sb.append("Tickets / Reservations:\n");

	    List<Ticket> tickets = gui.fetchTicketsFromServer();
	    if (tickets == null || tickets.isEmpty()) {
	        sb.append("  (none)\n");
	    } else {
	        for (Ticket t : tickets) {
	            sb.append("  #")
	              .append(t.getTicketID())
	              .append(" | Slot: ");
	            if (t.getSlot() != null) {
	                sb.append(t.getSlot().getSlotID());
	            } else {
	                sb.append("-");
	            }

	            sb.append(" | Plate: ");
	            if (t.getVehicle() != null) {
	                sb.append(t.getVehicle().getPlateNumber());
	            } else {
	                sb.append("-");
	            }

	            sb.append(" | Status: ")
	              .append(t.isActive() ? "ACTIVE" : "CLOSED");

	            sb.append(" | Entry: ")
	              .append(t.getEntryTime());

	            sb.append(" | Exit: ");
	            if (t.getExitTime() != null) {
	                sb.append(t.getExitTime());
	            } else {
	                sb.append("-");
	            }

	            sb.append(" | Fee: ")
	              .append(t.getTotalFee());

	            sb.append("\n");
	        }
	    }

	    return sb.toString();
	}
	
	
	//will add/remove to this maybe
	public void openAdminSlotConfigDialog(ParkingSlot slot) {
	    if (slot == null) return;

	    User user = gui.getCurrentUser();
	    if (!(user instanceof Admin)) {
	        JOptionPane.showMessageDialog(this,"Only admins can configure slots.","Error", JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    java.awt.Window owner = SwingUtilities.getWindowAncestor(this);
	    JDialog dialog = new JDialog(owner, "Configure Slot #" + slot.getSlotID(), Dialog.ModalityType.APPLICATION_MODAL);
	    dialog.setSize(350, 250);
	    dialog.setLayout(new BorderLayout(10, 10));
	    dialog.setLocationRelativeTo(this);

	    JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
	    form.setBorder(new EmptyBorder(10, 10, 10, 10));

	    //type selector
	    form.add(new JLabel("Allowed vehicle type:"));
	    JComboBox<Object> typeCombo = new JComboBox<>();
	    typeCombo.addItem("ANY");
	    for (VehicleType vt : VehicleType.values()) {
	        typeCombo.addItem(vt);
	    }
	    VehicleType currentType = slot.getAllowedType();
	    if (currentType == null) {
	        typeCombo.setSelectedItem("ANY");
	    } else {
	        typeCombo.setSelectedItem(currentType);
	    }
	    form.add(typeCombo);

	    //rate field
	    form.add(new JLabel("Hourly rate (0 = default):"));
	    JSpinner rateSpinner = new JSpinner(new SpinnerNumberModel(slot.getHourlyRate() > 0 ? slot.getHourlyRate() : 0.0, 0.0, 100.0, 0.5));
	    form.add(rateSpinner);

	    dialog.add(form, BorderLayout.CENTER);

	    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
	    JButton saveBtn = new JButton("Save");
	    JButton cancelBtn = new JButton("Cancel");

	    saveBtn.addActionListener(e -> {
	        Object sel = typeCombo.getSelectedItem();
	        if (sel instanceof VehicleType vt) {
	            slot.setAllowedType(vt);
	        } else {
	            slot.setAllowedType(null); //ANY
	        }

	        double rate = ((Number) rateSpinner.getValue()).doubleValue();
	        slot.setHourlyRate(rate);

	        //refresh grid so colors + tooltips update
	        loadSlots(gui.slots);

	        dialog.dispose();
	    });

	    cancelBtn.addActionListener(e -> dialog.dispose());

	    buttons.add(saveBtn);
	    buttons.add(cancelBtn);
	    dialog.add(buttons, BorderLayout.SOUTH);

	    dialog.setVisible(true);
	}
	
}
