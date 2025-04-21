/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author sheila
 */
public class KoneksiDB {
    private static Connection mysqlconfig;
    
    public static Connection koneksi(){
        if (mysqlconfig == null) {
            try {
                String url = "jdbc:mysql://localhost/db_sia_sma";
                String user = "root"; 
                String pass = "root";

                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
  
                mysqlconfig = DriverManager.getConnection(url, user, pass);
                System.out.println("Koneksi database berhasil!");

            } catch (SQLException e) {
                System.err.println("Koneksi gagal: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Koneksi ke database gagal: " + e.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            }
        }
        return mysqlconfig;
    }
    public static void tutupKoneksi() {
        try {
            if (mysqlconfig != null && !mysqlconfig.isClosed()) {
                mysqlconfig.close();
                mysqlconfig = null;
                System.out.println("Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        Connection conn = KoneksiDB.koneksi();
        if (conn != null) {
            System.out.println("Tes koneksi berhasil.");
            KoneksiDB.tutupKoneksi();
        } else {
            System.out.println("Tes koneksi gagal.");
        }
    }
}
    
