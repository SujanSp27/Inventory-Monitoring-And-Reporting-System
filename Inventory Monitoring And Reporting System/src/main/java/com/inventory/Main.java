package com.inventory;

import com.inventory.service.InventoryManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== INVENTORY MANAGEMENT =====");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Update Product");
            System.out.println("4. Search Product");
            System.out.println("5. View All Products");
            System.out.println("6. Load Products from CSV");
            System.out.println("7. Exit");
            System.out.print("Enter your choice (1-7): ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        manager.addProduct();
                        break;
                    case 2:
                        manager.removeProduct();
                        break;
                    case 3:
                        manager.updateProduct();
                        break;
                    case 4:
                        boolean searching = true;
                        while (searching) {
                            System.out.println("\n===== SEARCH MENU =====");
                            System.out.println("1. Search by ID");
                            System.out.println("2. Search by Name");
                            System.out.println("3. Search by Category");
                            System.out.println("4. View All Products");
                            System.out.println("5. Back");
                            System.out.print("Enter choice: ");

                            try {
                                int searchChoice = Integer.parseInt(sc.nextLine());

                                switch (searchChoice) {
                                    case 1:
                                        manager.searchProductById();
                                        break;
                                    case 2:
                                        manager.searchProductByName();
                                        break;
                                    case 3:
                                        manager.searchProductByCategory();
                                        break;
                                    case 4:
                                        manager.displayAll();
                                        break;
                                    case 5:
                                        searching = false; // exit search menu
                                        break;
                                    default:
                                        System.out.println("Invalid choice! Try again.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Please enter a number!");
                            }
                        }
                        break;

                    case 5:
                        manager.displayAll();
                        break;
                    case 6:
                        manager.loadProductsFromCSV(); // 👈 call CSV load
                        break;
                    case 7:
                        System.out.println("Exiting... Thank you!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please select between 1 and 7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number between 1 and 7.");
            }
        }

        sc.close();
    }
}
