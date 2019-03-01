package com.dexma.hometest.business;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dexma.hometest.domain.BalanceResult;
import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Coin;
import com.dexma.hometest.domain.Stock;
import com.dexma.hometest.error.CashManagerException;
import com.dexma.hometest.error.ProductManagerException;


/**
 *
 */
public class CashManager
{
    private final Stock<Cash> cashStock;
    private double currentBalance;
    private final ChangeProcessorFactory changeProcessorFactory;

    public CashManager(final Stock<Cash> cashStock,
                       final double currentBalance,
                       final ChangeProcessorFactory changeProcessorFactory)
    {
        this.cashStock = cashStock;
        this.currentBalance = currentBalance;
        this.changeProcessorFactory = changeProcessorFactory;
    }

    public CashManager()
    {
        this.cashStock = new Stock<>(new HashMap<>());
        this.currentBalance = 0;
        this.changeProcessorFactory = new ChangeProcessorFactory();
    }

    public boolean isCashItemAllowed(final Cash cash)
    {
        return cash != null && getValidCashItems().contains(cash);
    }

    public List<Cash> getValidCashItems()
    {
        return Arrays.asList(Coin.getValidCoins());
    }

    public Map<Cash, Integer> getCashStock()
    {
        return cashStock.getStockMap();
    }

    //// TO CHECK

    private void validateCashEntry(final Cash cash, final int quantity)
    {
        if (isCashItemAllowed(cash))
        {
            throw new ProductManagerException("Invalid cash specified!");
        }

        if (quantity <= 0)
        {
            throw new ProductManagerException("Invalid quantity specified!");
        }
    }

    public void insertCashItemsInStock(final Map<Cash, Integer> cashMap)
    {
        if (cashMap == null || cashMap.isEmpty())
        {
            throw new ProductManagerException("At least one cash item should be provided");
        }

        //
        for (final Map.Entry<Cash, Integer> entry : cashMap.entrySet())
        {
            final Cash productToAdd = entry.getKey();
            final int quantityOfProduct = entry.getValue();
            validateCashEntry(productToAdd, quantityOfProduct);
            cashStock.insertItem(productToAdd, quantityOfProduct);
        }
    }

    public void removeCashItemsFromStock(final Map<Cash, Integer> cashMap)
    {
        if (cashMap == null || cashMap.isEmpty())
        {
            throw new ProductManagerException("At least one cash item should be provided");
        }

        //
        for (final Map.Entry<Cash, Integer> entry : cashMap.entrySet())
        {
            final Cash productToAdd = entry.getKey();
            final int quantityOfProduct = entry.getValue();
            validateCashEntry(productToAdd, quantityOfProduct);
            cashStock.deleteItem(productToAdd, quantityOfProduct);
        }
    }

//    public boolean isCashItemAvailable(final Cash cash)
//    {
//        return cashStock.hasItem(cash);
//    }

    /////////////////////// OTHER FUNCTIONALITIES

    public Map<Cash, Integer> getCashItemsForChange(final double changeToRefund)
    {
        final ChangeProcessor changeProcessor = changeProcessorFactory.getChangeProcessor();
        return changeProcessor.processChange(cashStock.getStockMap(), changeToRefund);
    }

    public Map<Cash, Integer> calculateRefund()
    {
        final Map<Cash, Integer> refund = getCashItemsForChange(currentBalance);
        if (refund != null)
        {
            // update cashStock (decrement coins from stock)
            removeCashItemsFromStock(refund);

            // update current balance
            resetCurrentBalance();
        }
        return refund;
    }

    public double receiveCash(final Cash cash)
    {
        if (isCashItemAllowed(cash))
        {
            // increment current balance and update cash stock
            incrementCurrentBalance(cash.getValue());
            // insert quantity 1
            cashStock.insertItem(cash);
        }
        else
        {
            throw new CashManagerException("A valid cash item should be provided");
        }
        return getCurrentBalance();
    }

    ///////////////////// CURRENT BALANCE

    public BalanceResult isPossibleToPurchaseProduct(final double productPrice)
    {
        return BalanceResult.getBalanceResultByValue(Double.compare(productPrice, getCurrentBalance()));
    }

    public double calculateRemainingChange(final double productPrice)
    {
        return getCurrentBalance() - productPrice;
    }

    public boolean hasCurrentBalance()
    {
        return currentBalance > 0;
    }

    public double getCurrentBalance()
    {
        return this.currentBalance;
    }

    private void incrementCurrentBalance(final double amount)
    {
        this.currentBalance += amount;
    }

    public void setCurrentBalance(final double currentBalance)
    {
        this.currentBalance = currentBalance;
    }

    public void resetCurrentBalance()
    {
        setCurrentBalance(0);
    }

    private void decrementCurrentBalance(final double amount)
    {
        final double finalValue = this.currentBalance - amount;
        this.currentBalance = finalValue >= 0 ? finalValue : 0;
    }

}
