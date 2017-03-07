package com.Jordan.overcast;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.TableView.TableCell;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Parser {
	String authToken = "none";
	
	/**
	 * Will be called by GUI. Will return (an array?) JPanels to be displayed in the GUI
	 */
	
	/**
	 * Logs in to overcast and sets the local property authToken to the authentication token used by overcast to login. Either this method or the method loginFromFile must be called before any other connection to overcast can be made.
	 * @param user The username to login to overcast with
	 * @param pass The password to login with the username
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
	
	
	TitleCell getShowPanel(BufferedImage img, String show, String url) {
		TitleCell panel = new TitleCell(new GridBagLayout());
		GridBagConstraints imgConsts = new GridBagConstraints();
		GridBagConstraints showConsts = new GridBagConstraints();
		
		panel.setBackground(Color.WHITE);
		
		imgConsts.fill = GridBagConstraints.BOTH;
		imgConsts.gridx = 0;
		imgConsts.gridy = 0;
		imgConsts.gridheight = 1;
		imgConsts.insets = new Insets(0,0,0,2);
		
		showConsts.fill = GridBagConstraints.BOTH;
		showConsts.gridx = 1;
		showConsts.gridy = 0;
		showConsts.gridheight = 1;
		
		JLabel imgComp = new JLabel(new ImageIcon(img.getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		JLabel showComp = new JLabel(show);
		
		imgComp.setPreferredSize(new Dimension(80,80));
		
		showComp.setVerticalAlignment(JLabel.CENTER);
		showComp.setHorizontalAlignment(JLabel.LEFT);
		
		panel.add(imgComp, imgConsts);
		panel.add(showComp, showConsts);
		
		panel.url = url;
		
		return panel;
	}
	
	JPanel getEpisodePanel(String title, String date, String descript) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints titleConsts = new GridBagConstraints();
		GridBagConstraints dateConsts = new GridBagConstraints();
		GridBagConstraints descriptConsts = new GridBagConstraints();
		
		panel.setBackground(Color.WHITE);
		
		titleConsts.fill = GridBagConstraints.BOTH;
		titleConsts.gridx = 0;
		titleConsts.gridy = 0;
		titleConsts.gridheight = 1;
		
		dateConsts.fill = GridBagConstraints.BOTH;
		dateConsts.gridx = 0;
		dateConsts.gridy = 1;
		dateConsts.gridheight = 1;
		
		descriptConsts.fill = GridBagConstraints.BOTH;
		descriptConsts.gridx = 0;
		descriptConsts.gridy = 2;
		descriptConsts.gridheight = 1;
		
		JLabel titleComp = new JLabel(title);
		JLabel dateComp = new JLabel(date);
		JLabel descriptComp = new JLabel(descript);
		
		titleComp.setHorizontalAlignment(JLabel.LEFT);
		dateComp.setHorizontalAlignment(JLabel.LEFT);
		descriptComp.setHorizontalAlignment(JLabel.LEFT);
		
		panel.add(titleComp, titleConsts);
		panel.add(dateComp, dateConsts);
		panel.add(descriptComp, descriptConsts);
		
		return panel;
	}
	
	ArrayList<TitleCell> getTitles() {
		Element home;
		ArrayList<TitleCell> panels = new ArrayList<TitleCell>();
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
		/**
		 * For each element e, get the href and see if it's in the database. If it is, load from db instead of from url.
		 * TODO create new class to manage db connections
		 * refer https://www.tutorialspoint.com/sqlite/sqlite_java.htm
		 */
		Elements podcasts = home.getElementsByClass("feedcell");
		for( Element e : podcasts ) {
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
			String title = e.getElementsByClass("title").get(0).text();
			String showURL = e.attr("abs:href");
			panels.add(getShowPanel(img, title, showURL));
		}
		
		
		
		return panels;
	}
	ArrayList<JPanel> getEpisodes(String URL) {
		Element cast;
		ArrayList<JPanel> panels = new ArrayList<JPanel>();
		if(authToken.equals("none")) {
			System.out.println("Not logged in!");
			return null;
		}
		try {
			cast = Jsoup.connect(URL).cookie("o", authToken).get().body();
		} catch (IOException e) {
			// TODO Catch no Internet
			e.printStackTrace();
			return null;
		}
		Elements episodes = cast.getElementsByClass("extendedepisodecell");
		for(Element e : episodes) {
			Elements children = e.getElementsByClass("titlestack").get(0).children();
			System.out.println("Children: " + children.size());
			String title = children.get(0).text();
			String date = children.get(1).text();
			String description = children.get(2).text();
			/**
			System.out.println("Title: " + title);
			System.out.println("Date: " + date);
			System.out.println("Description: " + description);
			*/
			panels.add(getEpisodePanel(title, date, description));
		}
		return panels;
	}
}
