/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author sheila
 */
public class Database {

    private static Connection connection;

    // JDBC Driver class name and Database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // If you are using an older MySQL Connector/J 5.x, the driver class is:
    // private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private static final String DB_URL = "jdbc:mysql://localhost/sia_sma";

    private static final String USER = "root";
    private static final String PASS = "";

    // Private constructor to prevent instantiation (Singleton Pattern)
    private Database() {
    }

    static {
        try {
            // Explicitly load and register the JDBC driver.
            // For JDK 8 and NetBeans 8, if you have the MySQL Connector/J JAR
            // in your project's libraries, JDBC 4.0+ drivers should auto-register.
            // However, Class.forName() is a common and explicit way to ensure it's loaded.
            Class.forName(JDBC_DRIVER);
            System.out.println("MySQL JDBC Driver registered successfully via Class.forName().");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            System.err.println("Ensure the MySQL Connector/J JAR file is added to your project's Libraries in NetBeans.");
            // For a GUI application, showing an error dialog is user-friendly for critical errors.
            JOptionPane.showMessageDialog(null,
                    "Critical Error: MySQL JDBC Driver not found.\nPlease add the Connector/J JAR to project libraries.",
                    "Driver Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                System.out.println("Attempting to establish database connection to: " + DB_URL);
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Database connection successful!");
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Koneksi ke database gagal: " + e.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
                throw e;
            }
        }

        return connection;
    }

    public static synchronized void closeConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Database connection was already closed or not initialized.");
            } else {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Same main method for testing
        Connection testConnection;
        try {
            System.out.println("Attempting to get database connection for testing...");
            testConnection = Database.getConnection();
            if (testConnection != null) {
                System.out.println("Test connection successful. Connection object: " + testConnection);
                System.out.println("Connection is closed? " + testConnection.isClosed());
            }
        } catch (SQLException e) {
            System.err.println("Test connection failed with SQLException: " + e.getMessage());
        } finally {
            System.out.println("Closing test connection...");
            Database.closeConnection();
            System.out.println("Test finished.");
        }
    }

}
