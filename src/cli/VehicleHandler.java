package cli;

import java.util.Scanner;
import model.User;
import model.Van;
import model.Vehicle;
import model.Bicycle;
import model.Bike;
import model.Bus;
import model.Car;
import model.Color;
import model.EV;
import model.Scooter;
import model.Truck;
import model.VehicleType;
import storage.DataManager;

import util.ConsoleInput;

public class VehicleHandler {
	
	public static Vehicle AddNewVehicle(Scanner s, User u, String plateNumber) {
		 System.out.println("Select Vehicle Type");
		 VehicleType[] types = VehicleType.values();
		 int typeIndex;
		 do {
			 for (int i=0; i < types.length; i++) {
				 System.out.println("\t" + (i+1) + ". "+ types[i]);
			 }
			 typeIndex = ConsoleInput.readInt(s)-1;
		 } while (typeIndex < 0 || typeIndex >= types.length);
		 VehicleType type = types[typeIndex];
		 
		 System.out.println("Select Vehicle Color");
		 Color[] colors = Color.values();
		 int colorIndex;
		 do {
			 for (int i=0; i < colors.length; i++) {
				 System.out.println("\t"+(i+1)+". "+colors[i]);
			 }
		 colorIndex = ConsoleInput.readInt(s)-1;
		 } while (colorIndex < 0 || colorIndex >= colors.length);
		 Color color = colors[colorIndex];
		 
		 System.out.print("Vehicle Brand: ");
		 String brand = ConsoleInput.readString(s);
		 System.out.print("Vehicle Model: ");
		 String model = ConsoleInput.readString(s);
		 
		 Vehicle vehicle;
		
		 switch (type) {
		 case VehicleType.CAR:
			 vehicle = new Car();
			 break;
		 case VehicleType.BIKE:
			 vehicle = new Bike();
			 break;
		 case VehicleType. BUS:
			 vehicle = new Bus ();
			 break;
		 case VehicleType. TRUCK:
			 vehicle = new Truck();
			 break;
		 case VehicleType. VAN:
			 vehicle = new Van();
			 break;
		 case VehicleType.SCOOTER:
			 vehicle = new Scooter ();
			 break;
		 case VehicleType.BICYCLE:
			 vehicle = new Bicycle();
			 break;
		 case VehicleType.EV:
			 vehicle = new EV();
			 break;
			 default:
				 
				 System.out.println("Invalid vehicle type");
				 return null;
		 }
		 vehicle.setPlateNumber(plateNumber);
		 vehicle.setOwnerID(u.getID());
		 vehicle.setColor(color);
		 vehicle.setBrand(brand);
		 vehicle.setModel(model);
		 DataManager.registeredVehicles.add(vehicle);
		 return vehicle;
	}
}

	
