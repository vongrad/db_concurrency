/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

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

	public Client(int clientid, String plane_nr) {
		this.plane_nr = plane_nr;
		this.clientid = clientid;
		this.reservation = new Reservation();
	}

	@Override
	public void run() {
		sleepThisThreadRandom(1, 10);
		reservedSeatNr = reservation.reserve(plane_nr, clientid);
		if(reservedSeatNr == null) {
			return;
                }
                
		sleepThisThreadRandom(5, 100);
                
                if(!makeBooking()){
                    return;
                }
                
		bookingCode = reservation.book(plane_nr, reservedSeatNr, clientid);
		
                
/*
		if (!reservation.isAllReserved(plane_nr)) {
			reservedSeatNr = reservation.reserve(plane_nr, clientid);
		} else {
			reservedSeatNr = null;
			return;
		}*/

/*		if (!reservation.isAllBooked(plane_nr)) {

		}
		*/
	}

	private void sleepThisThreadRandom(int fromMilli, int toMilli) {
		try {
			Thread.sleep(Toolkit.getSleepTime(fromMilli, toMilli));
		} catch (InterruptedException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public String getReservedSeatNr() {
		return reservedSeatNr;
	}

	public Reservation.ReturnTypes getBookingCode() {
		return bookingCode;
	}
        
        
        public boolean makeBooking(){
            int decision = Toolkit.getSleepTime(0, 1);
            return decision == 1;
        }
}
