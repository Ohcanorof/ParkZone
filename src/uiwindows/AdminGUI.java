package uiwindows;

import uiwindows.AdminPanel;
import javax.swing.*;

import model.EventStreamClient;
import model.User;

import java.awt.*;
import java.io.IOException;

/**
 * Admin GUI - Separate interface for administrative users
 * Provides gate attendant functionality and system monitoring
 */
public class AdminGUI {
    private JFrame frame;
    private EventStreamClient client;
    private boolean connected = false;
    private User currentUser;
    
    public AdminGUI() {
        // Constructor
    }
    
    public void start() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("ParkZone - Admin Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            
            // Show admin panel directly (assuming already logged in)
            showAdminPanel();
            
            frame.setVisible(true);
        });
    }
    
    public void connect(String host, int port) {
        try {
            client = new EventStreamClient(host, port);
            connected = true;
            System.out.println("[AdminGUI] Connected to server at " + host + ":" + port);
        } catch (IOException e) {
            System.err.println("[AdminGUI] Connection failed: " + e.getMessage());
            connected = false;
            handleError("Could not connect to server at " + host + ":" + port);
        }
    }
    
    public boolean login(String email, String password) {
        if (!connected) {
            handleError("Not connected to server");
            return false;
        }
        
        try {
            User user = client.login(email, password);
            if (user != null) {
                this.currentUser = user;
                
                // Verify admin role
                if (!"ADMIN".equalsIgnoreCase(user.getAccountType())) {
                    handleError("Access denied. Admin privileges required.");
                    return false;
                }
                
                System.out.println("[AdminGUI] Admin login successful: " + user.getEmail());
                return true;
            } else {
                handleError("Login failed. Invalid credentials.");
                return false;
            }
        } catch (Exception e) {
            handleError("Login error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void showAdminPanel() {
        frame.getContentPane().removeAll();
        AdminPanel adminPanel = new AdminPanel(this, null);  // No SlotsPanel in standalone AdminGUI
        frame.add(adminPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
    
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (client != null) {
                client.close();
            }
            System.out.println("[AdminGUI] Logged out");
            System.exit(0);
        }
    }
    
    public void handleError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                frame,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        });
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}