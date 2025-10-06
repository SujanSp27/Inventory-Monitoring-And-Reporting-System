package com.inventory.DataAccessObject;

import com.inventory.model.User;
import com.inventory.util.dbConnection;
import java.sql.*;

public class UserDAO {

    // Add new user
    public void addUser(User user) {
        String sql = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());

            stmt.executeUpdate();
            System.out.println("✅ User added successfully!");
        } catch (SQLException e) {
            System.out.println("❌ Error adding user: " + e.getMessage());
        }
    }

    // Get user by username
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            } else {
                System.out.println("⚠️ No user found with username: " + username);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching user: " + e.getMessage());
        }

        return user;
    }
}

