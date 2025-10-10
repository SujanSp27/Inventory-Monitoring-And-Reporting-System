package com.inventory.service;

import com.inventory.DataAccessObject.UserDAO;
import com.inventory.DataAccessObject.impl.UserDaoImpl;
import com.inventory.model.User;
import com.inventory.util.dbConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        try {
            Connection connection = dbConnection.getConnection();
            this.userDAO = new UserDaoImpl(connection);

        } catch (SQLException e) {
            throw new RuntimeException("❌ Failed to establish database connection: " + e.getMessage());
        }
    }

    public boolean login(String username, String password) {
        try {
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                System.out.println("\n⚠️  User not found! Please register first.");
                return false;
            }

            if (user.getPassword().equals(password)) {
                System.out.println("\n🎉 Login successful! Welcome back, " + user.getUsername() + " 👋");
                return true;
            } else {
                System.out.println("\n❌ Incorrect password! Please try again.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("\n🚫 Database error during login: " + e.getMessage());
            return false;
        }
    }

    public String getRole(String username) {
        try {
            User user = userDAO.getUserByUsername(username);
            if (user != null) {
                System.out.println("🔍 Role fetched successfully: " + user.getRole());
                return user.getRole();
            } else {
                System.out.println("⚠️  User role not found, defaulting to USER.");
            }
        } catch (SQLException e) {
            System.out.println("🚫 Error fetching role: " + e.getMessage());
        }
        return "USER";
    }

    public boolean register(User user) {
        try {
            userDAO.addUser(user);
            System.out.println("\n✅ Registration successful! Welcome aboard, " + user.getUsername() + " 🚀");
            return true;
        } catch (Exception e) {
            System.out.println("\n❌ Error during registration: " + e.getMessage());
            return false;
        }
    }
}
