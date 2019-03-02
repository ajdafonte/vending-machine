package com.dexma.hometest.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dexma.hometest.domain.BalanceResult;
import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Coin;
import com.dexma.hometest.domain.Stock;
import com.dexma.hometest.error.CashManagerException;


/**
 *
 */
public class CashManager
{
    private final Stock<Cash> cashStock;
    private BigDecimal currentBalance;
    private final ChangeProcessorFactory changeProcessorFactory;

    public CashManager(final Stock<Cash> cashStock,
                       final ChangeProcessorFactory changeProcessorFactory)
    {
        this.cashStock = cashStock;
        this.currentBalance = BigDecimal.ZERO;
        this.changeProcessorFactory = changeProcessorFactory;
    }

//    public CashManager()
//    {
//        this.cashStock = new Stock<>(new HashMap<>());
//        this.currentBalance = BigDecimal.ZERO;
//        this.changeProcessorFactory = new ChangeProcessorFactory();
//    }

    private boolean isCashItemAllowed(final Cash cash)
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
        if (!isCashItemAllowed(cash))
        {
            throw new CashManagerException("Invalid cash item specified.");
        }

        if (quantity <= 0)
        {
            throw new CashManagerException("Invalid quantity specified.");
        }
    }

    public void insertCashItemsInStock(final Map<Cash, Integer> cashMap)
    {
        if (cashMap == null || cashMap.isEmpty())
        {
            throw new CashManagerException("At least one cash item should be provided.");
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
            throw new CashManagerException("At least one cash item should be provided.");
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

    public Map<Cash, Integer> getCashItemsForChange(final BigDecimal changeToRefund)
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

    public BigDecimal receiveCash(final Cash cash)
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
            throw new CashManagerException("A valid cash item should be provided.");
        }
        return getCurrentBalance();
    }

    ///////////////////// CURRENT BALANCE

    public BalanceResult isPossibleToPurchaseProduct(final BigDecimal productPrice)
    {
        if (productPrice == null || productPrice.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new CashManagerException("Invalid product price.");
        }

        return BalanceResult.getBalanceResultByValue(currentBalance.compareTo(productPrice));
    }

    public BigDecimal calculateRemainingChange(final BigDecimal productPrice)
    {
        if (productPrice == null || productPrice.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new CashManagerException("Invalid product price.");
        }

        return hasCurrentBalance() ? currentBalance.subtract(productPrice) : BigDecimal.ZERO;
    }

    public boolean hasCurrentBalance()
    {
        return currentBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getCurrentBalance()
    {
        return this.currentBalance;
    }

    private void incrementCurrentBalance(final BigDecimal amount)
    {
        currentBalance = currentBalance.add(amount);
    }

    void setCurrentBalance(final BigDecimal currentBalance)
    {
        this.currentBalance = currentBalance;
    }

    public void resetCurrentBalance()
    {
        setCurrentBalance(BigDecimal.ZERO);
    }

//    private void decrementCurrentBalance(final BigDecimal amount)
//    {
//        final BigDecimal finalValue = this.currentBalance.subtract(amount);
//        this.currentBalance = finalValue.compareTo(BigDecimal.ZERO) >= 0 ? finalValue : BigDecimal.ZERO;
//    }

}
