package com.Jordan.overcast;

import java.sql.*;

public class DB {
	Connection c = null;
	String createTable = "CREATE TABLE Podcasts ";
	
	public DB() {
		try {
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
		} catch (Exception e) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		//System.out.println("Opened connection succesfully");
	}
}
