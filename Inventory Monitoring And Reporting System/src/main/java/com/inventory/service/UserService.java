package com.inventory.service;

import com.inventory.DataAccessObject.UserDAO;
import com.inventory.model.User;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public boolean login(String username, String password) {
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            System.out.println("❌ User not found!");
            return false;
        }

        if (user.getPassword().equals(password)) {
            String role = user.getRole().toUpperCase();
            System.out.println("✅ Login successful! Welcome, " + user.getUsername());
            System.out.println("Role: " + role);
            return true;
        } else {
            System.out.println("❌ Invalid password!");
            return false;
        }
    }
}
