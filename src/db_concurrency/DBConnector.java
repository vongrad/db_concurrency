/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author adamv
 */
public class DBConnector {

	public static String USERNAME = "DB_039";
	public static String PASSWORD = "db2015";
	public static String HOST = "datdb.cphbusiness.dk";
	public static String PORT = "1521";
	public static String SID = "dat";

	public Connection getConnection() {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

		} catch (ClassNotFoundException e) {
			System.out.println("JDBC Driver not found");
			e.printStackTrace();
			return null;
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:oracle:thin:@" + HOST + ":" + PORT + ":" + SID, USERNAME, PASSWORD);

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}
		return connection;
	}

}
