package uiwindows;

import model.Client;
import model.ClientGUI;
import model.ParkingSlot;
import model.User;
import model.Vehicle;
import model.VehicleType;
import model.Admin;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//slot cell class inside this one
public class SlotCell extends JPanel{
	private final ClientGUI gui;
	private final ParkingSlot slot;
	private final VehicleType filterType;
	private boolean hovered = false;
	
	SlotCell(ClientGUI gui, ParkingSlot slot, VehicleType filterType){
		this.gui = gui;
		this.slot = slot;
		this.filterType = filterType;
		
		setPreferredSize(new Dimension(40, 40));
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,1));
		String typeText = (slot.getAllowedType() != null) ? slot.getAllowedType().name() : "ANY";
		String rateText = (slot.getHourlyRate() > 0) ? String.format("$%.2f/hr", slot.getHourlyRate()) : "(default rate)";

		setToolTipText("Slot #" + slot.getSlotID() + " | Type: " + typeText + " | " + rateText);
		
		JLabel label = new JLabel(String.valueOf(slot.getSlotID()), SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.BOLD, 12));
		setLayout(new BorderLayout());
		add(label, BorderLayout.CENTER);
		updateColors();
		
		//lister for color changing when clicking and hovering over.
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hovered = true;
				updateColors();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				hovered = false;
				updateColors();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				gui.selectSlot(slot.getSlotID());
				User user = gui.getCurrentUser();
			    if (user instanceof model.Admin) {
			        // admins configure slot, not reserve it
			        gui.openAdminSlotConfigDialog(slot);
			    } else {
			        // customers make reservations
			        gui.openReservationDialog(slot);
			    }
			}
		});
		
		
	}
	
	//funtion for the grid colors
	private void updateColors() {
		Color base;
		boolean occupied = slot.isOccupied();
		boolean mine = isSlotOwnedByCurrentUser(slot);
		boolean compatible = isSlotCompatibleWithFilter(slot, filterType);
		
		if(!compatible) {
			//grey (slot doesnt fit in the filtered search)
			base = new Color(0xDDDDDD);
		}else if(occupied && mine) {
			//yellow = taken by the current user
            base = new Color(0xFFF3A3);
		}else if(occupied) {
			//red = taken by another client
            base = new Color(0xFFCCCC);
		} else {
			//green = available slot
			base = new Color(0xCCFFCC);
		}
		
		//this if chain will be for the color change when your mouse hovers over it
		Color hoverAdjust;
		if(!compatible) {
			hoverAdjust = new Color(0xC0C0C0);
		}else if(occupied && mine) {
			hoverAdjust = new Color(0xFFE070);
		}else if(occupied) {
			hoverAdjust = new Color(0xFF9999);
		} else {
			hoverAdjust = new Color(0x99FF99);
		}
		
		setBackground(hovered ? hoverAdjust : base);
	}
	
	//---------------------
	//helper functions
	private boolean isSlotOwnedByCurrentUser(ParkingSlot slot) {
		User user = gui.getCurrentUser();
		if(!(user instanceof Client)) {
			return false;
		}
		Vehicle slotVehicle = slot.getVehicle();
		if(slotVehicle == null) {
			return false;
		}
		Client client = (Client) user;
		for(Vehicle v : client.getRegisteredVehicles()) {
			if(v.getPlateNumber() != null && v.getPlateNumber().equalsIgnoreCase(slotVehicle.getPlateNumber())) {
				return true;
			}
		}
		return false;
	}
	
	//function for the filter to slot compatibility
	// UX FIX: Filter by actual vehicle type in occupied slots
	private boolean isSlotCompatibleWithFilter(ParkingSlot slot, VehicleType filterType) {
		if(filterType == null) {
			return true;  // "All" selected - show everything
		}
		
		// If slot is occupied, check the VEHICLE's type
		if(slot.isOccupied() && slot.getVehicle() != null) {
			Vehicle vehicle = slot.getVehicle();
			VehicleType vehicleType = vehicle.getType();
			return vehicleType == filterType;
		}
		
		// If slot is available, check if it ALLOWS this vehicle type
		VehicleType allowed = slot.getAllowedType();
		if(allowed == null) {
			return true;  // No restriction - allows all types
		}
		
		return allowed == filterType;
	}
	
}