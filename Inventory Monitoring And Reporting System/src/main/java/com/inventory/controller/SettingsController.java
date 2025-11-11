package com.inventory.controller;

import com.inventory.service.EmailService;
import com.inventory.util.dbConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.SQLException;

public class SettingsController {
    
    @FXML private TextField txtSmtpHost;
    @FXML private TextField txtSmtpPort;
    @FXML private Label lblFromEmail;
    @FXML private Label lblEmailConfigStatus;
    @FXML private TextField txtDbUrl;
    @FXML private TextField txtDbUsername;
    @FXML private PasswordField txtDbPassword;
    @FXML private Label lblDbConfigStatus;
    
    @FXML
    public void initialize() {
        // Load current settings
        loadEmailSettings();
        loadDatabaseSettings();
        
        // Check email configuration
        checkEmailConfiguration();
        
        // Check database connection
        checkDatabaseConnection();
    }
    
    private void loadEmailSettings() {
        // Email settings are read from environment variables
        String fromEmail = System.getenv("MAIL_USER");
        if (fromEmail != null && !fromEmail.isEmpty()) {
            lblFromEmail.setText(fromEmail);
        } else {
            lblFromEmail.setText("(Not set - configure MAIL_USER env variable)");
            lblFromEmail.setStyle("-fx-text-fill: #e74c3c;");
        }
        
        // Default SMTP settings
        txtSmtpHost.setText("smtp.gmail.com");
        txtSmtpPort.setText("587");
    }
    
    private void loadDatabaseSettings() {
        // Note: Database settings are hardcoded in dbConnection class
        // In a production app, these would be loaded from a config file
        txtDbUrl.setText("jdbc:mysql://localhost:3306/inventoryDB");
        txtDbUsername.setText("root");
        // Password is not displayed for security
    }
    
    @FXML
    private void checkEmailConfiguration() {
        new Thread(() -> {
            String fromEmail = System.getenv("MAIL_USER");
            String password = System.getenv("MAIL_PASS");
            
            Platform.runLater(() -> {
                if (fromEmail != null && !fromEmail.isEmpty() && 
                    password != null && !password.isEmpty()) {
                    lblEmailConfigStatus.setText("✅ Email credentials configured");
                    lblEmailConfigStatus.setStyle("-fx-text-fill: #27ae60;");
                } else {
                    lblEmailConfigStatus.setText("⚠️ Email credentials not configured (MAIL_USER, MAIL_PASS)");
                    lblEmailConfigStatus.setStyle("-fx-text-fill: #f39c12;");
                }
            });
        }).start();
    }
    
    @FXML
    private void onTestEmailConfig() {
        String recipient = lblFromEmail.getText();
        
        if (recipient == null || recipient.isEmpty() || recipient.contains("Not set")) {
            showAlert(Alert.AlertType.WARNING, "Configuration Error", 
                "Email credentials are not configured. Please set MAIL_USER and MAIL_PASS environment variables.");
            return;
        }
        
        // Try to send a test email
        lblEmailConfigStatus.setText("Testing email configuration...");
        lblEmailConfigStatus.setStyle("-fx-text-fill: #3498db;");
        
        new Thread(() -> {
            try {
                EmailService.sendReport(recipient, "Test Email Configuration", 
                    "This is a test email to verify your email configuration.", null);
                
                Platform.runLater(() -> {
                    lblEmailConfigStatus.setText("✅ Email configuration test successful!");
                    lblEmailConfigStatus.setStyle("-fx-text-fill: #27ae60;");
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Test email sent successfully! Check your inbox at " + recipient);
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblEmailConfigStatus.setText("❌ Email configuration test failed: " + e.getMessage());
                    lblEmailConfigStatus.setStyle("-fx-text-fill: #e74c3c;");
                    showAlert(Alert.AlertType.ERROR, "Error", 
                        "Failed to send test email: " + e.getMessage() + 
                        "\n\nPlease verify:\n" +
                        "1. MAIL_USER and MAIL_PASS environment variables are set\n" +
                        "2. Email credentials are correct\n" +
                        "3. Internet connection is available");
                });
            }
        }).start();
    }
    
    @FXML
    private void checkDatabaseConnection() {
        new Thread(() -> {
            try {
                Connection conn = dbConnection.getConnection();
                if (conn != null && !conn.isClosed()) {
                    Platform.runLater(() -> {
                        lblDbConfigStatus.setText("✅ Database connection successful");
                        lblDbConfigStatus.setStyle("-fx-text-fill: #27ae60;");
                    });
                    conn.close();
                }
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    lblDbConfigStatus.setText("❌ Database connection failed: " + e.getMessage());
                    lblDbConfigStatus.setStyle("-fx-text-fill: #e74c3c;");
                });
            }
        }).start();
    }
    
    @FXML
    private void onTestDbConnection() {
        String url = txtDbUrl.getText().trim();
        String username = txtDbUsername.getText().trim();
        String password = txtDbPassword.getText().trim();
        
        if (url.isEmpty() || username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", 
                "Please enter database URL and username.");
            return;
        }
        
        lblDbConfigStatus.setText("Testing database connection...");
        lblDbConfigStatus.setStyle("-fx-text-fill: #3498db;");
        
        new Thread(() -> {
            try {
                // Note: This is a test connection, not using the actual dbConnection class
                // In production, you would update dbConnection to use these values
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = java.sql.DriverManager.getConnection(url, username, password);
                
                if (conn != null && !conn.isClosed()) {
                    Platform.runLater(() -> {
                        lblDbConfigStatus.setText("✅ Database connection test successful!");
                        lblDbConfigStatus.setStyle("-fx-text-fill: #27ae60;");
                        showAlert(Alert.AlertType.INFORMATION, "Success", 
                            "Database connection test successful!");
                    });
                    conn.close();
                }
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblDbConfigStatus.setText("❌ Database connection test failed: " + e.getMessage());
                    lblDbConfigStatus.setStyle("-fx-text-fill: #e74c3c;");
                    showAlert(Alert.AlertType.ERROR, "Error", 
                        "Failed to connect to database: " + e.getMessage() + 
                        "\n\nPlease verify:\n" +
                        "1. MySQL server is running\n" +
                        "2. Database URL is correct\n" +
                        "3. Username and password are correct\n" +
                        "4. Database 'inventoryDB' exists");
                });
            }
        }).start();
    }
    
    @FXML
    private void onSaveDbSettings() {
        // Note: In a production app, you would save these settings to a config file
        // and update the dbConnection class to use them
        // For now, this is a placeholder that shows a message
        showAlert(Alert.AlertType.INFORMATION, "Settings", 
            "Database settings displayed for reference.\n\n" +
            "Note: To change database settings, update the dbConnection class " +
            "or implement a configuration file system.");
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

