package com.dexma.hometest.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Coin;


/**
 * GreedyChangeProcessorTest class - GreedyChangeProcessor test class.
 */
public class GreedyChangeProcessorTest
{
    private GreedyChangeProcessor greedyChangeProcessor;

    @BeforeEach
    public void setUp()
    {
        this.greedyChangeProcessor = new GreedyChangeProcessor();
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

//    private Stock<Cash> generateCashStock(final Map<Cash, Integer> values)
//    {
//        return new Stock<>(values);
//    }

    // test - cash stock with enough cash and valid changeAmount -- return change (other case)
    // test - cash stock with enough cash and valid changeAmount -- return change (other case)
    @Test
    public void givenCashStockAndHighAmount_whenProcessChange_thenReturnCollectionWithCashAndQuantity()
    {
        // given
        final BigDecimal mockAmount = BigDecimal.valueOf(10);
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(2, 2, 5, 2, 4, 2);
        final Map<Cash, Integer> expectedChange = new HashMap<>();
        expectedChange.put(Coin.TWENTY_CENTS, 5);
        expectedChange.put(Coin.FIFTY_CENTS, 2);
        expectedChange.put(Coin.ONE, 4);
        expectedChange.put(Coin.TWO, 2);

        // when
        final Map<Cash, Integer> change = greedyChangeProcessor.processChange(mockCashStockMap, mockAmount);

        // then
        assertEquals(expectedChange.size(), change.size());
        assertEquals(expectedChange, change);
    }

    @Test
    public void givenCashStockAndLowAmount_whenProcessChange_thenReturnCollectionWithCashAndQuantity()
    {
        // given
        final BigDecimal mockAmount = BigDecimal.valueOf(0.75);
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(2, 2, 5, 2, 4, 2);
        final Map<Cash, Integer> expectedChange = new HashMap<>();
        expectedChange.put(Coin.TWENTY_CENTS, 1);
        expectedChange.put(Coin.FIFTY_CENTS, 1);
        expectedChange.put(Coin.FIVE_CENTS, 1);

        // when
        final Map<Cash, Integer> change = greedyChangeProcessor.processChange(mockCashStockMap, mockAmount);

        // then
        assertEquals(expectedChange.size(), change.size());
        assertEquals(expectedChange, change);
    }

    @Test
    public void givenCashStockAndAmountEqualsToCertainCashItem_whenProcessChange_thenReturnCollectionWithCashAndQuantity()
    {
        // given
        final BigDecimal mockAmount = BigDecimal.valueOf(0.1);
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(2, 2, 5, 2, 4, 2);
        final Map<Cash, Integer> expectedChange = new HashMap<>();
        expectedChange.put(Coin.TEN_CENTS, 1);

        // when
        final Map<Cash, Integer> change = greedyChangeProcessor.processChange(mockCashStockMap, mockAmount);

        // then
        assertEquals(expectedChange.size(), change.size());
        assertEquals(expectedChange, change);
    }

    @Test
    public void givenCashStockAndAmountMultipleOfCashItem_whenProcessChange_thenReturnCollectionWithCashAndQuantity()
    {
        // given
        final BigDecimal mockAmount = BigDecimal.valueOf(8);
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(2, 2, 5, 2, 4, 4);
        final Map<Cash, Integer> expectedChange = new HashMap<>();
        expectedChange.put(Coin.TWO, 4);

        // when
        final Map<Cash, Integer> change = greedyChangeProcessor.processChange(mockCashStockMap, mockAmount);

        // then
        assertEquals(expectedChange.size(), change.size());
        assertEquals(expectedChange, change);
    }

    @Test
    public void givenCashStockAndAmount_whenProcessChange_thenReturnCollectionWithCashAndQuantity()
    {
        // given
        final BigDecimal mockAmount = BigDecimal.valueOf(0.60);
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(2, 2, 2, 2, 2, 2);
        final Map<Cash, Integer> expectedChange = new HashMap<>();
        expectedChange.put(Coin.TEN_CENTS, 1);
        expectedChange.put(Coin.FIFTY_CENTS, 1);

        // when
        final Map<Cash, Integer> change = greedyChangeProcessor.processChange(mockCashStockMap, mockAmount);

        // then
        assertEquals(expectedChange.size(), change.size());
        assertEquals(expectedChange, change);
    }

    // test - cash stock will have some cash not available and valid changeAmount -- return change but with diff result from above
    @Test
    public void givenCashStockWithLimitedItemsAndAmount_whenProcessChange_thenReturnCollectionWithCashAndQuantityWithoutDefaultValue()
    {
        // given
        final BigDecimal mockAmount = BigDecimal.valueOf(0.60);
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(2, 2, 3, 0, 2, 2);
        final Map<Cash, Integer> expectedChange = new HashMap<>();
        expectedChange.put(Coin.TWENTY_CENTS, 3);

        // when
        final Map<Cash, Integer> change = greedyChangeProcessor.processChange(mockCashStockMap, mockAmount);

        // then
        assertEquals(expectedChange.size(), change.size());
        assertEquals(expectedChange, change);
    }

    // test - cash stock with enough cash and invalid changeAmount -- not able to return change
    @Test
    public void givenCashStockAndInvalidAmount_whenProcessChange_thenReturnNullValue()
    {
        // given
        final BigDecimal[] testCases = {BigDecimal.valueOf(-1.5), BigDecimal.ZERO};
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(2, 2, 2, 2, 2, 2);

        // when + then
        Arrays.stream(testCases).map(testCase -> greedyChangeProcessor.processChange(mockCashStockMap, testCase)).forEach(Assertions::assertNull);
    }

    // test - cash stock will have some cash not available and valid changeAmount -- not able to return change
    @Test
    public void givenCashStockWithLimitedItemsAndAmount_whenProcessChange_thenReturnNullValue()
    {
        // given
        final BigDecimal[] testCases = {BigDecimal.valueOf(20), BigDecimal.valueOf(300)};
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(2, 2, 2, 2, 2, 2);

        // when + then
        for (int i = 0; i < testCases.length; i++)
        {
            assertNull(greedyChangeProcessor.processChange(mockCashStockMap, testCases[i]));
        }
    }

    // test - with null or empty cash stock and valid changeAmount - not able to return change
    @Test
    public void givenInvalidCashStockAndAmount_whenProcessChange_thenReturnNullValue()
    {
        // given
        final BigDecimal mockAmount = BigDecimal.valueOf(1.5);
        final Map[] mockCashStockMap = new Map[] {
            null, Collections.emptyMap()
        };

        // when + then
        for (final Map map : mockCashStockMap)
        {
            assertNull(greedyChangeProcessor.processChange(map, mockAmount));
        }
    }

    // test - with cash stock with items = 0 and valid changeAmount - not able to return change
    @Test
    public void givenCashStockWithEmptyItemsAndAmount_whenProcessChange_thenReturnNullValue()
    {
        // given
        final BigDecimal mockAmount = BigDecimal.valueOf(1.5);
        final Map<Cash, Integer> mockCashStockMap = generateCashStockValues(0, 0, 0, 0, 0, 0);

        // when + then
        assertNull(greedyChangeProcessor.processChange(mockCashStockMap, mockAmount));
    }

}
