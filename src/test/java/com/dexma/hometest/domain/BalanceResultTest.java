package com.dexma.hometest.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;


/**
 * BalanceResultTest class - Test BalanceResult enum.
 */
public class BalanceResultTest
{
    // test getBalanceResultByValue - ok values
    @Test
    public void givenValidIntValues_whenGetBalanceResultByValue_thenReturnCorrectValue()
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
    public void giveInvalidIntValues_whenGetBalanceResultByValue_thenReturnNullValue()
    {
        // given
        final int[] testCases = {
            -40, 11, 3, 2
        };

        // when + then
        for (int i = 0; i < testCases.length; i++)
        {
            assertNull(BalanceResult.getBalanceResultByValue(testCases[i]));
        }
    }

}
