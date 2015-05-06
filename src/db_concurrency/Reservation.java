/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import db_concurrency.connector.IConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adamv
 */
public class Reservation {
	private IConnector connector;
	private Connection conn;
	private static final long EXPIRES_AFTER = 50;

	protected enum ReturnTypes {
		success, not_reserved, reserved_other, reservation_timeout, occupied, other_error
	}

	public Reservation(IConnector connector) {
		try {
			this.connector = connector;
			this.conn = connector.getConnection();
		} catch(SQLException ex) {
			Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean createTables() {
		boolean retVal = false;
		//Connection conn = null;
		try {
			//conn = connector.getConnection();
			PreparedStatement ps1 = conn.prepareCall(
					"BEGIN\n"
					+ "	EXECUTE IMMEDIATE 'DROP TABLE seat';\n"
					+ "EXCEPTION\n"
					+ "	WHEN OTHERS THEN\n"
					+ "		IF SQLCODE != -942 THEN\n"
					+ "			RAISE;\n"
					+ "		END IF;\n"
					+ "END;\n");
			ps1.executeUpdate();
			ps1 = conn.prepareCall(
					"BEGIN\n"
					+ "	EXECUTE IMMEDIATE 'DROP TABLE airplane';\n"
					+ "EXCEPTION\n"
					+ "	WHEN OTHERS THEN\n"
					+ "		IF SQLCODE != -942 THEN\n"
					+ "			RAISE;\n"
					+ "		END IF;\n"
					+ "END;\n");
			ps1.executeUpdate();
			ps1 = conn.prepareCall(
					"CREATE TABLE airplane (\n"
					+ "  plane_no VARCHAR2(10),\n"
					+ "  model VARCHAR2(30),\n"
					+ "  seats NUMBER(*,0),\n"
					+ "  CONSTRAINT airplane_pk PRIMARY KEY (plane_no)\n"
					+ ")");
			ps1.executeUpdate();
			ps1 = conn.prepareCall(
					"CREATE TABLE seat (\n"
					+ "  PLANE_NO VARCHAR2(10),\n"
					+ "  SEAT_NO VARCHAR2(4),\n"
					+ "  RESERVED NUMBER,\n"
					+ "  BOOKED NUMBER,\n"
					+ "  BOOKING_TIME NUMBER,\n"
					+ "  CONSTRAINT seat_pk PRIMARY KEY (plane_no, seat_no),\n"
					+ "  CONSTRAINT seat_fk FOREIGN KEY (plane_no) REFERENCES airplane\n"
					+ ")");
			ps1.executeUpdate();
			ps1 = conn.prepareCall(
					"INSERT INTO airplane VALUES('CR9', 'SAS Airlines Canadair CRJ-900', 96)");
			ps1.executeUpdate();
			ps1 = conn.prepareCall(
					"BEGIN\n"
					+ "  FOR x IN 1..24 LOOP\n"
					+ "    INSERT INTO seat VALUES ('CR9', 'A'||x, NULL, NULL, NULL);\n"
					+ "    INSERT INTO seat VALUES ('CR9', 'C'||x, NULL, NULL, NULL);\n"
					+ "    INSERT INTO seat VALUES ('CR9', 'D'||x, NULL, NULL, NULL);\n"
					+ "    INSERT INTO seat VALUES ('CR9', 'F'||x, NULL, NULL, NULL);\n"
					+ "  END LOOP;\n"
					+ "END;");

			retVal = ps1.executeUpdate() > 0;
		} catch(SQLException ex) {
			Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			retVal = false;
		} finally {
			/*
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException ignore) {
				}
			}
					*/
		}
		return retVal;
	}

	public String reserve(String plane_no, long userid) {
		//Connection conn = null;
		try {
			//conn = connector.getConnection();

			PreparedStatement ps;
			int idx;
			ps = conn.prepareStatement(
					"UPDATE seat "
					+ "SET reserved = ?, booking_time = ? "
					+ "WHERE (reserved is null OR booking_time  < ?) "
					+ "AND booked is null AND rownum = 1");

			idx = 1;
			long bookingTime = System.currentTimeMillis();
			ps.setLong(idx++, userid);
			ps.setLong(idx++, bookingTime);
			ps.setLong(idx++, bookingTime - EXPIRES_AFTER);
			boolean foundASeat = ps.executeUpdate() > 0;

			ps = conn.prepareStatement(
					"SELECT seat_no "
					+ "FROM seat "
					+ "WHERE reserved = ? AND plane_no = ?");
			idx = 1;
			ps.setLong(idx++, userid);
			ps.setString(idx++, plane_no);
			ResultSet rs = ps.executeQuery();
			if(!rs.next()) {
				return null;
			}
			String seat_no = rs.getString("seat_no");
			return seat_no;
		} catch(SQLException ex) {
			Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		} finally {
			/*
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException ignore) {
				}
			}
			*/
		}
	}

	public Reservation.ReturnTypes book(String plane_no, String seat_no, long userid) {
		//Connection conn = null;
		try {
			long currentTime = System.currentTimeMillis();

			//conn = connector.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT * "
					+ "FROM seat "
					+ "WHERE plane_no = ?"
					+ "AND seat_no = ?");

			ps.setString(1, plane_no);
			ps.setString(2, seat_no);

			ResultSet rs = ps.executeQuery();

			if(!rs.next()) {
				return ReturnTypes.other_error;
			}

			if(rs.getString("reserved") == null) {
				return ReturnTypes.not_reserved;
			}

			if(!rs.getString("reserved").equals(String.valueOf(userid))) {
				return ReturnTypes.reserved_other;
			}

			if(rs.getLong("booking_time") < currentTime - EXPIRES_AFTER) {
				return ReturnTypes.reservation_timeout;
			}

			if(rs.getString("booked") != null) {
				return ReturnTypes.occupied;
			}
			
			try {
				Thread.sleep(Toolkit.getSleepTime(10, 70));
			} catch(InterruptedException ex) {
				Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			}

			ps = conn.prepareStatement(
					"UPDATE seat "
					+ "SET booked = ?, booking_time = ? "
					+ "WHERE plane_no = ? AND seat_no = ?");
			ps.setLong(1, userid);
			ps.setLong(2, currentTime);
			ps.setString(3, plane_no);
			ps.setString(4, seat_no);

			if(ps.executeUpdate() > 0) {
				return ReturnTypes.success;
			} else {
				return ReturnTypes.other_error;
			}
		} catch(SQLException ex) {
			Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			return ReturnTypes.other_error;
		} finally {
			/*
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException ignore) {
				}
			}
			*/
		}
	}

	public int bookAll(String plane_no, long userid) {
		//Connection conn = null;
		try {
			//conn = connector.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"UPDATE seat "
					+ "SET reserved = ?, booked = ?, booking_time = ? "
					+ "WHERE plane_no = ? AND booked is null AND (booking_time is null OR booking_time < ?)");
			long currentBookTime = System.currentTimeMillis();
			int idx = 1;
			ps.setLong(idx++, userid);
			ps.setLong(idx++, userid);
			ps.setLong(idx++, currentBookTime);
			ps.setString(idx++, plane_no);
			ps.setLong(idx++, currentBookTime - EXPIRES_AFTER);
			System.out.println("HASDFASDF");
			return ps.executeUpdate();
		} catch(SQLException ex) {
			Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			return -1;
		} finally {
			/*
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException ignore) {
				}
			}
			*/
		}
	}

	public int clearAllBookings(String plane_no) {
		System.out.println("CLEAR ALL BOOKINGS");
		//Connection conn = null;
		try {
			//conn = connector.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"UPDATE seat "
					+ "SET booked = null, reserved = null, booking_time = null "
					+ "WHERE plane_no = ?");
			int idx = 0;
			ps.setString(++idx, plane_no);
			return ps.executeUpdate();
		} catch(SQLException ex) {
			Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			return -1;
		} finally {
			/*
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException ignore) {
				}
			}
			*/
		}
	}

	public boolean isAllBooked(String plane_no) {
		//Connection conn = null;
		try {
			//conn = connector.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT count(*) as count "
					+ "FROM seat "
					+ "WHERE booked is null AND plane_no = ?");
			int idx = 1;
			ps.setString(idx++, plane_no);
			ResultSet results = ps.executeQuery();
			if(results.next()) {
				return results.getInt("count") == 0;
			}
			throw new RuntimeException("Something is rotten in the state of Denmark");
		} catch(SQLException ex) {
			Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} finally {
			/*
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException ignore) {
				}
			}
					*/
		}
	}

	public boolean isAllReserved(String plane_no) {
		//Connection conn = null;
		try {
			//conn = connector.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"SELECT count(*) as count "
					+ "FROM seat "
					+ "WHERE reserved is null AND plane_no = ?");
			int idx = 1;
			ps.setString(idx++, plane_no);
			ResultSet results = ps.executeQuery();
			if(results.next()) {
				return results.getInt("count") == 0;
			}
			throw new RuntimeException("Something is rotten in the state of Denmark");
		} catch(SQLException ex) {
			Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} finally {
			/*
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException ignore) {
				}
			}
			*/
		}
	}
	
	public void closeConnection(){
		if(conn != null){
			try {
				conn.close();
			} catch(SQLException ex) {
				Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
