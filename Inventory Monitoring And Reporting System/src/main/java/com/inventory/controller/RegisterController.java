package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.UserService;
import com.inventory.service.OTPService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class RegisterController {
    private UserService userService = new UserService();
    private Stage primaryStage;
    
    public void showRegisterScene(Stage stage) {
        this.primaryStage = stage;
        
        // Main container
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");
        
        // Center card
        VBox card = createRegisterCard();
        root.setCenter(card);
        
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
    }
    
    private VBox createRegisterCard() {
        VBox card = new VBox(15);
        card.setMaxWidth(500);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 10);");
        
        // Title
        Label title = new Label("📝 Register New Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#667eea"));
        
        // Email
        Label emailLabel = new Label("Email");
        emailLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-padding: 10;");
        
        // Username
        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-padding: 10;");
        
        // Password
        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-padding: 10;");
        
        // Role
        Label roleLabel = new Label("Role");
        roleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("USER", "ADMIN");
        roleCombo.setValue("USER");
        roleCombo.setPrefHeight(40);
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0;");
        
        // OTP Field
        Label otpLabel = new Label("OTP Code");
        otpLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        HBox otpBox = new HBox(10);
        TextField otpField = new TextField();
        otpField.setPromptText("Enter OTP");
        otpField.setPrefHeight(40);
        otpField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0; -fx-padding: 10;");
        
        Button sendOtpBtn = new Button("Send OTP");
        sendOtpBtn.setPrefHeight(40);
        sendOtpBtn.setStyle("-fx-background-color: #764ba2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        sendOtpBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please enter email first!");
            } else {
                String otp = OTPService.generateOTP(email);
                OTPService.sendOTPEmail(email, otp);
                showAlert(Alert.AlertType.INFORMATION, "Success", "OTP sent to " + email);
            }
        });
        
        HBox.setHgrow(otpField, Priority.ALWAYS);
        otpBox.getChildren().addAll(otpField, sendOtpBtn);
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 12));
        messageLabel.setWrapText(true);
        
        // Register button
        Button registerBtn = new Button("Register");
        registerBtn.setPrefHeight(45);
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 8;");
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle("-fx-background-color: #5568d3; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 8;"));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; -fx-background-radius: 8;"));
        
        registerBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String role = roleCombo.getValue();
            String otp = otpField.getText().trim();
            
            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || otp.isEmpty()) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("⚠️ Please fill all fields and verify OTP");
                return;
            }
            
            if (!OTPService.verifyOTP(email, otp)) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("❌ Invalid or expired OTP!");
                return;
            }
            
            User user = new User(username, password, email, role, false);
            if (userService.register(user)) {
                userService.verifyUser(email);
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("✅ Registration successful!");
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account created! You can now login.");
                
                // Go back to login
                LoginController loginController = new LoginController();
                loginController.showLoginScene(primaryStage);
            } else {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("❌ Registration failed! Username or email may already exist.");
            }
        });
        
        // Back to login link
        Hyperlink backLink = new Hyperlink("Already have an account? Login here");
        backLink.setStyle("-fx-text-fill: #667eea; -fx-font-size: 13;");
        backLink.setOnAction(e -> {
            LoginController loginController = new LoginController();
            loginController.showLoginScene(primaryStage);
        });
        
        card.getChildren().addAll(
            title,
            emailLabel, emailField,
            userLabel, usernameField,
            passLabel, passwordField,
            roleLabel, roleCombo,
            otpLabel, otpBox,
            messageLabel,
            registerBtn,
            backLink
        );
        
        return card;
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
