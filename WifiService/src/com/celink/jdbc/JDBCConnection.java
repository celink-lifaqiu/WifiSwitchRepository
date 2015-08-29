package com.celink.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.celink.util.PropertiesUtils;

public class JDBCConnection {
	public static String DRIVER = PropertiesUtils.getInstance("config").getValue("driver");
	public static String URL = PropertiesUtils.getInstance("config").getValue("url");
	public static String USER = PropertiesUtils.getInstance("config").getValue("user");
	public static String PWD = PropertiesUtils.getInstance("config").getValue("pwd");
	
	public static Connection getConnection(){
		Connection conn = null;
		try {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL, USER, PWD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

}
