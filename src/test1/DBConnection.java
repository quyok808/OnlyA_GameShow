/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author account
 */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/doanmmt";
    private static final String USER = "root"; // Thay bằng tài khoản MySQL của bạn
    private static final String PASSWORD = ""; // Thay bằng mật khẩu MySQL của bạn

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
