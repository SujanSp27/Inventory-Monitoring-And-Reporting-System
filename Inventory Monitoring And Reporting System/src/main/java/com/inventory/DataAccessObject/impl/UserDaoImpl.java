package com.inventory.DataAccessObject.impl;

import com.inventory.DataAccessObject.UserDAO;
import com.inventory.model.User;

import java.sql.*;

public class UserDaoImpl implements UserDAO {

    private final Connection connection;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, role, is_verified) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isVerified());
            ps.executeUpdate();

            System.out.println("✅ User added successfully!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("⚠️ Username or email already exists!");
        }
    }


    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        return null; // No user found
    }


    @Override
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        return null;
    }


    @Override
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }


    @Override
    public void verifyUser(String email) throws SQLException {
        String sql = "UPDATE users SET is_verified = TRUE WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            int updated = ps.executeUpdate();

            if (updated > 0) {
                System.out.println("✅ Email verified successfully! You can now log in.");
            } else {
                System.out.println("⚠️ No user found with the given email.");
            }
        }
    }


    @Override
    public String getRoleByUsername(String username) throws SQLException {
        String sql = "SELECT role FROM users WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }
        }
        return "USER"; // Default role
    }

    // 🔹 Helper method to map ResultSet to User object
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("role"),
                rs.getBoolean("is_verified")
        );
    }


//    public User getAdminUser() {
//        try {
//            String sql = "SELECT * FROM users WHERE role = 'ADMIN' AND email IS NOT NULL LIMIT 1";
//            try (PreparedStatement ps = connection.prepareStatement(sql)) {
//                ResultSet rs = ps.executeQuery();
//                if (rs.next()) {
//                    return new User(
//                            rs.getInt("id"),
//                            rs.getString("username"),
//                            rs.getString("password"),
//                            rs.getString("email"),
//                            rs.getString("role"),
//                            rs.getBoolean("is_verified")
//                    );
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("❌ Error fetching admin user: " + e.getMessage());
//        }
//        return null;
//    }

}
