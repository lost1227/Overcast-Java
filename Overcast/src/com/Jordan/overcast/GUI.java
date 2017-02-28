package com.Jordan.overcast;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

class exit extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
}

class login extends JPanel implements ActionListener {
	JTextField email;
	JPasswordField pass;
	JLabel error;
	Button submit;
	TitledBorder emailB, passB;
	Parser parser;
	GridLayout layout = new GridLayout(3,1);
	
	void showError(String text) {
		//need to somehow add a row to the grid layout.
		//set the switch statement to use this method
	}
	
	public login(Parser parser) {
		super(new GridLayout(3,1));
		this.parser = parser;
		setPreferredSize(new Dimension(200,120));
		email = new JTextField();
		pass = new JPasswordField();
		error = new JLabel();
		
		emailB = BorderFactory.createTitledBorder("Email");
		passB = BorderFactory.createTitledBorder("Password");
		email.setBorder(emailB);
		pass.setBorder(passB);
		error.setText("Invalid username/password");
		error.setForeground(Color.RED);
		error.setVisible(false);
		submit = new Button("Submit");
		submit.addActionListener(this);
		add(email);
		add(pass);
		add(error);
		add(submit);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source.equals(this.submit)){
			System.out.println("Submit!!");
			switch(parser.login(email.getText(), pass.getPassword())) {
			case 0:
				GUI.loggedIn = true;
				//TODO replace frame once logged in
				break;
			case 1:
				error.setVisible(true);
				break;
			case 2:
				error.setText("No internet!");
				error.setVisible(true);
				break;
			case 3:
			case 4:
				error.setText("Could not save credentials!");
				error.setVisible(true);
				break;
			case 5:
				error.setText("An unknown error has occured");
				error.setVisible(true);
				break;
			}
		}
	}
}

public class GUI extends JFrame {
	
	static boolean loggedIn = false;
	static Parser parser;
	
	public GUI() {
		setTitle("Overcast");
		setLocation(20,0);
	}

	public static void main(String[] args) {
		GUI main = new GUI();
		parser = new Parser();
		main.addWindowListener(new exit());
		JPanel base = new JPanel(new BorderLayout());
		main.add(base);
		login login = new login(parser);
		base.add(login, BorderLayout.CENTER);
		main.pack();
		main.setVisible(true);
	}

}
