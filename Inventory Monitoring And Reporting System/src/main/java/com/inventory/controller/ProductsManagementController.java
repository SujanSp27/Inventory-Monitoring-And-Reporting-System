package com.inventory.controller;

import com.inventory.DataAccessObject.ProductDAO;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.product;
import com.inventory.util.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductsManagementController {
    
    @FXML private TableView<product> productsTable;
    @FXML private TableColumn<product, Integer> colId;
    @FXML private TableColumn<product, String> colName;
    @FXML private TableColumn<product, String> colCategory;
    @FXML private TableColumn<product, Integer> colQuantity;
    @FXML private TableColumn<product, Double> colPrice;
    @FXML private TableColumn<product, Integer> colThreshold;
    @FXML private TableColumn<product, Double> colStockValue;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbCategoryFilter;
    @FXML private Label lblStatus;
    @FXML private Label lblRecordCount;
    
    private ObservableList<product> products = FXCollections.observableArrayList();
    private ObservableList<product> allProducts = FXCollections.observableArrayList();
    private ProductDAO productDAO;
    private String currentUsername;
    private String currentUserEmail;
    private String currentFilter = "ALL"; // ALL, CATEGORY, PRICE_RANGE, PAGINATED
    private int currentPage = 1;
    private int pageSize = 10;
    
    @FXML
    public void initialize() {
        productDAO = new ProductDAO();
        setupTableColumns();
        setupCategoryFilter();
        loadProducts();
        
        // Enable double-click to view details
        productsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && productsTable.getSelectionModel().getSelectedItem() != null) {
                onViewProductDetails();
            }
        });
    }
    
    private void setupCategoryFilter() {
        // Load all unique categories
        cmbCategoryFilter.getItems().add("All Categories");
        cmbCategoryFilter.setValue("All Categories");
        
        // Load categories from database
        new Thread(() -> {
            try {
                List<product> allProds = productDAO.getAllProducts();
                javafx.application.Platform.runLater(() -> {
                    java.util.Set<String> categories = new java.util.HashSet<>();
                    for (product p : allProds) {
                        if (p.getCategory() != null && !p.getCategory().trim().isEmpty()) {
                            categories.add(p.getCategory());
                        }
                    }
                    cmbCategoryFilter.getItems().addAll(categories);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void setUserInfo(String username, String email) {
        this.currentUsername = username;
        this.currentUserEmail = email;
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colThreshold.setCellValueFactory(new PropertyValueFactory<>("threshold"));
        
        // Stock Value column (calculated)
        colStockValue.setCellValueFactory(cellData -> {
            product p = cellData.getValue();
            return javafx.beans.binding.Bindings.createObjectBinding(() -> 
                p.getQuantity() * p.getPrice()
            );
        });
        
        // Format price column
        colPrice.setCellFactory(column -> new TableCell<product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", price));
                }
            }
        });
        
        // Format stock value column
        colStockValue.setCellFactory(column -> new TableCell<product, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", value));
                }
            }
        });
        
        // Highlight low stock items
        productsTable.setRowFactory(tv -> new TableRow<product>() {
            @Override
            protected void updateItem(product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    int threshold = item.getThreshold() > 0 ? item.getThreshold() : 10;
                    if (item.getQuantity() < threshold) {
                        setStyle("-fx-background-color: #ffe6e6;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }
    
    @FXML
    private void loadProducts() {
        try {
            products.clear();
            allProducts.clear();
            List<product> productList = productDAO.getAllProducts();
            allProducts.addAll(productList);
            products.addAll(productList);
            productsTable.setItems(products);
            updateStatus("✅ Loaded " + products.size() + " products", true);
            
            // Refresh category filter
            javafx.application.Platform.runLater(() -> {
                refreshCategoryFilter();
            });
        } catch (Exception e) {
            updateStatus("❌ Error loading products: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }
    
    private void refreshCategoryFilter() {
        java.util.Set<String> categories = new java.util.HashSet<>();
        categories.add("All Categories");
        for (product p : allProducts) {
            if (p.getCategory() != null && !p.getCategory().trim().isEmpty()) {
                categories.add(p.getCategory());
            }
        }
        String currentSelection = cmbCategoryFilter.getValue();
        cmbCategoryFilter.getItems().clear();
        cmbCategoryFilter.getItems().addAll(categories);
        if (currentSelection != null && cmbCategoryFilter.getItems().contains(currentSelection)) {
            cmbCategoryFilter.setValue(currentSelection);
        } else {
            cmbCategoryFilter.setValue("All Categories");
        }
    }
    
    @FXML
    private void onAddProduct() {
        Dialog<product> dialog = createProductDialog("Add New Product", null);
        dialog.showAndWait().ifPresent(p -> {
            try {
                // Get next available ID
                int nextId = getNextProductId();
                p.setId(nextId);
                
                // Insert into database
                try (Connection conn = dbConnection.getConnection()) {
                    String sql = "INSERT INTO products (id, name, category, quantity, price, threshold) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, p.getId());
                    ps.setString(2, p.getName());
                    ps.setString(3, p.getCategory());
                    ps.setInt(4, p.getQuantity());
                    ps.setDouble(5, p.getPrice());
                    ps.setInt(6, p.getThreshold());
                    ps.executeUpdate();
                    
                    loadProducts();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add product: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void onEditProduct() {
        product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to edit.");
            return;
        }
        
        Dialog<product> dialog = createProductDialog("Edit Product", selected);
        dialog.showAndWait().ifPresent(p -> {
            try {
                try (Connection conn = dbConnection.getConnection()) {
                    String sql = "UPDATE products SET name=?, category=?, quantity=?, price=?, threshold=? WHERE id=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, p.getName());
                    ps.setString(2, p.getCategory());
                    ps.setInt(3, p.getQuantity());
                    ps.setDouble(4, p.getPrice());
                    ps.setInt(5, p.getThreshold());
                    ps.setInt(6, selected.getId());
                    ps.executeUpdate();
                    
                    loadProducts();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update product: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void onDeleteProduct() {
        product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to delete.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Product");
        confirm.setContentText("Are you sure you want to delete:\n" + selected.getName() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    productDAO.removeProduct(selected.getId());
                    loadProducts();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Product deleted successfully!");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete product: " + e.getMessage());
                }
            }
        });
    }
    
    @FXML
    private void onRefresh() {
        loadProducts();
    }
    
    @FXML
    private void onSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        currentFilter = "SEARCH";
        
        if (keyword.isEmpty()) {
            // If search is empty, show all products or apply category filter
            if (cmbCategoryFilter.getValue() != null && !cmbCategoryFilter.getValue().equals("All Categories")) {
                onCategoryFilter();
            } else {
                products.clear();
                products.addAll(allProducts);
                productsTable.setItems(products);
                updateStatus("Showing all " + products.size() + " products", true);
            }
            return;
        }
        
        ObservableList<product> filtered = FXCollections.observableArrayList();
        List<product> sourceList = cmbCategoryFilter.getValue() != null && 
                                  !cmbCategoryFilter.getValue().equals("All Categories") ?
                                  getProductsByCategory(cmbCategoryFilter.getValue()) : allProducts;
        
        for (product p : sourceList) {
            if (p.getName().toLowerCase().contains(keyword) ||
                p.getCategory().toLowerCase().contains(keyword) ||
                String.valueOf(p.getId()).contains(keyword)) {
                filtered.add(p);
            }
        }
        
        products.clear();
        products.addAll(filtered);
        productsTable.setItems(products);
        updateStatus("Found " + filtered.size() + " matching products", true);
    }
    
    @FXML
    private void onCategoryFilter() {
        String selectedCategory = cmbCategoryFilter.getValue();
        currentFilter = "CATEGORY";
        
        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            products.clear();
            products.addAll(allProducts);
            productsTable.setItems(products);
            updateStatus("Showing all " + products.size() + " products", true);
        } else {
            List<product> filtered = getProductsByCategory(selectedCategory);
            products.clear();
            products.addAll(filtered);
            productsTable.setItems(products);
            updateStatus("Showing " + filtered.size() + " products in category: " + selectedCategory, true);
        }
    }
    
    private List<product> getProductsByCategory(String category) {
        try {
            return productDAO.getProductsByCategory(category);
        } catch (Exception e) {
            // If category not found, return empty list
            return new java.util.ArrayList<>();
        }
    }
    
    @FXML
    private void onSearchById() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Product by ID");
        dialog.setHeaderText("Enter Product ID");
        dialog.setContentText("Product ID:");
        
        dialog.showAndWait().ifPresent(idString -> {
            try {
                int id = Integer.parseInt(idString.trim());
                product p = productDAO.getProductById(id);
                
                // Show product in table (filter to show only this product)
                products.clear();
                products.add(p);
                productsTable.setItems(products);
                productsTable.getSelectionModel().select(p);
                productsTable.scrollTo(p);
                updateStatus("✅ Product found: " + p.getName(), true);
                
                // Show details dialog
                showProductDetails(p);
                
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric ID.");
            } catch (ProductNotFoundException e) {
                showAlert(Alert.AlertType.WARNING, "Product Not Found", "No product found with ID: " + idString);
                loadProducts(); // Reload all products
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search product: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void onSearchByPriceRange() {
        Dialog<javafx.util.Pair<Double, Double>> dialog = new Dialog<>();
        dialog.setTitle("Search by Price Range");
        dialog.setHeaderText("Enter Price Range");
        
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField minPriceField = new TextField();
        minPriceField.setPromptText("Minimum Price");
        TextField maxPriceField = new TextField();
        maxPriceField.setPromptText("Maximum Price");
        
        grid.add(new Label("Min Price:"), 0, 0);
        grid.add(minPriceField, 1, 0);
        grid.add(new Label("Max Price:"), 0, 1);
        grid.add(maxPriceField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                try {
                    double min = Double.parseDouble(minPriceField.getText().trim());
                    double max = Double.parseDouble(maxPriceField.getText().trim());
                    if (min > max) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Range", "Minimum price cannot be greater than maximum price.");
                        return null;
                    }
                    return new javafx.util.Pair<>(min, max);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numbers for price range.");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(range -> {
            try {
                List<product> filtered = productDAO.getProductsByPriceRange(range.getKey(), range.getValue());
                currentFilter = "PRICE_RANGE";
                products.clear();
                products.addAll(filtered);
                productsTable.setItems(products);
                updateStatus("Found " + filtered.size() + " products in price range ₹" + 
                    String.format("%.2f", range.getKey()) + " - ₹" + String.format("%.2f", range.getValue()), true);
            } catch (ProductNotFoundException e) {
                showAlert(Alert.AlertType.INFORMATION, "No Products Found", 
                    "No products found in the specified price range.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search products: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void onShowPagination() {
        Dialog<javafx.util.Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Pagination View");
        dialog.setHeaderText("Enter Pagination Details");
        
        ButtonType viewButtonType = new ButtonType("View", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(viewButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField pageField = new TextField("1");
        pageField.setPromptText("Page Number");
        TextField pageSizeField = new TextField("10");
        pageSizeField.setPromptText("Items per Page");
        
        grid.add(new Label("Page Number:"), 0, 0);
        grid.add(pageField, 1, 0);
        grid.add(new Label("Page Size:"), 0, 1);
        grid.add(pageSizeField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == viewButtonType) {
                try {
                    int page = Integer.parseInt(pageField.getText().trim());
                    int size = Integer.parseInt(pageSizeField.getText().trim());
                    if (page < 1 || size < 1) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", "Page number and page size must be greater than 0.");
                        return null;
                    }
                    return new javafx.util.Pair<>(page, size);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numbers.");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(pageInfo -> {
            try {
                currentPage = pageInfo.getKey();
                pageSize = pageInfo.getValue();
                List<product> paginated = productDAO.getProductsPaginated(currentPage, pageSize);
                currentFilter = "PAGINATED";
                products.clear();
                products.addAll(paginated);
                productsTable.setItems(products);
                updateStatus("Showing page " + currentPage + " (" + paginated.size() + " products, " + 
                    pageSize + " per page)", true);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load paginated products: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void onClearFilters() {
        currentFilter = "ALL";
        txtSearch.clear();
        cmbCategoryFilter.setValue("All Categories");
        loadProducts();
        updateStatus("All filters cleared. Showing all products.", true);
    }
    
    @FXML
    private void onViewProductDetails() {
        product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to view details.");
            return;
        }
        showProductDetails(selected);
    }
    
    private void showProductDetails(product p) {
        Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
        detailsAlert.setTitle("Product Details");
        detailsAlert.setHeaderText("Product Information: " + p.getName());
        
        double stockValue = p.getQuantity() * p.getPrice();
        int threshold = p.getThreshold() > 0 ? p.getThreshold() : 10;
        String stockStatus = p.getQuantity() < threshold ? "⚠️ LOW STOCK" : "✅ In Stock";
        
        String details = String.format(
            "Product ID: %d\n" +
            "Name: %s\n" +
            "Category: %s\n" +
            "Quantity: %d\n" +
            "Price: ₹%.2f\n" +
            "Threshold: %d\n" +
            "Stock Value: ₹%.2f\n" +
            "Status: %s\n\n" +
            "%s",
            p.getId(), p.getName(), p.getCategory(), p.getQuantity(),
            p.getPrice(), threshold, stockValue, stockStatus,
            p.getQuantity() < threshold ? 
                "⚠️ Warning: Quantity is below threshold!" : 
                "✅ Stock level is healthy."
        );
        
        detailsAlert.setContentText(details);
        detailsAlert.showAndWait();
    }
    
    private Dialog<product> createProductDialog(String title, product existingProduct) {
        Dialog<product> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Enter Product Details");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
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
        
        if (existingProduct != null) {
            nameField.setText(existingProduct.getName());
            categoryField.setText(existingProduct.getCategory());
            quantityField.setText(String.valueOf(existingProduct.getQuantity()));
            priceField.setText(String.valueOf(existingProduct.getPrice()));
            thresholdField.setText(String.valueOf(existingProduct.getThreshold()));
        }
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Threshold:"), 0, 4);
        grid.add(thresholdField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
        });
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    int threshold = thresholdField.getText().trim().isEmpty() ? 10 : 
                                   Integer.parseInt(thresholdField.getText().trim());
                    
                    return new product(
                        0,
                        nameField.getText().trim(),
                        quantity,
                        price,
                        categoryField.getText().trim(),
                        threshold
                    );
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", 
                        "Please enter valid numbers for Quantity, Price, and Threshold.");
                    return null;
                }
            }
            return null;
        });
        
        return dialog;
    }
    
    private int getNextProductId() throws SQLException {
        try (Connection conn = dbConnection.getConnection()) {
            String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS next_id FROM products";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        }
        return 1;
    }
    
    private void updateStatus(String message, boolean success) {
        lblStatus.setText(message);
        lblStatus.setStyle(success ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
        
        int count = productsTable.getItems().size();
        lblRecordCount.setText(count + " product(s)");
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

