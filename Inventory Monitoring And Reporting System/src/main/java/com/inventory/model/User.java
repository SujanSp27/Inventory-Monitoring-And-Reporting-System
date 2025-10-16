package com.inventory.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;
    private boolean isVerified;

    public User() {}

    // ✅ Correct parameter order
    public User(int id, String username, String password, String email, String role, boolean isVerified) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isVerified = isVerified;
    }

    // ✅ Overloaded constructor without ID
    public User(String username, String password, String email, String role, boolean isVerified) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.isVerified = isVerified;
    }

    // ✅ Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", verified=" + isVerified +
                '}';
    }
}
