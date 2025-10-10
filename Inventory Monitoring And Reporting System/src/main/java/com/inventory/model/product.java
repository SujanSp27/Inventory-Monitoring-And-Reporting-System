package com.inventory.model;

public class product {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private String category;

        public product() {
     }

    public product(int id, String name, int quantity, double price ,String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }
    public double stockValue() {
        return quantity * price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void display() {
        System.out.println("+----+----------------------+----------------------+----------+----------+");
        System.out.println("| ID | Name                 | Category             | Quantity |  Price   |");
        System.out.println("+----+----------------------+----------------------+----------+----------+");

        System.out.printf(
                "| %-2d | %-20s | %-20s | %-8d | %-8.2f |\n",
                id, name, category, quantity, price
        );

        System.out.println("+----+----------------------+----------------------+----------+----------+");
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }


}
