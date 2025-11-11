package com.inventory.util;

import com.inventory.model.product;
import javafx.collections.ObservableList;
import java.io.FileWriter;
import java.io.IOException;

public class ReportGenerator {
    public static void exportToCSV(ObservableList<product> products) throws IOException {
        FileWriter writer = new FileWriter("inventory_report.csv");
        writer.write("ID,Name,Category,Quantity,Price\n");
        for (product p : products) {
            writer.write(p.getId() + "," + p.getName() + "," + p.getCategory() + "," + p.getQuantity() + "," + p.getPrice() + "\n");
        }
        writer.close();
    }
}
