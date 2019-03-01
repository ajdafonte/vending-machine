package com.dexma.hometest.domain;

/**
 *
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
            public double getPrice()
            {
                return 1.5;
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
            public double getPrice()
            {
                return 1.4;
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
            public double getPrice()
            {
                return 0.9;
            }
        };

    public static Beverage[] getValidBeverages()
    {
        return Beverage.values();
    }
}
