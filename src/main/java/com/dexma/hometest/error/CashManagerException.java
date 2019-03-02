package com.dexma.hometest.error;

/**
 * CashManagerException exception - Exceptions thrown by the CashManager.
 */
public class CashManagerException extends RuntimeException
{
    private static final long serialVersionUID = -3801962925398359403L;

    public CashManagerException(final String message)
    {
        super(message);
    }
}
