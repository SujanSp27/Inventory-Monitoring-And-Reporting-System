package com.inventory.controller;

import com.inventory.DataAccessObject.ProductDAO;
import com.inventory.model.product;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class UserController {
    private String username;
    private ProductDAO productDAO = new ProductDAO();
    private TableView<product> productTable;
    private ObservableList<product> productList = FXCollections.observableArrayList();
    private Label totalProductsLabel;
    private Label totalCategoriesLabel;
    
    public UserController(String username) {
        this.username = username;
    }
    
    public void showUserDashboard(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");
        
        // Top bar
        HBox topBar = createTopBar(stage);
        root.setTop(topBar);
        
        // Left sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);
        
        // Center content
        VBox centerContent = createDashboardContent();
        root.setCenter(centerContent);
        
        Scene scene = new Scene(root, 1400, 800);
        stage.setScene(scene);
        
        // Load products
        refreshProducts();
    }
    
    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #2ecc71, #27ae60); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("📦 Inventory Management System - User Panel");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("👤 " + username);
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        userLabel.setTextFill(Color.WHITE);
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            LoginController loginController = new LoginController();
            loginController.showLoginScene(stage);
        });
        
        topBar.getChildren().addAll(title, spacer, userLabel, logoutBtn);
        return topBar;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #34495e; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 2, 0);");
        
        Label menuTitle = new Label("MENU");
        menuTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        menuTitle.setTextFill(Color.web("#bdc3c7"));
        menuTitle.setPadding(new Insets(0, 0, 10, 0));
        
        Button dashboardBtn = createMenuButton("📊 Dashboard");
        Button viewAllBtn = createMenuButton("📄 View All Products");
        Button searchBtn = createMenuButton("🔍 Search Product");
        Button priceRangeBtn = createMenuButton("💰 Search by Price");
        Button categoryBtn = createMenuButton("📚 View by Category");
        Button refreshBtn = createMenuButton("🔄 Refresh");
        
        viewAllBtn.setOnAction(e -> refreshProducts());
        searchBtn.setOnAction(e -> showSearchDialog());
        priceRangeBtn.setOnAction(e -> showPriceRangeDialog());
        categoryBtn.setOnAction(e -> showCategoryDialog());
        refreshBtn.setOnAction(e -> refreshProducts());
        
        sidebar.getChildren().addAll(menuTitle, dashboardBtn, viewAllBtn, searchBtn, 
                                     priceRangeBtn, categoryBtn, refreshBtn);
        
        return sidebar;
    }
    
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 5;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; -fx-font-size: 14; -fx-cursor: hand; -fx-background-radius: 5;"));
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
        VBox card1 = createStatCard("Available Products", totalProductsLabel, "#3498db");
        
        totalCategoriesLabel = new Label("0");
        VBox card2 = createStatCard("Total Categories", totalCategoriesLabel, "#9b59b6");
        
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        
        statsBox.getChildren().addAll(card1, card2);
        return statsBox;
    }
    
    private VBox createStatCard(String title, Label valueLabel, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.web("#7f8c8d"));
        
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        valueLabel.setTextFill(Color.web(color));
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private VBox createProductTable() {
        VBox container = new VBox(15);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        container.setPadding(new Insets(20));
        
        Label tableTitle = new Label("Product Catalog");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        tableTitle.setTextFill(Color.web("#2c3e50"));
        
        productTable = new TableView<>();
        productTable.setItems(productList);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        idCol.setPrefWidth(60);
        
        TableColumn<product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(250);
        
        TableColumn<product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        categoryCol.setPrefWidth(150);
        
        TableColumn<product, Integer> quantityCol = new TableColumn<>("Available Qty");
        quantityCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        quantityCol.setPrefWidth(120);
        
        TableColumn<product, Double> priceCol = new TableColumn<>("Price ($)");
        priceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        priceCol.setPrefWidth(100);
        
        productTable.getColumns().addAll(idCol, nameCol, categoryCol, quantityCol, priceCol);
        
        VBox.setVgrow(productTable, Priority.ALWAYS);
        container.getChildren().addAll(tableTitle, productTable);
        
        return container;
    }
    
    private void refreshProducts() {
        List<product> products = productDAO.getAllProducts();
        productList.clear();
        productList.addAll(products);
        
        // Update statistics
        totalProductsLabel.setText(String.valueOf(products.size()));
        
        long categories = products.stream().map(product::getCategory).distinct().count();
        totalCategoriesLabel.setText(String.valueOf(categories));
    }
    
    private void showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Product");
        dialog.setHeaderText("🔍 Search Product by Name");
        dialog.setContentText("Enter product name:");
        
        dialog.showAndWait().ifPresent(name -> {
            try {
                product p = productDAO.getProductByName(name);
                productList.clear();
                productList.add(p);
                showAlert(Alert.AlertType.INFORMATION, "Product Found", p.toString());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Product not found!");
            }
        });
    }
    
    private void showPriceRangeDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Search by Price Range");
        dialog.setHeaderText("💰 Enter Price Range");
        
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField minField = new TextField();
        minField.setPromptText("Min Price");
        TextField maxField = new TextField();
        maxField.setPromptText("Max Price");
        
        grid.add(new Label("Minimum Price:"), 0, 0);
        grid.add(minField, 1, 0);
        grid.add(new Label("Maximum Price:"), 0, 1);
        grid.add(maxField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                try {
                    double min = Double.parseDouble(minField.getText());
                    double max = Double.parseDouble(maxField.getText());
                    List<product> products = productDAO.getProductsByPriceRange(min, max);
                    productList.clear();
                    productList.addAll(products);
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Found " + products.size() + " products in range $" + min + " - $" + max);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid price range!");
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showCategoryDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search by Category");
        dialog.setHeaderText("📚 View Products by Category");
        dialog.setContentText("Enter category:");
        
        dialog.showAndWait().ifPresent(category -> {
            try {
                List<product> products = productDAO.getProductsByCategory(category);
                productList.clear();
                productList.addAll(products);
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    "Found " + products.size() + " products in category: " + category);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "No products found in this category!");
            }
        });
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
