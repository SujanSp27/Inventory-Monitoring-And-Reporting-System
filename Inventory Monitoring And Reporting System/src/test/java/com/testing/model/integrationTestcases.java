package com.testing.model;
import com.inventory.DataAccessObject.ProductDAO;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.model.product;
import com.inventory.util.dbConnection;
import org.junit.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class integrationTestcases {

    private static ProductDAO dao;

    @BeforeClass
    public static void setupDatabase() throws Exception {
        dao = new ProductDAO();
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "category VARCHAR(100), " +
                    "quantity INT, " +
                    "price DOUBLE)");
            stmt.execute("DELETE FROM products");
        }
    }

    @AfterClass
    public static void cleanup() throws Exception {
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS products");
        }
    }

    @Test
    public void testAddAndFetchProduct() {
        product p = new product(101, "Laptop", 5, 60000.0, "Electronics");
        dao.addProduct(p);

        product fetched = dao.getProductById(101);
        Assert.assertEquals("Laptop", fetched.getName());
        Assert.assertEquals(5, fetched.getQuantity());
    }

    @Test
    public void testUpdateProduct() {
        product p = new product(102, "Keyboard", 10, 800.0, "Peripherals");
        dao.addProduct(p);

        dao.updateProduct(102, 15, 900.0);
        product updated = dao.getProductById(102);

        Assert.assertEquals(15, updated.getQuantity());
        Assert.assertEquals(900.0, updated.getPrice(), 0.001);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testRemoveProductNotFound() {
        dao.removeProduct(9999);
    }

    @Test
    public void testRemoveProductSuccess() {
        product p = new product(103, "Mouse", 20, 400.0, "Peripherals");
        dao.addProduct(p);

        boolean removed = dao.removeProduct(103);
        Assert.assertTrue(removed);

        try {
            dao.getProductById(103);
            Assert.fail("ProductNotFoundException expected");
        } catch (ProductNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testGetProductsByCategory() {
        product p1 = new product(104, "Monitor", 3, 12000.0, "Electronics");
        product p2 = new product(105, "Headset", 7, 2000.0, "Electronics");
        dao.addProduct(p1);
        dao.addProduct(p2);

        List<product> electronics = dao.getProductsByCategory("Electronics");
        Assert.assertTrue(electronics.size() >= 2);
    }

    @Test
    public void testGetAllProducts() {
        List<product> all = dao.getAllProducts();
        Assert.assertFalse(all.isEmpty());
    }
}
