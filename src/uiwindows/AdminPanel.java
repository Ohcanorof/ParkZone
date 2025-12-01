package uiwindows;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import model.Admin;
import model.Bike;
import model.Car;
import model.Client;
import model.ParkingSlot;
import model.ParkingSystem;
import model.Ticket;
import model.User;
import model.Vehicle;
import model.VehicleType;

/**
 * Enhanced Admin Panel - Gate attendant and system management interface
 * Features: Manual vehicle entry/exit, active tickets view, system reports
 * Supports both registered vehicles (quick entry) and walk-up customers (manual entry)
 */
public class AdminPanel extends JPanel {
	private final Object parentGUI;  // Keep this
    private JPanel contentPanel;
    private CardLayout contentLayout;
    
    // Card names
    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final String CARD_MANUAL_ENTRY = "MANUAL_ENTRY";
    private static final String CARD_ACTIVE_TICKETS = "ACTIVE_TICKETS";
    private static final String CARD_REPORTS = "REPORTS";
    
    // Components
    private JTextArea dashboardArea;
    private JTextArea reportsArea;
    private DefaultTableModel ticketsTableModel;
    private JTable ticketsTable;
 
    
    public AdminPanel(Object gui) {
    	this.parentGUI = gui;
    	
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel title = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);
        
        // Side menu
        JPanel menuPanel = createSideMenu();
        add(menuPanel, BorderLayout.WEST);
        
        // Content area with cards
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Build individual cards
        JPanel dashboardCard = buildDashboardCard();
        JPanel manualEntryCard = buildManualEntryCard();
        JPanel activeTicketsCard = buildActiveTicketsCard();
        JPanel reportsCard = buildReportsCard();
        
        contentPanel.add(dashboardCard, CARD_DASHBOARD);
        contentPanel.add(manualEntryCard, CARD_MANUAL_ENTRY);
        contentPanel.add(activeTicketsCard, CARD_ACTIVE_TICKETS);
        contentPanel.add(reportsCard, CARD_REPORTS);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton refreshBtn = new JButton("Refresh All");
        JButton logoutBtn = new JButton("Logout");
        
        refreshBtn.addActionListener(e -> refreshAllData());
        logoutBtn.addActionListener(e -> logout());  // Call our logout method instead
        
        bottomPanel.add(refreshBtn);
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Show dashboard by default
        contentLayout.show(contentPanel, CARD_DASHBOARD);
    }
    
    private void logout() {
        if (parentGUI instanceof AdminGUI) {
            ((AdminGUI) parentGUI).logout();
        } else if (parentGUI instanceof model.ClientGUI) {
            ((model.ClientGUI) parentGUI).logout();
        }
    }
    // ============================================================
    // SIDE MENU
    // ============================================================
    
    private JPanel createSideMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton dashboardBtn = new JButton("Dashboard");
        JButton manualEntryBtn = new JButton("Manual Entry/Exit");
        JButton activeTicketsBtn = new JButton("Active Tickets");
        JButton reportsBtn = new JButton("Reports");
        
        Dimension menuButtonSize = new Dimension(180, 40);
        for (JButton btn : new JButton[]{dashboardBtn, manualEntryBtn, activeTicketsBtn, reportsBtn}) {
            btn.setPreferredSize(menuButtonSize);
            menuPanel.add(btn);
        }
        
        // Wire up navigation
        dashboardBtn.addActionListener(e -> {
            refreshDashboard();
            contentLayout.show(contentPanel, CARD_DASHBOARD);
        });
        
        manualEntryBtn.addActionListener(e -> {
            contentLayout.show(contentPanel, CARD_MANUAL_ENTRY);
        });
        
        activeTicketsBtn.addActionListener(e -> {
            refreshActiveTickets();
            contentLayout.show(contentPanel, CARD_ACTIVE_TICKETS);
        });
        
        reportsBtn.addActionListener(e -> {
            refreshReports();
            contentLayout.show(contentPanel, CARD_REPORTS);
        });
        
        return menuPanel;
    }
    
    // ============================================================
    // DASHBOARD CARD
    // ============================================================
    
    private JPanel buildDashboardCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel header = new JLabel("System Dashboard", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);
        
        dashboardArea = new JTextArea();
        dashboardArea.setEditable(false);
        dashboardArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        dashboardArea.setText(buildDashboardText());
        
        panel.add(new JScrollPane(dashboardArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private String buildDashboardText() {
        ParkingSystem ps = ParkingSystem.getInstance();
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("           PARKZONE ADMIN DASHBOARD\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        // Slot statistics
        int totalSlots = ps.getSlots().size();
        int occupiedSlots = 0;
        for (ParkingSlot slot : ps.getSlots()) {
            if (slot.isOccupied()) occupiedSlots++;
        }
        int availableSlots = totalSlots - occupiedSlots;
        double occupancyRate = totalSlots > 0 ? (occupiedSlots * 100.0 / totalSlots) : 0;
        
        sb.append("PARKING SLOTS:\n");
        sb.append("  Total Slots:     ").append(totalSlots).append("\n");
        sb.append("  Occupied:        ").append(occupiedSlots).append("\n");
        sb.append("  Available:       ").append(availableSlots).append("\n");
        sb.append("  Occupancy Rate:  ").append(String.format("%.1f%%", occupancyRate)).append("\n\n");
        
        // Ticket statistics
        int activeTickets = 0;
        int completedTickets = 0;
        for (Ticket t : ps.getTickets()) {
            if (t.isActive()) {
                activeTickets++;
            } else {
                completedTickets++;
            }
        }
        
        sb.append("TICKETS:\n");
        sb.append("  Active Sessions: ").append(activeTickets).append("\n");
        sb.append("  Completed:       ").append(completedTickets).append("\n");
        sb.append("  Total Issued:    ").append(ps.getTickets().size()).append("\n\n");
        
        // User statistics
        int totalUsers = ps.getUsers().size();
        int adminCount = 0;
        int clientCount = 0;
        for (User u : ps.getUsers()) {
            if (u instanceof Admin) adminCount++;
            else if (u instanceof Client) clientCount++;
        }
        
        sb.append("USERS:\n");
        sb.append("  Total Users:     ").append(totalUsers).append("\n");
        sb.append("  Admins:          ").append(adminCount).append("\n");
        sb.append("  Customers:       ").append(clientCount).append("\n\n");
        
        // Vehicle statistics
        int totalVehicles = 0;
        for (User u : ps.getUsers()) {
            if (u instanceof Client) {
                totalVehicles += ((Client) u).getRegisteredVehicles().size();
            }
        }
        
        sb.append("REGISTERED VEHICLES:\n");
        sb.append("  Total:           ").append(totalVehicles).append("\n\n");
        
        sb.append("═══════════════════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    // ============================================================
    // MANUAL ENTRY/EXIT CARD
    // ============================================================
    
    private JPanel buildManualEntryCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel header = new JLabel("Manual Vehicle Entry/Exit", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);
        
        JPanel instructions = new JPanel(new BorderLayout());
        instructions.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextArea instructionsText = new JTextArea();
        instructionsText.setEditable(false);
        instructionsText.setFont(new Font("Arial", Font.PLAIN, 13));
        instructionsText.setText(
            "GATE ATTENDANT WORKFLOW:\n\n" +
            "VEHICLE ENTRY:\n" +
            "  • Quick Entry: Customer has registered account\n" +
            "     - Click 'Process Entry'\n" +
            "     - Enter plate number only\n" +
            "     - System finds registered vehicle\n\n" +
            "  • Manual Entry: Walk-up customer (no account)\n" +
            "     - Click 'Process Entry'\n" +
            "     - Switch to 'Manual Entry' mode\n" +
            "     - Fill complete vehicle information\n" +
            "     - System creates temporary vehicle record\n\n" +
            "VEHICLE EXIT:\n" +
            "  1. Customer returns to gate\n" +
            "  2. Click 'Process Exit'\n" +
            "  3. Enter ticket ID or plate number\n" +
            "  4. System calculates fee\n" +
            "  5. Collect payment from customer\n\n" +
            "REGISTERED TEST VEHICLES:\n" +
            "  • ABC123 - Toyota Camry (Blue)\n" +
            "  • XYZ789 - Honda Civic (Red)\n" +
            "  • BIKE01 - Harley Sportster (Black)"
        );
        instructions.add(instructionsText, BorderLayout.CENTER);
        panel.add(instructions, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton entryBtn = new JButton("Process Entry");
        JButton exitBtn = new JButton("Process Exit");
        
        entryBtn.setPreferredSize(new Dimension(150, 40));
        exitBtn.setPreferredSize(new Dimension(150, 40));
        
        entryBtn.addActionListener(e -> showEnhancedEntryDialog());
        exitBtn.addActionListener(e -> showExitDialog());
        
        buttonPanel.add(entryBtn);
        buttonPanel.add(exitBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================================
    // ACTIVE TICKETS CARD
    // ============================================================
    
    private JPanel buildActiveTicketsCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel header = new JLabel("Active Parking Sessions", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"Ticket ID", "Plate", "Vehicle", "Slot", "Entry Time", "Duration"};
        ticketsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ticketsTable = new JTable(ticketsTableModel);
        ticketsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ticketsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton refreshBtn = new JButton("Refresh");
        JButton processExitBtn = new JButton("Process Exit for Selected");
        
        refreshBtn.addActionListener(e -> refreshActiveTickets());
        processExitBtn.addActionListener(e -> processSelectedTicket());
        
        bottomPanel.add(refreshBtn);
        bottomPanel.add(processExitBtn);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Load initial data
        refreshActiveTickets();
        
        return panel;
    }
    
    private void refreshActiveTickets() {
        ticketsTableModel.setRowCount(0);
        
        ParkingSystem ps = ParkingSystem.getInstance();
        for (Ticket t : ps.getTickets()) {
            if (t.isActive()) {
                String ticketId = t.getTicketIDCode();
                String plate = t.getVehicle().getPlateNumber();
                String vehicle = t.getVehicle().getBrand() + " " + t.getVehicle().getModel();
                String slot = "#" + t.getSlot().getSlotID();
                String entryTime = t.getEntryTime().toString();
                String duration = t.calculateDuration() + " min";
                
                ticketsTableModel.addRow(new Object[]{ticketId, plate, vehicle, slot, entryTime, duration});
            }
        }
    }
    
    private void processSelectedTicket() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a ticket from the table.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        
        // Find the ticket and process exit
        ParkingSystem ps = ParkingSystem.getInstance();
        for (Ticket t : ps.getTickets()) {
            if (t.isActive() && t.getTicketIDCode().equals(ticketId)) {
                processExit(t);
                refreshActiveTickets();
                refreshDashboard();
                return;
            }
        }
    }
    
    // ============================================================
    // REPORTS CARD
    // ============================================================
    
    private JPanel buildReportsCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel header = new JLabel("System Reports", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);
        
        reportsArea = new JTextArea();
        reportsArea.setEditable(false);
        reportsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        reportsArea.setText(buildReportsText());
        
        panel.add(new JScrollPane(reportsArea), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton exportBtn = new JButton("Export Report (TODO)");
        exportBtn.setEnabled(false);
        buttonPanel.add(exportBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String buildReportsText() {
        ParkingSystem ps = ParkingSystem.getInstance();
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("           PARKZONE SYSTEM REPORTS\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        
        // Revenue report
        double totalRevenue = 0;
        int completedSessions = 0;
        long totalMinutes = 0;
        
        for (Ticket t : ps.getTickets()) {
            if (!t.isActive()) {
                totalRevenue += t.getTotalFee();
                completedSessions++;
                totalMinutes += t.calculateDuration();
            }
        }
        
        double avgDuration = completedSessions > 0 ? (totalMinutes / (double) completedSessions) : 0;
        
        sb.append("REVENUE REPORT:\n");
        sb.append("  Total Revenue:      $").append(String.format("%.2f", totalRevenue)).append("\n");
        sb.append("  Completed Sessions: ").append(completedSessions).append("\n");
        sb.append("  Avg Duration:       ").append(String.format("%.1f", avgDuration)).append(" minutes\n\n");
        
        // Popular slots
        sb.append("SLOT USAGE:\n");
        int[] slotUsage = new int[ps.getSlots().size() + 1];
        for (Ticket t : ps.getTickets()) {
            int slotId = t.getSlot().getSlotID();
            if (slotId > 0 && slotId < slotUsage.length) {
                slotUsage[slotId]++;
            }
        }
        
        for (int i = 1; i < slotUsage.length; i++) {
            if (slotUsage[i] > 0) {
                sb.append("  Slot #").append(i).append(": ").append(slotUsage[i]).append(" uses\n");
            }
        }
        
        sb.append("\n═══════════════════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    // ============================================================
    // ENHANCED VEHICLE ENTRY DIALOG
    // ============================================================
    
    private void showEnhancedEntryDialog() {
        ParkingSystem ps = ParkingSystem.getInstance();
        
        // Check for available slots first
    
        ParkingSlot tempSlot = null;
        for (ParkingSlot slot : ps.getSlots()) {
            if (!slot.isOccupied()) {
                tempSlot = slot;
                break;
            }
        }
        final ParkingSlot availableSlot = tempSlot;
        
        if (availableSlot == null) {
            JOptionPane.showMessageDialog(this,
                "No available parking slots!\n\nAll slots are currently occupied.",
                "Garage Full",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Manual Vehicle Entry", true);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Title
        JLabel title = new JLabel("Gate Attendant - Vehicle Entry", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(10, 0, 10, 0));
        dialog.add(title, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Entry mode selection
        JComboBox<String> entryModeCombo = new JComboBox<>(new String[]{
            "Quick Entry (Registered Vehicle)",
            "Manual Entry (Walk-up Customer)"
        });
        
        // Input fields
        JTextField plateField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        
        // Color dropdown
        JComboBox<String> colorCombo = new JComboBox<>(new String[]{
            "BLACK", "WHITE", "RED", "BLUE", "GREEN", 
            "YELLOW", "SILVER", "GOLDEN", "ORANGE", "PURPLE"
        });
        
        // Vehicle type dropdown
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "CAR", "MOTORCYCLE", "TRUCK", "VAN", "SUV", "EV", "BUS", "SCOOTER"
        });
        
        // Fee override
        JCheckBox feeOverrideCheck = new JCheckBox("Override Fee");
        JTextField feeOverrideField = new JTextField();
        feeOverrideField.setEnabled(false);
        
        // Add components to form
        formPanel.add(new JLabel("Entry Mode:"));
        formPanel.add(entryModeCombo);
        
        formPanel.add(new JLabel("License Plate: *"));
        formPanel.add(plateField);
        
        formPanel.add(new JLabel("Brand/Make:"));
        formPanel.add(brandField);
        
        formPanel.add(new JLabel("Model:"));
        formPanel.add(modelField);
        
        formPanel.add(new JLabel("Color:"));
        formPanel.add(colorCombo);
        
        formPanel.add(new JLabel("Vehicle Type:"));
        formPanel.add(typeCombo);
        
        formPanel.add(feeOverrideCheck);
        formPanel.add(feeOverrideField);
        
        // Instructions
        JLabel instructions = new JLabel(
            "<html><i>* Required field<br>" +
            "Quick Entry: Enter plate only<br>" +
            "Manual Entry: Fill all fields</i></html>"
        );
        instructions.setForeground(java.awt.Color.GRAY);
        formPanel.add(instructions);
        formPanel.add(new JLabel(""));
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Mode switching logic
        entryModeCombo.addActionListener(e -> {
            boolean isManual = entryModeCombo.getSelectedIndex() == 1;
            brandField.setEnabled(isManual);
            modelField.setEnabled(isManual);
            colorCombo.setEnabled(isManual);
            typeCombo.setEnabled(isManual);
            
            if (!isManual) {
                brandField.setText("");
                modelField.setText("");
            }
        });
        
        // Fee override toggle
        feeOverrideCheck.addActionListener(e -> {
            feeOverrideField.setEnabled(feeOverrideCheck.isSelected());
            if (!feeOverrideCheck.isSelected()) {
                feeOverrideField.setText("");
            }
        });
        
        // Initially disable manual fields
        brandField.setEnabled(false);
        modelField.setEnabled(false);
        colorCombo.setEnabled(false);
        typeCombo.setEnabled(false);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton submitBtn = new JButton("Issue Ticket");
        JButton cancelBtn = new JButton("Cancel");
        
        submitBtn.addActionListener(e -> {
            String plate = plateField.getText().trim().toUpperCase();
            boolean isManualEntry = entryModeCombo.getSelectedIndex() == 1;
            
            // Validation
            if (plate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "License plate is required!", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Vehicle vehicle = null;
            
            if (isManualEntry) {
                // Manual entry - create vehicle on the fly
                String brand = brandField.getText().trim();
                String vehicleModel = modelField.getText().trim();  
                
                if (brand.isEmpty() || vehicleModel.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Brand and model are required for manual entry!", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String colorStr = (String) colorCombo.getSelectedItem();
                String typeStr = (String) typeCombo.getSelectedItem();
               
                
                model.Colors vehicleColor;
                switch(colorStr) {
                    case "BLACK": vehicleColor = model.Colors.BLACK; break;
                    case "WHITE": vehicleColor = model.Colors.WHITE; break;
                    case "RED": vehicleColor = model.Colors.RED; break;
                    case "BLUE": vehicleColor = model.Colors.BLUE; break;
                    case "GREEN": vehicleColor = model.Colors.GREEN; break;
                    case "YELLOW": vehicleColor = model.Colors.YELLOW; break;
                    case "SILVER": vehicleColor = model.Colors.SILVER; break;
                    case "GOLDEN": vehicleColor = model.Colors.GOLDEN; break;
                    case "ORANGE": vehicleColor = model.Colors.ORANGE; break;
                    case "PURPLE": vehicleColor = model.Colors.PURPLE; break;
                    default: vehicleColor = model.Colors.BLACK; break;
                }

                VehicleType type = VehicleType.valueOf(typeStr);

                // Then use vehicleColor instead of color
                switch(type) {
                    case CAR:
                    case SUV:
                    case EV:
                        vehicle = new Car(plate, brand, vehicleModel, vehicleColor);
                        break;
                    case MOTORCYCLE:
                    case SCOOTER:
                        vehicle = new Bike(plate, brand, vehicleModel, vehicleColor);
                        break;
                    default:
                        vehicle = new Car(plate, brand, vehicleModel, vehicleColor);
                        break;
                }
                    
                    System.out.println("[AdminPanel] Created guest vehicle: " + plate);
                    
                
            
                
                if (vehicle == null) {
                    int choice = JOptionPane.showConfirmDialog(dialog,
                        "Vehicle '" + plate + "' not found in system.\n\nSwitch to Manual Entry mode?",
                        "Vehicle Not Found",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        entryModeCombo.setSelectedIndex(1);
                    }
                    return;
                }
            }
            
            // Issue ticket
            Ticket ticket = ps.issueTicket(vehicle, availableSlot);
            
            // Apply fee override if set
            if (feeOverrideCheck.isSelected() && !feeOverrideField.getText().trim().isEmpty()) {
                try {
                    double overrideFee = Double.parseDouble(feeOverrideField.getText().trim());
                    ticket.setFeeOverride(overrideFee);
                    System.out.println("[AdminPanel] Fee override applied: $" + overrideFee);
                } catch (NumberFormatException ex) {
                    System.err.println("[AdminPanel] Invalid fee override value");
                }
            }
            
            // Success message
            String message = String.format(
                "✓ Vehicle Parked Successfully!\n\n" +
                "═══════════════════════════════════════\n" +
                "TICKET ID:    %s\n" +
                "PLATE:        %s\n" +
                "VEHICLE:      %s %s\n" +
                "TYPE:         %s\n" +
                "SLOT:         #%d\n" +
                "ENTRY TIME:   %s\n" +
                "═══════════════════════════════════════\n\n" +
                "Entry Type: %s\n\n" +
                "Please give this ticket to the customer.",
                ticket.getTicketIDCode(),
                vehicle.getPlateNumber(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getType(),
                availableSlot.getSlotID(),
                ticket.getEntryTime().toString(),
                isManualEntry ? "Walk-up Customer" : "Registered Customer"
            );
            
            JOptionPane.showMessageDialog(
                dialog,
                message,
                "Ticket Issued - " + ticket.getTicketIDCode(),
                JOptionPane.INFORMATION_MESSAGE
            );
            
            dialog.dispose();
            refreshAllData();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    // ============================================================
    // VEHICLE EXIT DIALOG
    // ============================================================
    
    private void showExitDialog() {
        ParkingSystem ps = ParkingSystem.getInstance();
        
        String input = JOptionPane.showInputDialog(
            this,
            "Enter ticket ID or plate number:",
            "Manual Vehicle Exit",
            JOptionPane.PLAIN_MESSAGE
        );
        
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
            JOptionPane.showMessageDialog(this,
                "No active parking session found for: " + input + "\n\n" +
                "Please verify the ticket ID or plate number.",
                "Session Not Found",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        processExit(ticket);
        refreshAllData();
    }
    
    private void processExit(Ticket ticket) {
        ParkingSystem ps = ParkingSystem.getInstance();
        
        ps.endParking(ticket.getTicketID());
        
        int duration = ticket.calculateDuration();
        double fee = ticket.getTotalFee();
        
        String message = String.format(
            "✓ Vehicle Exit Processed\n\n" +
            "═══════════════════════════════════════\n" +
            "TICKET ID:    %s\n" +
            "PLATE:        %s\n" +
            "VEHICLE:      %s %s\n" +
            "SLOT:         #%d\n\n" +
            "ENTRY:        %s\n" +
            "EXIT:         %s\n" +
            "DURATION:     %d minutes\n\n" +
            "═══════════════════════════════════════\n" +
            "TOTAL FEE:    $%.2f\n" +
            "═══════════════════════════════════════\n\n" +
            "Please collect payment from customer.",
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
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "Payment Due - $" + String.format("%.2f", fee),
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    private void refreshDashboard() {
        if (dashboardArea != null) {
            dashboardArea.setText(buildDashboardText());
        }
    }
    
    private void refreshReports() {
        if (reportsArea != null) {
            reportsArea.setText(buildReportsText());
        }
    }
    
    private void refreshAllData() {
        refreshDashboard();
        refreshActiveTickets();
        refreshReports();
    }
}