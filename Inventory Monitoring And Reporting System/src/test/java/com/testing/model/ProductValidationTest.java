package com.testing.model;

import com.inventory.model.product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link product} class.
 * Verifies constructors, getters, setters, and toString() functionality.
 */
public class ProductValidationTest {

    @Test
    void testProductConstructorAndGetters() {
        product p = new product(1, "Keyboard", 10, 500.0, "Electronics");

        assertEquals(1, p.getId());
        assertEquals("Keyboard", p.getName());
        assertEquals("Electronics", p.getCategory());
        assertEquals(10, p.getQuantity());
        assertEquals(500.0, p.getPrice(), 0.001);
    }

    @Test
    void testSettersUpdateValuesCorrectly() {
        product p = new product(2, "Mouse", 5, 250.0, "Electronics");

        p.setName("Gaming Mouse");
        p.setQuantity(8);
        p.setPrice(300.0);
        p.setCategory("Peripherals");

        assertEquals("Gaming Mouse", p.getName());
        assertEquals(8, p.getQuantity());
        assertEquals(300.0, p.getPrice(), 0.001);
        assertEquals("Peripherals", p.getCategory());
    }

    @Test
    void testDefaultConstructorInitialValues() {
        product p = new product();

        assertEquals(0, p.getId());
        assertNull(p.getName());
        assertNull(p.getCategory());
        assertEquals(0, p.getQuantity());
        assertEquals(0.0, p.getPrice(), 0.001);
    }

    @Test
    void testToStringContainsImportantDetails() {
        product p = new product(3, "Monitor", 2, 7000.0, "Electronics");
        String str = p.toString();

        assertTrue(str.contains("Monitor"));
        assertTrue(str.contains("Electronics"));
    }
}
