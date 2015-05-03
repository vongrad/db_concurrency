/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import db_concurrency.connector.DBCredentials;
import db_concurrency.connector.IConnector;
import db_concurrency.connector.OraclePoolConnector;

/**
 *
 * @author Preuss
 */
public class MainStarter {
	public static void main(String[] args) {
		IConnector conn = new OraclePoolConnector();
		Reservation r = new Reservation(conn);
		/*
		 boolean output = r.createTables();
		 System.out.println("Created Tables: " + output);
		 */

		r.clearAllBookings("CR9");
		Master m = new Master(10, conn);
		new Thread(m).start();
	}
}
