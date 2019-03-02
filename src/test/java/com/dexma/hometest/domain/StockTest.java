package com.dexma.hometest.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 *
 */
class StockTest
{
    private static final String MOCK_ITEM1 = "mockItem1";
    private static final String MOCK_ITEM2 = "mockItem2";
    private static final String MOCK_ITEM3 = "mockItem3";
    private static final String MOCK_UNKNOWN_ITEM = "unknown";
    private static final int MOCK_QUANTITY1 = 1;
    private static final int MOCK_QUANTITY2 = 2;
    private static final int MOCK_EMPTY_QUANTITY = 0;

    private static Stock<String> MOCK_STOCK;

    private Map<String, Integer> generateStockMap()
    {
        final Map<String, Integer> mockStockMap = new HashMap<>();
        mockStockMap.put(MOCK_ITEM1, MOCK_QUANTITY1);
        mockStockMap.put(MOCK_ITEM2, MOCK_QUANTITY2);
        return mockStockMap;
    }

    private Map<String, Integer> generateStockMapItemsWithNoQuantity()
    {
        final Map<String, Integer> mockStockMap = new HashMap<>();
        mockStockMap.put(MOCK_ITEM1, MOCK_EMPTY_QUANTITY);
        mockStockMap.put(MOCK_ITEM2, MOCK_EMPTY_QUANTITY);
        return mockStockMap;
    }

    @BeforeEach
    void setUp()
    {
        MOCK_STOCK = new Stock<>(generateStockMap());
    }

    // getItem - ok
    @Test
    void givenExistentItem_whenGetItem_thenReturnExpectedItem()
    {
        // given

        // when + then
        assertEquals(MOCK_ITEM1, MOCK_STOCK.getItem(MOCK_ITEM1));
        assertEquals(MOCK_ITEM2, MOCK_STOCK.getItem(MOCK_ITEM2));
    }

    // getItem - nok
    @Test
    void givenNotExistentItem_whenGetItem_thenReturnNullValue()
    {
        // given

        // when + then
        assertNull(MOCK_STOCK.getItem(MOCK_UNKNOWN_ITEM));
    }

    // getItem - nok
    @Test
    void givenInvalidItem_whenGetItem_thenReturnNullValue()
    {
        // given

        // when + then
        assertNull(MOCK_STOCK.getItem(null));
    }

    // insertItem - ok
    @Test
    void givenNewItem_whenInsertItem_thenExpectNewItemInserted()
    {
        // given

        // when
        MOCK_STOCK.insertItem(MOCK_ITEM3);

        // then
        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();
        assertEquals(3, stockMap.size());
        assertEquals(1, stockMap.get(MOCK_ITEM3));
    }

    @Test
    void givenExistentItem_whenInsertItem_thenExpectExistentItemWithQuantityIncremented()
    {
        // given
        final int expectedQuantity = MOCK_QUANTITY1 + 1;

        // when
        MOCK_STOCK.insertItem(MOCK_ITEM1);

        // then
        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();
        assertEquals(2, stockMap.size());
        assertEquals(expectedQuantity, stockMap.get(MOCK_ITEM1));
    }

    @Test
    void givenExistentItem_whenInsertItemWithSeveralQuantity_thenExpectExistentItemWithRespectiveQuantityIncremented()
    {
        // given
        final int mockQuantityToIncrease = 4;
        final int expectedQuantity = MOCK_QUANTITY1 + mockQuantityToIncrease;

        // when
        MOCK_STOCK.insertItem(MOCK_ITEM1, mockQuantityToIncrease);

        // then
        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();
        assertEquals(2, stockMap.size());
        assertEquals(expectedQuantity, stockMap.get(MOCK_ITEM1));
    }

    // insertItem - nok
//    @Test
//    void givenInvalidItem_whenInsertItem_thenExpectItemNotInserted()
//    {
//        // given
////        final Stock<String> MOCK_STOCK = new Stock<>(MOCK_STOCK_MAP);
//
//        // when
//        MOCK_STOCK.insertItem(null);
//
//        // then
//        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();
//        assertEquals(2, stockMap.size());
//    }

    // deleteItem - ok
    @Test
    void givenExistentItem_whenDeleteItem_thenExpectItemWithQuantityDecreased()
    {
        // given
        final int expectedQuantity = MOCK_QUANTITY1 - 1;

        // when
        MOCK_STOCK.deleteItem(MOCK_ITEM1);

        // then
        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();
        assertEquals(2, stockMap.size());
        assertThat(stockMap, IsMapContaining.hasEntry(MOCK_ITEM1, expectedQuantity));
    }

    @Test
    void givenExistentItem_whenDeleteItemWithSeveralQuantity_thenExpectExistentItemWithRespectiveQuantityDecreased()
    {
        // given
        final int mockQuantityToDecrease = 2;
        final int expectedQuantity = MOCK_QUANTITY2 - mockQuantityToDecrease;

        // when
        MOCK_STOCK.deleteItem(MOCK_ITEM2, mockQuantityToDecrease);

        // then
        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();
        assertEquals(2, stockMap.size());
        assertThat(stockMap, IsMapContaining.hasEntry(MOCK_ITEM2, expectedQuantity));
    }

    @Test
    void givenExistentItem_whenDeleteItemWithMoreThanAvailableQuantity_thenExpectExistentItemWithZeroQuantity()
    {
        // given
        final int mockQuantityToDecrease = 10;
        final int expectedQuantity = 0;

        // when
        MOCK_STOCK.deleteItem(MOCK_ITEM2, mockQuantityToDecrease);

        // then
        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();
        assertEquals(2, stockMap.size());
        assertThat(stockMap, IsMapContaining.hasEntry(MOCK_ITEM2, expectedQuantity));
    }

    // deleteItem - nok
    @Test
    void givenInvalidItem_whenDeleteItem_thenExpectItemNotRemoved()
    {
        // given

        // when
        MOCK_STOCK.deleteItem(null);

        // then
        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();
        assertEquals(2, stockMap.size());
    }

    // hasItem - ok
    @Test
    void givenExistentItem_whenHasItem_thenReturnTrueValue()
    {
        // when + then
        assertTrue(MOCK_STOCK.hasItem(MOCK_ITEM1));
        assertTrue(MOCK_STOCK.hasItem(MOCK_ITEM2));
    }

    @Test
    void givenExistentItemWithNoQuantity_whenHasItem_thenReturnFalseValue()
    {
        // given
        final Stock<String> mockStock = new Stock<>(generateStockMapItemsWithNoQuantity());

        // when + then
        assertFalse(mockStock.hasItem(MOCK_ITEM1));
        assertFalse(mockStock.hasItem(MOCK_ITEM2));
        final Map<String, Integer> stockMap = mockStock.getStockMap();
        assertEquals(2, stockMap.size());
        assertThat(stockMap, IsMapContaining.hasEntry(MOCK_ITEM1, MOCK_EMPTY_QUANTITY));
        assertThat(stockMap, IsMapContaining.hasEntry(MOCK_ITEM2, MOCK_EMPTY_QUANTITY));
    }

    // hasItem - nok
    @Test
    void givenInvalidItem_whenHasItem_thenReturnFalseValue()
    {
        // when + then
        assertFalse(MOCK_STOCK.hasItem(null));
    }

    // getStockMap - ok
    @Test
    void givenStock_whenGetStockMaps_thenReturnExpectedValue()
    {
        // given
        final Map<String, Integer> expectedStockMap = generateStockMap();

        // when
        final Map<String, Integer> stockMap = MOCK_STOCK.getStockMap();

        // then
        assertThat(stockMap, is(expectedStockMap));
        assertThat(stockMap.size(), is(2));
        assertThat(stockMap, IsMapContaining.hasEntry(MOCK_ITEM1, MOCK_QUANTITY1));
        assertThat(stockMap, IsMapContaining.hasEntry(MOCK_ITEM2, MOCK_QUANTITY2));
    }
}
