/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_concurrency;

import java.sql.Connection;

/**
 *
 * @author adamv
 */
public class Db_concurrency {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DBConnector connector = new DBConnector();
        Connection conn = connector.getConnection();
        
        
    }
    
}
