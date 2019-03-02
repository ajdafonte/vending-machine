package com.dexma.hometest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dexma.hometest.business.CashManager;
import com.dexma.hometest.business.ChangeProcessorFactory;
import com.dexma.hometest.business.ProductManager;
import com.dexma.hometest.domain.Beverage;
import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Coin;
import com.dexma.hometest.domain.Product;
import com.dexma.hometest.domain.Stock;
import com.dexma.hometest.error.VendingMachineException;

import javafx.util.Pair;


/**
 * VendingMachineApiIntegrationTest class - Class that allows to test some scenarios of vending machine.
 */
class VendingMachineApiIntegrationTest
{
    private ProductManager productManager;
    private CashManager cashManager;

    private VendingMachineApi vendingMachineApi;

    @BeforeEach
    void setUp()
    {
        // setup product manager
        final Stock<Product> productStock = new Stock<>(new HashMap<>());
        this.productManager = new ProductManager(productStock);

        // setup cash manager
        final Stock<Cash> cashStock = new Stock<>(new HashMap<>());
        final ChangeProcessorFactory changeProcessorFactory = new ChangeProcessorFactory();
        this.cashManager = new CashManager(cashStock, changeProcessorFactory);

        this.vendingMachineApi = new VendingMachineApi(productManager, cashManager);

        // initial setup
        initialSetup();
    }

    private Map<Cash, Integer> generateCashStockValues(final int fiveCentsQnt,
                                                       final int tenCentsQnt,
                                                       final int twentyCentsQnt,
                                                       final int fiftyCentsQnt,
                                                       final int oneQnt,
                                                       final int twoQnt)
    {
        final Map<Cash, Integer> values = new HashMap<>();
        values.put(Coin.FIVE_CENTS, fiveCentsQnt);
        values.put(Coin.TEN_CENTS, tenCentsQnt);
        values.put(Coin.TWENTY_CENTS, twentyCentsQnt);
        values.put(Coin.FIFTY_CENTS, fiftyCentsQnt);
        values.put(Coin.ONE, oneQnt);
        values.put(Coin.TWO, twoQnt);

        return values;
    }

    private Map<Product, Integer> generateProductStockValues(final int spriteQnt,
                                                             final int cokeQnt,
                                                             final int waterQnt)
    {
        final Map<Product, Integer> values = new HashMap<>();
        values.put(Beverage.SPRITE, spriteQnt);
        values.put(Beverage.COKE, cokeQnt);
        values.put(Beverage.WATER, waterQnt);
        return values;
    }

    private void initialSetup()
    {
        // only initial setup
        // check that there's no stock (cash and products)
        final Map<Cash, Integer> cashStockStatus = vendingMachineApi.getCashStockStatus();
        assertTrue(cashStockStatus.isEmpty());
        final Map<Product, Integer> productStockStatus = vendingMachineApi.getProductStockStatus();
        assertTrue(productStockStatus.isEmpty());

        // supplier need to refill vending machine
        // - each cash item has quantity = 2
        final Map<Cash, Integer> cashInputMap = generateCashStockValues(1, 2, 2, 1, 1, 1);
        vendingMachineApi.refillCash(cashInputMap);
        // - each product item has quantity = 1
        final Map<Product, Integer> productInputMap = generateProductStockValues(1, 1, 1);
        vendingMachineApi.refillProducts(productInputMap);

        // validate refill
        final int expectedCashItems = 6;
        final int expectedProductItems = 3;
        assertEquals(expectedCashItems, cashStockStatus.size());
        assertEquals(expectedProductItems, productStockStatus.size());
    }

    @Test
    void testScenarioHappyPath()
    {
        final Map<Cash, Integer> cashStockStatus = vendingMachineApi.getCashStockStatus();
        final Map<Product, Integer> productStockStatus = vendingMachineApi.getProductStockStatus();

        // user starts interacting with vending machine
        // goal: buy water inserting two coins of fifty cents - current balance = 1 - expected change = 0.1
        final Product targetProduct = Beverage.WATER;
        final List<Cash> userCash = Arrays.asList(Coin.FIFTY_CENTS, Coin.FIFTY_CENTS);
        final Map<Cash, Integer> expectedChange = new HashMap<>();
        expectedChange.put(Coin.TEN_CENTS, 1);
        final BigDecimal expectedProductPrice = targetProduct.getPrice();

        // - user get available products
        final List<Product> availableProducts = vendingMachineApi.getAvailableProducts();
        assertTrue(availableProducts.contains(targetProduct));

        // - user select water and see its price
        final BigDecimal productPrice = vendingMachineApi.selectProduct(targetProduct);
        assertEquals(expectedProductPrice, productPrice);
        assertEquals(targetProduct, productManager.getSelectedProduct());

        // - user insert two coins of fifty cents
        BigDecimal currentBalance = vendingMachineApi.insertCash(userCash.get(0));
        assertEquals(0, currentBalance.compareTo(BigDecimal.valueOf(0.5)));
        assertEquals(2, cashStockStatus.get(Coin.FIFTY_CENTS));

        currentBalance = vendingMachineApi.insertCash(userCash.get(1));
        assertEquals(0, currentBalance.compareTo(BigDecimal.ONE));
        assertEquals(3, cashStockStatus.get(Coin.FIFTY_CENTS));

        // - user confirm purchase of water
        final Pair<Product, Map<Cash, Integer>> result = vendingMachineApi.confirmPurchase();
        // check result
        final Product productPurchased = result.getKey();
        final Map<Cash, Integer> remainingChange = result.getValue();
        assertEquals(targetProduct, productPurchased);
        assertThat(remainingChange, is(expectedChange));
        assertThat(remainingChange.size(), is(expectedChange.size()));
        assertThat(remainingChange, IsMapContaining.hasEntry(Coin.TEN_CENTS, 1));

        // check vending machine status
        // - current balance
        assertFalse(cashManager.hasCurrentBalance());
        // - selected product
        assertNull(productManager.getSelectedProduct());
        // - check cash stock
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.TEN_CENTS, 1));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.FIFTY_CENTS, 3));
        // - check product stock
        assertThat(productStockStatus, IsMapContaining.hasEntry(Beverage.WATER, 0));
    }

    @Test
    void testScenarioBadUserUsage()
    {
        final Map<Cash, Integer> cashStockStatus = vendingMachineApi.getCashStockStatus();

        // user starts interacting with vending machine with incorrect usage
        // - try to purchase product without selecting first
        VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.confirmPurchase());
        assertEquals("Before confirm a purchase one product should be selected first.", thrown.getMessage());

        // - try to get refund with no cash inserted
        assertNull(vendingMachineApi.refund());

        // - insert coin of two and then refund
        final Cash userCashItem = Coin.TWO;
        final BigDecimal currentBalance = vendingMachineApi.insertCash(userCashItem);
        assertEquals(0, currentBalance.compareTo(BigDecimal.valueOf(2)));
        assertEquals(2, cashStockStatus.get(Coin.TWO));

        final Map<Cash, Integer> expectedRefund = new HashMap<>();
        expectedRefund.put(Coin.TWO, 1);
        final Map<Cash, Integer> refund = vendingMachineApi.refund();
        assertThat(refund, is(expectedRefund));
        assertThat(refund.size(), is(expectedRefund.size()));
        assertThat(refund, IsMapContaining.hasEntry(Coin.TWO, 1));
        assertEquals(1, cashStockStatus.get(Coin.TWO));

        // - user select product and then try to purchase without inserting money
        final Product targetProduct = Beverage.WATER;
        final BigDecimal productPrice = vendingMachineApi.selectProduct(targetProduct);
        assertEquals(targetProduct.getPrice(), productPrice);

        thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.confirmPurchase());
        assertEquals("Current balance is not enough to buy Product " + targetProduct.getName() + ".", thrown.getMessage());
    }

    @Test
    void testScenarioSomeUserInteraction()
    {
        final Map<Cash, Integer> cashStockStatus = vendingMachineApi.getCashStockStatus();
        final Map<Product, Integer> productStockStatus = vendingMachineApi.getProductStockStatus();

        // user starts interacting with vending machine
        // current cashStock: {TWENTY_CENTS=2, ONE=1, FIVE_CENTS=1, TWO=1, FIFTY_CENTS=1, TEN_CENTS=2}
        // goal: buy coke inserting two coins of one - current balance = 2 - expected change = 0.5 (one coin of fifty cents)
        // --------------------------------------------------------------------------------------------------------------------
        Product targetProduct = Beverage.COKE;
        List<Cash> userCash = Arrays.asList(Coin.ONE, Coin.ONE);
        Map<Cash, Integer> expectedChange = new HashMap<>();
        expectedChange.put(Coin.FIFTY_CENTS, 1);
        BigDecimal expectedProductPrice = targetProduct.getPrice();

        // - user get available products
        List<Product> availableProducts = vendingMachineApi.getAvailableProducts();
        assertTrue(availableProducts.contains(targetProduct));

        // - user select water and see its price
        BigDecimal productPrice = vendingMachineApi.selectProduct(targetProduct);
        assertEquals(expectedProductPrice, productPrice);
        assertEquals(targetProduct, productManager.getSelectedProduct());

        // - user insert two coins of one
        BigDecimal currentBalance = vendingMachineApi.insertCash(userCash.get(0));
        assertEquals(0, currentBalance.compareTo(BigDecimal.valueOf(1)));
        assertEquals(2, cashStockStatus.get(Coin.ONE));

        currentBalance = vendingMachineApi.insertCash(userCash.get(1));
        assertEquals(0, currentBalance.compareTo(BigDecimal.valueOf(2)));
        assertEquals(3, cashStockStatus.get(Coin.ONE));

        // - user confirm purchase of water
        Pair<Product, Map<Cash, Integer>> result = vendingMachineApi.confirmPurchase();
        // check result
        Product productPurchased = result.getKey();
        Map<Cash, Integer> remainingChange = result.getValue();
        assertEquals(targetProduct, productPurchased);
        assertThat(remainingChange, is(expectedChange));
        assertThat(remainingChange.size(), is(expectedChange.size()));
        assertThat(remainingChange, IsMapContaining.hasEntry(Coin.FIFTY_CENTS, 1));

        // check vending machine status
        // - current balance
        assertFalse(cashManager.hasCurrentBalance());
        // - selected product
        assertNull(productManager.getSelectedProduct());
        // - check cash stock
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.FIFTY_CENTS, 0));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.ONE, 3));
        // - check product stock
        assertThat(productStockStatus, IsMapContaining.hasEntry(Beverage.COKE, 0));

        // current cashStock: {TWENTY_CENTS=2, ONE=3, FIVE_CENTS=1, TWO=1, FIFTY_CENTS=0, TEN_CENTS=2}
        // goal: buy sprite inserting two coins of one - current balance = 2 - expected change = 0.6 (two coins of twenty and two coins of ten -
        // because there are no coins of fifty)
        // --------------------------------------------------------------------------------------------------------------------------------------------
        targetProduct = Beverage.SPRITE;
        userCash = Arrays.asList(Coin.ONE, Coin.ONE);
        expectedChange = new HashMap<>();
        expectedChange.put(Coin.TWENTY_CENTS, 2);
        expectedChange.put(Coin.TEN_CENTS, 2);
        expectedProductPrice = targetProduct.getPrice();

        // - user get available products
        availableProducts = vendingMachineApi.getAvailableProducts();
        assertTrue(availableProducts.contains(targetProduct));

        // - user select water and see its price
        productPrice = vendingMachineApi.selectProduct(targetProduct);
        assertEquals(expectedProductPrice, productPrice);
        assertEquals(targetProduct, productManager.getSelectedProduct());

        // - user insert two coins of one
        currentBalance = vendingMachineApi.insertCash(userCash.get(0));
        assertEquals(0, currentBalance.compareTo(BigDecimal.valueOf(1)));
        assertEquals(4, cashStockStatus.get(Coin.ONE));

        currentBalance = vendingMachineApi.insertCash(userCash.get(1));
        assertEquals(0, currentBalance.compareTo(BigDecimal.valueOf(2)));
        assertEquals(5, cashStockStatus.get(Coin.ONE));

        // - user confirm purchase of water
        result = vendingMachineApi.confirmPurchase();
        // check result
        productPurchased = result.getKey();
        remainingChange = result.getValue();
        assertEquals(targetProduct, productPurchased);
        assertThat(remainingChange, is(expectedChange));
        assertThat(remainingChange.size(), is(expectedChange.size()));
        assertThat(remainingChange, IsMapContaining.hasEntry(Coin.TWENTY_CENTS, 2));
        assertThat(remainingChange, IsMapContaining.hasEntry(Coin.TEN_CENTS, 2));

        // check vending machine status
        // - current balance
        assertFalse(cashManager.hasCurrentBalance());
        // - selected product
        assertNull(productManager.getSelectedProduct());
        // - check cash stock
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.TWENTY_CENTS, 0));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.TEN_CENTS, 0));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.ONE, 5));
        // - check product stock
        assertThat(productStockStatus, IsMapContaining.hasEntry(Beverage.SPRITE, 0));

        // current cashStock: {TWENTY_CENTS=0, ONE=5, FIVE_CENTS=1, TWO=1, FIFTY_CENTS=0, TEN_CENTS=0}
        // current balance = 1
        // selected product = water
        // goal: buy water inserting one coin of one - current balance = 1 - should be rejected because there's no change available
        // ----------------------------------------------------------------------------------------------------------------------------
        targetProduct = Beverage.WATER;
        userCash = Collections.singletonList(Coin.ONE);
        expectedProductPrice = targetProduct.getPrice();

        // - user get available products
        availableProducts = vendingMachineApi.getAvailableProducts();
        assertTrue(availableProducts.contains(targetProduct));

        // - user select water and see its price
        productPrice = vendingMachineApi.selectProduct(targetProduct);
        assertEquals(expectedProductPrice, productPrice);
        assertEquals(targetProduct, productManager.getSelectedProduct());

        // - user insert one coin of one
        currentBalance = vendingMachineApi.insertCash(userCash.get(0));
        assertEquals(0, currentBalance.compareTo(BigDecimal.valueOf(1)));
        assertEquals(6, cashStockStatus.get(Coin.ONE));

        // - user confirm purchase of water
        VendingMachineException thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.confirmPurchase());
        assertEquals("Not sufficient change to provide.", thrown.getMessage());

        // check vending machine status
        // - current balance
        assertTrue(cashManager.hasCurrentBalance());
        assertEquals(0, cashManager.getCurrentBalance().compareTo(BigDecimal.ONE));
        // - selected product
        assertEquals(targetProduct, productManager.getSelectedProduct());
        // - check cash stock
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.ONE, 6));
        // - check product stock
        assertThat(productStockStatus, IsMapContaining.hasEntry(Beverage.WATER, 1));

        // goal: buy coke - should be rejected because the product is not available
        // current balance = 1
        // selected product = water
        // --------------------------------------------------------------------------
        // - user get available products
        availableProducts = vendingMachineApi.getAvailableProducts();
        assertFalse(availableProducts.contains(Beverage.COKE));

        // - user select water and see its price
        thrown = assertThrows(VendingMachineException.class, () -> vendingMachineApi.selectProduct(Beverage.COKE));
        assertEquals("Product " + Beverage.COKE.getName() + " is not available in stock.", thrown.getMessage());

        // check vending machine status
        // - current balance
        assertTrue(cashManager.hasCurrentBalance());
        assertEquals(0, cashManager.getCurrentBalance().compareTo(BigDecimal.ONE));
        // - selected product
        assertEquals(Beverage.WATER, productManager.getSelectedProduct());
        // - check cash stock
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.ONE, 6));
        // - check product stock
        assertThat(productStockStatus, IsMapContaining.hasEntry(Beverage.WATER, 1));

        // user get refund to get 1
        // current cashStock: {TWENTY_CENTS=0, ONE=5, FIVE_CENTS=1, TWO=1, FIFTY_CENTS=0, TEN_CENTS=0}
        // current balance = 0
        // selected product = water
        // -----------------------------------------------------------------------------------------------
        final Map<Cash, Integer> expectedRefund = new HashMap<>();
        expectedRefund.put(Coin.ONE, 1);
        final Map<Cash, Integer> refund = vendingMachineApi.refund();
        assertThat(refund, is(expectedRefund));
        assertThat(refund.size(), is(expectedRefund.size()));
        assertThat(refund, IsMapContaining.hasEntry(Coin.ONE, 1));
        assertEquals(5, cashStockStatus.get(Coin.ONE));

        // check vending machine status (final status)
        // - current balance
        assertFalse(cashManager.hasCurrentBalance());
        // - selected product
        assertEquals(Beverage.WATER, productManager.getSelectedProduct());
        // - check cash stock
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.ONE, 5));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.TWO, 1));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.FIFTY_CENTS, 0));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.TWENTY_CENTS, 0));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.TEN_CENTS, 0));
        assertThat(cashStockStatus, IsMapContaining.hasEntry(Coin.FIVE_CENTS, 1));
        // - check product stock
        assertThat(productStockStatus, IsMapContaining.hasEntry(Beverage.WATER, 1));
        assertThat(productStockStatus, IsMapContaining.hasEntry(Beverage.COKE, 0));
        assertThat(productStockStatus, IsMapContaining.hasEntry(Beverage.SPRITE, 0));
    }
}
