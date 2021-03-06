package com.dexma.hometest.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dexma.hometest.domain.Beverage;
import com.dexma.hometest.domain.Product;
import com.dexma.hometest.domain.Stock;
import com.dexma.hometest.error.ProductManagerException;


/**
 * ProductManager class - Contains all the operations product related in the vending machine.
 */
public class ProductManager
{
    private final Stock<Product> productStock;
    private Product selectedProduct;

    public ProductManager(final Stock<Product> productStock)
    {
        this.productStock = productStock;
    }

    /***
     * Cash stock related operations
     */

    public boolean isProductItemAllowed(final Product product)
    {
        return product != null && getValidProductItems().contains(product);
    }

    public boolean isProductItemAvailable(final Product product)
    {
        return productStock.hasItem(product);
    }

    public List<Product> getAvailableProducts()
    {
        return Arrays.stream(Beverage.getValidBeverages())
            .filter(this::isProductItemAvailable)
            .collect(Collectors.toList());
    }

    public Map<Product, Integer> getProductStock()
    {
        return productStock.getStockMap();
    }

    public void insertProductItemsInStock(final Map<Product, Integer> productMap)
    {
        if (productMap == null || productMap.isEmpty())
        {
            throw new ProductManagerException("At least one valid product should be provided.");
        }

        for (final Map.Entry<Product, Integer> entry : productMap.entrySet())
        {
            final Product productToAdd = entry.getKey();
            final int quantityOfProduct = entry.getValue();
            validateProductEntry(productToAdd, quantityOfProduct);
            productStock.insertItem(productToAdd, quantityOfProduct);
        }
    }

    public void removeProductItemFromStock(final Product product)
    {
        if (!isProductItemAllowed(product))
        {
            throw new ProductManagerException("Invalid product specified.");
        }
        productStock.deleteItem(product);
    }

    private List<Product> getValidProductItems()
    {
        return Arrays.asList(Beverage.getValidBeverages());
    }

    private void validateProductEntry(final Product product, final int quantity)
    {
        if (!isProductItemAllowed(product))
        {
            throw new ProductManagerException("Invalid product specified.");
        }

        if (quantity <= 0)
        {
            throw new ProductManagerException("Invalid quantity specified.");
        }
    }

    /***
     * Selected product related operations
     */

    public void setSelectedProduct(final Product product)
    {
        this.selectedProduct = product;
    }

    public BigDecimal getPriceOfSelectedProduct()
    {
        return isProductSelected() ? selectedProduct.getPrice() : BigDecimal.ZERO;
    }

    public void resetSelectedProduct()
    {
        setSelectedProduct(null);
    }

    public Product getSelectedProduct()
    {
        return selectedProduct;
    }

    public boolean isProductSelected()
    {
        return this.selectedProduct != null;
    }

}
