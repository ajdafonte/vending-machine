package com.dexma.hometest.domain;

import java.util.Map;


/**
 * Remark: This wrapper does not allow null keys ?? think
 */
public class Stock<T>
{
    private final Map<T, Integer> stockMap;

    public Stock(final Map<T, Integer> stockMap)
    {
        this.stockMap = stockMap;
    }

//    public boolean isStockItemAllowed(final T item)
//    {
//        return item != null && stockMap.containsKey(item);
//    }

    public int getQuantity(final T item)
    {
        final Integer quantity = stockMap.get(item);
        return quantity == null ? 0 : quantity;
    }

    public T getItem(final T item)
    {
        return hasItem(item) ? item : null;
    }

    public void insertItem(final T item)
    {
        int quantity = getQuantity(item);
        stockMap.put(item, ++quantity);
    }

    public void insertItem(final T item, final int quantity)
    {
        final int finalQuantity = getQuantity(item) + quantity;
        stockMap.put(item, finalQuantity);
    }

    public void deleteItem(final T item)
    {
        if (hasItem(item))
        {
            final int quantity = stockMap.get(item);
            final int finalQuantity = quantity - 1;
            stockMap.put(item, finalQuantity >= 0 ? finalQuantity : 0);
        }
    }

    public void deleteItem(final T item, final int quantity)
    {
        final int finalQuantity = getQuantity(item) - quantity;
        stockMap.put(item, finalQuantity >= 0 ? finalQuantity : 0);
    }

    public boolean hasItem(final T item)
    {
        return getQuantity(item) > 0;
    }

    public void clearAllItems()
    {
        stockMap.clear();
    }

//    public void putItem(final T item, final int quantity)
//    {
//        stockMap.put(item, quantity);
//    }

    public Map<T, Integer> getStockMap()
    {
        return stockMap;
    }
}
