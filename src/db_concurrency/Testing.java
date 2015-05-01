/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import db_concurrency.connector.IConnector;
import db_concurrency.connector.OracleConnector;
import db_concurrency.connector.OraclePoolConnector;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Preuss
 */
public class Testing {
	private static String USERNAME = "DB_039";
	private static String PASSWORD = "db2015";
	private static String HOST = "datdb.cphbusiness.dk";
	private static String PORT = "1521";
	private static String SID = "dat";
	private static String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

	public static void main(String[] args) throws PropertyVetoException, SQLException, ClassNotFoundException {
	}
	
	private void tryIt() throws SQLException {
		String jdbcUrl = "jdbc:oracle:thin:@" + HOST + ":" + PORT + ":" + SID;

		DataSource ds_unpooled = DataSources.unpooledDataSource(jdbcUrl, USERNAME, PASSWORD);

		Map overrides = new HashMap();
		overrides.put("maxStatements", "200");         //Stringified property values work
		overrides.put("maxPoolSize", new Integer(10)); //"boxed primitives" also work

		// create the PooledDataSource using the default configuration and our overrides
		DataSource ds_pooled = DataSources.pooledDataSource(ds_unpooled, overrides);
		Connection conn = ds_pooled.getConnection();
	}
}
