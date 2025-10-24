package com.inventory;

import com.inventory.model.User;
import com.inventory.service.InventoryManager;
import com.inventory.service.UserService;
import com.inventory.service.OTPService;
import com.inventory.service.StockAlertService;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
            System.out.println("\n📋 Main Menu");
            System.out.println("1️⃣  Login");
            System.out.println("2️⃣  Register");
            System.out.println("3️⃣  Verify Email");
            System.out.println("4️⃣  Exit");
            System.out.print("👉 Enter your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> loginUser(sc, userService, manager);
                case "2" -> registerUser(sc, userService);
                case "3" -> verifyEmail(sc, userService);
                case "4" -> {
                    System.out.println("\n👋 Thank you for using Inventory System! Goodbye!");
                    running = false;
                }
                default -> System.out.println("⚠️ Invalid choice! Please try again.");
            }
        }
        sc.close();
    }

    private static void registerUser(Scanner sc, UserService userService) {
        System.out.println("\n📝===== User Registration =====");
        System.out.print("📧 Enter Email: ");
        String email = sc.nextLine().trim();
        System.out.print("👤 Enter Username: ");
        String username = sc.nextLine();
        System.out.print("🔑 Enter Password: ");
        String password = sc.nextLine();
        System.out.print("⚙️  Enter Role (ADMIN/USER): ");
        String role = sc.nextLine().toUpperCase();

        String otp = OTPService.generateOTP(email);
        OTPService.sendOTPEmail(email, otp);

        System.out.print("\n✉️  Enter the OTP sent to your email: ");
        String enteredOTP = sc.nextLine().trim();

        if (!OTPService.verifyOTP(email, enteredOTP)) {
            System.out.println("❌ Invalid or expired OTP! Registration failed.");
            return;
        }

        User user = new User(username, password, role, email, false);
        if (userService.register(user)) {
            System.out.println("🎉 Registration successful!");
        } else {
            System.out.println("⚠️ Registration failed! Try again.");
        }
    }

    private static void verifyEmail(Scanner sc, UserService userService) {
        System.out.println("\n📧===== Email Verification =====");
        System.out.print("Enter your email: ");
        String email = sc.nextLine().trim();

        if (!userService.existsByEmail(email)) {
            System.out.println("❌ No user found with this email.");
            return;
        }

        String otp = OTPService.generateOTP(email);
        OTPService.sendOTPEmail(email, otp);

        System.out.print("Enter OTP (or type 'exit' to cancel): ");
        String enteredOtp = sc.nextLine().trim();

        if (enteredOtp.equalsIgnoreCase("exit")) {
            System.out.println("🚪 Returning to main menu...");
            return;
        }

        if (OTPService.verifyOTP(email, enteredOtp)) {
            userService.verifyUser(email);
        } else {
            System.out.println("❌ Invalid OTP! Verification failed.");
        }
    }

    private static void loginUser(Scanner sc, UserService userService, InventoryManager manager) {
        System.out.println("\n🔐===== User Login =====");
        System.out.print("👤 Enter Username: ");
        String username = sc.nextLine();
        System.out.print("🔑 Enter Password: ");
        String password = sc.nextLine();

        if (!userService.login(username, password)) {
            System.out.println("🚫 Invalid credentials! Try again.");
            return;
        }

        String role = userService.getRole(username);
        System.out.println("\n🎉 Welcome, " + username + "! You are logged in as: " + role);

        // ✅ Fetch verified email of the logged-in user
        String loggedInEmail = userService.getEmailByUsername(username);
        System.out.println("📧 Verified email found: " + loggedInEmail);

        if (role.equalsIgnoreCase("ADMIN")) {
            // ✅ Start Stock Alert Scheduler for this specific admin
            StockAlertService alertService = new StockAlertService();
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            scheduler.scheduleAtFixedRate(() -> alertService.checkStockAlerts(username), 0, 1, TimeUnit.MINUTES);
            System.out.println("🕒 Stock alert scheduler started for admin: " + username);

            // ✅ Then open admin menu
            adminMenu(manager, sc, loggedInEmail, scheduler);
        } else {
            userMenu(manager, sc);
        }
    }

    // ✅ Admin Menu - uses loggedInEmail for report sending
    private static void adminMenu(InventoryManager manager, Scanner sc, String loggedInEmail, ScheduledExecutorService scheduler) {
        boolean running = true;
        while (running) {
            System.out.println("\n🧑‍💼===== ADMIN MENU =====");
            System.out.println("1️⃣ Add Product");
            System.out.println("2️⃣ Remove Product");
            System.out.println("3️⃣ Update Product");
            System.out.println("4️⃣ View All Products");
            System.out.println("5️⃣ Search Product");
            System.out.println("6️⃣ Generate Report");
            System.out.println("7️⃣ Pagination View");
            System.out.println("8️⃣ Search by Price Range");
            System.out.println("9️⃣ Logout");
            System.out.print("👉 Enter your choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1 -> manager.addProduct();
                    case 2 -> manager.removeProduct();
                    case 3 -> manager.updateProduct();
                    case 4 -> manager.displayAll();
                    case 5 -> manager.searchProduct();
                    case 6 -> manager.generateReport(loggedInEmail);
                    case 7 -> manager.displayPaginated();
                    case 8 -> manager.searchProductByPriceRange();
                    case 9 -> {
                        System.out.println("\n🔓 Logging out... Returning to main menu.");
                        running = false;
                        scheduler.shutdown();
                        System.out.println("🛑 Stock alert scheduler stopped.");
                    }
                    default -> System.out.println("⚠️ Invalid choice! Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❗ Please enter a valid number!");
            }
        }
    }

    private static void userMenu(InventoryManager manager, Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n🙍‍♂️===== USER MENU =====");
            System.out.println("1️⃣ View All Products");
            System.out.println("2️⃣ Search Product");
            System.out.println("3️⃣ View Products with Pagination");
            System.out.println("4️⃣ Search by Price Range");
            System.out.println("5️⃣ Logout");
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
