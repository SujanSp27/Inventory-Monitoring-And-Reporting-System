package com.inventory.controller;

import com.inventory.DataAccessObject.ProductDAO;
import com.inventory.model.product;
import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.util.List;

public class UserController {
    private String username;
    private String email;
    private ProductDAO productDAO = new ProductDAO();
    private TableView<product> productTable;
    private ObservableList<product> productList = FXCollections.observableArrayList();
    private Label totalProductsLabel;
    private Label totalCategoriesLabel;
    
    public UserController(String username) {
        this.username = username;
        this.email = ""; // Default empty email
    }
    
    public UserController(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    public void showUserDashboard(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a2e 0%, #16213e 50%, #0f3460 100%);");
        
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
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        
        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        // Load products
        refreshProducts();
    }
    
    private HBox createTopBar(Stage stage) {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #0a0e27 0%, #1a1a2e 100%); " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.3), 15, 0.3, 0, 5); " +
                        "-fx-border-color: #00d4ff; " +
                        "-fx-border-width: 0 0 2 0; " +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent; " +
                        "-fx-background-insets: 0; " +  // Remove any dotted borders
                        "-fx-border-insets: 0;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        // Add entrance animation with slide effect
        topBar.setOpacity(0);
        topBar.setTranslateY(-20);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), topBar);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), topBar);
        slideIn.setFromY(-20);
        slideIn.setToY(0);
        
        ParallelTransition entrance = new ParallelTransition(fadeIn, slideIn);
        entrance.play();
        
        Label title = new Label("⚡ Inventory Management System - User Panel");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#00d4ff"));
        
        // Add glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#00d4ff", 0.8));
        glow.setRadius(10);
        title.setEffect(glow);
        
        // Add pulsing animation to title
        Timeline titlePulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(title.opacityProperty(), 1)),
            new KeyFrame(Duration.millis(1000), new KeyValue(title.opacityProperty(), 0.8)),
            new KeyFrame(Duration.millis(2000), new KeyValue(title.opacityProperty(), 1))
        );
        titlePulse.setCycleCount(Timeline.INDEFINITE);
        titlePulse.play();
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Interactive user badge
        HBox userBadge = new HBox(8);
        userBadge.setAlignment(Pos.CENTER);
        userBadge.setPadding(new Insets(8, 15, 8, 15));
        userBadge.setStyle("-fx-background-color: rgba(0, 212, 255, 0.15); " +
                          "-fx-background-radius: 20; " +
                          "-fx-border-color: #00d4ff; " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 20; " +
                          "-fx-cursor: hand; " +
                          "-fx-focus-color: transparent; " +
                          "-fx-faint-focus-color: transparent;");
        
        // Enhanced user icon with better visibility
        Label userIcon = new Label("👤");
        userIcon.setFont(Font.font("System", FontWeight.BOLD, 20));
        userIcon.setTextFill(Color.web("#00d4ff")); // Neon blue for better visibility
        
        // Add glow effect to icon
        DropShadow iconGlow = new DropShadow();
        iconGlow.setColor(Color.web("#00d4ff", 0.8));
        iconGlow.setRadius(8);
        userIcon.setEffect(iconGlow);
        
        Label userLabel = new Label(username);
        userLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        userLabel.setTextFill(Color.web("#00d4ff"));
        
        userBadge.getChildren().addAll(userIcon, userLabel);
        
        // Simplified hover effect without animations
        userBadge.setOnMouseEntered(e -> {
            userBadge.setStyle("-fx-background-color: rgba(0, 212, 255, 0.3); " +
                              "-fx-background-radius: 20; " +
                              "-fx-border-color: #00d4ff; " +
                              "-fx-border-width: 2; " +
                              "-fx-border-radius: 20; " +
                              "-fx-cursor: hand; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,212,255,0.8), 20, 0.5, 0, 0); " +
                              "-fx-focus-color: transparent; " +
                              "-fx-faint-focus-color: transparent;");
        });
        
        userBadge.setOnMouseExited(e -> {
            userBadge.setStyle("-fx-background-color: rgba(0, 212, 255, 0.15); " +
                              "-fx-background-radius: 20; " +
                              "-fx-border-color: #00d4ff; " +
                              "-fx-border-width: 1; " +
                              "-fx-border-radius: 20; " +
                              "-fx-cursor: hand; " +
                              "-fx-focus-color: transparent; " +
                              "-fx-faint-focus-color: transparent;");
        });
        
        // Simplified click effect without animations
        userBadge.setOnMouseClicked(e -> {
            showUserInfo();
        });
        
        Button logoutBtn = new Button("🚪 Logout");
        logoutBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b); " +
                          "-fx-text-fill: white; " +
                          "-fx-font-weight: bold; " +
                          "-fx-background-radius: 8; " +
                          "-fx-padding: 10 20; " +
                          "-fx-cursor: hand; " +
                          "-fx-focus-color: transparent; " +
                          "-fx-faint-focus-color: transparent;");
        
        // Enhanced hover animation for logout button
        logoutBtn.setOnMouseEntered(e -> {
            logoutBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #ff6b6b, #ff4757); " +
                              "-fx-text-fill: white; " +
                              "-fx-font-weight: bold; " +
                              "-fx-background-radius: 8; " +
                              "-fx-padding: 10 20; " +
                              "-fx-cursor: hand; " +
                              "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.8), 15, 0.5, 0, 0); " +
                              "-fx-focus-color: transparent; " +
                              "-fx-faint-focus-color: transparent;");
            
            // Scale and glow animation
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), logoutBtn);
            scale.setToX(1.05);
            scale.setToY(1.05);
            
            // Pulse effect
            Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(logoutBtn.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(200), new KeyValue(logoutBtn.opacityProperty(), 0.8)),
                new KeyFrame(Duration.millis(400), new KeyValue(logoutBtn.opacityProperty(), 1))
            );
            
            ParallelTransition parallel = new ParallelTransition(scale, pulse);
            parallel.play();
        });
        
        logoutBtn.setOnMouseExited(e -> {
            logoutBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b); " +
                              "-fx-text-fill: white; " +
                              "-fx-font-weight: bold; " +
                              "-fx-background-radius: 8; " +
                              "-fx-padding: 10 20; " +
                              "-fx-cursor: hand; " +
                              "-fx-focus-color: transparent; " +
                              "-fx-faint-focus-color: transparent;");
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), logoutBtn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        logoutBtn.setOnAction(e -> {
            // Enhanced fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            // Add scale transition for smooth exit
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(500), stage.getScene().getRoot());
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.95);
            scaleOut.setToY(0.95);
            
            ParallelTransition exitTransition = new ParallelTransition(fadeOut, scaleOut);
            exitTransition.setOnFinished(event -> {
                LoginController loginController = new LoginController();
                loginController.showLoginScene(stage);
            });
            exitTransition.play();
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
                        "-fx-border-color: rgba(0, 212, 255, 0.3); " +
                        "-fx-border-width: 0 2 0 0; " +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent; " +
                        "-fx-background-insets: 0; " +  // Remove any dotted borders
                        "-fx-border-insets: 0;");
        
        // Add entrance animation with slide and fade effect
        sidebar.setOpacity(0);
        sidebar.setTranslateX(-220);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), sidebar);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideInSidebar = new TranslateTransition(Duration.millis(500), sidebar);
        slideInSidebar.setFromX(-220);
        slideInSidebar.setToX(0);
        
        ParallelTransition entrance = new ParallelTransition(fadeIn, slideInSidebar);
        entrance.setDelay(Duration.millis(300)); // Slight delay for better visual effect
        entrance.play();
        
        Label menuTitle = new Label("✨ USER MENU");
        menuTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        menuTitle.setTextFill(Color.web("#00d4ff"));
        menuTitle.setPadding(new Insets(0, 0, 10, 0));
        
        // Add glow to title
        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.web("#00d4ff", 0.6));
        titleGlow.setRadius(8);
        menuTitle.setEffect(titleGlow);
        
        // Add pulsing animation to menu title
        Timeline titlePulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(menuTitle.opacityProperty(), 1)),
            new KeyFrame(Duration.millis(1500), new KeyValue(menuTitle.opacityProperty(), 0.7)),
            new KeyFrame(Duration.millis(3000), new KeyValue(menuTitle.opacityProperty(), 1))
        );
        titlePulse.setCycleCount(Timeline.INDEFINITE);
        titlePulse.play();
        
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
        
        // Slide in animation
        sidebar.setTranslateX(-220);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), sidebar);
        slideIn.setFromX(-220);
        slideIn.setToX(0);
        slideIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
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
                    "-fx-background-radius: 8; " +
                    "-fx-focus-color: transparent; " +
                    "-fx-faint-focus-color: transparent; " +
                    "-fx-background-insets: 0; " +  // Remove any dotted borders
                    "-fx-border-insets: 0;");
        
        // Enhanced hover animation with multiple effects
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: rgba(0, 212, 255, 0.3); " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-font-size: 14; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8; " +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent; " +
                        "-fx-background-insets: 0; " +
                        "-fx-border-insets: 0;");
            
            // Create a more complex animation sequence
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
            scale.setToX(1.02);
            scale.setToY(1.02);
            
            // Add glow effect
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#00d4ff", 0.6));
            glow.setRadius(10);
            glow.setOffsetX(0);
            glow.setOffsetY(0);
            btn.setEffect(glow);
            
            // Translate transition for slide effect
            TranslateTransition slide = new TranslateTransition(Duration.millis(150), btn);
            slide.setToX(5);
            
            ParallelTransition parallel = new ParallelTransition(scale, slide);
            parallel.play();
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: #e0e6ed; " +
                        "-fx-font-size: 14; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8; " +
                        "-fx-focus-color: transparent; " +
                        "-fx-faint-focus-color: transparent; " +
                        "-fx-background-insets: 0; " +
                        "-fx-border-insets: 0;");
            
            // Remove glow effect
            btn.setEffect(null);
            
            // Return to normal
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            
            // Return to original position
            TranslateTransition slide = new TranslateTransition(Duration.millis(150), btn);
            slide.setToX(0);
            
            ParallelTransition parallel = new ParallelTransition(scale, slide);
            parallel.play();
        });
        
        // Add click animation
        btn.setOnMousePressed(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
            scale.setToX(0.98);
            scale.setToY(0.98);
            scale.play();
        });
        
        btn.setOnMouseReleased(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
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
        
        // Add glow effect
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
        
        // Initial fade in
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
        
        Label tableTitle = new Label("📦 Product Inventory");
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
                             "-fx-control-inner-background: rgba(26, 32, 44, 0.4); " +
                             "-fx-table-cell-border-color: rgba(102, 126, 234, 0.2);");
        
        // Style table headers and cells to match admin panel
        productTable.setRowFactory(tv -> {
            TableRow<product> row = new TableRow<product>() {
                @Override
                protected void updateItem(product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else {
                        // Alternate row colors like in the image
                        if (getIndex() % 2 == 0) {
                            setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                                   "-fx-text-fill: white;");
                        } else {
                            setStyle("-fx-background-color: rgba(0, 212, 255, 0.15); " +
                                   "-fx-text-fill: white;");
                        }
                    }
                }
            };
            
            // Hover effect
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: rgba(0, 212, 255, 0.25); " +
                               "-fx-text-fill: white; " +
                               "-fx-cursor: hand;");
                }
            });
            
            row.setOnMouseExited(e -> {
                if (!row.isEmpty()) {
                    if (row.getIndex() % 2 == 0) {
                        row.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); " +
                                   "-fx-text-fill: white;");
                    } else {
                        row.setStyle("-fx-background-color: rgba(0, 212, 255, 0.15); " +
                                   "-fx-text-fill: white;");
                    }
                }
            });
            
            return row;
        });
        
        TableColumn<product, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        idCol.setPrefWidth(60);
        idCol.setStyle("-fx-text-fill: white; -fx-alignment: CENTER;");
        
        TableColumn<product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-text-fill: white;");
        
        TableColumn<product, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        categoryCol.setPrefWidth(150);
        categoryCol.setStyle("-fx-text-fill: white;");
        
        TableColumn<product, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        quantityCol.setPrefWidth(100);
        quantityCol.setStyle("-fx-text-fill: white; -fx-alignment: CENTER;");
        
        TableColumn<product, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        priceCol.setPrefWidth(100);
        priceCol.setStyle("-fx-text-fill: white; -fx-alignment: CENTER;");
        
        TableColumn<product, Integer> thresholdCol = new TableColumn<>("Threshold");
        thresholdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getThreshold()).asObject());
        thresholdCol.setPrefWidth(100);
        thresholdCol.setStyle("-fx-text-fill: white; -fx-alignment: CENTER;");
        
        TableColumn<product, Double> valueCol = new TableColumn<>("Stock Value");
        valueCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().stockValue()).asObject());
        valueCol.setPrefWidth(120);
        valueCol.setStyle("-fx-text-fill: white; -fx-alignment: CENTER;");
        
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
        // Animate table during refresh
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), productTable);
        fadeOut.setToValue(0.3);
        fadeOut.setOnFinished(e -> {
            List<product> products = productDAO.getAllProducts();
            productList.clear();
            productList.addAll(products);
            
            // Update statistics
            totalProductsLabel.setText(String.valueOf(products.size()));
            
            long categories = products.stream().map(product::getCategory).distinct().count();
            totalCategoriesLabel.setText(String.valueOf(categories));
            
            // Fade back in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), productTable);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
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
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            
            FadeTransition fade = new FadeTransition(Duration.millis(200), alert.getDialogPane());
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        });
        
        alert.showAndWait();
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
        
        // Style buttons
        Node okButton = dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8;");
        }
        
        alert.showAndWait();
    }
    
    private void showUserInfo() {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("User Information");
        infoAlert.setHeaderText("👤 User Profile");
        
        String userInfo = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                      "🔑 Username: " + username + "\n" +
                      "📧 Email: " + email + "\n" +
                      "👥 Role: User\n" +
                      "📅 Session: Active\n" +
                      "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                      "✅ View product information\n" +
                      "✅ Search products\n" +
                      "✅ View by category\n" +
                      "✅ Email notifications enabled";
        
        infoAlert.setContentText(userInfo);
        
        // Apply dark theme with visible neon text
        DialogPane dialogPane = infoAlert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1a1a2e; " +
                           "-fx-border-color: #00d4ff; " +
                           "-fx-border-width: 2; " +
                           "-fx-border-radius: 10; " +
                           "-fx-background-radius: 10;");
        
        // Style the content text to be visible with neon color
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #00d4ff; " +
                                                     "-fx-font-size: 14px; " +
                                                     "-fx-font-weight: bold;");
        
        // Style buttons
        Node okButton = dialogPane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8;");
        }
        
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
