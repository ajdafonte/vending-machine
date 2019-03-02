package com.dexma.hometest.domain;

import java.math.BigDecimal;


/**
 * Coin enum - Describes and contains details about allowed Coins.
 */
public enum Coin implements Cash
{
    FIVE_CENTS
        {
            @Override
            public BigDecimal getValue()
            {
                return BigDecimal.valueOf(0.05);
            }
        },
    TEN_CENTS
        {
            @Override
            public BigDecimal getValue()
            {
                return BigDecimal.valueOf(0.1);
            }
        },
    TWENTY_CENTS
        {
            @Override
            public BigDecimal getValue()
            {
                return BigDecimal.valueOf(0.2);
            }
        },
    FIFTY_CENTS
        {
            @Override
            public BigDecimal getValue()
            {
                return BigDecimal.valueOf(0.5);
            }
        },
    ONE
        {
            @Override
            public BigDecimal getValue()
            {
                return BigDecimal.valueOf(1);
            }
        },
    TWO
        {
            @Override
            public BigDecimal getValue()
            {
                return BigDecimal.valueOf(2);
            }
        };

    public static Coin[] getValidCoins()
    {
        return Coin.values();
    }

}
