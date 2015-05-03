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
	private static String USERNAME = DBCredentials.getUSERNAME();
	private static String PASSWORD = DBCredentials.getPASSWORD();
	private static String HOST_NAME = DBCredentials.getHOST_NAME();
	private static String PORT = DBCredentials.getPORT();
	private static String SID = DBCredentials.getSID();
	private static String DRIVER_CLASS = DBCredentials.getDRIVER_CLASS();
	private static String JDBC_URL = DBCredentials.getJDBC_URL();

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
