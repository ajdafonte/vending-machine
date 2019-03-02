package com.dexma.hometest.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;


/**
 * BalanceResultTest class - Test BalanceResult enum.
 */
class BalanceResultTest
{
    // test getBalanceResultByValue - ok values
    @Test
    void givenValidIntValues_whenGetBalanceResultByValue_thenReturnCorrectValue()
    {
        // given
        final int[] testCases = {
            -1, 1, 0
        };
        final BalanceResult[] expectedValues = {
            BalanceResult.BELOW_AMOUNT, BalanceResult.ABOVE_AMOUNT, BalanceResult.EXACT_AMOUNT
        };

        // when + then
        for (int i = 0; i < testCases.length; i++)
        {
            assertEquals(expectedValues[i], BalanceResult.getBalanceResultByValue(testCases[i]));
        }
    }

    // test getBalanceResultByValue - nok values
    @Test
    void giveInvalidIntValues_whenGetBalanceResultByValue_thenReturnNullValue()
    {
        // given
        final int[] testCases = {
            -40, 11, 3, 2
        };

        // when + then
        for (final int testCase : testCases)
        {
            assertNull(BalanceResult.getBalanceResultByValue(testCase));
        }
    }

}
