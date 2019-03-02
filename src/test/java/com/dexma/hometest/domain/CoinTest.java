package com.dexma.hometest.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;


/**
 * CoinTest class - Test Coin class.
 */
class CoinTest
{
    // test get value
    @Test
    void givenEnumValues_whenGetValue_returnCorrectValue()
    {
        assertEquals(BigDecimal.valueOf(0.05), Coin.FIVE_CENTS.getValue());
        assertEquals(BigDecimal.valueOf(0.1), Coin.TEN_CENTS.getValue());
        assertEquals(BigDecimal.valueOf(0.2), Coin.TWENTY_CENTS.getValue());
        assertEquals(BigDecimal.valueOf(0.5), Coin.FIFTY_CENTS.getValue());
        assertEquals(BigDecimal.valueOf(1), Coin.ONE.getValue());
        assertEquals(BigDecimal.valueOf(2), Coin.TWO.getValue());
    }

    // test get valid values
    @Test
    void givenEnumValues_whenGetValidCoins_returnCorrectValue()
    {
        // given
        final Coin[] expectedResult = {Coin.FIVE_CENTS, Coin.TEN_CENTS, Coin.TWENTY_CENTS,
            Coin.FIFTY_CENTS, Coin.ONE, Coin.TWO};

        // when
        final Coin[] result = Coin.getValidCoins();

        assertEquals(expectedResult.length, result.length);
        assertTrue(Arrays.asList(expectedResult).containsAll(Arrays.asList(result)));
    }
}
