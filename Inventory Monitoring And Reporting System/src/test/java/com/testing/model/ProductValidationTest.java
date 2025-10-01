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

}
