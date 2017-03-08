package com.Jordan.overcast;

import java.awt.*;

import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.AbstractDocument.Content;

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
				GUI.shows = new mainList();
				GUI.scrollpaneviewport.add(GUI.shows);
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

class clickable implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("Clicked!");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		System.out.println("Hovered!");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		System.out.println("Unhovered!");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Clicked down!");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("Clicked up!");
		TitleCell clicked = (TitleCell)e.getComponent();
		System.out.println("Size: " + GUI.scrollpaneviewport.getComponentCount());
		GUI.scrollpaneviewport.remove(GUI.shows);
		GUI.scrollpaneviewport.add(new subList(clicked.url));
		GUI.scrollpaneviewport.validate();
		GUI.scrollpaneviewport.repaint();
	}
	
}

class subList extends JPanel {
	public subList(String URL) {
		super(new GridBagLayout());
		GridBagConstraints consts = new GridBagConstraints();
		ArrayList<JPanel> panels = GUI.parser.getEpisodes(URL);
		consts.fill = GridBagConstraints.NONE;
		consts.anchor = GridBagConstraints.WEST;
		consts.gridx = 0;
		consts.gridy = 0;
		consts.insets = new Insets(2,2,2,2);
		System.out.println("Panels size: " + panels.size());
		System.out.println("URL: " + URL);
		for(int i = 0; i < panels.size(); i++) {
			consts.gridy = i;
			add(panels.get(i),consts);
		}
		setBackground(Color.WHITE); 
	}
}

class mainList extends JPanel {
	public mainList() {
		super(new GridBagLayout());
		GridBagConstraints consts = new GridBagConstraints();
		ArrayList<TitleCell> panels = GUI.parser.getTitles();
		consts.fill = GridBagConstraints.NONE;
		consts.anchor = GridBagConstraints.WEST;
		consts.gridx = 0;
		consts.gridy = 0;
		consts.insets = new Insets(2,2,2,2);
		for(int i = 0; i < panels.size(); i++) {
			consts.gridy = i;
			panels.get(i).addMouseListener(GUI.clicky);
			add(panels.get(i),consts);
		}
		setBackground(Color.WHITE);
	}
}

public class GUI extends JFrame {
	
	static boolean loggedIn = false;
	static Parser parser;
	static GUI main;
	static JScrollPane scrollpane;
	static JViewport scrollpaneviewport;
	static DB db;
	static clickable clicky;
	static mainList shows;
	
	Container mainContent;
	
	public GUI() {
		setTitle("Overcast");
		setLocation(20,0);
		setPreferredSize(new Dimension(420, 560));
		mainContent = getContentPane();
		mainContent.setLayout(new BorderLayout());
	}

	public static void main(String[] args) {
		main = new GUI();
		parser = new Parser();
		db = new DB();
		clicky = new clickable();
		scrollpane = new JScrollPane();
		scrollpaneviewport = scrollpane.getViewport();
		main.addWindowListener(new exit());
		main.add(scrollpane);
		switch(parser.loginFromFile()) {
		case 0:
			loggedIn = true;
			shows = new mainList();
			scrollpaneviewport.add(shows);
			break;
		case 1:
			loggedIn = false;
			System.out.println("No authtoken saved");
			main.add(new login(parser, new GridLayout(3,1)), BorderLayout.CENTER);
			main.setPreferredSize(new Dimension(420,560));
		case 2:
		case 3:
			loggedIn = false;
			System.out.println("Error reading file");
			main.add(new login(parser, new GridLayout(3,1)), BorderLayout.CENTER);
		case 4:
			loggedIn = false;
			System.out.println("Unknown error");
			main.add(new login(parser, new GridLayout(3,1)), BorderLayout.CENTER);
		}
		main.pack();
		main.setVisible(true);
	}

}
