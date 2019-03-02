package com.dexma.hometest.business;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dexma.hometest.domain.Beverage;
import com.dexma.hometest.domain.Product;
import com.dexma.hometest.domain.Stock;
import com.dexma.hometest.error.ProductManagerException;


/**
 * ProductManagerTest class - ProductManager test class.
 */
@ExtendWith(MockitoExtension.class)
class ProductManagerTest
{
    @Mock
    private Stock<Product> mockProductStock;

    private ProductManager productManager;

    @BeforeEach
    void setUp()
    {
        this.productManager = new ProductManager(mockProductStock);
    }

    // isProductItemAllowed
    @Test
    void givenValidProductItem_whenCheckIfIsAllowed_thenReturnTrue()
    {
        // given
        final Product[] testCases = {
            Beverage.SPRITE, Beverage.WATER, Beverage.COKE
        };

        // when + then
        for (final Product testCase : testCases)
        {
            assertTrue(productManager.isProductItemAllowed(testCase));
        }
    }

    @Test
    void givenInvalidValidProductItem_whenCheckIfIsAllowed_thenReturnFalse()
    {
        // given

        // when + then
        assertFalse(productManager.isProductItemAllowed(null));
    }

    // isProductItemAvailable
    @Test
    void givenValidAndAvailableProductItem_whenCheckIfIsAvailable_thenReturnTrue()
    {
        // given
        final Product mockProduct = Beverage.COKE;
        when(mockProductStock.hasItem(mockProduct)).thenReturn(true);

        // when + then
        assertTrue(productManager.isProductItemAvailable(mockProduct));
    }

    @Test
    void givenValidAndNotAvailableProductItem_whenCheckIfIsAvailable_thenReturnFalse()
    {
        // given
        final Product mockProduct = Beverage.SPRITE;
        when(mockProductStock.hasItem(mockProduct)).thenReturn(false);
        final Map<Product, Integer> mockStockMap = new HashMap<>();
        mockStockMap.put(Beverage.COKE, 1);
        mockStockMap.put(Beverage.SPRITE, 0);
        when(mockProductStock.getStockMap()).thenReturn(mockStockMap);

        // when + then
        assertFalse(productManager.isProductItemAvailable(mockProduct));
        final Map<Product, Integer> stockMap = productManager.getProductStock();
        assertThat(stockMap, is(mockStockMap));
        assertThat(stockMap.size(), is(mockStockMap.size()));
        assertThat(stockMap, IsMapContaining.hasKey(Beverage.SPRITE));
    }

    // getAvailableProducts
    @Test
    void givenProductsInStockWhereAllAvailable_whenGetAvailableProducts_thenReturnAllProducts()
    {
        // given
        final List<Product> expectedProducts = Arrays.asList(Beverage.COKE, Beverage.SPRITE, Beverage.WATER);
        when(mockProductStock.hasItem(ArgumentMatchers.any(Product.class))).thenReturn(true);

        // when
        final List<Product> result = productManager.getAvailableProducts();

        // then
        assertThat(result, not(IsEmptyCollection.empty()));
        assertThat(result, is(expectedProducts));
        assertThat(result.size(), is(expectedProducts.size()));
    }

    @Test
    void givenProductsInStockWhereNotAllAvailable_whenGetAvailableProducts_thenReturnOnlyAvailableProducts()
    {
        // given
        final List<Product> expectedProducts = Arrays.asList(Beverage.COKE, Beverage.WATER);
        when(mockProductStock.hasItem(ArgumentMatchers.any(Product.class))).thenReturn(true);
        when(mockProductStock.hasItem(Beverage.SPRITE)).thenReturn(false);

        // when
        final List<Product> result = productManager.getAvailableProducts();

        // then
        assertThat(result, not(IsEmptyCollection.empty()));
        assertThat(result, is(expectedProducts));
        assertThat(result.size(), is(expectedProducts.size()));
    }

    @Test
    void givenProductsInStockWhereAllNotAvailable_whenGetAvailableProducts_thenReturnEmptyProductsCollection()
    {
        // given
        when(mockProductStock.hasItem(ArgumentMatchers.any(Product.class))).thenReturn(false);

        // when
        final List<Product> result = productManager.getAvailableProducts();

        // then
        assertThat(result, IsEmptyCollection.empty());
    }

    // insertProductItemsInStock
    // - null or empty map
    @Test
    void givenInvalidInputMap_whenInsertProductsInStock_thenThrowSpecificException()
    {
        // given
        final Map[] testCases = new Map[] {null, Collections.emptyMap()};
        final String expectedMsg = "At least one valid product should be provided.";

        // when + then
        for (final Map testCase : testCases)
        {
            final ProductManagerException thrown =
                assertThrows(ProductManagerException.class, () -> productManager.insertProductItemsInStock(testCase));
            assertEquals(expectedMsg, thrown.getMessage());
        }
    }

    // - map with invalid product
    @Test
    void givenInputMapWithInvalidProduct_whenInsertProductsInStock_thenThrowSpecificException()
    {
        // given
        final Map<Product, Integer> mockMap = new HashMap<>();
        mockMap.put(Beverage.COKE, 1);
        mockMap.put(null, 1);
        final String expectedMsg = "Invalid product specified.";

        // when + then
        final ProductManagerException thrown = assertThrows(ProductManagerException.class, () -> productManager.insertProductItemsInStock(mockMap));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - map with invalid quantity
    @Test
    void givenInputMapWithProductWithInvalidQuantity_whenInsertProductsInStock_thenThrowSpecificException()
    {
        // given
        final Map<Product, Integer> mockMap = new HashMap<>();
        mockMap.put(Beverage.COKE, -1);
        mockMap.put(Beverage.SPRITE, 1);
        final String expectedMsg = "Invalid quantity specified.";

        // when + then
        final ProductManagerException thrown = assertThrows(ProductManagerException.class, () -> productManager.insertProductItemsInStock(mockMap));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - map with all ok
    @Test
    void givenValidInputMap_whenInsertProductsInStock_thenExpectToInsertProductsInStock()
    {
        // given
        final Map<Product, Integer> mockMap = new HashMap<>();
        mockMap.put(Beverage.COKE, 2);
        mockMap.put(Beverage.SPRITE, 2);
        doNothing().when(mockProductStock).insertItem(any(Product.class), anyInt());

        // when + then
        assertDoesNotThrow(() -> productManager.insertProductItemsInStock(mockMap));
        verify(mockProductStock, times(1)).insertItem(Beverage.COKE, 2);
        verify(mockProductStock, times(1)).insertItem(Beverage.SPRITE, 2);
    }

    // removeProductItemFromStock
    // - product not allowed
    @Test
    void givenInvalidProduct_whenRemoveProductFromStock_thenThrowSpecificException()
    {
        // given
        final Product mockProduct = null;
        final String expectedMsg = "Invalid product specified.";

        // when + then
        final ProductManagerException thrown =
            assertThrows(ProductManagerException.class, () -> productManager.removeProductItemFromStock(mockProduct));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - product allowed with quantity > 0
    // - product allowed with quantity = 0
    @Test
    void givenProductInStockWithQuantityMoreThanZero_whenRemoveProductFromStock_thenExpectRemoveProductInStock()
    {
        // given
        final Product mockProduct = Beverage.WATER;
        doNothing().when(mockProductStock).deleteItem(any(Product.class));

        // when + then
        assertDoesNotThrow(() -> productManager.removeProductItemFromStock(mockProduct));
        verify(mockProductStock, times(1)).deleteItem(mockProduct);
    }

    // getPriceOfSelectedProduct
    // - selected product ok -- return price
    @Test
    void givenSelectedProduct_whenGetPrice_thenReturnPrice()
    {
        // given
        productManager.setSelectedProduct(Beverage.WATER);
        final BigDecimal expectedPrice = BigDecimal.valueOf(0.9);

        // when
        final BigDecimal result = productManager.getPriceOfSelectedProduct();

        // then
        assertEquals(expectedPrice, result);
    }

    // - not selected product -- return 0
    @Test
    void givenNotSelectedProduct_whenGetPrice_thenReturnPrice()
    {
        // given
        productManager.setSelectedProduct(null);
        final BigDecimal expectedPrice = BigDecimal.ZERO;

        // when
        final BigDecimal result = productManager.getPriceOfSelectedProduct();

        // then
        assertEquals(expectedPrice, result);
    }

    // resetSelectedProduct
    // - check if null after call
    @Test
    void givenSelectedProduct_whenResetSelectedProduct_thenExpectNullSelectProduct()
    {
        // given
        productManager.setSelectedProduct(Beverage.COKE);

        // when
        productManager.resetSelectedProduct();

        // then
        assertNull(productManager.getSelectedProduct());
    }

    // isProductSelected
    // - selectedProd == null
    @Test
    void givenNotSelectedProduct_whenCheckIfExistSelectedProduct_thenExpectNullSelectProduct()
    {
        // given
        productManager.setSelectedProduct(null);

        // when + then
        assertFalse(productManager.isProductSelected());
    }

    // - selectedProd != null
    @Test
    void givenSelectedProduct_whenCheckIfExistSelectedProduct_thenExpectSelectProduct()
    {
        // given
        productManager.setSelectedProduct(Beverage.COKE);

        // when + then
        assertTrue(productManager.isProductSelected());
    }
}
