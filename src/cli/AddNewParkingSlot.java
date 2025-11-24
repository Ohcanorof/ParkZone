package cli;

import java.util.Scanner;

import model.Actionable;
import model.ParkingSlot;
import model.SlotType;
import model.User;
import storage.DataManager;
import util.ConsoleInput;

public class AddNewParkingSlot implements Actionable{

	@Override
	public String getLabel() {
		return "Add New Parking Slot";
	}

	@Override
	public void execute(Scanner s, User u) {
			System.out.println("Select new slot type");
			SlotType[] types = SlotType.values();
			int selected;
			do {
				for (int i = 0;i < types.length; i++){
					System.out.println((i+1)+". " + types[i]);
				}
				selected = ConsoleInput.readInt(s)-1;
			}while (selected < 0 || selected >= types.length);
			ParkingSlot slot = new ParkingSlot(types [selected]);
			DataManager.parkingSlots.add(slot);
			System.out.println("Parking Slot added successfully");
	}

	@Override
	public boolean isAdminOnly() {
		return true;
	}

}
