package com.inventory.util;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/inventoryDB";
    private static final String USER = "root";
    private static final String PASSWORD = "Sujanpoojary27";

    // Private constructor to prevent instantiation
    private dbConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found.", e);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to the database.", e);
        }
    }
}
