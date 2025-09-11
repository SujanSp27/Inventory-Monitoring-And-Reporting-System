package com.inventory.service;

import com.inventory.model.product;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class InventoryManager {
    Map<Integer, product> products = new HashMap<>();
    Scanner sc = new Scanner(System.in);

    public void addProduct() {
        try {
            System.out.print("Enter ID: ");
            int id = Integer.parseInt(sc.nextLine());

            if (products.containsKey(id)) {
                System.out.println("Product ID already exists.");
                return;
            }

            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Category: ");
            String category = sc.nextLine();

            System.out.print("Enter Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Price: ");
            double price = Double.parseDouble(sc.nextLine());

            product p = new product(id, name, qty, price, category);
            products.put(id, p);
            System.out.println("Product Added");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter correct numbers.");
        }
    }

    public void removeProduct() {
        try {
            System.out.print("Enter ID to remove: ");
            int id = Integer.parseInt(sc.nextLine());

            if (products.remove(id) != null) {
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

            product p = products.get(id);
            if (p != null) {
                System.out.print("Enter new Quantity: ");
                int qty = Integer.parseInt(sc.nextLine());
                System.out.print("Enter new Price: ");
                double price = Double.parseDouble(sc.nextLine());

                p.setQuantity(qty);
                p.setPrice(price);
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

        boolean found = false;
        for (product p : products.values()) {
            if (p.getName().equalsIgnoreCase(name)) {
                p.display();
                found = true;
            }
        }
        if (!found) {
            System.out.println("Product not found");
        }
    }

    public void displayAll() {
        if (products.isEmpty()) {
            System.out.println("No products available");
        } else {
            for (product p : products.values()) {
                p.display();
            }
        }
    }
}
