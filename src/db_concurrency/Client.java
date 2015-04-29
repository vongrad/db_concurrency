/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

/**
 *
 * @author adamv
 */
public class Client implements Runnable{
    
    private int clientid;
    private String plane_nr;
    private Reservation reservation;
    
    private int result;
    
    public Client(int clientid, String plane_nr){
        this.plane_nr = plane_nr;
        this.clientid = clientid;
        this.reservation = new Reservation();
    }
   
    @Override
    public void run() {
        if(!reservation.isAllReserver(plane_nr)){
            result = reservation.reserve(plane_nr, clientid);
        }
        else{
            result = -4;  
        }
        
        Thread.sleep(Toolkit.getSleepTime(1, 10));
        
        if(result == 0 && !reservation.isAllBooked(plane_nr)){
        
        }
    }
    
    public int getResult(){
        return result;
    }
    
}
