package com.inventory.service;

import com.inventory.model.product;
import com.inventory.DataAccessObject.ProductDAO;
import java.util.List;
import java.util.Scanner;

public class InventoryManager {
    Scanner sc = new Scanner(System.in);
    ProductDAO dao = new ProductDAO();

    public void addProduct() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Category: ");
            String category = sc.nextLine();

            System.out.print("Enter Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Price: ");
            double price = Double.parseDouble(sc.nextLine());

            product p = new product(id, name, qty, price, category);

            dao.addProduct(p);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter correct numbers.");
        }
    }

    public void removeProduct() {
        try {
            System.out.print("Enter ID to remove: ");
            int id = Integer.parseInt(sc.nextLine());

            boolean removed = dao.removeProduct(id);
            if (removed) {
                System.out.println("Product Removed");
            } else {
                System.out.println("Product not found");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid ID.");
        }
    }

    public void updateProduct() {
        try {
            System.out.print("Enter ID to update: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Enter new Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());
            System.out.print("Enter new Price: ");
            double price = Double.parseDouble(sc.nextLine());

            boolean updated = dao.updateProduct(id, qty, price); // ✅ Call DAO
            if (updated) {
                System.out.println("Product Updated");
            } else {
                System.out.println("Product not found");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter correct numbers.");
        }
    }

    public void searchProduct() {
        System.out.print("Enter Name to search: ");
        String name = sc.nextLine();

        product p = dao.getProductByName(name);
        if (p != null) {
            p.display();
        } else {
            System.out.println("Product not found");
        }
    }

    public void displayAll() {
        List<product> list = dao.getAllProducts();
        if (list.isEmpty()) {
            System.out.println("No products available.");
        } else {
            for (product p : list) {
                p.display();
            }
        }
    }


}
