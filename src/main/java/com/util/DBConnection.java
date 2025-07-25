package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // --- IMPORTANT: UPDATE THESE VALUES FOR YOUR DATABASE ---
    // Replace "AddImage" if your database name is different.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/AddImage?useSSL=false";
    // Replace "root" with your MySQL username.
    private static final String USER = "root";
    // Replace "password" with your MySQL password.
    private static final String PASS = "sql@7"; 
    
    // The JDBC driver for MySQL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Establishes a connection to the database.
     * @return a Connection object to the database.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Register the JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Successfully connected to the database.");

        } catch (ClassNotFoundException e) {
            System.err.println("Database connection error: MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection error: Could not connect to the database.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) {
        // You can run this main method to quickly test your database connection
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("Test connection successful!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Test connection failed.");
        }
    }
}
