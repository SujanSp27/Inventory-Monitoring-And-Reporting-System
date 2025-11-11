package com.inventory;

import com.inventory.model.product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;

public class InventoryController {

    @FXML private TableView<product> tableView;
    @FXML private TableColumn<product, Integer> colId;
    @FXML private TableColumn<product, String> colName;
    @FXML private TableColumn<product, String> colCategory;
    @FXML private TableColumn<product, Integer> colQuantity;
    @FXML private TableColumn<product, Double> colPrice;
    @FXML private TextField txtSearch;
    @FXML private Label lblStatus;

    private final ObservableList<product> products = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tableView.setItems(products);
        lblStatus.setText("No products yet — click 'Add Product' to start!");

        txtSearch.textProperty().addListener((obs, old, newVal) -> filterProducts(newVal));
    }

    // ---------- BUTTON ACTIONS ----------

    @FXML
    private void onAdd() {
        showAddProductDialog();
    }

    @FXML
    private void onUpdate() {
        product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Please select a product to update!");
            return;
        }
        showEditDialog(selected);
    }

    @FXML
    private void onRemove() {
        product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Please select a product to remove!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Confirmation");
        confirm.setHeaderText("Remove " + selected.getName() + "?");
        confirm.setContentText("This action cannot be undone.");
        if (confirm.showAndWait().get() == ButtonType.OK) {
            products.remove(selected);
            updateStatus();
        }
    }

    @FXML
    private void onExit() {
        Alert exit = new Alert(Alert.AlertType.CONFIRMATION);
        exit.setHeaderText("Exit Application?");
        exit.setContentText("Are you sure you want to close the app?");
        if (exit.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) tableView.getScene().getWindow();
            stage.close();
        }
    }

    // ---------- DIALOGS ----------

    private void showAddProductDialog() {
        Dialog<product> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        dialog.initStyle(StageStyle.UTILITY);

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField categoryField = new TextField();
        TextField qtyField = new TextField();
        TextField priceField = new TextField();

        VBox box = new VBox(10,
                new Label("ID:"), idField,
                new Label("Name:"), nameField,
                new Label("Category:"), categoryField,
                new Label("Quantity:"), qtyField,
                new Label("Price:"), priceField);
        box.setStyle("-fx-padding: 15; -fx-background-color: #fdfdfd;");
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    int qty = Integer.parseInt(qtyField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    return new product(id, nameField.getText(), qty, price, categoryField.getText());
                } catch (Exception e) {
                    showAlert("Invalid input! Please check your values.");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(p -> {
            products.add(p);
            updateStatus();
        });
    }

    private void showEditDialog(product p) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Product: " + p.getName());
        dialog.initStyle(StageStyle.UTILITY);

        TextField qtyField = new TextField(String.valueOf(p.getQuantity()));
        TextField priceField = new TextField(String.valueOf(p.getPrice()));

        VBox box = new VBox(10,
                new Label("New Quantity:"), qtyField,
                new Label("New Price:"), priceField);
        box.setStyle("-fx-padding: 15; -fx-background-color: #fdfdfd;");
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    p.setQuantity(Integer.parseInt(qtyField.getText()));
                    p.setPrice(Double.parseDouble(priceField.getText()));
                    tableView.refresh();
                    updateStatus();
                } catch (NumberFormatException e) {
                    showAlert("Invalid input format!");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // ---------- UTILS ----------

    private void filterProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            tableView.setItems(products);
            updateStatus();
            return;
        }
        ObservableList<product> filtered = FXCollections.observableArrayList();
        for (product p : products) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    p.getCategory().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(p);
            }
        }
        tableView.setItems(filtered);
        lblStatus.setText(filtered.size() + " match(es) found.");
    }

    private void updateStatus() {
        lblStatus.setText(products.size() + " total product(s).");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
