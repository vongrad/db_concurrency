/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import db_concurrency.connector.OraclePoolConnector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adamv
 */
public class Client implements Runnable {
	private int clientid;
	private String plane_nr;
	private Reservation reservation;

	private String reservedSeatNr;
	private Reservation.ReturnTypes bookingCode;

	public Client(Reservation reservation, int clientid, String plane_nr) {
		this.plane_nr = plane_nr;
		this.clientid = clientid;
		this.reservation = reservation;
	}

	@Override
	public void run() {
		Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientid + " START");
		sleepThisThreadRandom(1, 1000);
		reservedSeatNr = reservation.reserve(plane_nr, clientid);
		if(reservedSeatNr == null) {
			Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientid + " NO RESERVATION");
			System.out.println("Client [" + clientid + "]: Could not get RESERVATION");
			return;
		}

		sleepThisThreadRandom(5, 1000);
		Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientid + " AFTER SLEEP");

		if(!makeBooking()) {
			Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientid + " DON'T NEED BOOKING");
			System.out.println("Client [" + clientid + "]: Decided to NO BOOKING");
			return;
		}

		Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientid + " BEFORE BOOKING");
		bookingCode = reservation.book(plane_nr, reservedSeatNr, clientid);
		Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientid + " FINISHED BOOKING");
		System.out.println("Client [" + clientid + "]: " + bookingCode);
	}

	private void sleepThisThreadRandom(int fromMilli, int toMilli) {
		try {
			int x = Toolkit.getSleepTime(fromMilli, toMilli);
			System.out.println("Sleep time: " + x);
			Thread.sleep(x);
		} catch(InterruptedException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public String getReservedSeatNr() {
		return reservedSeatNr;
	}

	public Reservation.ReturnTypes getBookingCode() {
		return bookingCode;
	}

	public boolean makeBooking() {
		int decision = Toolkit.getSleepTime(0, 1);
		return decision == 1;
	}
}
