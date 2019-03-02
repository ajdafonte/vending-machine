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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dexma.hometest.domain.BalanceResult;
import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Coin;
import com.dexma.hometest.domain.Stock;
import com.dexma.hometest.error.CashManagerException;


/**
 * CashManagerTest class - CashManager test class.
 */
@ExtendWith(MockitoExtension.class)
class CashManagerTest
{
    @Mock
    private Stock<Cash> mockCashStock;

    @Mock
    private ChangeProcessorFactory mockChangeProcessorFactory;

    @Mock
    private GreedyChangeProcessor mockGreedyChangeProcessor;

    private static final Map<Cash, Integer> MOCK_CASH_STOCK_MAP;

    private CashManager cashManager;

    static
    {
        MOCK_CASH_STOCK_MAP = generateCashStockValues();
    }

    private static Map<Cash, Integer> generateCashStockValues()
    {
        final Map<Cash, Integer> values = new HashMap<>();
        values.put(Coin.FIVE_CENTS, 2);
        values.put(Coin.TEN_CENTS, 2);
        values.put(Coin.TWENTY_CENTS, 2);
        values.put(Coin.FIFTY_CENTS, 2);
        values.put(Coin.ONE, 2);
        values.put(Coin.TWO, 2);

        return values;
    }

    @BeforeEach
    void setUp()
    {
        this.cashManager = new CashManager(mockCashStock, mockChangeProcessorFactory);
    }

    // getValidCashItems
    @Test
    void givenCashItems_whenGetValidCashItems_thenReturnAllCashItems()
    {
        // given
        final List<Cash> expectedCashItems = Arrays.asList(Coin.FIVE_CENTS, Coin.TEN_CENTS, Coin.TWENTY_CENTS,
            Coin.FIFTY_CENTS, Coin.ONE, Coin.TWO);

        // when
        final List<Cash> result = cashManager.getValidCashItems();

        // then
        assertThat(result, not(IsEmptyCollection.empty()));
        assertThat(result, is(expectedCashItems));
        assertThat(result.size(), is(expectedCashItems.size()));
    }

    // getCashStock
    @Test
    void givenCashStock_whenGetCashStock_thenReturnMapWithExpectedCashStock()
    {
        // given
        when(mockCashStock.getStockMap()).thenReturn(MOCK_CASH_STOCK_MAP);

        // when
        final Map<Cash, Integer> result = cashManager.getCashStock();

        // then
        assertThat(result, is(MOCK_CASH_STOCK_MAP));
        assertThat(result.size(), is(MOCK_CASH_STOCK_MAP.size()));
    }

    // insert
    // - null or empty map
    @Test
    void givenInvalidInputMap_whenInsertCashItemsInStock_thenThrowSpecificException()
    {
        // given
        final Map[] testCases = new Map[] {null, Collections.emptyMap()};
        final String expectedMsg = "At least one cash item should be provided.";

        // when + then
        for (final Map testCase : testCases)
        {
            final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.insertCashItemsInStock(testCase));
            assertEquals(expectedMsg, thrown.getMessage());
        }
    }

    // - map with invalid product
    @Test
    void givenInputMapWithInvalidCashItem_whenInsertCashItemsInStock_thenThrowSpecificException()
    {
        // given
        final Map<Cash, Integer> mockMap = new HashMap<>();
        mockMap.put(Coin.TWO, 1);
        mockMap.put(null, 1);
        final String expectedMsg = "Invalid cash item specified.";

        // when + then
        final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.insertCashItemsInStock(mockMap));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - map with invalid quantity
    @Test
    void givenInputMapWithCashItemWithInvalidQuantity_whenInsertCashItemsInStock_thenThrowSpecificException()
    {
        // given
        final Map<Cash, Integer> mockMap = new HashMap<>();
        mockMap.put(Coin.TWO, -1);
        mockMap.put(Coin.TEN_CENTS, 1);
        final String expectedMsg = "Invalid quantity specified.";

        // when + then
        final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.insertCashItemsInStock(mockMap));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - map with all ok
    @Test
    void givenValidInputMap_whenInsertCashItemsInStock_thenExpectToInsertCashItemsInStock()
    {
        // given
        final Map<Cash, Integer> mockMap = new HashMap<>();
        mockMap.put(Coin.TWO, 3);
        mockMap.put(Coin.TEN_CENTS, 2);
        doNothing().when(mockCashStock).insertItem(any(Cash.class), anyInt());

        // when + then
        assertDoesNotThrow(() -> cashManager.insertCashItemsInStock(mockMap));
        verify(mockCashStock, times(1)).insertItem(Coin.TWO, 3);
        verify(mockCashStock, times(1)).insertItem(Coin.TEN_CENTS, 2);
    }

    // remove
    // - null or empty map
    @Test
    void givenInvalidInputMap_whenRemoveCashItemsInStock_thenThrowSpecificException()
    {
        // given
        final Map[] testCases = new Map[] {null, Collections.emptyMap()};
        final String expectedMsg = "At least one cash item should be provided.";

        // when + then
        for (final Map testCase : testCases)
        {
            final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.removeCashItemsFromStock(testCase));
            assertEquals(expectedMsg, thrown.getMessage());
        }
    }

    // - map with invalid product
    @Test
    void givenInputMapWithInvalidCashItem_whenRemoveCashItemsInStock_thenThrowSpecificException()
    {
        // given
        final Map<Cash, Integer> mockMap = new HashMap<>();
        mockMap.put(Coin.TWO, 1);
        mockMap.put(null, 1);
        final String expectedMsg = "Invalid cash item specified.";

        // when + then
        final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.removeCashItemsFromStock(mockMap));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - map with invalid quantity
    @Test
    void givenInputMapWithCashItemWithInvalidQuantity_whenRemoveCashItemsInStock_thenThrowSpecificException()
    {
        // given
        final Map<Cash, Integer> mockMap = new HashMap<>();
        mockMap.put(Coin.TWO, -1);
        mockMap.put(Coin.TEN_CENTS, 1);
        final String expectedMsg = "Invalid quantity specified.";

        // when + then
        final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.removeCashItemsFromStock(mockMap));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // - map with all ok
    @Test
    void givenValidInputMap_whenRemoveCashItemsInStock_thenExpectToRemoveCashItemsInStock()
    {
        // given
        final Map<Cash, Integer> mockMap = new HashMap<>();
        mockMap.put(Coin.TWO, 2);
        mockMap.put(Coin.TEN_CENTS, 1);
        doNothing().when(mockCashStock).deleteItem(any(Cash.class), anyInt());

        // when + then
        assertDoesNotThrow(() -> cashManager.removeCashItemsFromStock(mockMap));
        verify(mockCashStock, times(1)).deleteItem(Coin.TWO, 2);
        verify(mockCashStock, times(1)).deleteItem(Coin.TEN_CENTS, 1);
    }

    // getCashItemsForChange
    // valid input
    @Test
    void givenPositiveChangeToRefund_whenGetCashItemsForChange_thenReturnMapWithExpectedCashItems()
    {
        // given
        final BigDecimal mockValue = BigDecimal.valueOf(4);
        final Map<Cash, Integer> expectedResult = new HashMap<>();
        final Coin expectedItem = Coin.TWO;
        final int expectedQuantity = 2;
        expectedResult.put(expectedItem, expectedQuantity);
        when(mockChangeProcessorFactory.getChangeProcessor()).thenReturn(mockGreedyChangeProcessor);
        when(mockCashStock.getStockMap()).thenReturn(MOCK_CASH_STOCK_MAP);
        when(mockGreedyChangeProcessor.processChange(MOCK_CASH_STOCK_MAP, mockValue)).thenReturn(expectedResult);

        // when
        final Map<Cash, Integer> result = cashManager.getCashItemsForChange(mockValue);

        // then
        verify(mockGreedyChangeProcessor, times(1)).processChange(MOCK_CASH_STOCK_MAP, mockValue);
        assertThat(result, is(expectedResult));
        assertThat(result.size(), is(expectedResult.size()));
        assertThat(result, IsMapContaining.hasEntry(expectedItem, expectedQuantity));
    }

    @Test
    void givenNegativeChangeToRefund_whenGetCashItemsForChange_thenReturnNullValue()
    {
        // given
        final BigDecimal mockValue = BigDecimal.valueOf(-4);
        when(mockChangeProcessorFactory.getChangeProcessor()).thenReturn(mockGreedyChangeProcessor);
        when(mockCashStock.getStockMap()).thenReturn(MOCK_CASH_STOCK_MAP);
        when(mockGreedyChangeProcessor.processChange(MOCK_CASH_STOCK_MAP, mockValue)).thenReturn(null);

        // when
        final Map<Cash, Integer> result = cashManager.getCashItemsForChange(mockValue);

        // then
        verify(mockGreedyChangeProcessor, times(1)).processChange(MOCK_CASH_STOCK_MAP, mockValue);
        assertNull(result);
    }

    // invalid input
    @Test
    void givenInvalidChangeToRefund_whenGetCashItemsForChange_thenReturnNullValue()
    {
        // given
        when(mockChangeProcessorFactory.getChangeProcessor()).thenReturn(mockGreedyChangeProcessor);
        when(mockCashStock.getStockMap()).thenReturn(MOCK_CASH_STOCK_MAP);
        when(mockGreedyChangeProcessor.processChange(MOCK_CASH_STOCK_MAP, null)).thenReturn(null);

        // when
        final Map<Cash, Integer> result = cashManager.getCashItemsForChange(null);

        // then
        verify(mockGreedyChangeProcessor, times(1)).processChange(MOCK_CASH_STOCK_MAP, null);
        assertNull(result);
    }

    // calculateRefund
    // - cash balance positive
    @Test
    void givenPositiveCurrentBalance_whenCalculateRefund_thenReturnMapWithExpectedCashItemsAndZeroCurrentBalance()
    {
        // given
        final BigDecimal mockValue = BigDecimal.valueOf(4);
        cashManager.setCurrentBalance(mockValue);
        final Map<Cash, Integer> expectedResult = new HashMap<>();
        final Coin expectedItem = Coin.TWO;
        final int expectedQuantity = 2;
        expectedResult.put(expectedItem, expectedQuantity);
        when(mockChangeProcessorFactory.getChangeProcessor()).thenReturn(mockGreedyChangeProcessor);
        when(mockCashStock.getStockMap()).thenReturn(MOCK_CASH_STOCK_MAP);
        when(mockGreedyChangeProcessor.processChange(MOCK_CASH_STOCK_MAP, mockValue)).thenReturn(expectedResult);
        doNothing().when(mockCashStock).deleteItem(expectedItem, expectedQuantity);

        // when
        final Map<Cash, Integer> result = cashManager.calculateRefund();

        // then
        verify(mockGreedyChangeProcessor, times(1)).processChange(MOCK_CASH_STOCK_MAP, mockValue);
        verify(mockCashStock, times(1)).deleteItem(expectedItem, expectedQuantity);
        assertThat(result, is(expectedResult));
        assertThat(result.size(), is(expectedResult.size()));
        assertThat(result, IsMapContaining.hasEntry(expectedItem, expectedQuantity));
        assertEquals(BigDecimal.ZERO, cashManager.getCurrentBalance());
    }

    // - cash balance not positive
    @Test
    void givenZeroCurrentBalance_whenCalculateRefund_thenReturnNullValue()
    {
        // given
        final BigDecimal mockValue = BigDecimal.ZERO;
        cashManager.setCurrentBalance(mockValue);
        when(mockChangeProcessorFactory.getChangeProcessor()).thenReturn(mockGreedyChangeProcessor);
        when(mockCashStock.getStockMap()).thenReturn(MOCK_CASH_STOCK_MAP);
        when(mockGreedyChangeProcessor.processChange(MOCK_CASH_STOCK_MAP, mockValue)).thenReturn(null);

        // when
        final Map<Cash, Integer> result = cashManager.calculateRefund();

        // then
        verify(mockGreedyChangeProcessor, times(1)).processChange(MOCK_CASH_STOCK_MAP, mockValue);
        assertNull(result);
        assertEquals(BigDecimal.ZERO, cashManager.getCurrentBalance());
    }

    // receiveCash
    // - cash item ok
    @Test
    void givenValidCashItem_whenReceiveCash_thenReturnExpectedCurrentBalance()
    {
        // given
        final Cash mockCash = Coin.FIFTY_CENTS;
        final BigDecimal mockValue = BigDecimal.valueOf(1);
        cashManager.setCurrentBalance(mockValue);
        final BigDecimal expectedResult = BigDecimal.valueOf(1.5);
        doNothing().when(mockCashStock).insertItem(any(Cash.class));

        // when
        final BigDecimal result = cashManager.receiveCash(mockCash);

        // then
        verify(mockCashStock, times(1)).insertItem(Coin.FIFTY_CENTS);
        assertEquals(expectedResult, result);
    }

    // - cash item nok
    @Test
    void givenInvalidCashItem_whenReceiveCash_thenThrowSpecificException()
    {
        // given
        final Cash mockCash = null;
        final BigDecimal mockValue = BigDecimal.valueOf(1);
        cashManager.setCurrentBalance(mockValue);
        final String expectedMsg = "A valid cash item should be provided.";

        // when + then
        verify(mockCashStock, times(0)).insertItem(any(Cash.class));
        final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.receiveCash(mockCash));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // isPossibleToPurchaseProduct
    // - case below
    @Test
    void givenProductPriceBiggerThanCurrentBalance_whenCheckIsPossibleToPurchaseProduct_thenReturnExpectedResult()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.valueOf(0.30));
        final BigDecimal mockProductPrice = BigDecimal.valueOf(0.90);
        final BalanceResult expectedResult = BalanceResult.BELOW_AMOUNT;

        // when
        final BalanceResult result = cashManager.isPossibleToPurchaseProduct(mockProductPrice);

        // then
        assertEquals(expectedResult, result);
    }

    // - case above
    @Test
    void givenProductPriceSmallerThanCurrentBalance_whenCheckIsPossibleToPurchaseProduct_thenReturnExpectedResult()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.valueOf(0.90));
        final BigDecimal mockProductPrice = BigDecimal.valueOf(0.30);
        final BalanceResult expectedResult = BalanceResult.ABOVE_AMOUNT;

        // when
        final BalanceResult result = cashManager.isPossibleToPurchaseProduct(mockProductPrice);

        // then
        assertEquals(expectedResult, result);
    }

    // - case exact
    @Test
    void givenProductPriceEqualToCurrentBalance_whenCheckIsPossibleToPurchaseProduct_thenReturnExpectedResult()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.valueOf(0.90));
        final BigDecimal mockProductPrice = BigDecimal.valueOf(0.90);
        final BalanceResult expectedResult = BalanceResult.EXACT_AMOUNT;

        // when
        final BalanceResult result = cashManager.isPossibleToPurchaseProduct(mockProductPrice);

        // then
        assertEquals(expectedResult, result);
    }

    // - case null
    @Test
    void givenInvalidProductPrice_whenCheckIsPossibleToPurchaseProduct_thenThrowSpecificException()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.valueOf(0.90));
        final BigDecimal mockProductPrice = null;
        final String expectedMsg = "Invalid product price.";

        // when + then
        final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.isPossibleToPurchaseProduct(mockProductPrice));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // calculateRemainingChange
    // - current balance > 0
    @Test
    void givenPositiveCurrentBalance_whenCalculateRemainingChange_thenReturnExpectedValue()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.ONE);
        final BigDecimal mockProductPrice = BigDecimal.valueOf(0.30);
        final BigDecimal expectedResult = BigDecimal.valueOf(0.70);

        // when
        final BigDecimal result = cashManager.calculateRemainingChange(mockProductPrice);

        // then
        assertEquals(expectedResult, result);
    }

    // - current balance < 0
    @Test
    void givenZeroCurrentBalance_whenCalculateRemainingChange_thenReturnZero()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.ZERO);
        final BigDecimal mockProductPrice = BigDecimal.valueOf(0.30);
        final BigDecimal expectedResult = BigDecimal.ZERO;

        // when
        final BigDecimal result = cashManager.calculateRemainingChange(mockProductPrice);

        // then
        assertEquals(expectedResult, result);
    }

    // - input null value
    @Test
    void givenCurrentBalanceAndNullProductPrice_whenCalculateRemainingChange_thenThrowSpecificException()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.ONE);
        final BigDecimal mockProductPrice = null;
        final String expectedMsg = "Invalid product price.";

        // when + then
        final CashManagerException thrown = assertThrows(CashManagerException.class, () -> cashManager.calculateRemainingChange(mockProductPrice));
        assertEquals(expectedMsg, thrown.getMessage());
    }

    // hasCurrentBalance
    // - balance > 0
    @Test
    void givenPositiveCurrentBalance_whenCheckIfHasCurrentBalance_thenReturnTrue()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.ONE);

        // when + then
        assertTrue(cashManager.hasCurrentBalance());
    }

    // - balance = 0
    @Test
    void givenZeroCurrentBalance_whenCheckIfHasCurrentBalance_thenReturnFalse()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.ZERO);

        // when + then
        assertFalse(cashManager.hasCurrentBalance());
    }

    // - balance < 0
    @Test
    void givenNegativeCurrentBalance_whenCheckIfHasCurrentBalance_thenReturnFalse()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.valueOf(-1.5));

        // when + then
        assertFalse(cashManager.hasCurrentBalance());
    }

    // resetCurrentBalance
    @Test
    void givenCurrentBalance_whenResetCurrentBalance_thenCurrentBalanceShouldBeZero()
    {
        // given
        cashManager.setCurrentBalance(BigDecimal.ONE);

        // when
        cashManager.resetCurrentBalance();

        // then
        assertEquals(BigDecimal.ZERO, cashManager.getCurrentBalance());
    }
}
