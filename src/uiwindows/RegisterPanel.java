package uiwindows;

import model.ClientGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//------------------------------------------
	//register window
	public class RegisterPanel extends JPanel{
	    private final ClientGUI gui;
		private JTextField firstNameField;
		private JTextField lastNameField;
		private JTextField usernameField;
		private JTextField emailField;
		private JPasswordField passwordField;//might change this to JTextField
		private JComboBox<String> roleBox;
		
		public RegisterPanel(ClientGUI gui) {
	        this.gui = gui;
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(20,20,20,20));
			
			JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
			title.setFont(new Font("Arial", Font.BOLD, 26));
			add(title, BorderLayout.NORTH);
			
			JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
			form.setBorder(new EmptyBorder(20, 220, 20, 220));
			
			firstNameField = new JTextField();
			lastNameField = new JTextField();
			usernameField = new JTextField();
			emailField = new JTextField();
			passwordField = new JPasswordField();
			roleBox = new JComboBox<>(new String[] {"Customer", "Admin"  });
			
			form.add(new JLabel("First Name:"));
			form.add(firstNameField);
			
			form.add(new JLabel("Last Name:"));
			form.add(lastNameField);
			
			form.add(new JLabel("Username:"));
			form.add(usernameField);
			
			form.add(new JLabel("Email:"));
			form.add(emailField);
			
			form.add(new JLabel("Password:"));
			form.add(passwordField);
			
			form.add(new JLabel("Role:"));
			form.add(roleBox);
			
			add(form, BorderLayout.CENTER);
			
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
			JButton createBtn = new JButton("Create Account");
			JButton backBtn = new JButton("Back");
			
			createBtn.addActionListener(e ->{
				//TODO: (server): ParkingSystem.createAccount(new Client(...)) send create acc req to the server
				//simulating success if the fields arent empty
				String firstName = firstNameField.getText().trim();
			    String lastName  = lastNameField.getText().trim();
			    String username  = usernameField.getText().trim(); // not used yet on server
			    String email     = emailField.getText().trim();
			    String password  = new String(passwordField.getPassword()).trim();
			    
				if (firstNameField.getText().isBlank() ||
	                    lastNameField.getText().isBlank() ||
	                    usernameField.getText().isBlank() ||
	                    emailField.getText().isBlank() ||
	                    new String(passwordField.getPassword()).isBlank()) {

					gui.handleError("Please fill in all boxes!");
	                return;
				}
				
				// For now: treat all signups as CUSTOMER, fixing a bug
			    String accountType = "CUSTOMER";
				boolean ok = gui.createAccount(firstName, lastName, email, password, accountType);
				
				if(ok) {
					JOptionPane.showMessageDialog(this, "Account created on server! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
			        gui.showLoginPage();
				}
				else {
					gui.handleError("Account creation failed. Try a different email.");
				}
			});
			
			backBtn.addActionListener(e -> gui.showLoginPage());
			buttons.add(createBtn);
			buttons.add(backBtn);
			add(buttons, BorderLayout.SOUTH);

		}
	}