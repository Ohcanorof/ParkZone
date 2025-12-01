package model;

import java.time.LocalDateTime;

/**
 * Main - ParkZone Application Entry Point
 * Initializes 100-slot parking structure on Floor 1
 */
public class Main {
    public static void main(String[] args) {
        // Start server in background thread
    	Thread serverThread = new Thread(() -> {
    	    ParkingSystemServer server = new ParkingSystemServer(8080);
    	    try {
    	        server.start();  // ✅ Now handles IOException
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	    }
    	});
        serverThread.setDaemon(false);
        serverThread.start();

        // Wait for server to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Initialize parking system
        ParkingSystem ps = ParkingSystem.getInstance();

        // ============================================================
        // INITIALIZE 100 SLOTS ON FLOOR 1
        // Floor 1 divided into 5 sections (A-E)
        // Each section has 20 slots arranged in 4×5 grid
        // ============================================================
        
        System.out.println("\n[Main] Initializing 100-slot parking structure...");
        
        for (int i = 1; i <= 100; i++) {
            int floor = 1;  // All slots on Floor 1 initially
            String section;
            
            // Assign section based on slot ID
            if (i <= 20) {
                section = "A";  // Slots 1-20 → Section A
            } else if (i <= 40) {
                section = "B";  // Slots 21-40 → Section B
            } else if (i <= 60) {
                section = "C";  // Slots 41-60 → Section C
            } else if (i <= 80) {
                section = "D";  // Slots 61-80 → Section D
            } else {
                section = "E";  // Slots 81-100 → Section E
            }
            
            ParkingSlot slot = new ParkingSlot(i, floor, section);
            ps.addSlot(slot);
        }
        
        System.out.println("[Main] ✓ Initialized 100 parking slots");
        System.out.println("[Main]   Floor 1, Section A: Slots 1-20");
        System.out.println("[Main]   Floor 1, Section B: Slots 21-40");
        System.out.println("[Main]   Floor 1, Section C: Slots 41-60");
        System.out.println("[Main]   Floor 1, Section D: Slots 61-80");
        System.out.println("[Main]   Floor 1, Section E: Slots 81-100");

        // ============================================================
        // CREATE TEST USERS
        // ============================================================
        
        System.out.println("\n[Main] Creating test users...");
        
        // Admin account
        Admin admin = new Admin(
            1,                      // ID
            "Admin",                // firstName
            "User",                 // lastName
            "admin@parkzone.com",   // email
            "admin123"              // password
        );
        ps.addUser(admin);
        System.out.println("[Main] Created test admin: admin@parkzone.com / admin123");

        // Test client with 3 vehicles
        Client testClient = new Client(
            2,                      // ID
            "Test",                 // firstName
            "Customer",             // lastName
            "client@parkzone.com",  // email
            "client123"             // password
        );

        // Vehicle 1: Toyota Camry
        Car car1 = new Car("ABC123", "Toyota", "Camry", Colors.BLUE);
        car1.setType(VehicleType.CAR);
        testClient.addRegisteredVehicles(car1);

        // Vehicle 2: Honda Civic
        Car car2 = new Car("XYZ789", "Honda", "Civic", Colors.RED);
        car2.setType(VehicleType.CAR);
        testClient.addRegisteredVehicles(car2);

        // Vehicle 3: Harley Davidson Motorcycle
        Bike bike1 = new Bike("BIKE01", "Harley", "Sportster", Colors.BLACK);
        bike1.setType(VehicleType.MOTORCYCLE);
        testClient.addRegisteredVehicles(bike1);

        ps.addUser(testClient);
        
        System.out.println("[Main] Created test client with 3 vehicles:");
        System.out.println("[Main]   - ABC123 (Toyota Camry)");
        System.out.println("[Main]   - XYZ789 (Honda Civic)");
        System.out.println("[Main]   - BIKE01 (Harley Bike)");

        // ============================================================
        // LAUNCH CLIENT GUI
        // ============================================================
        
        System.out.println("\n[Main] Launching client GUI...");
        
        // Check for admin mode argument
        boolean adminMode = false;
        if (args.length > 0 && "admin".equalsIgnoreCase(args[0])) {
            adminMode = true;
        }

        if (adminMode) {
            // Launch standalone AdminGUI (legacy mode)
            System.out.println("[Main] Starting in ADMIN mode...");
            javax.swing.SwingUtilities.invokeLater(() -> {
                new uiwindows.AdminGUI();
            });
        } else {
            // Launch main ClientGUI (default)
            System.out.println("[Main] Starting CLIENT mode...");
            javax.swing.SwingUtilities.invokeLater(() -> {
                ClientGUI clientGUI = new ClientGUI();
                clientGUI.start();  // Use start() instead of showWelcomePage()
                clientGUI.connect("localhost", 8080);
            });
        }
        
        System.out.println("[Main] ✓ Application started successfully\n");
    }
}