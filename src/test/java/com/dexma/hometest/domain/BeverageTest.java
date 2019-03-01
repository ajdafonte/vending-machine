package com.dexma.hometest.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;


/**
 * BeverageTest class - Test Beverage class.
 */
public class BeverageTest
{
    // test get name
    @Test
    public void givenEnumValues_whenGetName_returnCorrectValue()
    {
        assertEquals("Coke", Beverage.COKE.getName());
        assertEquals("Sprite", Beverage.SPRITE.getName());
        assertEquals("Water", Beverage.WATER.getName());
    }

    // test get price
    @Test
    public void givenEnumValues_whenGetPrice_returnCorrectValue()
    {
        assertEquals(BigDecimal.valueOf(1.5), Beverage.COKE.getPrice());
        assertEquals(BigDecimal.valueOf(1.4), Beverage.SPRITE.getPrice());
        assertEquals(BigDecimal.valueOf(0.9), Beverage.WATER.getPrice());
    }

    // test get valid values
    @Test
    public void givenEnumValues_whenGetValidBeverages_returnCorrectValue()
    {
        // given
        final Beverage[] expectedResult = {Beverage.COKE, Beverage.WATER, Beverage.SPRITE};

        // when
        final Beverage[] result = Beverage.getValidBeverages();

        assertEquals(expectedResult.length, result.length);
        assertTrue(Arrays.asList(expectedResult).containsAll(Arrays.asList(result)));
    }

}
