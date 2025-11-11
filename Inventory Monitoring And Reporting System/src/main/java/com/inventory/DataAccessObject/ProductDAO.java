package com.inventory.DataAccessObject;

import com.inventory.model.product;
import com.inventory.util.dbConnection;
import com.inventory.util.ProductDAOHelper;
import com.inventory.exception.ProductNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ✅ Add Product
    public void addProduct(product product) {
        String sql = "INSERT INTO products (id, name, category, quantity, price, threshold) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, product.getId());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getCategory());
            stmt.setInt(4, product.getQuantity());
            stmt.setDouble(5, product.getPrice());
            stmt.setInt(6, product.getThreshold() > 0 ? product.getThreshold() : 10); // Default threshold: 10

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Product added successfully: " + product.getName());
            }

        } catch (SQLException e) {
            // If threshold column doesn't exist, try without it
            if (e.getMessage().contains("threshold")) {
                try (Connection conn = dbConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO products (id, name, category, quantity, price) VALUES (?, ?, ?, ?, ?)")) {
                    stmt.setInt(1, product.getId());
                    stmt.setString(2, product.getName());
                    stmt.setString(3, product.getCategory());
                    stmt.setInt(4, product.getQuantity());
                    stmt.setDouble(5, product.getPrice());
                    stmt.executeUpdate();
                    System.out.println("✅ Product added successfully: " + product.getName());
                } catch (SQLException e2) {
                    System.out.println("❌ Error adding product: " + e2.getMessage());
                }
            } else {
                System.out.println("❌ Error adding product: " + e.getMessage());
            }
        }
    }

    // ✅ Remove Product by ID
    public boolean removeProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new ProductNotFoundException("Product with ID " + id + " not found.");
            }
            System.out.println("✅ Product with ID " + id + " removed.");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error removing product: " + e.getMessage());
        }
        return false;
    }

    // ✅ Update Product (Quantity & Price)
    public boolean updateProduct(int id, int qty, double price) {
        String sql = "UPDATE products SET quantity = ?, price = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, qty);
            stmt.setDouble(2, price);
            stmt.setInt(3, id);
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new ProductNotFoundException("Product with ID " + id + " not found.");
            }

            System.out.println("✅ Product with ID " + id + " updated.");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error updating product: " + e.getMessage());
        }
        return false;
    }

    // ✅ Get Product by Name
    public product getProductByName(String name) {
        String sql = "SELECT * FROM products WHERE name = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int threshold = ProductDAOHelper.getThreshold(rs, 10);
                return new product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        threshold
                );
            } else {
                throw new ProductNotFoundException("Product with name '" + name + "' not found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching product by name: " + e.getMessage());
        }
        return null;
    }

    // ✅ Get Product by ID
    public product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int threshold = ProductDAOHelper.getThreshold(rs, 10);
                return new product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        threshold
                );
            } else {
                throw new ProductNotFoundException("Product with ID " + id + " not found.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching product by ID: " + e.getMessage());
        }
        return null;
    }

    // ✅ Fetch All Products
    public List<product> getAllProducts() {
        List<product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int threshold = ProductDAOHelper.getThreshold(rs, 10);
                list.add(new product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        threshold
                ));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching all products: " + e.getMessage());
        }
        return list;
    }

    // ✅ Get Products by Category
    public List<product> getProductsByCategory(String category) {
        List<product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int threshold = ProductDAOHelper.getThreshold(rs, 10);
                list.add(new product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        threshold
                ));
            }

            if (list.isEmpty()) {
                throw new ProductNotFoundException("No products found in category: " + category);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching products by category: " + e.getMessage());
        }
        return list;
    }

    // ✅ Fetch Products with Pagination
    public List<product> getProductsPaginated(int page, int pageSize) {
        List<product> list = new ArrayList<>();
        String sql = "SELECT * FROM products LIMIT ? OFFSET ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int offset = (page - 1) * pageSize;
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int threshold = ProductDAOHelper.getThreshold(rs, 10);
                list.add(new product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        threshold
                ));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching paginated products: " + e.getMessage());
        }
        return list;
    }

    // ✅ Get Products by Price Range
    public List<product> getProductsByPriceRange(double minPrice, double maxPrice) {
        List<product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int threshold = ProductDAOHelper.getThreshold(rs, 10);
                list.add(new product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        threshold
                ));
            }

            if (list.isEmpty()) {
                throw new ProductNotFoundException("No products found in price range " + minPrice + " - " + maxPrice);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching products by price range: " + e.getMessage());
        }
        return list;
    }
}
