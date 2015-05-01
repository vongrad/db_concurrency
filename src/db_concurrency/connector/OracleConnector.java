/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adamv
 */
public class OracleConnector implements IConnector {
	private static String USERNAME = "DB_039";
	private static String PASSWORD = "db2015";
	private static String HOST_NAME = "datdb.cphbusiness.dk";
	private static String PORT = "1521";
	private static String SID = "dat";
	private static String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
	private static String JDBC_URL = "jdbc:oracle:thin:@" + HOST_NAME + ":" + PORT + ":" + SID;

	static {
		try {
			Class.forName(DRIVER_CLASS);
		} catch(ClassNotFoundException ex) {
			Logger.getLogger(OracleConnector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection connection = null;

		connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

		return connection;
	}
}
