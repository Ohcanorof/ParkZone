package uiwindows;

import model.ClientGUI;
import model.ParkingSlot;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

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

	    
		private DefaultListModel<String> slotModel;
		private JList<String> slotList;
		private DefaultListModel<String> reservationsModel;
		private JList<String> reservationsList;
		
		public SlotsPanel(ClientGUI gui) {
	        this.gui = gui;
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(20,20,20,20));
			
			//window title
			JLabel title = new JLabel("Available Parking Slots", SwingConstants.CENTER);
			title.setFont(new Font("Arial", Font.BOLD, 24));
			add(title, BorderLayout.NORTH);
			
			//side menu
			//title for the menu on the left
			JPanel menuPanel = new JPanel(new GridLayout(0, 1 ,10,10));
			menuPanel.setBorder(new EmptyBorder(10,10,10,10));
			
			//side menu buttons
			JButton viewSlotsBtn = new JButton("View Slots");
			JButton vehicleRegBtn = new JButton("Register Vehicle");
			JButton viewAccountBtn = new JButton("View Account");
			JButton viewReservationsBtn = new JButton("View Reservations");
			JButton logoutBtn = new JButton("Logout");

			//this gives the box look for the buttons
			Dimension menuButtonSize = new Dimension(160, 40);
			for(JButton b : new JButton[] {viewSlotsBtn, vehicleRegBtn, viewAccountBtn, viewReservationsBtn, logoutBtn}) {
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
			JPanel vehicleRegCard = buildRegisterVehicleCard();
			JPanel accountCard = buildAccountCard();
			JPanel reservationCard = buildReservationCard();
			
			contentPanel.add(slotsCard, CARD_SLOTS);
			contentPanel.add(vehicleRegCard, CARD_VEHICLEREG);
			contentPanel.add(accountCard, CARD_ACCOUNT);
			contentPanel.add(reservationCard, CARD_RESERVATIONS);

			add(contentPanel, BorderLayout.CENTER);
			
			//bottom button (might remove or move somewhere else)
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
			JButton refreshBtn = new JButton("Refresh");

			refreshBtn.addActionListener(e -> {
				List<ParkingSlot> refreshed = gui.refreshSlots(gui.selectedGarageId, "ALL");
				loadSlots(gui.slots);
			});
			buttons.add(refreshBtn);
			add(buttons, BorderLayout.SOUTH);
			
			//connectivity for side menu buttons
			viewSlotsBtn.addActionListener(e->{
				contentLayout.show(contentPanel, CARD_SLOTS);
			});
			
			vehicleRegBtn.addActionListener(e->{
				contentLayout.show(contentPanel, CARD_VEHICLEREG);
			});
			
			viewAccountBtn.addActionListener(e->{
				refreshAccountInfo();
				contentLayout.show(contentPanel, CARD_ACCOUNT);
			});
			
			viewReservationsBtn.addActionListener(e->{
				contentLayout.show(contentPanel, CARD_RESERVATIONS);
			});
			
			logoutBtn.addActionListener(e -> gui.logout());
			
			//THE DEFAULT
			contentLayout.show(contentPanel,  CARD_SLOTS);
			
			}
		

		public void refreshAccountInfo() {
			if(accountInfoArea != null) {
				accountInfoArea.setText(buildAccountInfoText());
			}
		}
		//functions for the cards
		
		private JPanel buildSlotsCard() {
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(new EmptyBorder(0,0,0,0));
			
			JLabel header = new JLabel("Available Parking Slots" , SwingConstants.CENTER);
			header.setFont(new Font("Arial", Font.BOLD, 18));
			panel.add(header, BorderLayout.NORTH);
			
			slotModel = new DefaultListModel<>();
			slotList = new JList<>(slotModel);
			panel.add(new JScrollPane(slotList), BorderLayout.CENTER);
			
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
			
			JTextField plateField = new JTextField();
			JTextField brandField = new JTextField();
			JTextField modelField = new JTextField();
			JTextField colorField = new JTextField();
			
			// MIGHT CHANGE LATER!!!
			JComboBox<String> typeCombo = new JComboBox<>(new String[] { "CAR", "MOTORCYCLE", "EV", "TRUCK", "VAN", "BUS", "SCOOTER" });
			
			form.add(new JLabel("Plate Number:"));
			form.add(plateField);
			form.add(new JLabel("Brand:"));
			form.add(brandField);
			form.add(new JLabel("Model:"));
			form.add(modelField);
			form.add(new JLabel("Color:"));
			form.add(colorField);
			form.add(new JLabel("Vehicle Type:"));
			form.add(typeCombo);
			
			panel.add(form, BorderLayout.CENTER);
			JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
			JButton submitBtn = new JButton("Register Vehicle");
			
			submitBtn.addActionListener(e -> {
	            // TODO: hook up to gui / server later
	            if (plateField.getText().isBlank()) {
	                JOptionPane.showMessageDialog(panel, "Please enter at a plate number.", "Validation", JOptionPane.WARNING_MESSAGE);
	                return;
	            }
	            JOptionPane.showMessageDialog(panel,"Vehicle registered (stub).", "Info", JOptionPane.INFORMATION_MESSAGE);
	        });
			
			bottom.add(submitBtn);
			panel.add(bottom, BorderLayout.SOUTH);

			return panel;
		}
		
		//not complete, basically a placeholder so we can see how it 'should' look
		private JPanel buildAccountCard() {
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(new EmptyBorder(20, 20, 20, 20));
	        JLabel header = new JLabel("Account Information", SwingConstants.CENTER);

	        header.setFont(new Font("Arial", Font.BOLD, 18));
	        panel.add(header, BorderLayout.NORTH);
			
	        accountInfoArea = new JTextArea();
	        accountInfoArea.setEditable(false);
	        accountInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
			
	        //some initial text
	        accountInfoArea.setText(buildAccountInfoText());
			panel.add(new JScrollPane(accountInfoArea), BorderLayout.CENTER);
			return panel;
		}
		
		//also a placeholder
		private JPanel buildReservationCard() {
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(new EmptyBorder(10, 20, 10, 20));
			
			JLabel header = new JLabel("Active Reservations", SwingConstants.CENTER);
			header.setFont(new Font("Arial", Font.BOLD, 18));
			panel.add(header, BorderLayout.NORTH);
			
			reservationsModel = new DefaultListModel<>();
			reservationsList = new JList<>(reservationsModel);
			
			//stub
			reservationsModel.addElement("(no reseravtions...yet");
			
			panel.add(new JScrollPane(reservationsList), BorderLayout.CENTER);
			
			
			return panel;
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
		
		private String buildAccountInfoText() {
			StringBuilder sb = new StringBuilder();
			
			User user = gui.getCurrentUser();
			String role = gui.getRole();
			
			if(user == null) {
				sb.append("Not logged in or user info not loaded yet.\n\n");
				sb.append("Role: ").append(role != null ? role : "(unknown)").append("\n");
				sb.append("\nLater, this can be populated from the real server user object.");
			}
			else {
				sb.append("Name: ").append(user.getFullName() != null ? user.getFullName() : "(unknown)").append("\n");
				sb.append("Email: ").append(user.getEmail() != null ? user.getEmail() : "(unknown)").append("\n");
				sb.append("Role: ");
				if(user.getAccountType() != null) {
					sb.append(user.getAccountType());
				}else if(role != null){
					sb.append(role);
				}
				else {
					sb.append("(unknown)");
				}
				sb.append("\n");
				sb.append("User ID: ").append(user.getID()).append("\n");
				
			}
			
			return sb.toString();
		}
	}
