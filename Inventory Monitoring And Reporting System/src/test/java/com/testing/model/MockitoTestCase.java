package com.testing.model;

import com.inventory.DataAccessObject.ProductDAO;
import com.inventory.model.product;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MockitoTestCase {

    private ProductDAO productDAO;

    @Before
    public void setup() {
        // Create mock object for DAO
        productDAO = mock(ProductDAO.class);
    }

    @Test
    public void testAddProduct() {
        product p = new product(101, "Laptop", 5, 60000, "Electronics");

        // Stub behavior
        doNothing().when(productDAO).addProduct(p);

        // Call method
        productDAO.addProduct(p);

        // Verify interaction
        verify(productDAO, times(1)).addProduct(p);
    }

    @Test
    public void testRemoveProduct() {
        when(productDAO.removeProduct(101)).thenReturn(true);

        boolean result = productDAO.removeProduct(101);

        verify(productDAO, times(1)).removeProduct(101);
        assert(result);
    }

    @Test
    public void testUpdateProduct() {
        when(productDAO.updateProduct(101, 10, 65000)).thenReturn(true);

        boolean updated = productDAO.updateProduct(101, 10, 65000);

        verify(productDAO, times(1)).updateProduct(101, 10, 65000);
        assert(updated);
    }

    @Test
    public void testGetProductById() {
        product mockProduct = new product(101, "Laptop", 5, 60000, "Electronics");

        when(productDAO.getProductById(101)).thenReturn(mockProduct);

        product p = productDAO.getProductById(101);

        verify(productDAO, times(1)).getProductById(101);
        assert(p.getName().equals("Laptop"));
    }

    @Test
    public void testGetProductByName() {
        product mockProduct = new product(102, "Mouse", 15, 500, "Electronics");

        when(productDAO.getProductByName("Mouse")).thenReturn(mockProduct);

        product p = productDAO.getProductByName("Mouse");

        verify(productDAO, times(1)).getProductByName("Mouse");
        assert(p.getId() == 102);
    }
}
