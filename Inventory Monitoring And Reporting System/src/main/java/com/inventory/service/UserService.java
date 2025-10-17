package com.inventory.service;

import com.inventory.DataAccessObject.UserDAO;
import com.inventory.DataAccessObject.impl.UserDaoImpl;
import com.inventory.model.User;
import com.inventory.util.dbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    // 🔹 Register new user
    public boolean register(User user) {
        try {
            // Check if username or email already exists
            if (userDAO.getUserByUsername(user.getUsername()) != null) {
                System.out.println("\n⚠️ Username already exists! Try a different one.");
                return false;
            }

            if (userDAO.existsByEmail(user.getEmail())) {
                System.out.println("\n⚠️ Email already registered! Try logging in or use another email.");
                return false;
            }

            // Add user to DB
            userDAO.addUser(user);

            System.out.println("\n\uD83D\uDCE7 Please verify your email to activate your account before logging in.");
            return true;

        } catch (SQLException e) {
            System.out.println("\n❌ Database error during registration: " + e.getMessage());
            return false;
        }
    }

    // 🔹 User login
    public boolean login(String username, String password) {
        try {
            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                System.out.println("\n⚠️ User not found! Please register first.");
                return false;
            }

            if (!user.isVerified()) {
                System.out.println("\n⚠️ Email not verified! Please verify your email before logging in.");
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

    // 🔹 Fetch role by username
    public String getRole(String username) {
        try {
            String role = userDAO.getRoleByUsername(username);
            System.out.println("🔍 Role fetched successfully: " + role);
            return role;
        } catch (SQLException e) {
            System.out.println("🚫 Error fetching role: " + e.getMessage());
            return "USER"; // Default
        }
    }

    // 🔹 Get user by email
    public User getUserByEmail(String email) {
        try {
            return userDAO.getUserByEmail(email);
        } catch (SQLException e) {
            System.out.println("🚫 Error fetching user by email: " + e.getMessage());
            return null;
        }
    }

    // 🔹 Check if user exists by email
    public boolean existsByEmail(String email) {
        try {
            return userDAO.existsByEmail(email);
        } catch (SQLException e) {
            System.out.println("🚫 Error checking email existence: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Verify user by email
    public boolean verifyUser(String email) {
        try {
            userDAO.verifyUser(email);
            return true;
        } catch (SQLException e) {
            System.out.println("🚫 Error verifying user: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Get user by email or username
    public User getUserByEmailOrUsername(String identifier) {
        try {
            User user = userDAO.getUserByUsername(identifier);
            if (user == null) {
                user = userDAO.getUserByEmail(identifier);
            }
            return user;
        } catch (SQLException e) {
            System.out.println("🚫 Error fetching user: " + e.getMessage());
            return null;
        }
    }

    // 🔹 Get first admin user (used for sending reports)
    public User getAdminUser() {
        try (Connection conn = dbConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE role = 'ADMIN' LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("email"),
                        rs.getBoolean("is_verified")
                );
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching admin user: " + e.getMessage());
        }
        return null;
    }

    public String getAdminEmail() {
        try {
            String sql = "SELECT email FROM users WHERE role = 'ADMIN' AND is_verified = TRUE LIMIT 1";
            var conn = com.inventory.util.dbConnection.getConnection();
            try (var ps = conn.prepareStatement(sql)) {
                var rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching admin email: " + e.getMessage());
        }
        return null;
    }


    public String getEmailByUsername(String username) {
        String email = null;
        String sql = "SELECT email FROM users WHERE username = ? AND is_verified = 1";
        try (Connection conn = com.inventory.util.dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching email: " + e.getMessage());
        }
        return email;
    }


}
