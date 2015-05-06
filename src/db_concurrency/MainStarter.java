package db_concurrency;

import db_concurrency.connector.DBCredentials;
import db_concurrency.connector.IConnector;
import db_concurrency.connector.OraclePoolConnector;
import java.sql.SQLException;

public class MainStarter {
	public static void main(String[] args) throws SQLException, InterruptedException, Exception {
		IConnector conn = new OraclePoolConnector();
		Reservation r = new Reservation(conn.getConnection());
		/*
		 boolean output = r.createTables();
		 System.out.println("Created Tables: " + output);
		 */

		r.clearAllBookings("CR9");
		r.close();
		
		Master m = new Master(10, conn);
		Thread masterThread = new Thread(m);
		masterThread.start();
		masterThread.join(); // Wait for masterThread
		m.printTheStats();
	}
}
