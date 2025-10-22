package com.inventory.service;
import com.inventory.service.EmailService;
import com.inventory.util.dbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StockAlertService {

    private final EmailService emailService = new EmailService();


    public void checkStockAlerts() {
        List<String> lowStockProducts = getLowStockProducts();

        if (!lowStockProducts.isEmpty()) {
            String subject = "⚠️ Low Stock Alert: Action Needed!";
            StringBuilder message = new StringBuilder();
            message.append("Dear Admin,\n\n");
            message.append("The following products are running low on stock:\n\n");

            for (String product : lowStockProducts) {
                message.append("• ").append(product).append("\n");
            }

            message.append("\nPlease restock them soon to avoid shortages.\n\n");
            message.append("~ Inventory Management System");

            // ✅ Replace with your admin email
            String adminEmail = "admin@gmail.com";
            emailService.sendEmail(adminEmail, subject, message.toString());

            System.out.println("📧 Stock alert email sent to admin.");
        } else {
            System.out.println("✅ All stock levels are sufficient.");
        }
    }


    private List<String> getLowStockProducts() {
        List<String> products = new ArrayList<>();
        String query = "SELECT name, quantity, threshold FROM product WHERE quantity < threshold";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String productName = rs.getString("name");
                int qty = rs.getInt("quantity");
                int threshold = rs.getInt("threshold");
                products.add(productName + " (Qty: " + qty + " / Threshold: " + threshold + ")");
            }

        } catch (Exception e) {
            System.out.println("❌ Error checking stock alerts: " + e.getMessage());
        }

        return products;
    }
}
