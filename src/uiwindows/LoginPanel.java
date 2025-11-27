package uiwindows;

import model.ClientGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

//---------------------------------------
	//login window
	public class LoginPanel extends JPanel{
		private final ClientGUI gui;
		private JTextField emailField;
		private JPasswordField passwordField;
		private JLabel titleLabel;
		
		public LoginPanel(ClientGUI gui) {
			this.gui = gui;
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(20, 20, 20, 20));
			
			JLabel title = new JLabel("ParkZone Login", SwingConstants.CENTER);
			title.setFont(new Font("Arial", Font.BOLD, 26));
			add(title, BorderLayout.NORTH);
			
			JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
			form.setBorder(new EmptyBorder(40, 220, 40, 220));
			
			emailField = new JTextField();
			passwordField = new JPasswordField();
			
			form.add(new JLabel("Email:")); //probably going to change this to username instead
			form.add(emailField);
			form.add(new JLabel("Password:"));
			form.add(passwordField);
			
			add(form, BorderLayout.CENTER);
			
			JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
			
			JButton loginBtn = new JButton("Login");
			JButton registerBtn= new JButton("Create Account");
			JButton backBtn = new JButton("Back");
			
			loginBtn.addActionListener(e ->{
				String email = emailField.getText();
				String password = new String(passwordField.getPassword());
				
				if(gui.login(email, password)) {
					//show slots list after success
					gui.showSlots(0, "ALL");//default for now
				}
				else {
					gui.handleError("Login failed. Check your credentials.");
				}
			});
			
			registerBtn.addActionListener(e -> gui.showRegisterPage());
			
			buttons.add(loginBtn);
			buttons.add(registerBtn);
			buttons.add(backBtn);
			add(buttons, BorderLayout.SOUTH);
			
			
		}
		
		//not necessary might remove
		public void setRole(String role) {
			if("ADMIN".equals(role)) {
				titleLabel.setText("Admin Login");
			}
			else if("CUSTOMER".equals(role)) {
				titleLabel.setText("Customer Login");
			}
			else {
				titleLabel.setText("Login");

			}
		}
	}
