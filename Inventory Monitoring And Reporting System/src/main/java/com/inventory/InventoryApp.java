package com.inventory;

import com.inventory.controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

public class InventoryApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("📦 Inventory Monitoring & Reporting System");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);
        
        // Load login screen
        LoginController loginController = new LoginController();
        loginController.showLoginScene(primaryStage);
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
