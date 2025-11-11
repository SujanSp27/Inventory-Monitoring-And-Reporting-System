package com.inventory.controller;

import com.inventory.InventoryApp;
import com.inventory.DataAccessObject.ProductDAO;
import com.inventory.model.product;
import com.inventory.util.dbConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DashboardController {
    
    @FXML private StackPane contentPane;
    @FXML private Label lblUserInfo;
    
    // Dashboard view components
    private Label lblTotalProducts;
    private Label lblLowStock;
    private Label lblTotalValue;
    private Label lblLastReport;
    private Label lblDbStatus;
    private ListView<String> lowStockList;
    
    private ProductDAO productDAO;
    private String currentUsername;
    private String currentUserEmail;
    
    @FXML
    public void initialize() {
        productDAO = new ProductDAO();
        // Check database connection on startup
        checkDatabaseConnection();
    }
    
    public void setUserInfo(String username, String email) {
        this.currentUsername = username;
        this.currentUserEmail = email;
        if (lblUserInfo != null) {
            lblUserInfo.setText("👤 " + username + " (" + email + ")");
        }
        // Load dashboard by default
        showDashboard();
    }
    
    @FXML
    private void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard_view.fxml"));
            VBox dashboardView = loader.load();
            
            // Get references to dashboard components
            lblTotalProducts = (Label) dashboardView.lookup("#lblTotalProducts");
            lblLowStock = (Label) dashboardView.lookup("#lblLowStock");
            lblTotalValue = (Label) dashboardView.lookup("#lblTotalValue");
            lblLastReport = (Label) dashboardView.lookup("#lblLastReport");
            lblDbStatus = (Label) dashboardView.lookup("#lblDbStatus");
            lowStockList = (ListView<String>) dashboardView.lookup("#lowStockList");
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(dashboardView);
            
            // Update dashboard statistics
            updateDashboardStats();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/products_view.fxml"));
            Node productsView = loader.load();
            ProductsManagementController controller = loader.getController();
            controller.setUserInfo(currentUsername, currentUserEmail);
            contentPane.getChildren().clear();
            contentPane.getChildren().add(productsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reports_view.fxml"));
            Node reportsView = loader.load();
            ReportsController controller = loader.getController();
            controller.setUserInfo(currentUsername, currentUserEmail);
            contentPane.getChildren().clear();
            contentPane.getChildren().add(reportsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void showSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings_view.fxml"));
            Node settingsView = loader.load();
            SettingsController controller = loader.getController();
            controller.initialize();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(settingsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onLogout() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) lblUserInfo.getScene().getWindow();
            LoginController loginController = new LoginController();
            loginController.showLoginScene(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateDashboardStats() {
        try {
            List<product> products = productDAO.getAllProducts();
            
            final int totalProducts = products.size();
            final int[] lowStockCount = {0};
            final double[] totalValue = {0};
            final java.util.List<String> lowStockItems = new java.util.ArrayList<>();
            
            if (lowStockList != null) {
                lowStockList.getItems().clear();
            }
            
            for (product p : products) {
                double stockValue = p.getQuantity() * p.getPrice();
                totalValue[0] += stockValue;
                
                // Check low stock (using threshold if available, otherwise default to 10)
                int threshold = p.getThreshold() > 0 ? p.getThreshold() : 10;
                if (p.getQuantity() < threshold) {
                    lowStockCount[0]++;
                    lowStockItems.add(
                        String.format("%s (Qty: %d, Threshold: %d)", 
                            p.getName(), p.getQuantity(), threshold)
                    );
                }
            }
            
            // Update labels on JavaFX thread
            final double finalTotalValue = totalValue[0];
            final int finalLowStockCount = lowStockCount[0];
            Platform.runLater(() -> {
                if (lblTotalProducts != null) {
                    lblTotalProducts.setText(String.valueOf(totalProducts));
                }
                if (lblLowStock != null) {
                    lblLowStock.setText(String.valueOf(finalLowStockCount));
                }
                if (lblTotalValue != null) {
                    lblTotalValue.setText(String.format("₹%.2f", finalTotalValue));
                }
                if (lblLastReport != null) {
                    // Try to get last report time from file system or database
                    lblLastReport.setText("Today"); // Simplified - can be enhanced
                }
                if (lowStockList != null && !lowStockItems.isEmpty()) {
                    lowStockList.getItems().addAll(lowStockItems);
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                if (lblDbStatus != null) {
                    lblDbStatus.setText("❌ Error loading statistics: " + e.getMessage());
                }
            });
        }
    }
    
    private void checkDatabaseConnection() {
        new Thread(() -> {
            try {
                Connection conn = dbConnection.getConnection();
                if (conn != null && !conn.isClosed()) {
                    Platform.runLater(() -> {
                        if (lblDbStatus != null) {
                            lblDbStatus.setText("✅ Database connected successfully");
                            lblDbStatus.setStyle("-fx-text-fill: #27ae60;");
                        }
                    });
                    conn.close();
                }
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    if (lblDbStatus != null) {
                        lblDbStatus.setText("❌ Database connection failed: " + e.getMessage());
                        lblDbStatus.setStyle("-fx-text-fill: #e74c3c;");
                    }
                });
            }
        }).start();
    }
}

