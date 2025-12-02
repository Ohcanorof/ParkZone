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
                server.start();
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
        // INITIALIZE 600 SLOTS ACROSS 6 FLOORS
        // Each floor has 100 slots divided into 5 sections (A-E)
        // Each section has 20 slots arranged in 4×5 grid
        // Total capacity: 600 slots
        // ============================================================
        
        System.out.println("\n[Main] Initializing 600-slot parking structure...");
        
        int slotID = 1;
        for (int floor = 1; floor <= 6; floor++) {
            System.out.println("[Main] Creating Floor " + floor + "...");
            
            for (int sectionIndex = 0; sectionIndex < 5; sectionIndex++) {
                String section = String.valueOf((char)('A' + sectionIndex));
                
                // Create 20 slots for this section
                for (int slotInSection = 0; slotInSection < 20; slotInSection++) {
                    ParkingSlot slot = new ParkingSlot(slotID, floor, section);
                    ps.addSlot(slot);
                    slotID++;
                }
                
                System.out.println("[Main]   Floor " + floor + ", Section " + section + 
                    ": Slots " + (slotID - 20) + "-" + (slotID - 1));
            }
        }
        
        System.out.println("[Main] ✓ Initialized 600 parking slots across 6 floors");
        System.out.println("[Main]   Slots 1-100:    Floor 1 (Sections A-E)");
        System.out.println("[Main]   Slots 101-200:  Floor 2 (Sections A-E)");
        System.out.println("[Main]   Slots 201-300:  Floor 3 (Sections A-E)");
        System.out.println("[Main]   Slots 301-400:  Floor 4 (Sections A-E)");
        System.out.println("[Main]   Slots 401-500:  Floor 5 (Sections A-E)");
        System.out.println("[Main]   Slots 501-600:  Floor 6 (Sections A-E)");

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
                clientGUI.start();  // Initialize GUI
                // Auto-connect to server
                clientGUI.connect("localhost", 8080);
            });
        }
        
        System.out.println("[Main] ✓ Application started successfully\n");
    }
}