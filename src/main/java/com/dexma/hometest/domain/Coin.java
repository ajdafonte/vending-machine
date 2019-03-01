package com.dexma.hometest.domain;

/**
 *
 */
public enum Coin implements Cash
{
    FIVE_CENTS
        {
            @Override
            public double getValue()
            {
                return 0.05;
            }
        },
    TEN_CENTS
        {
            @Override
            public double getValue()
            {
                return 0.1;
            }
        },
    TWENTY_CENTS
        {
            @Override
            public double getValue()
            {
                return 0.2;
            }
        },
    FIFTY_CENTS
        {
            @Override
            public double getValue()
            {
                return 0.5;
            }
        },
    ONE
        {
            @Override
            public double getValue()
            {
                return 1;
            }
        },
    TWO
        {
            @Override
            public double getValue()
            {
                return 2;
            }
        };

    public static Coin[] getValidCoins()
    {
        return Coin.values();
    }
    
}
