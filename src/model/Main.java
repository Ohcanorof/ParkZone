package model;

public class Main {

    public static void main(String[] args) {
        int port = 8080;

        // Start server
        ParkingSystemServer server = new ParkingSystemServer(port);
        try {
            server.start();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Initialize parking slots
        initializeParkingSlots();
        
        // ✨ NEW: Add test vehicle
        initializeTestData();

        // Start GUI
        ClientGUI gui = new ClientGUI();
        gui.start();
        gui.connect("localhost", port);
    }
    
    private static void initializeParkingSlots() {
        ParkingSystem ps = ParkingSystem.getInstance();

        for (int i = 1; i <= 10; i++) {
            ParkingSlot slot = new ParkingSlot(i);
            ps.addSlot(slot);
            System.out.println("[Main] Added slot " + i);
        }

        System.out.println("[Main] Initialized " + ps.getSlots().size() + " parking slots\n");
    }
    
    private static void initializeTestData() {
        ParkingSystem ps = ParkingSystem.getInstance();
        
        // Create a test client
        Client testClient = new Client(1, "Test", "User", "test@test.com", "password");
        ps.addUser(testClient);
        
        // Create test vehicles
        Car car1 = new Car("ABC123", "Toyota", "Camry", Color.BLUE);
        Car car2 = new Car("XYZ789", "Honda", "Civic", Color.RED);
        Bike bike1 = new Bike("BIKE01", "Harley", "Sportster", Color.BLACK);
        
        // Register vehicles to client
        testClient.registerVehicle(car1);
        testClient.registerVehicle(car2);
        testClient.registerVehicle(bike1);
        
        System.out.println("[Main] Created test client with 3 vehicles:");
        System.out.println("  - " + car1.getPlateNumber() + " (Toyota Camry)");
        System.out.println("  - " + car2.getPlateNumber() + " (Honda Civic)");
        System.out.println("  - " + bike1.getPlateNumber() + " (Harley Bike)\n");
    }
}