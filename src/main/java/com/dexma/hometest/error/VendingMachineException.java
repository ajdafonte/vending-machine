package com.dexma.hometest.error;

/**
 *
 */
public class VendingMachineException extends RuntimeException
{
    private static final long serialVersionUID = -3801962925398359403L;

    public VendingMachineException(final String message)
    {
        super(message);
    }

    public VendingMachineException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
