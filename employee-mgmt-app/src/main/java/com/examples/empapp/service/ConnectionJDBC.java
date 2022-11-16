package com.examples.empapp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBC {
	public static Connection conn = null;
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String LOCAL_DB_URL = "jdbc:mysql://localhost:3307/mydata";
	static final String USER = "root";
	static final String PASS = "root1234";
	
	public static Connection createConnection() {
		if(conn == null) {
			try {
				   Class.forName(JDBC_DRIVER);
				    System.out.println("connection created SuccessFully");
				}
					catch(ClassNotFoundException e) {
						System.out.println("error Loading driver-" + e.getMessage());
				}
			try {
				conn = DriverManager.getConnection(LOCAL_DB_URL,USER,PASS );
				return conn;
			}
			catch(SQLException e) {
				System.out.println("error while creating conn" + e.getMessage());
				e.printStackTrace();
			}
		}
		
		return conn;
	}
}
