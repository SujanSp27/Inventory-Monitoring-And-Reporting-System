package com.inventory.DataAccessObject;

import com.inventory.model.User;
import java.sql.SQLException;

public interface UserDAO {

    // 🔹 Add a new user (registration)
    void addUser(User user) throws SQLException;

    // 🔹 Get user by username (for login)
    User getUserByUsername(String username) throws SQLException;

    // 🔹 Get user by email (for verification or duplicates)
    User getUserByEmail(String email) throws SQLException;

    // 🔹 Check if a user exists by email
    boolean existsByEmail(String email) throws SQLException;

    // 🔹 Update email verification status
    void verifyUser(String email) throws SQLException;

    // 🔹 Get role by username (Admin/User)
    String getRoleByUsername(String username) throws SQLException;
}
