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
	GridLayout layout;
	
	void showError(String text) {
		layout.setRows(4);
		addImpl(error,null,2);
		addImpl(submit,null,3);
		error.setText(text);
		setPreferredSize(new Dimension(200,160));
		GUI.main.pack();
		error.setVisible(true);
		//need to somehow add a row to the grid layout.
		//set the switch statement to use this method
	}
	
	public login(Parser parser, GridLayout layout) {
		super(layout);
		this.layout = layout;
		this.parser = parser;
		setPreferredSize(new Dimension(200,120));
		email = new JTextField();
		pass = new JPasswordField();
		error = new JLabel();
		emailB = BorderFactory.createTitledBorder("Email");
		passB = BorderFactory.createTitledBorder("Password");
		email.setBorder(emailB);
		pass.setBorder(passB);
		error.setForeground(Color.RED);
		error.setVisible(false);
		submit = new Button("Submit");
		submit.addActionListener(this);
		add(email);
		add(pass);
		add(submit);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source.equals(this.submit)){
			System.out.println("Submit!!");
			switch(parser.loginWeb(email.getText(), pass.getPassword())) {
			case 0:
				GUI.loggedIn = true;
				//TODO replace frame once logged in
				break;
			case 1:
				showError("Invalid username/password");
				break;
			case 2:
				showError("No internet!");
				break;
			case 3:
			case 4:
				showError("Could not save credentials!");
				break;
			case 5:
				showError("An unknown error has occured");
				break;
			}
		}
	}
}

class mainList extends JPanel {
	public mainList(GridLayout layout) {
		super(layout);
		// TODO Parser will return an array of JFrame with gridbag layouts, each representing a podcast.
	}
}

public class GUI extends JFrame {
	
	static boolean loggedIn = false;
	static Parser parser;
	static GUI main;
	
	public GUI() {
		setTitle("Overcast");
		setLocation(20,0);
	}

	public static void main(String[] args) {
		main = new GUI();
		parser = new Parser();
		main.addWindowListener(new exit());
		JPanel base = new JPanel(new BorderLayout());
		main.add(base);
		switch(parser.loginFromFile()) {
		case 0:
			loggedIn = true;
			//TODO replace frame once logged in
			break;
		case 1:
			loggedIn = false;
			System.out.println("No authtoken saved");
			base.add(new login(parser, new GridLayout(3,1)), BorderLayout.CENTER);
		case 2:
		case 3:
			loggedIn = false;
			System.out.println("Error reading file");
			base.add(new login(parser, new GridLayout(3,1)), BorderLayout.CENTER);
		case 4:
			loggedIn = false;
			System.out.println("Unknown error");
			base.add(new login(parser, new GridLayout(3,1)), BorderLayout.CENTER);
		}
		main.pack();
		main.setVisible(true);
	}

}
