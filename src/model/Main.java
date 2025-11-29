package model;

public class Main {

    public static void main(String[] args) {
        int port = 8080;

        // Start server ON THIS MACHINE
        ParkingSystemServer server = new ParkingSystemServer(port);
        try {
            server.start();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // ✨ ADD THIS LINE - Actually call the method!
        initializeParkingSlots();

        // Start GUI connecting to localhost
        ClientGUI gui = new ClientGUI();
        gui.start();
        gui.connect("localhost", port);
    }
    
    private static void initializeParkingSlots() {
        ParkingSystem ps = ParkingSystem.getInstance();

        // Add 10 parking slots for testing
        for (int i = 1; i <= 10; i++) {
            ParkingSlot slot = new ParkingSlot(i);
            ps.addSlot(slot);
            System.out.println("[Main] Added slot " + i);
        }

        System.out.println("[Main] Initialized " + ps.getSlots().size() + " parking slots\n");
    }
}