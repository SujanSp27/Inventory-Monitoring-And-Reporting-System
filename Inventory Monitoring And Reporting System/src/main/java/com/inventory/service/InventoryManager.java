package com.inventory.service;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.product;
import com.inventory.DataAccessObject.ProductDAO;
import java.util.List;
import java.util.Scanner;
import com.inventory.util.CSVHelper;

public class InventoryManager {
    Scanner sc = new Scanner(System.in);
    ProductDAO dao = new ProductDAO();
    CSVHelper csvDao = new CSVHelper();


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

            dao.addProduct(p);      // save to DB
            csvDao.saveProduct(p);  // save to CSV ✅

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter correct numbers.");
        }
    }

    public void removeProduct() {
        try {
            System.out.print("Enter ID to remove: ");
            int id = Integer.parseInt(sc.nextLine());

            dao.removeProduct(id); // Will throw exception if not found
            System.out.println("Product Removed");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid ID.");
        } catch (ProductNotFoundException e) {
            System.out.println(e.getMessage());
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

            dao.updateProduct(id, qty, price); // Will throw exception if not found
            System.out.println("Product Updated");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter correct numbers.");
        } catch (ProductNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchProduct() {
        try {
            System.out.print("Enter Name to search: ");
            String name = sc.nextLine();

            product p = dao.getProductByName(name); // throws exception if not found
            p.display();
        } catch (ProductNotFoundException e) {
            System.out.println(e.getMessage());
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

    public void loadProductsFromCSV() {
        List<product> products = CSVHelper.loadProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            for (product p : products) {
                System.out.println(p.getId() + " | " + p.getName() + " | " + p.getPrice() + " | " + p.getQuantity());
            }
        }
    }
    public void searchProductById() {
        try {
            System.out.print("Enter ID to search: ");
            int id = Integer.parseInt(sc.nextLine());

            product p = dao.getProductById(id); // 👈 new DAO method
            p.display();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid ID.");
        } catch (ProductNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchProductByName() {
        try {
            System.out.print("Enter Name to search: ");
            String name = sc.nextLine();

            product p = dao.getProductByName(name);
            p.display();
        } catch (ProductNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void searchProductByCategory() {
        try {
            System.out.print("Enter Category to search: ");
            String category = sc.nextLine();

            List<product> products = dao.getProductsByCategory(category);

            for (product p : products) {
                p.display();
            }
        } catch (ProductNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    public void displayPaginated() {
        try {
            System.out.print("Enter page number: ");
            int page = Integer.parseInt(sc.nextLine());

            System.out.print("Enter page size (items per page): ");
            int pageSize = Integer.parseInt(sc.nextLine());

            List<product> list = dao.getProductsPaginated(page, pageSize);

            if (list.isEmpty()) {
                System.out.println("No products found for this page.");
            } else {
                System.out.println("\n=== Products (Page " + page + ") ===");
                for (product p : list) {
                    p.display();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter valid numbers.");
        }
    }



}
