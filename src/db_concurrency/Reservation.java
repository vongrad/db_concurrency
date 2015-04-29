/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.SysexMessage;

/**
 *
 * @author adamv
 */
public class Reservation {

    private Connection conn;
    private static final long EXPIRES_AFTER = 50;
    
    private enum ReturnTypes{
        success, not_reserved, reserved_other, reservation_timeout, occupied, other_error
    }

    public Reservation() {
        try {
            //conn = new DBConnector().getConnection();
            conn = ConnectionPool.getOracleConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String reserve(String plane_no, long userid) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT seat_no FROM plane WHERE (reserved = ?  OR booking_time  < ?)"
                    + "AND plane_no = ? AND booked = ? AND rownum = 1 ");
            
            ps.setString(1, null);
            ps.setLong(2, System.currentTimeMillis() - EXPIRES_AFTER);
            ps.setString(3, plane_no);
            ps.setString(4, null);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String seat_nr = rs.getString("seat_no");

            ps = conn.prepareStatement("UPDATE plane SET reserved = ? WHERE seat_nr = ?");
            ps.setLong(1, userid);
            ps.setString(2, seat_nr);
            
            ps.close();
            return ps.executeUpdate() == 1 ? seat_nr : null;
        } catch (SQLException ex) {
            Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ReturnTypes book(String plane_no, String seat_no, long userid) {
        try {
            
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM plane WHERE plane_nr = ?"
                    + "AND seat_nr = ?");
            
            ps.setString(1, plane_no);
            ps.setString(2, seat_no);
            
            ResultSet rs = ps.executeQuery();
            
            if (!rs.next()) {
                return ReturnTypes.other_error;
            }
            
            if(rs.getString("reserved") == null){
                return ReturnTypes.not_reserved;
            }
            
            if(!rs.getString("reserved").equals(String.valueOf(userid))){
                return ReturnTypes.reserved_other;
            }
            
            if(rs.getLong("booking_time") > System.currentTimeMillis() - EXPIRES_AFTER){
                return ReturnTypes.reservation_timeout;
            }
            
            if(rs.getString("booked") != null){
                return ReturnTypes.occupied;
            }
            
            ps = conn.prepareStatement("UPDATE plane SET booked = ?, booking_time = ? WHERE plane_no = ? AND seat_no = ?");
            ps.setLong(1, userid);
            ps.setLong(2, System.currentTimeMillis());
            ps.setString(3, plane_no);
            ps.setString(4, seat_no);
            
            ps.close();
            
            return ReturnTypes.success;
            
        } catch (SQLException ex) {
            Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
            return ReturnTypes.other_error;
        }
    }

    public int bookAll(String plane_no, long userid) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE plane SET booked = ? WHERE plane_no = ? AND booked = ?");
            ps.setLong(1, userid);
            ps.setString(2, plane_no);
            ps.setString(3, null);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public int clearAllBookings(String plane_no) {
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE plane SET booked = ?, reserved = ?, booking_time = ? WHERE plane_no = ?");
            ps.setString(1, null);
            ps.setString(2, null);
            ps.setLong(3, 0);
            ps.setLong(4, 0);
            return ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public boolean isAllBooked(String plane_no) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT count(*) as count FROM plane WHERE booked = ? AND plane_no = ?");
            ps.setString(1, "");
            ps.setString(2, plane_no);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                return results.getInt("count") > 0;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean isAllReserver(String plane_no) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT count(*) as count FROM plane WHERE reserved = ? AND plane_no = ?");
            ps.setString(1, "");
            ps.setString(2, plane_no);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                return results.getInt("count") > 0;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(Reservation.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
