package com.inventory.service;

import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.product;
import com.inventory.DataAccessObject.ProductDAO;
import java.util.List;
import java.util.Scanner;
import com.inventory.service.UserService;
import com.inventory.util.CSVHelper;

public class InventoryManager {
    Scanner sc = new Scanner(System.in);
    ProductDAO dao = new ProductDAO();
    CSVHelper csvDao = new CSVHelper();

    // ✅ Add Product
    public void addProduct() {
        try {
            System.out.println("\n📦 === Add New Product ===");
            System.out.print("🆔 Enter ID: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("📝 Enter Product Name: ");
            String name = sc.nextLine();
            System.out.print("🏷️  Enter Category: ");
            String category = sc.nextLine();
            System.out.print("📊 Enter Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());
            System.out.print("💰 Enter Price: ");
            double price = Double.parseDouble(sc.nextLine());

            product p = new product(id, name, qty, price, category);
            dao.addProduct(p);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input. Please enter correct numbers.");
        }
    }

    // ✅ Remove Product
    public void removeProduct() {
        try {
            System.out.println("\n🗑️ === Remove Product ===");
            System.out.print("🔢 Enter ID to remove: ");
            int id = Integer.parseInt(sc.nextLine());

            dao.removeProduct(id);

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input. Please enter a valid ID.");
        } catch (ProductNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ✅ Update Product
    public void updateProduct() {
        try {
            System.out.println("\n🔄 === Update Product ===");
            System.out.print("🔢 Enter ID to update: ");
            int id = Integer.parseInt(sc.nextLine());
            System.out.print("📦 Enter new Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());
            System.out.print("💰 Enter new Price: ");
            double price = Double.parseDouble(sc.nextLine());

            dao.updateProduct(id, qty, price);

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input. Please enter correct numbers.");
        } catch (ProductNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ✅ Search Product by Name
    public void searchProduct() {
        try {
            System.out.println("\n🔍 === Search Product by Name ===");
            System.out.print("📝 Enter Name: ");
            String name = sc.nextLine();

            product p = dao.getProductByName(name);
            System.out.println("\n✅ Product Found:");
            p.display();
        } catch (ProductNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ✅ Display All Products
    public void displayAll() {
        List<product> products = dao.getAllProducts();
        System.out.println("\n📋 === All Products ===");
        System.out.println("--------------------------------------------------");

        if (products.isEmpty()) {
            System.out.println("📭 No products available.");
            return;
        }

        System.out.printf("%-5s %-15s %-15s %-10s %-10s%n", "ID", "Name", "Category", "Quantity", "Price");
        System.out.println("-----------------------------------------------------------");

        for (product p : products) {
            System.out.printf("%-5d %-15s %-15s %-10d %-10.2f%n",
                    p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
        }
    }

    // ✅ Search Product by ID
    public void searchProductById() {
        try {
            System.out.println("\n🔍 === Search Product by ID ===");
            System.out.print("🔢 Enter ID: ");
            int id = Integer.parseInt(sc.nextLine());

            product p = dao.getProductById(id);
            System.out.println("\n✅ Product Found:");
            System.out.println("--------------------------------------------------");
            p.display();
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input! Please enter a valid ID.");
        } catch (ProductNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ✅ Search Product by Category
    public void searchProductByCategory() {
        try {
            System.out.println("\n📦 === Search Products by Category ===");
            System.out.print("🏷️  Enter Category: ");
            String category = sc.nextLine();

            List<product> products = dao.getProductsByCategory(category);
            System.out.println("\n✅ Products Found:");
            System.out.println("--------------------------------------------------");

            for (product p : products) {
                p.display();
            }
        } catch (ProductNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ✅ Pagination
    public void displayPaginated() {
        try {
            System.out.println("\n📄 === Paginated Product Display ===");
            System.out.print("📃 Enter Page Number: ");
            int page = Integer.parseInt(sc.nextLine());
            System.out.print("📏 Enter Page Size: ");
            int pageSize = Integer.parseInt(sc.nextLine());

            List<product> list = dao.getProductsPaginated(page, pageSize);

            if (list.isEmpty()) {
                System.out.println("📭 No products found for this page.");
            } else {
                System.out.println("\n📄 === Products (Page " + page + ") ===");
                System.out.println("--------------------------------------------------");
                for (product p : list) {
                    p.display();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input! Please enter valid numbers.");
        }
    }

    // ✅ Search by Price Range
    public void searchProductByPriceRange() {
        try {
            System.out.println("\n💸 === Search Products by Price Range ===");
            System.out.print("🔽 Enter Minimum Price: ");
            double min = Double.parseDouble(sc.nextLine());
            System.out.print("🔼 Enter Maximum Price: ");
            double max = Double.parseDouble(sc.nextLine());

            List<product> products = dao.getProductsByPriceRange(min, max);

            System.out.println("\n✅ Products Found:");
            System.out.printf("%-5s %-15s %-15s %-10s %-10s%n", "ID", "Name", "Category", "Quantity", "Price");
            System.out.println("-----------------------------------------------------------");

            for (product p : products) {
                System.out.printf("%-5d %-15s %-15s %-10d %-10.2f%n",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input! Please enter valid numbers.");
        } catch (ProductNotFoundException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }



    public void generateReport(String loggedInEmail) {
        System.out.println("\n🧾 === Generate & Email Inventory Report ===");

        var products = dao.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("⚠️ No products available to generate report!");
            return;
        }

        String filePath = CSVHelper.generateReport(products, "Admin");

        if (filePath != null) {
            System.out.println("📁 Report generated successfully!");
            System.out.println("📂 Saved at: " + filePath);

            try {
                EmailService.sendReport(
                        loggedInEmail,
                        "📦 Inventory Report",
                        "Hello Admin,\n\nPlease find the attached inventory report.\n\nRegards,\nInventory System",
                        filePath
                );
            } catch (Exception e) {
                System.out.println("❌ Failed to send email: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Failed to generate report.");
        }
    }




}
