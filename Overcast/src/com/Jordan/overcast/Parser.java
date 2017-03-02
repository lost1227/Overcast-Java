package com.Jordan.overcast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.*;
import org.jsoup.Connection.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.FileOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Parser {
	String authToken = "none";
	
	/**
	 * Will be called by GUI. Will return (an array?) JPanels to be displayed in the GUI
	 */
	
	/**
	 * Logs in to overcast and sets the local property authToken to the authentication token used by overcast to login. Either this method or the method loginFromFile must be called before any other connection to overcast can be made.
	 * @param user The username to login to overcast with
	 * @param pass The password to login with the username
	 * @return True if successfully set authToken, False if IOException occurs.
	 */
	int loginWeb(String user, char[] pass) {
		try {
			System.out.println("User: " + user + " Pass: " + new String(pass));
			Connection.Response conn = Jsoup.connect("https://overcast.fm/login").data("email", user, "password", new String(pass)).method(Method.POST).execute();
			authToken = conn.cookie("o");
			if(authToken == null) {
				System.out.println("Invalid username/password");
				return 1;
			}
		} catch (IOException e) {
			System.out.println("Could not connect!");
			e.printStackTrace();
			return 2;
		}
		Properties prop = new Properties();
		OutputStream output = null;
		try {

			output = new FileOutputStream("storedVars");
			
			prop.setProperty("user", user);
			prop.setProperty("token",authToken);
			
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
			return 3;
		} finally {
			if (output != null) {
				try {
					output.close();
					return 0;
				} catch (IOException e) {
					e.printStackTrace();
					return 4;
				}
			}

		}
		return 5;
	}
	
	int loginFromFile() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("storedVars");
			prop.load(input);
			authToken = prop.getProperty("token");
		} catch (IOException ex) {
			ex.printStackTrace();
			return 2;
		} finally {
			if (input != null) {
				try {
					if ( authToken == null ) {
						input.close();
						return 1;
					}
					input.close();
					return 0;
				} catch (IOException e) {
					e.printStackTrace();
					return 3;
				}
			}
		}
		return 4;
	}
	
	JPanel getPanel(BufferedImage img, String show, String title, String date) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints imgConsts = new GridBagConstraints();
		GridBagConstraints showConsts = new GridBagConstraints();
		GridBagConstraints titleConsts = new GridBagConstraints();
		GridBagConstraints dateConsts = new GridBagConstraints();
		
		imgConsts.fill = GridBagConstraints.BOTH;
		imgConsts.gridx = 0;
		imgConsts.gridy = 0;
		imgConsts.gridheight = 4;
		
		showConsts.fill = GridBagConstraints.HORIZONTAL;
		showConsts.gridx = 1;
		showConsts.gridy = 0;
		
		titleConsts.fill = GridBagConstraints.HORIZONTAL;
		titleConsts.gridx = 1;
		titleConsts.gridy = 1;
		titleConsts.gridheight = 2;
		
		dateConsts.fill = GridBagConstraints.HORIZONTAL;
		dateConsts.gridx = 1;
		dateConsts.gridy = 3;
		
		JLabel imgComp = new JLabel(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		JLabel showComp = new JLabel(show);
		JLabel titleComp = new JLabel(title);
		JLabel dateComp = new JLabel(date);
		
		imgComp.setPreferredSize(new Dimension(80,80));
		
		titleComp.setFont(new Font("Helvetica", Font.PLAIN, 18));
		
		panel.add(imgComp, imgConsts);
		panel.add(showComp, showConsts);
		panel.add(titleComp, titleConsts);
		panel.add(dateComp, dateConsts);
		
		return panel;
		
		
	}
	
	ArrayList<JPanel> getMain() {
		Element home;
		ArrayList<JPanel> panels = new ArrayList<JPanel>();
		if(authToken.equals("none")) {
			System.out.println("Not logged in!");
			return null;
		}
		try {
			home = Jsoup.connect("https://overcast.fm/podcasts").cookie("o", authToken).get().body();
		} catch (IOException e) {
			// TODO Catch no Internet
			e.printStackTrace();
			return null;
		}
		Elements episodes = home.getElementsByClass("episodecell");
		for(Element e : episodes) {
			BufferedImage img = null;
			try {
				URL url = new URL(e.getElementsByClass("art").attr("src"));
				img = ImageIO.read(url);
			} catch (MalformedURLException e1) {
				System.out.println("Got bad url for image");
				e1.printStackTrace();
			} catch (IOException e1) {
				System.out.println("Could not read url as image");
				e1.printStackTrace();
			}
			Elements children = e.getElementsByClass("titlestack").get(0).children();
			System.out.println("Children: " + children.size());
			String show = children.get(0).text();
			String title = children.get(1).text();
			String date = children.get(2).text();
			panels.add(getPanel(img, show, title, date));
		}
		return panels;
	}
}
