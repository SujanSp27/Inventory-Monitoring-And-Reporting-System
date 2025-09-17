package com.inventory.util;
import com.inventory.model.product;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class CSVHelper {
    private static final String FILE_NAME = "products.csv";

    // Save product to CSV file
    public void saveProduct(product p) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            writer.println(p.getId() + "," + p.getName() + "," + p.getCategory() + "," + p.getQuantity() + "," + p.getPrice());
            System.out.println("Product saved to CSV successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to CSV: " + e.getMessage());
        }
    }

    // Read all products from CSV
    public static List<product> loadProducts() {
        List<product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                String category = data[2];
                int quantity = Integer.parseInt(data[3]);
                double price = Double.parseDouble(data[4]);

                products.add(new product(id, name, quantity, price, category));
            }
        } catch (IOException e) {
            System.out.println("Error reading from CSV: " + e.getMessage());
        }
        return products;
    }

}
