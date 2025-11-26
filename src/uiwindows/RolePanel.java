package uiwindows;

import model.ClientGUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


//------------------------------------------------------
//account window (choose to be an operator, Admin or customer (might remove operator altogether)




//need to add an exit button!!!!!!!






public class RolePanel extends JPanel{
	private final ClientGUI gui;
	
	public RolePanel(ClientGUI gui) {
		this.gui =gui;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(40, 40,40,40));
		
		//menu
		JLabel title = new JLabel("Welcome to ParkZone", SwingConstants.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 28));
		add(title, BorderLayout.NORTH);
		
		JLabel subtitle = new JLabel("Please choose how you want to log in:", SwingConstants.CENTER);
		subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
		add(subtitle, BorderLayout.CENTER);
		
		JPanel buttons =new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
		JButton adminBtn = new JButton("Login as Admin");
		JButton customerBtn = new JButton("Login as Customer");
		JButton createBtn = new JButton("Create Account");

		adminBtn.setPreferredSize(new Dimension(200, 40));
		customerBtn.setPreferredSize(new Dimension(200, 40));
		createBtn.setPreferredSize(new Dimension(200, 40));

		//button click
		adminBtn.addActionListener(e -> gui.startAdminLogin());
		//button click
		customerBtn.addActionListener(e -> gui.startCustomerLogin());
		//button click
		createBtn.addActionListener(e -> gui.showRegisterPage());
				
		buttons.add(adminBtn);
		buttons.add(customerBtn);
		buttons.add(createBtn);

		add(buttons, BorderLayout.SOUTH);
				
	}
}
