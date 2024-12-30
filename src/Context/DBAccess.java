/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Context;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import test1.DBConnection;

/**
 * 2180603884
 * NGUYEN CONG QUY
 */
public class DBAccess {
    
    private Connection con;
    private Statement stmt;

    public DBAccess() {
        try {
            DBConnection mycon = new DBConnection();
            con = mycon.getConnection();
            stmt = con.createStatement();
        } catch (Exception ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int Update (String str){
        try {
            int i = stmt.executeUpdate(str);
            return i;
        } catch (SQLException ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    
    public ResultSet Query(String srt){
        try {
            ResultSet rs = stmt.executeQuery(srt);
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }   
}
