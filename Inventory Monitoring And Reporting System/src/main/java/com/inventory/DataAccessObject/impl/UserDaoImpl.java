package com.inventory.DataAccessObject.impl;

import com.inventory.DataAccessObject.UserDAO;
import com.inventory.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;

public class UserDaoImpl implements UserDAO {

    private Connection connection;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    // ✅ Add a new user to the database
    @Override
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.executeUpdate();

            System.out.println("✅ User added successfully!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("⚠️ Username already exists!");
        }
    }

    // ✅ Retrieve a user by username
    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        }
        return null; // No user found
    }
}
