package com.dexma.hometest.error;

/**
 * ProductManagerException exception - Exceptions thrown by the ProductManager.
 */
public class ProductManagerException extends RuntimeException
{
    private static final long serialVersionUID = -3801962925398359403L;

    public ProductManagerException(final String message)
    {
        super(message);
    }
}
