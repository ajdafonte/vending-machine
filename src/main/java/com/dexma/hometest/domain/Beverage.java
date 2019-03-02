package com.dexma.hometest.domain;

import java.math.BigDecimal;


/**
 * Beverage enum - Describes and contains details about Beverages that can be sold.
 */
public enum Beverage implements Product
{
    COKE
        {
            @Override
            public String getName()
            {
                return "Coke";
            }

            @Override
            public BigDecimal getPrice()
            {
                return BigDecimal.valueOf(1.5);
            }
        },
    SPRITE
        {
            @Override
            public String getName()
            {
                return "Sprite";
            }

            @Override
            public BigDecimal getPrice()
            {
                return BigDecimal.valueOf(1.4);
            }
        },
    WATER
        {
            @Override
            public String getName()
            {
                return "Water";
            }

            @Override
            public BigDecimal getPrice()
            {
                return BigDecimal.valueOf(0.9);
            }
        };

    public static Beverage[] getValidBeverages()
    {
        return Beverage.values();
    }
}
