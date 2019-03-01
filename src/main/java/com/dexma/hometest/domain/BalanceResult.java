package com.dexma.hometest.domain;

/**
 *
 */
public enum BalanceResult
{
    BELOW_AMOUNT(-1), EXACT_AMOUNT(0), ABOVE_AMOUNT(1);

    private final int value;

    BalanceResult(final int value)
    {
        this.value = value;
    }

    public static BalanceResult getBalanceResultByValue(final int value)
    {
        for (final BalanceResult balanceResult : BalanceResult.values())
        {
            if (balanceResult.value == value)
            {
                return balanceResult;
            }
        }

        return null;
    }

}
