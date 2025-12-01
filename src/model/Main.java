package model;

import uiwindows.AdminGUI;

public class Main {
    public static void main(String[] args) {
        int port = 8080;
        
        ParkingSystemServer server = new ParkingSystemServer(port);
        try {
            server.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        // Initialize parking slots and test data
        initializeParkingSlots();
        initializeTestData();
        
        // ✨ NEW: Choose which GUI to launch
        String mode = args.length > 0 ? args[0] : "customer";
        
        if ("admin".equalsIgnoreCase(mode)) {
            // Launch admin panel
            AdminGUI adminGui = new AdminGUI();
            adminGui.start();
            adminGui.connect("localhost", port);
        } else {
            // Launch customer GUI (Jose's work)
            ClientGUI gui = new ClientGUI();
            gui.start();
            gui.connect("localhost", port);
        }
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
        
        // Create test admin
        Admin testAdmin = new Admin(999, "Admin", "User", "admin@parkzone.com", "admin123");
        testAdmin.setAccountType("ADMIN");
        ps.addUser(testAdmin);
        
        // Create test client
        Client testClient = new Client(1, "Test", "User", "test@test.com", "password");
        testClient.setAccountType("CUSTOMER");
        ps.addUser(testClient);
        
        // Create test vehicles
        Car car1 = new Car("ABC123", "Toyota", "Camry", Color.BLUE);
        Car car2 = new Car("XYZ789", "Honda", "Civic", Color.RED);
        Bike bike1 = new Bike("BIKE01", "Harley", "Sportster", Color.BLACK);
        
        testClient.registerVehicle(car1);
        testClient.registerVehicle(car2);
        testClient.registerVehicle(bike1);
        
        System.out.println("[Main] Created test admin: admin@parkzone.com / admin123");
        System.out.println("[Main] Created test client with 3 vehicles:");
        System.out.println("  - ABC123 (Toyota Camry)");
        System.out.println("  - XYZ789 (Honda Civic)");
        System.out.println("  - BIKE01 (Harley Bike)\n");
    }
}