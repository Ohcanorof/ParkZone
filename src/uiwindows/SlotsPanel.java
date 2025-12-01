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
import model.Ticket;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SlotsPanel - Enhanced with multi-floor, multi-section navigation
 * Phase 1+2 Implementation: Floor → Section → Slot navigation
 */
public class SlotsPanel extends JPanel {
    private final ClientGUI gui;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    private JTextArea accountInfoArea;
    private JButton gateAttendantBtn;
    
    // Navigation state
    private int currentFloor = 1;
    private String currentSection = "A";
    
    // Card names
    private static final String CARD_SLOTS = "SLOTS";
    private static final String CARD_VEHICLEREG = "REGISTER_VEHICLE";
    private static final String CARD_ACCOUNT = "ACCOUNT";
    private static final String CARD_RESERVATIONS = "RESERVATIONS";
    private static final String CARD_ADMIN_PANEL = "ADMIN_PANEL";
    
    // Side-menu buttons
    private JButton viewSlotsBtn;
    private JButton vehicleRegOrAddSlotsBtn;
    private JButton viewAccountBtn;
    private JButton viewReservationsBtn;
    private JButton logoutBtn;
    
    // Grid and filter components
    private JPanel gridPanel;
    private JComboBox<Object> filterCombo;
    private List<ParkingSlot> currentSlots = new ArrayList<>();
    
    // Navigation components
    private JLabel breadcrumbLabel;
    private JPanel floorSelectorPanel;
    private JPanel sectionSelectorPanel;
    
    // Reservation list
    private DefaultListModel<String> reservationsModel;
    private JList<String> reservationsList;
    private final List<Ticket> myReservations = new ArrayList<>();
    
    public SlotsPanel(ClientGUI gui) {
        this.gui = gui;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Window title
        JLabel title = new JLabel("Parking Map", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);
        
        // Side menu
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Side menu buttons
        viewSlotsBtn = new JButton("View Slots");
        vehicleRegOrAddSlotsBtn = new JButton("Register Vehicle");
        viewAccountBtn = new JButton("View Account");
        viewReservationsBtn = new JButton("View Reservations");
        gateAttendantBtn = new JButton("Gate Attendant");
        logoutBtn = new JButton("Logout");
        
        Dimension menuButtonSize = new Dimension(160, 40);
        for (JButton b : new JButton[]{
                viewSlotsBtn,
                vehicleRegOrAddSlotsBtn,
                viewAccountBtn,
                viewReservationsBtn,
                gateAttendantBtn,
                logoutBtn
        }) {
            b.setPreferredSize(menuButtonSize);
            menuPanel.add(b);
        }
        add(menuPanel, BorderLayout.WEST);
        
        // Wire up gate attendant button
        gateAttendantBtn.addActionListener(e -> {
            contentLayout.show(contentPanel, CARD_ADMIN_PANEL);
        });
        gateAttendantBtn.setVisible(isAdmin());
        
        // Content area with cards
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Build individual cards
        JPanel slotsCard = buildSlotsCard();
        JPanel vehicleRegCard = buildRegisterVehicleCard();
        JPanel accountCard = buildAccountCard();
        JPanel reservationCard = buildReservationCard();
        
        contentPanel.add(slotsCard, CARD_SLOTS);
        contentPanel.add(vehicleRegCard, CARD_VEHICLEREG);
        contentPanel.add(accountCard, CARD_ACCOUNT);
        contentPanel.add(reservationCard, CARD_RESERVATIONS);
        
        JPanel adminPanelCard = new AdminPanel(gui);
        contentPanel.add(adminPanelCard, CARD_ADMIN_PANEL);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Bottom button
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton refreshBtn = new JButton("Refresh");
        
        refreshBtn.addActionListener(e -> {
            List<ParkingSlot> refreshed = gui.refreshSlots(gui.selectedGarageId, "ALL");
            loadSlots(refreshed);
        });
        buttons.add(refreshBtn);
        add(buttons, BorderLayout.SOUTH);
        
        // Connect side menu buttons
        viewSlotsBtn.addActionListener(e -> {
            refreshGrid();
            contentLayout.show(contentPanel, CARD_SLOTS);
        });
        
        vehicleRegOrAddSlotsBtn.addActionListener(e -> {
            if (isAdmin()) {
                // Admins no longer have Add/Remove Slots
                JOptionPane.showMessageDialog(this,
                    "Slot management has been removed.\n" +
                    "Configure individual slots by clicking them on the parking map.",
                    "Feature Removed",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                contentLayout.show(contentPanel, CARD_VEHICLEREG);
            }
        });
        
        viewAccountBtn.addActionListener(e -> {
            refreshAccountInfo();
            contentLayout.show(contentPanel, CARD_ACCOUNT);
        });
        
        viewReservationsBtn.addActionListener(e -> {
            refreshReservationsList();
            contentLayout.show(contentPanel, CARD_RESERVATIONS);
        });
        
        logoutBtn.addActionListener(e -> gui.logout());
        
        // Show slots by default
        contentLayout.show(contentPanel, CARD_SLOTS);
    }
    
    // ============================================================
    // BUILD SLOTS CARD WITH FLOOR/SECTION NAVIGATION
    // ============================================================
    
    private JPanel buildSlotsCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header
        JLabel header = new JLabel("Parking Map", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Breadcrumb navigation
        breadcrumbLabel = new JLabel("Floor 1 > Section A", SwingConstants.LEFT);
        breadcrumbLabel.setFont(new Font("Arial", Font.BOLD, 14));
        breadcrumbLabel.setForeground(new Color(0x2C3E50));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(header, BorderLayout.NORTH);
        topPanel.add(breadcrumbLabel, BorderLayout.CENTER);
        
        // Floor selector
        floorSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        floorSelectorPanel.add(new JLabel("Select Floor:"));
        
        for (int floor = 1; floor <= 6; floor++) {
            JButton floorBtn = new JButton("Floor " + floor);
            floorBtn.setPreferredSize(new Dimension(90, 35));
            final int floorNum = floor;
            
            // Disable floors 2-6 (no slots yet)
            if (floor > 1) {
                floorBtn.setEnabled(false);
                floorBtn.setToolTipText("Coming soon - Floor " + floor + " not yet available");
            } else {
                floorBtn.addActionListener(e -> {
                    currentFloor = floorNum;
                    currentSection = "A";  // Reset to section A
                    updateBreadcrumb();
                    refreshGrid();
                });
            }
            
            floorSelectorPanel.add(floorBtn);
        }
        
        topPanel.add(floorSelectorPanel, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Section selector
        sectionSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        sectionSelectorPanel.add(new JLabel("Select Section:"));
        
        for (char section = 'A'; section <= 'E'; section++) {
            JButton sectionBtn = new JButton("Section " + section);
            sectionBtn.setPreferredSize(new Dimension(100, 35));
            final String sectionStr = String.valueOf(section);
            
            sectionBtn.addActionListener(e -> {
                currentSection = sectionStr;
                updateBreadcrumb();
                refreshGrid();
            });
            
            sectionSelectorPanel.add(sectionBtn);
        }
        
        // Filter by vehicle type
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.add(new JLabel("Filter by vehicle type:"));
        
        filterCombo = new JComboBox<>();
        filterCombo.addItem("All");
        for (VehicleType type : VehicleType.values()) {
            filterCombo.addItem(type);
        }
        filterCombo.addActionListener(e -> refreshGrid());
        filterPanel.add(filterCombo);
        
        JPanel centerTop = new JPanel(new BorderLayout());
        centerTop.add(sectionSelectorPanel, BorderLayout.NORTH);
        centerTop.add(filterPanel, BorderLayout.SOUTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(centerTop, BorderLayout.NORTH);
        
        // Grid panel in the center
        gridPanel = new JPanel();
        gridPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        centerPanel.add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateBreadcrumb() {
        if (breadcrumbLabel != null) {
            breadcrumbLabel.setText(String.format("Floor %d > Section %s", currentFloor, currentSection));
        }
    }
    
    // ============================================================
    // OTHER CARDS (Vehicle Registration, Account, Reservations)
    // ============================================================
    
    private JPanel buildRegisterVehicleCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel header = new JLabel("Register Vehicle", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);
        
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        
        JTextField plateNumber = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField colorField = new JTextField();
        
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
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
            String plate = plateNumber.getText().trim();
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            String colorText = colorField.getText().trim();
            
            if (plate.isEmpty() || brand.isEmpty() || model.isEmpty() || colorText.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Please fill in all fields.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User user = gui.getCurrentUser();
            if (!(user instanceof Client client)) {
                JOptionPane.showMessageDialog(panel,
                        "You must be logged in as a customer to register a vehicle.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String typeStr = (String) typeCombo.getSelectedItem();
            VehicleType vType;
            try {
                vType = VehicleType.valueOf(typeStr);
            } catch (Exception ex) {
                vType = VehicleType.CAR;
            }
            
            Colors vColor;
            try {
                vColor = Colors.valueOf(colorText.toUpperCase());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Unknown color '" + colorText + "'. Using default BLACK.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                vColor = Colors.BLACK;
            }
            
            Vehicle vehicle;
            switch (vType) {
                case MOTORCYCLE -> vehicle = new Bike();
                default -> vehicle = new Car(plate, brand, model, vColor);
            }
            
            vehicle.setPlateNumber(plate);
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setColor(vColor);
            vehicle.setType(vType);
            
            client.addRegisteredVehicles(vehicle);
            refreshAccountInfo();
            
            JOptionPane.showMessageDialog(panel,
                    "Vehicle registered successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            
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
    
    private JPanel buildAccountCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel header = new JLabel("Account Information", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);
        
        accountInfoArea = new JTextArea();
        accountInfoArea.setEditable(false);
        accountInfoArea.setFont(new Font("Arial", Font.PLAIN, 13));
        accountInfoArea.setText(buildAccountInfoText());
        
        panel.add(new JScrollPane(accountInfoArea), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel buildReservationCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel header = new JLabel("Active Reservations", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);
        
        reservationsModel = new DefaultListModel<>();
        reservationsList = new JList<>(reservationsModel);
        
        panel.add(new JScrollPane(reservationsList), BorderLayout.CENTER);
        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton payBtn = new JButton("Pay Selected");
        payBtn.addActionListener(e -> paySelectedReservation());
        bottom.add(payBtn);
        panel.add(bottom, BorderLayout.SOUTH);
        
        refreshReservationsList();
        
        return panel;
    }
    
    // ============================================================
    // GRID DISPLAY LOGIC
    // ============================================================
    
    public void loadSlots(List<ParkingSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            currentSlots = Collections.emptyList();
        } else {
            currentSlots = new ArrayList<>(slots);
        }
        refreshGrid();
    }
    
    private void refreshGrid() {
        if (gridPanel == null) {
            return;
        }
        gridPanel.removeAll();
        
        // Filter slots by current floor and section
        List<ParkingSlot> sectionSlots = new ArrayList<>();
        for (ParkingSlot slot : currentSlots) {
            if (slot.getFloor() == currentFloor && 
                slot.getSection().equals(currentSection)) {
                sectionSlots.add(slot);
            }
        }
        
        if (sectionSlots.isEmpty()) {
            gridPanel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel(
                String.format("No slots available in Floor %d, Section %s", 
                    currentFloor, currentSection),
                SwingConstants.CENTER
            );
            lbl.setFont(new Font("Arial", Font.ITALIC, 14));
            gridPanel.add(lbl, BorderLayout.CENTER);
        } else {
            // 4×5 grid for 20 slots per section
            int cols = 5;
            int rows = 4;
            gridPanel.setLayout(new GridLayout(rows, cols, 6, 6));
            
            VehicleType filterType = getSelectedFilterType();
            
            for (ParkingSlot slot : sectionSlots) {
                SlotCell cell = new SlotCell(gui, slot, filterType);
                gridPanel.add(cell);
            }
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    private VehicleType getSelectedFilterType() {
        if (filterCombo == null) {
            return null;
        }
        Object sel = filterCombo.getSelectedItem();
        if (sel instanceof VehicleType) {
            return (VehicleType) sel;
        }
        return null;
    }
    
    // ============================================================
    // RESERVATION MANAGEMENT
    // ============================================================
    
    public void openReservationDialog(ParkingSlot slot) {
        if (slot == null) return;
        
        User user = gui.getCurrentUser();
        if (!(user instanceof Client client)) {
            JOptionPane.showMessageDialog(this,
                    "You must be logged in as a customer to make a reservation.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ReservationDialog dialog = new ReservationDialog(
            SwingUtilities.getWindowAncestor(this), gui, client, slot,
            createdTicket -> { /* optional hook */ }
        );
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void refreshReservationsList() {
        if (reservationsModel == null) return;
        
        reservationsModel.clear();
        myReservations.clear();
        
        List<Ticket> tickets = gui.fetchTicketsFromServer();
        
        if (tickets == null || tickets.isEmpty()) {
            reservationsModel.addElement("(no reservations yet)");
            return;
        }
        
        for (Ticket t : tickets) {
            if (!t.isActive()) {
                continue;
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
    
    private void paySelectedReservation() {
        if (myReservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no reservations to pay.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int idx = reservationsList.getSelectedIndex();
        if (idx < 0 || idx >= myReservations.size()) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to pay.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Ticket ticket = myReservations.get(idx);
        if (!ticket.isActive()) {
            JOptionPane.showMessageDialog(this, "This ticket is already closed/paid.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
                String.format("Pay for Ticket #%d now?", ticket.getTicketID()),
                "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        
        ticket.closeTicket(LocalDateTime.now());
        JOptionPane.showMessageDialog(this,
                String.format("Payment complete.\nTotal: $%.2f", ticket.getTotalFee()),
                "Payment", JOptionPane.INFORMATION_MESSAGE);
        refreshReservationsList();
    }
    
    // ============================================================
    // ADMIN SLOT CONFIGURATION
    // ============================================================
    
    public void openAdminSlotConfigDialog(ParkingSlot slot) {
        if (slot == null) return;
        
        User user = gui.getCurrentUser();
        if (!(user instanceof Admin)) {
            JOptionPane.showMessageDialog(this,
                    "Only admins can configure slots.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        java.awt.Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner,
                "Configure Slot #" + slot.getSlotID() + " (" + slot.getCompositeID() + ")",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(this);
        
        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Location info (read-only)
        form.add(new JLabel("Slot ID: " + slot.getSlotID()));
        form.add(new JLabel("Location: Floor " + slot.getFloor() + ", Section " + slot.getSection()));
        form.add(new JLabel("Composite ID: " + slot.getCompositeID()));
        form.add(new JLabel(" ")); // Spacer
        
        // Type selector
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
        
        // Rate field
        form.add(new JLabel("Hourly rate (0 = default):"));
        JSpinner rateSpinner = new JSpinner(new SpinnerNumberModel(
                slot.getHourlyRate() > 0 ? slot.getHourlyRate() : 0.0,
                0.0, 100.0, 0.5));
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
                slot.setAllowedType(null);
            }
            
            double rate = ((Number) rateSpinner.getValue()).doubleValue();
            slot.setHourlyRate(rate);
            
            loadSlots(gui.slots);
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttons.add(saveBtn);
        buttons.add(cancelBtn);
        dialog.add(buttons, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    public void updateRoleUI() {
        if (vehicleRegOrAddSlotsBtn == null) {
            return;
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
            vehicleRegOrAddSlotsBtn.setText("Slot Config");
            vehicleRegOrAddSlotsBtn.setToolTipText("Configure individual slots on the map");
            gateAttendantBtn.setVisible(true);
        } else {
            vehicleRegOrAddSlotsBtn.setText("Register Vehicle");
            vehicleRegOrAddSlotsBtn.setToolTipText("Register a new vehicle");
            gateAttendantBtn.setVisible(false);
        }
    }
    
    private boolean isAdmin() {
        User user = gui.getCurrentUser();
        String role = gui.getRole();
        
        if (user != null && user.getAccountType() != null) {
            return "ADMIN".equalsIgnoreCase(user.getAccountType());
        }
        
        return role != null && role.equalsIgnoreCase("ADMIN");
    }
    
    public void refreshAccountInfo() {
        if (accountInfoArea != null) {
            accountInfoArea.setText(buildAccountInfoText());
        }
    }
    
    private String buildAccountInfoText() {
        StringBuilder sb = new StringBuilder();
        
        User user = gui.getCurrentUser();
        if (user == null) {
            sb.append("Not logged in.\n");
            return sb.toString();
        }
        
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
                    sb.append(t.getSlot().getSlotID())
                            .append(" (")
                            .append(t.getSlot().getCompositeID())
                            .append(")");
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
                
                sb.append(" | Fee: $")
                        .append(String.format("%.2f", t.getTotalFee()));
                
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
}