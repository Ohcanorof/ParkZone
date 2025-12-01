package uiwindows;

import model.Client;
import model.ParkingSlot;
import model.Ticket;
import model.TicketService;
import model.Vehicle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public class ReservationDialog extends JDialog {

    private final Client client;
    private final ParkingSlot slot;
    private final TicketService ticketService;
    private final Consumer<Ticket> onTicketCreated;

    private JComboBox<Vehicle> vehicleCombo;
    private JSpinner startOffsetSpinner;  // minutes from now
    private JSpinner durationSpinner;     // minutes

    //constructor
    public ReservationDialog(Window owner, Client client, ParkingSlot slot, TicketService ticketService, Consumer<Ticket> onTicketCreated) {
        super(owner, "Reserve Slot #" + slot.getSlotID(), ModalityType.APPLICATION_MODAL);
        this.client = client;
        this.slot = slot;
        this.ticketService = ticketService;
        this.onTicketCreated = onTicketCreated;

        setSize(420, 320);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Slot info label
        JLabel slotInfo = new JLabel(
                "Slot #" + slot.getSlotID(),
                SwingConstants.LEFT
        );
        form.add(slotInfo);

        //Vehicle selection
        form.add(new JLabel("Select vehicle:"));
        vehicleCombo = new JComboBox<>();
        List<Vehicle> vehicles = client.getRegisteredVehicles();
        if (vehicles != null) {
            for (Vehicle v : vehicles) {
                vehicleCombo.addItem(v);
            }
        }
        vehicleCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value == null) {
                lbl.setText("(no vehicles)");
            } else {
                String plate = value.getPlateNumber() != null ? value.getPlateNumber() : "(no plate)";
                String brand = value.getBrand() != null ? value.getBrand() : "(brand?)";
                String model = value.getModel() != null ? value.getModel() : "(model?)";
                lbl.setText(plate + " - " + brand + " " + model);
            }
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            }
            return lbl;
        });
        form.add(vehicleCombo);

        //Start offset (minutes from now)
        form.add(new JLabel("Start in (minutes from now):"));
        startOffsetSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 24 * 60, 15));
        form.add(startOffsetSpinner);

        //Duration (minutes)
        form.add(new JLabel("Duration (minutes):"));
        durationSpinner = new JSpinner(new SpinnerNumberModel(60, 30, 24 * 60, 15));
        form.add(durationSpinner);

        add(form, BorderLayout.CENTER);

        //Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton confirmBtn = new JButton("Confirm Reservation");
        JButton cancelBtn = new JButton("Cancel");

        confirmBtn.addActionListener(e -> onConfirm());
        cancelBtn.addActionListener(e -> dispose());

        buttons.add(confirmBtn);
        buttons.add(cancelBtn);
        add(buttons, BorderLayout.SOUTH);
    }

    private void onConfirm() {
        Vehicle selectedVehicle = (Vehicle) vehicleCombo.getSelectedItem();
        if (selectedVehicle == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please register a vehicle and select it.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        //should stop reservations on a slot that is already occupied.
        if (slot.isOccupied()) {
            JOptionPane.showMessageDialog(
                    this,
                    "This slot is already occupied.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int offsetMinutes = (Integer) startOffsetSpinner.getValue();
        int durationMinutes = (Integer) durationSpinner.getValue();

        LocalDateTime entryTime = LocalDateTime.now().plusMinutes(offsetMinutes);

        Ticket ticket;
        try {
            //Use TicketService for this 
            ticket = ticketService.createTicket(client, selectedVehicle, slot, entryTime);
            if (ticket == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to create ticket (slot may be occupied).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot create reservation: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        //estimate cost (based on the planned duration) and store it in the ticket
        double estimatedFee = 0.0;
        try {
            estimatedFee = selectedVehicle.calculateFee(durationMinutes);
        } catch (Exception ignored) {
        }
        ticket.setTotalFee(estimatedFee);

        //send ticket back to SlotsPanel
        if (onTicketCreated != null) {
            onTicketCreated.accept(ticket);
        }

        //quick summary including the estimate
        //readable
        JOptionPane.showMessageDialog(
                this,
                "Reservation created:\n" +
                        "Ticket #" + ticket.getTicketID() + "\n" +
                        "Slot: " + slot.getSlotID() + "\n" +
                        "Start: " + entryTime + "\n" +
                        "Duration: " + durationMinutes + " minutes\n" +
                        String.format("Estimated total: $%.2f", estimatedFee),
                "Reservation Confirmed",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }
}