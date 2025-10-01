package com.testing.model;

import com.inventory.model.product;
import org.junit.Assert;
import org.junit.Test;

public class ProductValidationTest {

    @Test
    public void testProductFields() {
        product p = new product(1, "KeyBoard", 10, 500, "Electronics");

        Assert.assertEquals(1, p.getId());
        Assert.assertEquals("KeyBoard", p.getName());
        Assert.assertEquals("Electronics", p.getCategory());
        Assert.assertEquals(10, p.getQuantity());
        Assert.assertEquals(500.0, p.getPrice(), 0.001); // delta for double comparison
    }

    @Test
    public void testProductSetters() {
        product p = new product(2, "Mouse", 5, 250.0, "Electronics");

        // change values
        p.setName("Gaming Mouse");
        p.setQuantity(8);
        p.setPrice(300.0);
        p.setCategory("Peripherals");

        // verify updated values
        Assert.assertEquals("Gaming Mouse", p.getName());
        Assert.assertEquals(8, p.getQuantity());
        Assert.assertEquals(300.0, p.getPrice(), 0.001);
        Assert.assertEquals("Peripherals", p.getCategory());
    }

    @Test
    public void testProductDefaultObject() {
        product p = new product();
        // default constructor values (should be null/0 depending on your class)
        Assert.assertEquals(0, p.getId());
        Assert.assertNull(p.getName());
        Assert.assertNull(p.getCategory());
        Assert.assertEquals(0, p.getQuantity());
        Assert.assertEquals(0.0, p.getPrice(), 0.001);
    }

    @Test
    public void testProductToString() {
        product p = new product(3, "Monitor", 2, 7000.0, "Electronics");
        String str = p.toString();
        Assert.assertTrue(str.contains("Monitor"));
        Assert.assertTrue(str.contains("Electronics"));
    }
}
