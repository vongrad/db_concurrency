package db_concurrency;

import db_concurrency.connector.IConnector;
import db_concurrency.connector.OraclePoolConnector;
import java.sql.Connection;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adamv
 */
public class Client implements Runnable {
	private int clientId;

	private String plane_nr;
	private Reservation reservation;

	private String reservedSeatNr;
	private StatisticResult bookingCode;
	private IStatistics stats;

	public Client(Connection connection, int clientid, String plane_nr, IStatistics stats) {
		this.plane_nr = plane_nr;
		this.clientId = clientid;
		this.reservation = new Reservation(connection);
		this.stats = stats;
	}

	@Override
	public void run() {
		try {
			Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientId + " START");
			//sleepThisThreadRandom(Reservation.EXPIRES_AFTER / 10, Reservation.EXPIRES_AFTER);
			reservedSeatNr = reservation.reserve(plane_nr, clientId);
			if(reservedSeatNr == null) {
				Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientId + " NO RESERVATION");
				System.out.println("Client [" + clientId + "]: Could not get RESERVATION");
				stats.putStat(clientId, StatisticResult.COULD_NOT_RESERVE);
				return;
			}

			sleepThisThreadRandom((Reservation.EXPIRES_AFTER / 2), (Reservation.EXPIRES_AFTER * 2));
			Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientId + " AFTER SLEEP");

			if(!makeBooking(25)) {
				Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientId + " DON'T NEED BOOKING");
				System.out.println("Client [" + clientId + "]: Decided to NO BOOKING");
				stats.putStat(clientId, StatisticResult.RESERVED_DECIDED_NOT_TO_BOOK);
				return;
			}

			Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientId + " BEFORE BOOKING");
			bookingCode = reservation.book(plane_nr, reservedSeatNr, clientId);
			StatisticResult result = null;
			stats.putStat(clientId, bookingCode);
			Logger.getLogger(Client.class.getName()).log(Level.INFO, "Client: " + this.clientId + " FINISHED BOOKING");
			System.out.println("Client [" + clientId + "]: " + bookingCode);
		} finally {
			try {
				reservation.close();
			} catch(Exception ex) {
				Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private void sleepThisThreadRandom(long fromMilli, long toMilli) {
		try {
			long sleepTime = Toolkit.getSleepTime(fromMilli, toMilli);
			System.out.println("Sleep time: " + sleepTime);
			Thread.sleep(sleepTime);
		} catch(InterruptedException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public String getReservedSeatNr() {
		return reservedSeatNr;
	}

	public StatisticResult getBookingCode() {
		return bookingCode;
	}

	public boolean makeBooking(int wantsToBookPercentPoint) {
		if(wantsToBookPercentPoint < 0 || wantsToBookPercentPoint > 100) {
			throw new IllegalArgumentException();
		}
		int percent = new Random().nextInt(100);
		return percent <= wantsToBookPercentPoint;
	}

	public int getClientId() {
		return clientId;
	}
}
