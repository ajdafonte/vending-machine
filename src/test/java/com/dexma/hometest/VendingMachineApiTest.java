package com.dexma.hometest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dexma.hometest.business.CashManager;
import com.dexma.hometest.business.ProductManager;
import com.dexma.hometest.domain.BalanceResult;
import com.dexma.hometest.domain.Beverage;
import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Coin;
import com.dexma.hometest.domain.Product;
import com.dexma.hometest.error.CashManagerException;
import com.dexma.hometest.error.ProductManagerException;
import com.dexma.hometest.error.VendingMachineException;

import javafx.util.Pair;


/**
 * VendingMachineApiTest class - VendingMachineApi test class.
 */
@ExtendWith(MockitoExtension.class)
class VendingMachineApiTest
{
    @Mock
    private ProductManager mockProductManager;

    @Mock
    private CashManager mockCashManager;

    private VendingMachineApi vendingMachineApi;

    @BeforeEach
    void setUp()
    {
        this.vendingMachineApi = new VendingMachineApi(mockProductManager, mockCashManager);
    }

    // refillProducts
    @Test
    void givenProductStock_whenRefillProductsValidProducts_thenRefillProductStock()
    {
        // given
        final Map<Product, Integer> mockInputMap = new HashMap<>();
        mockInputMap.put(Beverage.SPRITE, 1);
        doNothing().when(mockProductManager).insertProductItemsInStock(mockInputMap);

        // when + then
        assertDoesNotThrow(() -> vendingMachineApi.refillProducts(mockInputMap));
        verify(mockProductManager, times(1)).insertProductItemsInStock(mockInputMap);
    }

    @Test
    void givenProductStock_whenRefillProductsWithInvalidProducts_thenThrowSpecificException()
    {
        // given
        final Map<Product, Integer> mockInputMap = Collections.emptyMap();
        doThrow(ProductManagerException.class).when(mockProductManager).insertProductItemsInStock(mockInputMap);

        // when + then
        final VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.refillProducts(mockInputMap));
        assertThat(thrown.getCause(), instanceOf(ProductManagerException.class));
    }

    // refillCash
    @Test
    void givenCashStock_whenRefillCashWithValidCashItems_thenRefillCashStock()
    {
        // given
        final Map<Cash, Integer> mockInputMap = new HashMap<>();
        mockInputMap.put(Coin.ONE, 1);
        doNothing().when(mockCashManager).insertCashItemsInStock(mockInputMap);

        // when + then
        assertDoesNotThrow(() -> vendingMachineApi.refillCash(mockInputMap));
        verify(mockCashManager, times(1)).insertCashItemsInStock(mockInputMap);
    }

    @Test
    void givenCashStock_whenRefillCashWithInvalidCashItems_thenThrowSpecificException()
    {
        // given
        final Map<Cash, Integer> mockInputMap = Collections.emptyMap();
        doThrow(CashManagerException.class).when(mockCashManager).insertCashItemsInStock(mockInputMap);

        // when + then
        final VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.refillCash(mockInputMap));
        assertThat(thrown.getCause(), instanceOf(CashManagerException.class));
    }

    // getProductStockStatus
    @Test
    void givenProductStock_whenGetProductStockStatus_thenReturnProductStock()
    {
        // given
        final Map<Product, Integer> expectedResult = new HashMap<>();
        final Product expectedItem = Beverage.COKE;
        final int expectedQuantity = 2;
        expectedResult.put(expectedItem, expectedQuantity);
        when(mockProductManager.getProductStock()).thenReturn(expectedResult);

        // when
        final Map<Product, Integer> result = vendingMachineApi.getProductStockStatus();

        // then
        assertThat(result, is(expectedResult));
        assertThat(result.size(), is(expectedResult.size()));
        assertThat(result, IsMapContaining.hasEntry(expectedItem, expectedQuantity));
    }

    @Test
    void givenEmptyProductStock_whenGetProductStockStatus_thenReturnEmptyProductStock()
    {
        // given
        final Map<Product, Integer> expectedResult = Collections.emptyMap();
        when(mockProductManager.getProductStock()).thenReturn(expectedResult);

        // when
        final Map<Product, Integer> result = vendingMachineApi.getProductStockStatus();

        // then
        assertThat(result, is(expectedResult));
        assertTrue(result.isEmpty());
    }

    // getCashStockStatus
    @Test
    void givenCashStock_whenGetCashStockStatus_thenReturnCashStock()
    {
        // given
        final Map<Cash, Integer> expectedResult = new HashMap<>();
        final Cash expectedItem = Coin.FIFTY_CENTS;
        final int expectedQuantity = 1;
        expectedResult.put(expectedItem, expectedQuantity);
        when(mockCashManager.getCashStock()).thenReturn(expectedResult);

        // when
        final Map<Cash, Integer> result = vendingMachineApi.getCashStockStatus();

        // then
        assertThat(result, is(expectedResult));
        assertThat(result.size(), is(expectedResult.size()));
        assertThat(result, IsMapContaining.hasEntry(expectedItem, expectedQuantity));
    }

    @Test
    void givenEmptyCashStock_whenGetCashStockStatus_thenReturnEmptyCashStock()
    {
        // given
        final Map<Cash, Integer> expectedResult = Collections.emptyMap();
        when(mockCashManager.getCashStock()).thenReturn(expectedResult);

        // when
        final Map<Cash, Integer> result = vendingMachineApi.getCashStockStatus();

        // then
        assertThat(result, is(expectedResult));
        assertTrue(result.isEmpty());
    }

    // refund
    // has balance
    @Test
    void givenCurrentBalance_whenRefund_thenReturnListOfCashItemsWithAmount()
    {
        // given
        when(mockCashManager.hasCurrentBalance()).thenReturn(true);
        final Map<Cash, Integer> expectedResult = new HashMap<>();
        final Cash expectedItem = Coin.TWO;
        final int expectedQuantity = 2;
        expectedResult.put(expectedItem, expectedQuantity);
        when(mockCashManager.calculateRefund()).thenReturn(expectedResult);

        // when
        final Map<Cash, Integer> result = vendingMachineApi.refund();

        // then
        assertThat(result, is(expectedResult));
        assertThat(result.size(), is(expectedResult.size()));
        assertThat(result, IsMapContaining.hasEntry(expectedItem, expectedQuantity));
    }

    // does not have balance
    @Test
    void givenNoBalance_whenRefund_thenReturnEmptyListOfCashItems()
    {
        // given
        when(mockCashManager.hasCurrentBalance()).thenReturn(false);

        // when
        final Map<Cash, Integer> result = vendingMachineApi.refund();

        // then
        assertNull(result);
    }

    // selectProduct
    // - valid product and quantity > 0
    @Test
    void givenProductStock_whenSelectValidProduct_thenReturnProductPrice()
    {
        // given
        final Product mockProduct = Beverage.WATER;
        when(mockProductManager.isProductItemAllowed(mockProduct)).thenReturn(true);
        when(mockProductManager.isProductItemAvailable(mockProduct)).thenReturn(true);
        doNothing().when(mockProductManager).setSelectedProduct(mockProduct);
        final BigDecimal expectedResult = mockProduct.getPrice();
        when(mockProductManager.getPriceOfSelectedProduct()).thenReturn(expectedResult);

        // when
        final BigDecimal result = vendingMachineApi.selectProduct(mockProduct);

        // then
        assertEquals(expectedResult, result);
    }

    // - valid product and quantity = 0
    @Test
    void givenEmptyProductStock_whenSelectValidProduct_thenThrowSpecificException()
    {
        // given
        final Product mockProduct = Beverage.WATER;
        final String expectedMsg = "Product " + mockProduct.getName() + " is not available in stock.";
        when(mockProductManager.isProductItemAllowed(mockProduct)).thenReturn(true);
        when(mockProductManager.isProductItemAvailable(mockProduct)).thenReturn(false);

        // when + then
        final VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.selectProduct(mockProduct));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - invalid product
    @Test
    void givenProductStock_whenSelectInvalidProduct_thenThrowSpecificException()
    {
        // given
        when(mockProductManager.isProductItemAllowed(null)).thenReturn(false);
        final String expectedMsg = "A valid product item should be provided.";

        // when + then
        final VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.selectProduct(null));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // insertCash
    // - valid cash item
    @Test
    void givenCurrentBalance_whenInsertValidCash_thenReturnExpectedValue()
    {
        // given
        final Cash mockCash = Coin.TEN_CENTS;
        final BigDecimal expectedCurrentBalance = BigDecimal.valueOf(1.1);
        when(mockCashManager.receiveCash(mockCash)).thenReturn(expectedCurrentBalance);

        // when
        final BigDecimal result = vendingMachineApi.insertCash(mockCash);

        // then
        assertEquals(expectedCurrentBalance, result);
    }

    // - invalid cash item
    @Test
    void givenCurrentBalance_whenInsertInvalidCash_thenThrowSpecificException()
    {
        // given
        doThrow(CashManagerException.class).when(mockCashManager).receiveCash(null);
        final String expectedMsg = "A valid cash item should be provided.";

        // when + then
        final VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.insertCash(null));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // confirmPurchase
    // - no current item selected
    @Test
    void givenNoProductSelected_whenConfirmPurchase_thenThrowSpecificException()
    {
        // given
        when(mockProductManager.isProductSelected()).thenReturn(false);
        final String expectedMsg = "Before confirm a purchase one product should be selected first.";

        // when + then
        final VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.confirmPurchase());
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - current item selected + balance < product price
    @Test
    void givenNotEnoughBalanceAndProductSelected_whenConfirmPurchase_thenThrowSpecificException()
    {
        // given
        when(mockProductManager.isProductSelected()).thenReturn(true);
        final Product mockSelectedProduct = Beverage.WATER;
        when(mockProductManager.getSelectedProduct()).thenReturn(mockSelectedProduct);
        final BalanceResult balanceResult = BalanceResult.BELOW_AMOUNT;
        when(mockCashManager.isPossibleToPurchaseProduct(mockSelectedProduct.getPrice())).thenReturn(balanceResult);
        final String expectedMsg = "Current balance is not enough to buy Product " + mockSelectedProduct.getName() + ".";

        // when + then
        final VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.confirmPurchase());
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - current item selected + balance == product price
    @Test
    void givenBalanceEqualToPriceSelectedProduct_whenConfirmPurchase_thenReturnProductWithNoChange()
    {
        // given
        when(mockProductManager.isProductSelected()).thenReturn(true);
        final Product mockSelectedProduct = Beverage.WATER;
        when(mockProductManager.getSelectedProduct()).thenReturn(mockSelectedProduct);
        final BalanceResult balanceResult = BalanceResult.EXACT_AMOUNT;
        when(mockCashManager.isPossibleToPurchaseProduct(mockSelectedProduct.getPrice())).thenReturn(balanceResult);
        final Pair<Product, Map<Cash, Integer>> expectedResult = new Pair<>(mockSelectedProduct, Collections.emptyMap());

        // when
        final Pair<Product, Map<Cash, Integer>> result = vendingMachineApi.confirmPurchase();

        // then
        assertNotNull(result);
        final Product resultProduct = result.getKey();
        final Map<Cash, Integer> resultChange = result.getValue();
        assertEquals(expectedResult.getKey(), resultProduct);
        assertEquals(expectedResult.getValue(), resultChange);
        assertTrue(resultChange.isEmpty());
        assertThat(result, is(expectedResult));
    }

    // - current item selected + balance > product price + no available change
    @Test
    void givenBalanceBiggerThanPriceSelectedProduct_whenConfirmPurchase_thenThrowSpecificException()
    {
        // given
        when(mockProductManager.isProductSelected()).thenReturn(true);
        final Product mockSelectedProduct = Beverage.WATER;
        when(mockProductManager.getSelectedProduct()).thenReturn(mockSelectedProduct);
        final BalanceResult balanceResult = BalanceResult.ABOVE_AMOUNT;
        when(mockCashManager.isPossibleToPurchaseProduct(mockSelectedProduct.getPrice())).thenReturn(balanceResult);
        final BigDecimal mockChangeToRefund = BigDecimal.valueOf(0.1);
        when(mockCashManager.calculateRemainingChange(mockSelectedProduct.getPrice())).thenReturn(mockChangeToRefund);
        when(mockCashManager.getCashItemsForChange(mockChangeToRefund)).thenReturn(null);
        final String expectedMsg = "Not sufficient change to provide.";

        // when + then
        final VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.confirmPurchase());
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - current item selected + balance > product price + available change
    @Test
    void givenBalanceBiggerThanPriceSelectedProduct_whenConfirmPurchase_thenReturnProductWithChange()
    {
        // given
        when(mockProductManager.isProductSelected()).thenReturn(true);
        final Product mockSelectedProduct = Beverage.WATER;
        when(mockProductManager.getSelectedProduct()).thenReturn(mockSelectedProduct);
        final BalanceResult balanceResult = BalanceResult.ABOVE_AMOUNT;
        when(mockCashManager.isPossibleToPurchaseProduct(mockSelectedProduct.getPrice())).thenReturn(balanceResult);
        final BigDecimal mockChangeToRefund = BigDecimal.valueOf(0.1);
        when(mockCashManager.calculateRemainingChange(mockSelectedProduct.getPrice())).thenReturn(mockChangeToRefund);
        final Map<Cash, Integer> expectedRemainingChange = new HashMap<>();
        expectedRemainingChange.put(Coin.TEN_CENTS, 1);
        when(mockCashManager.getCashItemsForChange(mockChangeToRefund)).thenReturn(expectedRemainingChange);
        final Pair<Product, Map<Cash, Integer>> expectedResult = new Pair<>(mockSelectedProduct, expectedRemainingChange);

        // when
        final Pair<Product, Map<Cash, Integer>> result = vendingMachineApi.confirmPurchase();

        // then
        assertNotNull(result);
        final Product resultProduct = result.getKey();
        assertEquals(expectedResult.getKey(), resultProduct);

        final Map<Cash, Integer> resultChange = result.getValue();
        assertFalse(resultChange.isEmpty());
        assertThat(resultChange, is(expectedRemainingChange));
        assertThat(resultChange.size(), is(expectedRemainingChange.size()));
        assertThat(resultChange, IsMapContaining.hasEntry(Coin.TEN_CENTS, 1));

        assertThat(result, is(expectedResult));
    }

    // allowed cash items
    @Test
    void givenAllowedCashItems_whenRequestingAllowedCashItems_thenReturnCollectionOfAllowedCashItems()
    {
        // given
        final List<Cash> expectedResult = Arrays.asList(Coin.FIVE_CENTS, Coin.TEN_CENTS, Coin.TWENTY_CENTS,
            Coin.FIFTY_CENTS, Coin.ONE, Coin.TWO);
        when(mockCashManager.getValidCashItems()).thenReturn(expectedResult);

        // when
        final List<Cash> result = vendingMachineApi.getAllowedCashItems();

        // then
        assertThat(result, not(IsEmptyCollection.empty()));
        assertThat(result, is(expectedResult));
        assertThat(result.size(), is(expectedResult.size()));
    }

    // available products
    @Test
    void givenAvailableProducts_whenRequestingAvailableProducts_thenReturnCollectionAvailableProducts()
    {
        // given
        final List<Product> expectedResult = Arrays.asList(Beverage.COKE, Beverage.SPRITE, Beverage.WATER);
        when(mockProductManager.getAvailableProducts()).thenReturn(expectedResult);

        // when
        final List<Product> result = vendingMachineApi.getAvailableProducts();

        // then
        assertThat(result, not(IsEmptyCollection.empty()));
        assertThat(result, is(expectedResult));
        assertThat(result.size(), is(expectedResult.size()));
    }
}
