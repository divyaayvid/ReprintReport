package com.nyco.reprintreport.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	public DBConnection() {
		super();
	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		
		String devUrl="";
		Connection con = null;
//		DriverManager.getConnection(prodUrl, "XXXX", "xxxx");
		return con;
	}
	
}
