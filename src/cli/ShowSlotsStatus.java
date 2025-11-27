package cli;

import java.util.Scanner;
import java.util.ArrayList;
import storage.DataManager;
import model.ParkingSlot;
import model.Scooter;
import model.SlotType;
import model.Truck;
import model.Actionable;
import model.Bicycle;
import model.Bike;
import model.Bus;
import model.Car;
import model.EV;
import model.User;
import model.Van;
import model.Vehicle;

public class ShowSlotsStatus implements Actionable {

	@Override
	public String getLabel() {
		return "Show Parking Slots status";
	}

	@Override
	public void execute(Scanner s, User u) {
		// this will print available slot by slot type
		// Slot Type  num of available slots
		// then print occupied slots
		// slot num.	Type	Vehicle Type	Vehicle Plate
		
		
		// here we count the available slot of each type
		int bikeSlots = 0;
		int compactSlots = 0;
		int largeSlots = 0;
		int electricSlots = 0;
		int bicycleSlots = 0;
		
		
		// counter for occupied slots
		int occupiedSlots = 0;
	
		
		// printing occupied slots then counters of avaiable slots
		System.out.println("Occupied Slots: ");
		
		ArrayList<ParkingSlot > slots = DataManager.parkingSlots;
		for (ParkingSlot slot: slots) {
			if (slot.isAvailable()) {
				switch (slot.getType()){
				case SlotType.BIKE:
					bikeSlots++;
					break;
				case SlotType.COMPACT:
					compactSlots++;
					break;
				case SlotType.LARGE:
					largeSlots++;
					break;
				case SlotType.ELECTRIC:
					electricSlots++;
					break;
				case SlotType.BICYCLE:
					bicycleSlots++;
					break;
				}
			}else {
				// occupied
				occupiedSlots++;
				System.out.println("\tSlot Number: " + slot.getSlotNumber());
				System.out.println("\tSlot Type: "+ slot.getType());
				Vehicle vehicle = slot.getVehicle();
				if (vehicle instanceof Car) {
					System.out.println("\tVehicle Type: CAR");
				} else if (vehicle instanceof Bike) {
					System.out.println("\tVehicle Type: BIKE");
				} else if (vehicle instanceof Scooter) {
					System.out.println("\tVehicle Type: SCOOTER");
				} else if (vehicle instanceof Bicycle) {
					System.out.println("\tVehicle Type: BICYCLE");
				} else if (vehicle instanceof EV) {
					System.out.println("\tVehicle Type: EV");
				} else if (vehicle instanceof Bus) {
					System.out.println("\tVehicle Type: BUS");
				} else if (vehicle instanceof Truck) {
					System.out.println("\tVehicle Type: TRUCK");
				} else if (vehicle instanceof Van) {
						System.out.println("Vehicle Type: Van");
				} 
				System.out.println("\tVehicle Plate: " + vehicle.getPlateNumber());
				System.out.println("\t------------------------\n");
			
			}
		}
		
		// this will print no occupied slot if its counter = 0
		if (occupiedSlots == 0) {
			 System.out.println("\tNo occupied slots \n");
		}
		
		System.out.println("Available Slots: ");
		System.out.println("\tBike: " + bikeSlots);
		System.out.println("\tCompact: " + compactSlots);
		System.out.println("\tLarge: " + largeSlots);
		System.out.println("\tElectric: " + electricSlots);
		System.out.println("\tBicycle: " + bicycleSlots + "\n");
		
	}

	@Override
	public boolean isAdminOnly() {
		return false;
	}

}
