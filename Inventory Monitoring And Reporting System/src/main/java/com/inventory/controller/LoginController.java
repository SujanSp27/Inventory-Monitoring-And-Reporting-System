package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.UserService;
import com.inventory.service.OTPService;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {
    private UserService userService = new UserService();
    private Stage primaryStage;
    
    public void showLoginScene(Stage stage) {
        this.primaryStage = stage;
        
        // Main container with dark gradient matching admin panel
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a2e 0%, #16213e 50%, #0f3460 100%);");
        
        // Center card
        VBox card = createLoginCard();
        root.setCenter(card);
        
        Scene scene = new Scene(root, 1200, 700);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), card);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        // Slide in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), card);
        slideIn.setFromY(-30);
        slideIn.setToY(0);
        slideIn.play();
    }
    
    private VBox createLoginCard() {
        VBox card = new VBox(20);
        card.setMaxWidth(450);
        card.setMaxHeight(600);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                     "-fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 25, 0.4, 0, 10); " +
                     "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                     "-fx-border-width: 1; " +
                     "-fx-border-radius: 15;");
        
        // Title with glow effect
        Label title = new Label("⚡ Inventory System");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#00d4ff"));
        
        // Add glow effect to title
        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.web("#00d4ff", 0.8));
        titleGlow.setRadius(15);
        title.setEffect(titleGlow);
        
        Label subtitle = new Label("Welcome Back! Please login to continue");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#a0aec0"));
        
        // Username field with dark theme
        Label userLabel = new Label("👤 Username");
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        userLabel.setTextFill(Color.web("#e0e6ed"));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefHeight(45);
        usernameField.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                              "-fx-text-fill: white; " +
                              "-fx-prompt-text-fill: #718096; " +
                              "-fx-background-radius: 10; " +
                              "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                              "-fx-border-radius: 10; " +
                              "-fx-border-width: 2; " +
                              "-fx-padding: 10;");
        
        usernameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                usernameField.setStyle("-fx-background-color: rgba(26, 32, 44, 0.8); " +
                                     "-fx-text-fill: white; " +
                                     "-fx-prompt-text-fill: #718096; " +
                                     "-fx-background-radius: 10; " +
                                     "-fx-border-color: #00d4ff; " +
                                     "-fx-border-radius: 10; " +
                                     "-fx-border-width: 2; " +
                                     "-fx-padding: 10; " +
                                     "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.5), 15, 0.4, 0, 0);");
            } else {
                usernameField.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                                     "-fx-text-fill: white; " +
                                     "-fx-prompt-text-fill: #718096; " +
                                     "-fx-background-radius: 10; " +
                                     "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                                     "-fx-border-radius: 10; " +
                                     "-fx-border-width: 2; " +
                                     "-fx-padding: 10;");
            }
        });
        
        // Password field with dark theme
        Label passLabel = new Label("🔒 Password");
        passLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        passLabel.setTextFill(Color.web("#e0e6ed"));
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                              "-fx-text-fill: white; " +
                              "-fx-prompt-text-fill: #718096; " +
                              "-fx-background-radius: 10; " +
                              "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                              "-fx-border-radius: 10; " +
                              "-fx-border-width: 2; " +
                              "-fx-padding: 10;");
        
        passwordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                passwordField.setStyle("-fx-background-color: rgba(26, 32, 44, 0.8); " +
                                     "-fx-text-fill: white; " +
                                     "-fx-prompt-text-fill: #718096; " +
                                     "-fx-background-radius: 10; " +
                                     "-fx-border-color: #00d4ff; " +
                                     "-fx-border-radius: 10; " +
                                     "-fx-border-width: 2; " +
                                     "-fx-padding: 10; " +
                                     "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.5), 15, 0.4, 0, 0);");
            } else {
                passwordField.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                                     "-fx-text-fill: white; " +
                                     "-fx-prompt-text-fill: #718096; " +
                                     "-fx-background-radius: 10; " +
                                     "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                                     "-fx-border-radius: 10; " +
                                     "-fx-border-width: 2; " +
                                     "-fx-padding: 10;");
            }
        });
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 12));
        messageLabel.setWrapText(true);
        
        // Login button with animations
        Button loginBtn = new Button("🚀 Login");
        loginBtn.setPrefHeight(45);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                         "-fx-text-fill: white; " +
                         "-fx-font-size: 16; " +
                         "-fx-font-weight: bold; " +
                         "-fx-background-radius: 10; " +
                         "-fx-cursor: hand; " +
                         "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 15, 0.3, 0, 5);");
        
        // Hover animations
        loginBtn.setOnMouseEntered(e -> {
            loginBtn.setStyle("-fx-background-color: linear-gradient(to right, #00d4ff, #00a8cc); " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 16; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 10; " +
                             "-fx-cursor: hand; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.7), 20, 0.5, 0, 8);");
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), loginBtn);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        loginBtn.setOnMouseExited(e -> {
            loginBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 16; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 10; " +
                             "-fx-cursor: hand; " +
                             "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 15, 0.3, 0, 5);");
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), loginBtn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        loginBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel, loginBtn));
        
        // Register link with dark theme
        Hyperlink registerLink = new Hyperlink("✨ Don't have an account? Register here");
        registerLink.setStyle("-fx-text-fill: #00d4ff; -fx-font-size: 13;");
        registerLink.setOnAction(e -> showRegisterScene());
        
        // Email verification link
        Hyperlink verifyLink = new Hyperlink("📧 Verify your email");
        verifyLink.setStyle("-fx-text-fill: #00d4ff; -fx-font-size: 13;");
        verifyLink.setOnAction(e -> showVerifyEmailScene());
        
        HBox links = new HBox(20, registerLink, verifyLink);
        links.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(title, subtitle, userLabel, usernameField, passLabel, passwordField, messageLabel, loginBtn, links);
        
        return card;
    }
    
    private void handleLogin(TextField usernameField, PasswordField passwordField, Label messageLabel, Button loginBtn) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setTextFill(Color.web("#fc8181"));
            messageLabel.setText("⚠️ Please fill all fields");
            
            // Shake animation for error
            TranslateTransition shake = new TranslateTransition(Duration.millis(100), messageLabel);
            shake.setFromX(0);
            shake.setByX(10);
            shake.setCycleCount(4);
            shake.setAutoReverse(true);
            shake.play();
            return;
        }
        
        // Show loading state
        String originalText = loginBtn.getText();
        loginBtn.setText("⏳ Logging in...");
        loginBtn.setDisable(true);
        
        // Simulate async login with slight delay for UX
        new Thread(() -> {
            try {
                Thread.sleep(500);
                
                javafx.application.Platform.runLater(() -> {
                    if (userService.login(username, password)) {
                        String role = userService.getRole(username);
                        String email = userService.getEmailByUsername(username);
                        
                        messageLabel.setTextFill(Color.web("#48bb78"));
                        messageLabel.setText("✅ Login successful! Loading dashboard...");
                        
                        // Fade out animation before transition
                        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), loginBtn.getParent());
                        fadeOut.setFromValue(1.0);
                        fadeOut.setToValue(0.0);
                        fadeOut.setOnFinished(event -> {
                            if (role.equalsIgnoreCase("ADMIN")) {
                                AdminController adminController = new AdminController(username, email);
                                adminController.showAdminDashboard(primaryStage);
                            } else {
                                UserController userController = new UserController(username);
                                userController.showUserDashboard(primaryStage);
                            }
                        });
                        fadeOut.play();
                    } else {
                        messageLabel.setTextFill(Color.web("#fc8181"));
                        messageLabel.setText("❌ Invalid credentials or email not verified!");
                        loginBtn.setText(originalText);
                        loginBtn.setDisable(false);
                        
                        // Shake animation for error
                        TranslateTransition shake = new TranslateTransition(Duration.millis(100), messageLabel);
                        shake.setFromX(0);
                        shake.setByX(10);
                        shake.setCycleCount(4);
                        shake.setAutoReverse(true);
                        shake.play();
                    }
                });
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    private void showRegisterScene() {
        RegisterController registerController = new RegisterController();
        registerController.showRegisterScene(primaryStage);
    }
    
    private void showVerifyEmailScene() {
        // Create verify email dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Email Verification");
        dialog.setHeaderText("📧 Verify Your Email");
        
        // Set the button types
        ButtonType verifyButtonType = new ButtonType("Verify", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(verifyButtonType, ButtonType.CANCEL);
        
        // Create the email and OTP fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(300);
        TextField otpField = new TextField();
        otpField.setPromptText("OTP Code");
        
        Button sendOtpBtn = new Button("Send OTP");
        sendOtpBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white;");
        sendOtpBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (!email.isEmpty()) {
                if (userService.existsByEmail(email)) {
                    String otp = OTPService.generateOTP(email);
                    OTPService.sendOTPEmail(email, otp);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "OTP sent to " + email);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Email not found!");
                }
            }
        });
        
        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(sendOtpBtn, 2, 0);
        grid.add(new Label("OTP:"), 0, 1);
        grid.add(otpField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == verifyButtonType) {
                String email = emailField.getText().trim();
                String otp = otpField.getText().trim();
                
                if (OTPService.verifyOTP(email, otp)) {
                    userService.verifyUser(email);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "✅ Email verified successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "❌ Invalid OTP!");
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
