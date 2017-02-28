package com.Jordan.overcast;

import java.io.IOException;

import org.jsoup.*;
import org.jsoup.Connection.*;

import java.io.FileOutputStream;
import java.io.*;
import java.util.Properties;

public class Parser {
	String authToken;
	
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
}
