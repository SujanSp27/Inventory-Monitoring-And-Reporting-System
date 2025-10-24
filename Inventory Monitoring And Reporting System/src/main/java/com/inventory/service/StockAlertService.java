package com.inventory.service;

import com.inventory.util.dbConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StockAlertService {

    private final EmailService emailService = new EmailService();
    private List<String> lastAlertedProducts = new ArrayList<>();

    public void checkStockAlerts(String adminUsername) {
        List<String> lowStockProducts = getLowStockProducts();

        if (!lowStockProducts.isEmpty() && !lowStockProducts.equals(lastAlertedProducts)) {

            String subject = "⚠️ Low Stock Alert: Action Needed!";
            StringBuilder message = new StringBuilder();
            message.append("Dear Admin,\n\n");
            message.append("The following products are running low on stock:\n\n");

            for (String product : lowStockProducts) {
                message.append("• ").append(product).append("\n");
            }

            message.append("\nPlease restock them soon to avoid shortages.\n\n");
            message.append("~ Inventory Management System");

            String adminEmail = getAdminEmail(adminUsername);

            if (adminEmail != null) {
                EmailService.sendReport(adminEmail, subject, message.toString(), null);
                lastAlertedProducts = new ArrayList<>(lowStockProducts); // ✅ Update last alerted list
            } else {
                System.out.println("⚠️ No verified admin found for username: " + adminUsername);
            }

        } else if (lowStockProducts.isEmpty()) {

            lastAlertedProducts.clear();
            System.out.println("\n✅ All stock levels are sufficient.");
        } else {

            System.out.println("📧 Mail already sent for these low-stock products. No new email needed.");
        }
    }

    private String getAdminEmail(String username) {
        String query = "SELECT email FROM users WHERE username = ? AND role = 'ADMIN' AND is_verified = 1";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error fetching admin email: " + e.getMessage());
        }
        return null;
    }

    private List<String> getLowStockProducts() {
        List<String> products = new ArrayList<>();
        String query = "SELECT name, quantity, threshold FROM products WHERE quantity < threshold";

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
