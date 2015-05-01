package db_concurrency.connector;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataSource;

/**
 *
 * @author adamv
 */
public class OraclePoolConnector implements IConnector {
	private static String USERNAME = "DB_039";
	private static String PASSWORD = "db2015";
	private static String HOST_NAME = "datdb.cphbusiness.dk";
	private static String PORT = "1521";
	private static String SID = "dat";
	private static String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
	private static String JDBC_URL = "jdbc:oracle:thin:@" + HOST_NAME + ":" + PORT + ":" + SID;

	private static ComboPooledDataSource dataSource = setupDataSource();

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	private static ComboPooledDataSource setupDataSource() {
		ComboPooledDataSource cpds = new ComboPooledDataSource();

		try {
			cpds.setDriverClass(DRIVER_CLASS);
		} catch(PropertyVetoException ex) {
			Logger.getLogger(OraclePoolConnector.class.getName()).log(Level.SEVERE, null, ex);
		}

		cpds.setJdbcUrl(JDBC_URL);
		cpds.setUser(USERNAME);
		cpds.setPassword(PASSWORD);
		
		//cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(10);
		return cpds;
	}
}
