package com.dexma.hometest.error;

/**
 *
 */
public class CashManagerException extends RuntimeException
{
    private static final long serialVersionUID = -3801962925398359403L;

    public CashManagerException(final String message)
    {
        super(message);
    }

    public CashManagerException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
