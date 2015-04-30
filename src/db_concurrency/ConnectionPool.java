/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javax.activation.DataSource;

/**
 *
 * @author adamv
 */
public class ConnectionPool {

	private static String USERNAME = "DB_039";
	private static String PASSWORD = "db2015";
	private static String HOST = "datdb.cphbusiness.dk";
	private static String PORT = "1521";
	private static String SID = "dat";
	private static String DRIVER_NAME = "oracle";

	private static ComboPooledDataSource dataSource = setupDataSource();

	public static Connection getOracleConnection() throws SQLException {
		return dataSource.getConnection();
	}

	private static ComboPooledDataSource setupDataSource() {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(DRIVER_NAME);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		cpds.setJdbcUrl(HOST);
		cpds.setUser(USERNAME);
		cpds.setPassword(PASSWORD);
		cpds.setMinPoolSize(10);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(10);
		return cpds;
	}
}
