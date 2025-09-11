package com.inventory;

import com.inventory.service.InventoryManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();
        Scanner sc = new Scanner(System.in);

        int choice = 0;
        do {
            System.out.println("\n==== INVENTORY MENU ====");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Update Product");
            System.out.println("4. Search Product by Name");
            System.out.println("5. Display All Products");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1: manager.addProduct(); break;
                    case 2: manager.removeProduct(); break;
                    case 3: manager.updateProduct(); break;
                    case 4: manager.searchProduct(); break;
                    case 5: manager.displayAll(); break;
                    case 6: System.out.println("Exiting..."); break;
                    default: System.out.println("Invalid choice, try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid choice number.");
            }
        } while (choice != 6);
    }
}
