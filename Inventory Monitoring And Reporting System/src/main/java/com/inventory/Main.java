package com.inventory;

import com.inventory.model.User;
import com.inventory.service.InventoryManager;
import com.inventory.service.UserService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserService userService = new UserService();
        InventoryManager manager = new InventoryManager();

        System.out.println("\n🌟==============================================🌟");
        System.out.println("   🏪 Welcome to Inventory Management System 🏪");
        System.out.println("🌟==============================================🌟");

        boolean running = true;
        while (running) {
            System.out.println("\n📋  Main Menu");
            System.out.println("1️⃣  Login");
            System.out.println("2️⃣  Register");
            System.out.println("3️⃣  Exit");
            System.out.print("👉 Enter your choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> {
                    if (loginUser(sc, userService, manager)) {
                        running = false;
                    }
                }
                case "2" -> registerUser(sc, userService);
                case "3" -> {
                    System.out.println("\n👋 Thank you for using Inventory System! Goodbye!");
                    running = false;
                }
                default -> System.out.println("⚠️ Invalid choice! Please try again.");
            }
        }

        sc.close();
    }

    // ✅ Register New User
    private static void registerUser(Scanner sc, UserService userService) {
        System.out.println("\n📝===== User Registration =====");
        System.out.print("👤 Enter Username: ");
        String username = sc.nextLine();

        System.out.print("🔑 Enter Password: ");
        String password = sc.nextLine();

        System.out.print("⚙️  Enter Role (ADMIN/USER): ");
        String role = sc.nextLine().toUpperCase();

        User user = new User(username, password, role);

        boolean success = userService.register(user);

        if (success) {
            System.out.println("You can now login.");
        } else {
            System.out.println("Try again!");
        }
    }

    // ✅ Login User
    private static boolean loginUser(Scanner sc, UserService userService, InventoryManager manager) {
        System.out.println("\n🔐===== User Login =====");
        System.out.print("👤 Enter Username: ");
        String username = sc.nextLine();

        System.out.print("🔑 Enter Password: ");
        String password = sc.nextLine();

        if (!userService.login(username, password)) {
            System.out.println("🚫 Invalid credentials! Try again.");
            return false;
        }

        String role = userService.getRole(username);
        System.out.println("\n🎉 Welcome, " + username + "! You are logged in as: " + role);

        if (role.equalsIgnoreCase("ADMIN")) {
            adminMenu(manager, sc);
        } else {
            userMenu(manager, sc);
        }
        return true;
    }

    // ✅ Admin Menu
    private static void adminMenu(InventoryManager manager, Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n🧑‍💼===== ADMIN MENU =====");
            System.out.println("-------------------------------------------------");
            System.out.println("1️⃣  Add Product");
            System.out.println("2️⃣  Remove Product");
            System.out.println("3️⃣  Update Product");
            System.out.println("4️⃣  View All Products");
            System.out.println("5️⃣  Search Product");
            System.out.println("6️⃣  Generate Report and Send via Email");
            System.out.println("7️⃣  Pagination View");
            System.out.println("8️⃣  Search by Price Range");
            System.out.println("9️⃣  Logout");
            System.out.println("-------------------------------------------------");
            System.out.print("👉 Enter your choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1 -> manager.addProduct();
                    case 2 -> manager.removeProduct();
                    case 3 -> manager.updateProduct();
                    case 4 -> manager.displayAll();
                    case 5 -> manager.searchProduct();
                    case 6 -> manager.generateReport();
                    case 7 -> manager.displayPaginated();
                    case 8 -> manager.searchProductByPriceRange();
                    case 9 -> {
                        System.out.println("\n🔓 Logging out... Returning to main menu.");
                        running = false;
                    }
                    default -> System.out.println("⚠️ Invalid choice! Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number!");
            }
        }
    }

    // ✅ User Menu
    private static void userMenu(InventoryManager manager, Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n🙍‍♂️===== USER MENU =====");
            System.out.println("-------------------------------------------------");
            System.out.println("1️⃣  View All Products");
            System.out.println("2️⃣  Search Product");
            System.out.println("3️⃣  View Products with Pagination");
            System.out.println("4️⃣  Search by Price Range");
            System.out.println("5️⃣  Logout");
            System.out.println("-------------------------------------------------");
            System.out.print("👉 Enter your choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1 -> manager.displayAll();
                    case 2 -> manager.searchProduct();
                    case 3 -> manager.displayPaginated();
                    case 4 -> manager.searchProductByPriceRange();
                    case 5 -> {
                        System.out.println("\n🔓 Logging out... Returning to main menu.");
                        running = false;
                    }
                    default -> System.out.println("⚠️ Invalid choice! Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number!");
            }
        }
    }
}
