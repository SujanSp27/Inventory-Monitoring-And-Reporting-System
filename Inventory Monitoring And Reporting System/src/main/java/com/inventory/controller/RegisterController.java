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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RegisterController {
    private UserService userService = new UserService();
    private Stage primaryStage;
    
    public void showRegisterScene(Stage stage) {
        this.primaryStage = stage;
        
        // Main container with dark gradient matching login
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a2e 0%, #16213e 50%, #0f3460 100%);");
        
        // Center card
        VBox card = createRegisterCard();
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
    
    private VBox createRegisterCard() {
        VBox card = new VBox(15);
        card.setMaxWidth(500);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                     "-fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 25, 0.4, 0, 10); " +
                     "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                     "-fx-border-width: 1; " +
                     "-fx-border-radius: 15;");
        
        // Title with glow effect
        Label title = new Label("✨ Register New Account");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#00d4ff"));
        
        // Add glow effect to title
        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.web("#00d4ff", 0.8));
        titleGlow.setRadius(15);
        title.setEffect(titleGlow);
        
        Label subtitle = new Label("Create your account to get started");
        subtitle.setFont(Font.font("System", 14));
        subtitle.setTextFill(Color.web("#a0aec0"));
        
        // Email
        Label emailLabel = new Label("📧 Email");
        emailLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        emailLabel.setTextFill(Color.web("#e0e6ed"));
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(45);
        styleTextField(emailField);
        
        // Username
        Label userLabel = new Label("👤 Username");
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        userLabel.setTextFill(Color.web("#e0e6ed"));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setPrefHeight(45);
        styleTextField(usernameField);
        
        // Password
        Label passLabel = new Label("🔒 Password");
        passLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        passLabel.setTextFill(Color.web("#e0e6ed"));
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.setPrefHeight(45);
        stylePasswordField(passwordField);
        
        // Role
        Label roleLabel = new Label("👑 Role");
        roleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        roleLabel.setTextFill(Color.web("#e0e6ed"));
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("USER", "ADMIN");
        roleCombo.setValue("USER");
        roleCombo.setPrefHeight(45);
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                          "-fx-text-fill: white; " +
                          "-fx-background-radius: 10; " +
                          "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                          "-fx-border-radius: 10; " +
                          "-fx-border-width: 2;");
        
        // OTP Field
        Label otpLabel = new Label("🔑 OTP Code");
        otpLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        otpLabel.setTextFill(Color.web("#e0e6ed"));
        
        HBox otpBox = new HBox(10);
        TextField otpField = new TextField();
        otpField.setPromptText("Enter OTP");
        otpField.setPrefHeight(45);
        styleTextField(otpField);
        
        Button sendOtpBtn = new Button("📧 Send OTP");
        sendOtpBtn.setPrefHeight(45);
        sendOtpBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                           "-fx-text-fill: white; " +
                           "-fx-font-weight: bold; " +
                           "-fx-background-radius: 10; " +
                           "-fx-cursor: hand; " +
                           "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 10, 0.3, 0, 3);");
        
        addButtonHoverAnimation(sendOtpBtn);
        
        sendOtpBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                showAnimatedAlert(Alert.AlertType.WARNING, "Warning", "⚠️ Please enter email first!");
            } else {
                sendOtpBtn.setText("⏳ Sending...");
                sendOtpBtn.setDisable(true);
                
                new Thread(() -> {
                    String otp = OTPService.generateOTP(email);
                    OTPService.sendOTPEmail(email, otp);
                    
                    javafx.application.Platform.runLater(() -> {
                        sendOtpBtn.setText("📧 Send OTP");
                        sendOtpBtn.setDisable(false);
                        showAnimatedAlert(Alert.AlertType.INFORMATION, "Success", "✅ OTP sent to " + email);
                    });
                }).start();
            }
        });
        
        HBox.setHgrow(otpField, Priority.ALWAYS);
        otpBox.getChildren().addAll(otpField, sendOtpBtn);
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setFont(Font.font("System", 12));
        messageLabel.setWrapText(true);
        
        // Register button with animation
        Button registerBtn = new Button("🚀 Register");
        registerBtn.setPrefHeight(45);
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 10; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 15, 0.3, 0, 5);");
        
        addButtonHoverAnimation(registerBtn);
        
        registerBtn.setOnAction(e -> handleRegister(emailField, usernameField, passwordField, roleCombo, otpField, messageLabel, registerBtn));
        
        // Back to login link
        Hyperlink backLink = new Hyperlink("← Already have an account? Login here");
        backLink.setStyle("-fx-text-fill: #00d4ff; -fx-font-size: 13;");
        backLink.setOnAction(e -> {
            // Fade out before transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), card.getParent());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                LoginController loginController = new LoginController();
                loginController.showLoginScene(primaryStage);
            });
            fadeOut.play();
        });
        
        card.getChildren().addAll(
            title,
            subtitle,
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
    
    private void handleRegister(TextField emailField, TextField usernameField, PasswordField passwordField, 
                                ComboBox<String> roleCombo, TextField otpField, Label messageLabel, Button registerBtn) {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleCombo.getValue();
        String otp = otpField.getText().trim();
        
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || otp.isEmpty()) {
            messageLabel.setTextFill(Color.web("#fc8181"));
            messageLabel.setText("⚠️ Please fill all fields and verify OTP");
            
            // Shake animation
            TranslateTransition shake = new TranslateTransition(Duration.millis(100), messageLabel);
            shake.setFromX(0);
            shake.setByX(10);
            shake.setCycleCount(4);
            shake.setAutoReverse(true);
            shake.play();
            return;
        }
        
        if (!OTPService.verifyOTP(email, otp)) {
            messageLabel.setTextFill(Color.web("#fc8181"));
            messageLabel.setText("❌ Invalid or expired OTP!");
            
            // Shake animation
            TranslateTransition shake = new TranslateTransition(Duration.millis(100), messageLabel);
            shake.setFromX(0);
            shake.setByX(10);
            shake.setCycleCount(4);
            shake.setAutoReverse(true);
            shake.play();
            return;
        }
        
        // Show loading state
        String originalText = registerBtn.getText();
        registerBtn.setText("⏳ Registering...");
        registerBtn.setDisable(true);
        
        new Thread(() -> {
            User user = new User(username, password, email, role, false);
            boolean success = userService.register(user);
            
            javafx.application.Platform.runLater(() -> {
                if (success) {
                    userService.verifyUser(email);
                    messageLabel.setTextFill(Color.web("#48bb78"));
                    messageLabel.setText("✅ Registration successful!");
                    showAnimatedAlert(Alert.AlertType.INFORMATION, "Success", "✅ Account created! You can now login.");
                    
                    // Fade out before transition
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), registerBtn.getParent());
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(event -> {
                        LoginController loginController = new LoginController();
                        loginController.showLoginScene(primaryStage);
                    });
                    fadeOut.play();
                } else {
                    messageLabel.setTextFill(Color.web("#fc8181"));
                    messageLabel.setText("❌ Registration failed! Username or email may already exist.");
                    registerBtn.setText(originalText);
                    registerBtn.setDisable(false);
                    
                    // Shake animation
                    TranslateTransition shake = new TranslateTransition(Duration.millis(100), messageLabel);
                    shake.setFromX(0);
                    shake.setByX(10);
                    shake.setCycleCount(4);
                    shake.setAutoReverse(true);
                    shake.play();
                }
            });
        }).start();
    }
    
    private void styleTextField(TextField field) {
        field.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                      "-fx-text-fill: white; " +
                      "-fx-prompt-text-fill: #718096; " +
                      "-fx-background-radius: 10; " +
                      "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                      "-fx-border-radius: 10; " +
                      "-fx-border-width: 2; " +
                      "-fx-padding: 10;");
        
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                field.setStyle("-fx-background-color: rgba(26, 32, 44, 0.8); " +
                              "-fx-text-fill: white; " +
                              "-fx-prompt-text-fill: #718096; " +
                              "-fx-background-radius: 10; " +
                              "-fx-border-color: #00d4ff; " +
                              "-fx-border-radius: 10; " +
                              "-fx-border-width: 2; " +
                              "-fx-padding: 10; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.5), 15, 0.4, 0, 0);");
            } else {
                field.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                              "-fx-text-fill: white; " +
                              "-fx-prompt-text-fill: #718096; " +
                              "-fx-background-radius: 10; " +
                              "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                              "-fx-border-radius: 10; " +
                              "-fx-border-width: 2; " +
                              "-fx-padding: 10;");
            }
        });
    }
    
    private void stylePasswordField(PasswordField field) {
        field.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                      "-fx-text-fill: white; " +
                      "-fx-prompt-text-fill: #718096; " +
                      "-fx-background-radius: 10; " +
                      "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                      "-fx-border-radius: 10; " +
                      "-fx-border-width: 2; " +
                      "-fx-padding: 10;");
        
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                field.setStyle("-fx-background-color: rgba(26, 32, 44, 0.8); " +
                              "-fx-text-fill: white; " +
                              "-fx-prompt-text-fill: #718096; " +
                              "-fx-background-radius: 10; " +
                              "-fx-border-color: #00d4ff; " +
                              "-fx-border-radius: 10; " +
                              "-fx-border-width: 2; " +
                              "-fx-padding: 10; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.5), 15, 0.4, 0, 0);");
            } else {
                field.setStyle("-fx-background-color: rgba(26, 32, 44, 0.6); " +
                              "-fx-text-fill: white; " +
                              "-fx-prompt-text-fill: #718096; " +
                              "-fx-background-radius: 10; " +
                              "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                              "-fx-border-radius: 10; " +
                              "-fx-border-width: 2; " +
                              "-fx-padding: 10;");
            }
        });
    }
    
    private void addButtonHoverAnimation(Button btn) {
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: linear-gradient(to right, #00d4ff, #00a8cc); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.7), 20, 0.5, 0, 8);");
            
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 15, 0.3, 0, 5);");
            
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }
    
    private void showAnimatedAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Apply dark theme with visible neon text
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1a1a2e; " +
                           "-fx-border-color: #00d4ff; " +
                           "-fx-border-width: 2; " +
                           "-fx-border-radius: 10; " +
                           "-fx-background-radius: 10;");
        
        // Style the content text to be visible with neon color
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #00d4ff; " +
                                                     "-fx-font-size: 14px; " +
                                                     "-fx-font-weight: bold;");
        
        // Style header if present
        if (dialogPane.lookup(".header-panel") != null) {
            dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #16213e;");
        }
        
        // Style buttons
        dialogPane.lookupButton(ButtonType.OK).setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                                                       "-fx-text-fill: white; " +
                                                       "-fx-font-weight: bold; " +
                                                       "-fx-background-radius: 8;");
        
        // Add entrance animation
        alert.setOnShowing(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), alert.getDialogPane());
            scale.setFromX(0.7);
            scale.setFromY(0.7);
            scale.setToX(1.05);
            scale.setToY(1.05);
            
            ScaleTransition scaleBack = new ScaleTransition(Duration.millis(100), alert.getDialogPane());
            scaleBack.setFromX(1.05);
            scaleBack.setFromY(1.05);
            scaleBack.setToX(1.0);
            scaleBack.setToY(1.0);
            
            FadeTransition fade = new FadeTransition(Duration.millis(200), alert.getDialogPane());
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            
            SequentialTransition sequence = new SequentialTransition(scale, scaleBack);
            ParallelTransition parallel = new ParallelTransition(sequence, fade);
            parallel.play();
        });
        
        alert.showAndWait();
    }
}
