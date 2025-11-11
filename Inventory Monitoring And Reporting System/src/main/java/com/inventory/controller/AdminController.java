package com.inventory.controller;

import com.inventory.DataAccessObject.ProductDAO;
import com.inventory.model.product;
import com.inventory.service.StockAlertService;
import com.inventory.util.CSVHelper;
import com.inventory.service.EmailService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AdminController {
    private String username;
    private String email;
    private ProductDAO productDAO = new ProductDAO();
    private TableView<product> productTable;
    private ObservableList<product> productList = FXCollections.observableArrayList();
    private Label totalProductsLabel;
    private Label totalValueLabel;
    private Label lowStockLabel;
    private ScheduledExecutorService scheduler;
    
    public AdminController(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    public void showAdminDashboard(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a2e 0%, #16213e 50%, #0f3460 100%);");
        
        // Top bar with gradient
        HBox topBar = createTopBar(stage);
        root.setTop(topBar);
        
        // Left sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);
        
        // Center content
        VBox centerContent = createDashboardContent();
        root.setCenter(centerContent);
        
        Scene scene = new Scene(root, 1400, 800);
        scene.setFill(Color.TRANSPARENT);
        
        stage.setScene(scene);
        
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        // Start stock alert scheduler
        startStockAlertScheduler();
        
        // Load products with animation
        refreshProducts();
    }
    
    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%); " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0.3, 0, 5);");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("⚡ Inventory Management System - Admin Panel");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);
        
        // Add glow effect
        Glow glow = new Glow(0.3);
        title.setEffect(glow);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Interactive user badge
        HBox userBadge = new HBox(8);
        userBadge.setAlignment(Pos.CENTER);
        userBadge.setPadding(new Insets(8, 15, 8, 15));
        userBadge.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                          "-fx-background-radius: 20; " +
                          "-fx-cursor: hand;");
        
        Label userIcon = new Label("👤");
        userIcon.setFont(Font.font("System", 16));
        
        Label userLabel = new Label(username);
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        userLabel.setTextFill(Color.WHITE);
        
        userBadge.getChildren().addAll(userIcon, userLabel);
        
        // Hover animation for user badge
        userBadge.setOnMouseEntered(e -> {
            userBadge.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25); " +
                              "-fx-background-radius: 20; " +
                              "-fx-cursor: hand; " +
                              "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.4), 10, 0.5, 0, 0);");
            ScaleTransition st = new ScaleTransition(Duration.millis(150), userBadge);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();
        });
        
        userBadge.setOnMouseExited(e -> {
            userBadge.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                              "-fx-background-radius: 20; " +
                              "-fx-cursor: hand;");
            ScaleTransition st = new ScaleTransition(Duration.millis(150), userBadge);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
        
        // Show user info on click
        userBadge.setOnMouseClicked(e -> {
            showUserInfo();
        });
        
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                          "-fx-font-weight: bold; -fx-background-radius: 8; " +
                          "-fx-padding: 10 20; -fx-cursor: hand;");
        
        // Hover animation for logout button
        logoutBtn.setOnMouseEntered(e -> {
            logoutBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 8; " +
                              "-fx-padding: 10 20; -fx-cursor: hand; " +
                              "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.6), 10, 0.5, 0, 0);");
            ScaleTransition st = new ScaleTransition(Duration.millis(100), logoutBtn);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });
        
        logoutBtn.setOnMouseExited(e -> {
            logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-background-radius: 8; " +
                              "-fx-padding: 10 20; -fx-cursor: hand;");
            ScaleTransition st = new ScaleTransition(Duration.millis(100), logoutBtn);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
        
        logoutBtn.setOnAction(e -> {
            if (scheduler != null) {
                scheduler.shutdown();
            }
            // Fade out animation before logout
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                LoginController loginController = new LoginController();
                loginController.showLoginScene(stage);
            });
            fadeOut.play();
        });
        
        topBar.getChildren().addAll(title, spacer, userBadge, logoutBtn);
        return topBar;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: rgba(26, 26, 46, 0.95); " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0.4, 5, 0); " +
                        "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                        "-fx-border-width: 0 2 0 0;");
        
        Label menuTitle = new Label("✨ ADMIN MENU");
        menuTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        menuTitle.setTextFill(Color.web("#00d4ff"));
        menuTitle.setPadding(new Insets(0, 0, 10, 0));
        
        // Add glow to title
        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.web("#00d4ff", 0.6));
        titleGlow.setRadius(8);
        menuTitle.setEffect(titleGlow);
        
        Button dashboardBtn = createMenuButton("📊 Dashboard");
        Button addProductBtn = createMenuButton("➕ Add Product");
        Button removeProductBtn = createMenuButton("🗑️ Remove Product");
        Button updateProductBtn = createMenuButton("✏️ Update Product");
        Button searchBtn = createMenuButton("🔍 Search Product");
        Button reportBtn = createMenuButton("📄 Generate Report");
        Button refreshBtn = createMenuButton("🔄 Refresh");
        
        addProductBtn.setOnAction(e -> showAddProductDialog());
        removeProductBtn.setOnAction(e -> showRemoveProductDialog());
        updateProductBtn.setOnAction(e -> showUpdateProductDialog());
        searchBtn.setOnAction(e -> showSearchDialog());
        reportBtn.setOnAction(e -> generateAndSendReport());
        refreshBtn.setOnAction(e -> refreshProducts());
        
        sidebar.getChildren().addAll(menuTitle, dashboardBtn, addProductBtn, removeProductBtn, 
                                     updateProductBtn, searchBtn, reportBtn, refreshBtn);
        
        // Slide in animation for sidebar
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), sidebar);
        slideIn.setFromX(-220);
        slideIn.setToX(0);
        slideIn.play();
        
        return sidebar;
    }
    
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: #e0e6ed; " +
                    "-fx-font-size: 14; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 8;");
        
        // Hover animation
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: rgba(102, 126, 234, 0.3); " +
                        "-fx-text-fill: #00d4ff; " +
                        "-fx-font-size: 14; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.4), 10, 0.5, 0, 0);");
            
            TranslateTransition slide = new TranslateTransition(Duration.millis(150), btn);
            slide.setToX(8);
            slide.play();
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: #e0e6ed; " +
                        "-fx-font-size: 14; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8;");
            
            TranslateTransition slide = new TranslateTransition(Duration.millis(150), btn);
            slide.setToX(0);
            slide.play();
        });
        
        return btn;
    }
    
    private VBox createDashboardContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        
        // Statistics cards
        HBox statsBox = createStatsCards();
        
        // Products table
        VBox tableContainer = createProductTable();
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
        
        content.getChildren().addAll(statsBox, tableContainer);
        return content;
    }
    
    private HBox createStatsCards() {
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        totalProductsLabel = new Label("0");
        VBox card1 = createStatCard("Total Products", totalProductsLabel, "#3498db");
        
        totalValueLabel = new Label("$0.00");
        VBox card2 = createStatCard("Total Inventory Value", totalValueLabel, "#2ecc71");
        
        lowStockLabel = new Label("0");
        VBox card3 = createStatCard("Low Stock Alerts", lowStockLabel, "#e74c3c");
        
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        
        statsBox.getChildren().addAll(card1, card2, card3);
        return statsBox;
    }
    
    private VBox createStatCard(String title, Label valueLabel, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                     "-fx-background-radius: 15; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0.3, 0, 8); " +
                     "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                     "-fx-border-width: 1; " +
                     "-fx-border-radius: 15;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.web("#a0aec0"));
        
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.web(color));
        
        // Add glow effect to value
        DropShadow valueGlow = new DropShadow();
        valueGlow.setColor(Color.web(color, 0.6));
        valueGlow.setRadius(12);
        valueLabel.setEffect(valueGlow);
        
        card.getChildren().addAll(titleLabel, valueLabel);
        
        // Hover animation
        card.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
            
            card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                         "-fx-background-radius: 15; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.5), 25, 0.5, 0, 10); " +
                         "-fx-border-color: rgba(0, 212, 255, 0.6); " +
                         "-fx-border-width: 1; " +
                         "-fx-border-radius: 15;");
        });
        
        card.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            
            card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); " +
                         "-fx-background-radius: 15; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0.3, 0, 8); " +
                         "-fx-border-color: rgba(102, 126, 234, 0.3); " +
                         "-fx-border-width: 1; " +
                         "-fx-border-radius: 15;");
        });
        
        // Initial slide in animation
        FadeTransition fade = new FadeTransition(Duration.millis(600), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        
        return card;
    }
    
    private VBox createProductTable() {
        VBox container = new VBox(15);
        container.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                          "-fx-background-radius: 15; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0.3, 0, 8); " +
                          "-fx-border-color: rgba(102, 126, 234, 0.2); " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 15;");
        container.setPadding(new Insets(20));
        
        Label tableTitle = new Label("📊 Product Inventory");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        tableTitle.setTextFill(Color.web("#00d4ff"));
        
        // Add glow to title
        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.web("#00d4ff", 0.5));
        titleGlow.setRadius(10);
        tableTitle.setEffect(titleGlow);
        
        productTable = new TableView<>();
        productTable.setItems(productList);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productTable.setStyle("-fx-background-color: transparent; " +
                             "-fx-background-radius: 10; " +
                             "-fx-table-cell-border-color: rgba(102, 126, 234, 0.2);");
        
        TableColumn<product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        idCol.setPrefWidth(60);
        idCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);
        
        TableColumn<product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        categoryCol.setPrefWidth(150);
        
        TableColumn<product, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        quantityCol.setPrefWidth(100);
        quantityCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        priceCol.setPrefWidth(100);
        priceCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<product, Integer> thresholdCol = new TableColumn<>("Threshold");
        thresholdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getThreshold()).asObject());
        thresholdCol.setPrefWidth(100);
        thresholdCol.setStyle("-fx-alignment: CENTER;");
        
        TableColumn<product, Double> valueCol = new TableColumn<>("Stock Value");
        valueCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().stockValue()).asObject());
        valueCol.setPrefWidth(120);
        valueCol.setStyle("-fx-alignment: CENTER;");
        
        productTable.getColumns().addAll(idCol, nameCol, categoryCol, quantityCol, priceCol, thresholdCol, valueCol);
        
        VBox.setVgrow(productTable, Priority.ALWAYS);
        container.getChildren().addAll(tableTitle, productTable);
        
        // Fade in animation
        FadeTransition fade = new FadeTransition(Duration.millis(800), container);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        
        return container;
    }
    
    private void refreshProducts() {
        List<product> products = productDAO.getAllProducts();
        productList.clear();
        productList.addAll(products);
        
        // Update statistics
        totalProductsLabel.setText(String.valueOf(products.size()));
        
        double totalValue = products.stream().mapToDouble(product::stockValue).sum();
        totalValueLabel.setText(String.format("$%.2f", totalValue));
        
        long lowStock = products.stream().filter(p -> p.getQuantity() < p.getThreshold()).count();
        lowStockLabel.setText(String.valueOf(lowStock));
    }
    
    private void showAddProductDialog() {
        Dialog<product> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("➕ Enter Product Details");
        
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField idField = new TextField();
        idField.setPromptText("Product ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField thresholdField = new TextField();
        thresholdField.setPromptText("Threshold (default: 10)");
        thresholdField.setText("10");
        
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Price:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Threshold:"), 0, 5);
        grid.add(thresholdField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String name = nameField.getText();
                    String category = categoryField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    int threshold = Integer.parseInt(thresholdField.getText());
                    
                    product p = new product(id, name, quantity, price, category, threshold);
                    productDAO.addProduct(p);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
                    refreshProducts();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid input: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showRemoveProductDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove Product");
        dialog.setHeaderText("🗑️ Remove Product");
        dialog.setContentText("Enter Product ID:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(id -> {
            try {
                int productId = Integer.parseInt(id);
                if (productDAO.removeProduct(productId)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product removed successfully!");
                    refreshProducts();
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove product: " + e.getMessage());
            }
        });
    }
    
    private void showUpdateProductDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Update Product");
        dialog.setHeaderText("✏️ Update Product Details");
        
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField idField = new TextField();
        idField.setPromptText("Product ID");
        TextField quantityField = new TextField();
        quantityField.setPromptText("New Quantity");
        TextField priceField = new TextField();
        priceField.setPromptText("New Price");
        
        grid.add(new Label("Product ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    
                    if (productDAO.updateProduct(id, quantity, price)) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
                        refreshProducts();
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showSearchDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Search Product");
        dialog.setHeaderText("🔍 Search by Name or Price Range");
        
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Search by name
        HBox nameBox = new HBox(10);
        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");
        nameField.setPrefWidth(250);
        Button searchByNameBtn = new Button("Search by Name");
        searchByNameBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white;");
        nameBox.getChildren().addAll(nameField, searchByNameBtn);
        
        // Search by price range
        HBox priceBox = new HBox(10);
        TextField minField = new TextField();
        minField.setPromptText("Min Price");
        TextField maxField = new TextField();
        maxField.setPromptText("Max Price");
        Button searchByPriceBtn = new Button("Search by Price");
        searchByPriceBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white;");
        priceBox.getChildren().addAll(minField, maxField, searchByPriceBtn);
        
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(200);
        
        searchByNameBtn.setOnAction(e -> {
            try {
                product p = productDAO.getProductByName(nameField.getText());
                resultArea.setText(p.toString());
            } catch (Exception ex) {
                resultArea.setText("Product not found!");
            }
        });
        
        searchByPriceBtn.setOnAction(e -> {
            try {
                double min = Double.parseDouble(minField.getText());
                double max = Double.parseDouble(maxField.getText());
                List<product> products = productDAO.getProductsByPriceRange(min, max);
                StringBuilder sb = new StringBuilder();
                for (product p : products) {
                    sb.append(p.toString()).append("\n");
                }
                resultArea.setText(sb.toString());
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });
        
        content.getChildren().addAll(new Label("Search by Name:"), nameBox, 
                                     new Label("Search by Price Range:"), priceBox,
                                     new Label("Results:"), resultArea);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    private void generateAndSendReport() {
        List<product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No products available to generate report!");
            return;
        }
        
        // Validate email first
        if (email == null || email.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "❌ Admin email is not available! Please login again.");
            return;
        }
        
        // Create loading dialog with animation
        Alert loadingDialog = new Alert(Alert.AlertType.INFORMATION);
        loadingDialog.setTitle("Generating Report");
        loadingDialog.setHeaderText("⏳ Please wait...");
        loadingDialog.setContentText("Generating inventory report and sending email...");
        loadingDialog.show();
        
        // Run in background thread
        new Thread(() -> {
            try {
                // Generate CSV report
                String filePath = CSVHelper.generateReport(products, username);
                
                Platform.runLater(() -> {
                    loadingDialog.close();
                    
                    if (filePath != null) {
                        // Send email directly without checking env vars (already configured)
                        try {
                            System.out.println("\n📧 Sending email to: " + email.trim());
                            System.out.println("📎 Attaching file: " + filePath);
                            
                            EmailService.sendReport(
                                email.trim(),
                                "📦 Inventory Report - " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()),
                                "Hello " + username + ",\n\n" +
                                "Your inventory report has been generated successfully.\n\n" +
                                "Report Details:\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "📊 Total Products: " + products.size() + "\n" +
                                "📅 Generated On: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n" +
                                "💾 File Location: " + filePath + "\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                                "Please find the detailed inventory report in the attached CSV file.\n\n" +
                                "Best Regards,\n" +
                                "Inventory Management System\n" +
                                "⚡ Automated Report Generator",
                                filePath
                            );
                            
                            showAlert(Alert.AlertType.INFORMATION, "Success", 
                                "✅ Report Generated & Sent Successfully!\n\n" +
                                "📧 Email sent to: " + email + "\n" +
                                "📊 Total products: " + products.size() + "\n" +
                                "💾 Report saved at:\n" + filePath + "\n\n" +
                                "Please check your inbox!");
                                
                        } catch (Exception e) {
                            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                            showAlert(Alert.AlertType.ERROR, "Email Send Failed", 
                                "❌ Failed to send email!\n\n" +
                                "Error Details: " + errorMsg + "\n\n" +
                                "Troubleshooting Steps:\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "1. ✓ Check your internet connection\n" +
                                "2. ✓ Verify MAIL_USER and MAIL_PASS are set\n" +
                                "3. ✓ For Gmail, use App Password (not regular password)\n" +
                                "4. ✓ Check if antivirus/firewall is blocking\n" +
                                "5. ✓ Ensure 2-Step Verification is enabled\n\n" +
                                "💾 Report saved locally at:\n" + filePath);
                            System.err.println("\n❌ Email send error:");
                            e.printStackTrace();
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "❌ Failed to generate report file!");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingDialog.close();
                    showAlert(Alert.AlertType.ERROR, "Error", 
                        "❌ Report generation failed!\n\n" +
                        "Error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }
    
    private void startStockAlertScheduler() {
        StockAlertService alertService = new StockAlertService();
        scheduler = Executors.newScheduledThreadPool(1);
        
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> alertService.checkStockAlerts(username));
        }, 0, 5, TimeUnit.MINUTES);
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showUserInfo() {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("User Information");
        infoAlert.setHeaderText("👤 Admin Profile");
        
        String userInfo = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                      "🔑 Username: " + username + "\n" +
                      "📧 Email: " + email + "\n" +
                      "👑 Role: Administrator\n" +
                      "📅 Session: Active\n" +
                      "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                      "✅ Full access to all features\n" +
                      "✅ Can manage products\n" +
                      "✅ Can generate reports\n" +
                      "✅ Email notifications enabled";
        
        infoAlert.setContentText(userInfo);
        
        // Add animation to dialog
        infoAlert.show();
        
        ScaleTransition st = new ScaleTransition(Duration.millis(200), infoAlert.getDialogPane());
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }
}
