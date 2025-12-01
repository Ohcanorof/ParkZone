package uiwindows;

import model.Client;
import model.ParkingSlot;
import model.Ticket;
import model.TicketService;
import model.Vehicle;
import model.ClientGUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public class ReservationDialog extends JDialog {
	private final ClientGUI gui;
    private final Client client;
    private final ParkingSlot slot;
    private final Consumer<Ticket> onTicketCreated;

    private JComboBox<Vehicle> vehicleCombo;
    private LocalDateTime entryTime;
    private JSpinner durationSpinner;     // minutes

    //constructor
    public ReservationDialog(Window owner, ClientGUI gui, Client client, ParkingSlot slot, Consumer<Ticket> onTicketCreated) {
    	super(owner, "Reserve Slot " + slot.getSlotID(), ModalityType.APPLICATION_MODAL);
        this.gui = gui;
        this.client = client;
        this.slot = slot;
        this.onTicketCreated = onTicketCreated;

        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
    	JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Slot label
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Slot:"), gbc);

        gbc.gridx = 1;
        form.add(new JLabel(String.valueOf(slot.getSlotID())), gbc);
        row++;

        // Vehicle combo
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Vehicle:"), gbc);

        gbc.gridx = 1;
        vehicleCombo = new JComboBox<>();
        List<Vehicle> vehicles = client.getRegisteredVehicles();
        for (Vehicle v : vehicles) {
            vehicleCombo.addItem(v);
        }
        form.add(vehicleCombo, gbc);
        row++;

        // Duration
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Duration (minutes):"), gbc);

        gbc.gridx = 1;
        durationSpinner = new JSpinner(new SpinnerNumberModel(60, 15, 240, 15));
        form.add(durationSpinner, gbc);
        row++;

        content.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton confirm = new JButton("Confirm");

        cancel.addActionListener(e -> dispose());
        confirm.addActionListener(e -> onConfirm());

        buttons.add(cancel);
        buttons.add(confirm);

        content.add(buttons, BorderLayout.SOUTH);
    }

    private void onConfirm() {
        Vehicle selectedVehicle = (Vehicle) vehicleCombo.getSelectedItem();
        if (selectedVehicle == null) {
        	JOptionPane.showMessageDialog(this, "Please select a vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int durationMinutes = (Integer) durationSpinner.getValue();
        entryTime = LocalDateTime.now(); // client-side timestamp (server still owns truth)

        String plate = selectedVehicle.getPlateNumber();

        boolean ok;
        try {
        	gui.notifyServerSlotReserved(slot.getSlotID(), selectedVehicle);
            ok = true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error sending reservation: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Reservation failed. Please check logs for details.",
                    "Reservation Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // At this point, server has created the Ticket.
        // If you extend the protocol to return Ticket info, you can pass it to onTicketCreated.
        if (onTicketCreated != null) {
            onTicketCreated.accept(null); // placeholder until server returns Ticket
        }

        JOptionPane.showMessageDialog(
                this,
                "Reservation requested for Slot " + slot.getSlotID() +
                        "\nVehicle: " + plate +
                        "\nDuration: " + durationMinutes + " minutes",
                "Reservation Sent",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }
}