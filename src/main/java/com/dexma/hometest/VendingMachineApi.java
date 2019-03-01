package com.dexma.hometest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.dexma.hometest.business.CashManager;
import com.dexma.hometest.business.ProductManager;
import com.dexma.hometest.domain.BalanceResult;
import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Product;
import com.dexma.hometest.error.CashManagerException;
import com.dexma.hometest.error.ProductManagerException;
import com.dexma.hometest.error.VendingMachineException;

import javafx.util.Pair;


/**
 *
 */
public class VendingMachineApi implements VendingMachineUserOperations, VendingMachineSupplierOperations
{
    private final ProductManager productManager;
    private final CashManager cashManager;

    public VendingMachineApi(final ProductManager productManager, final CashManager cashManager)
    {
        this.productManager = productManager;
        this.cashManager = cashManager;
    }

    @Override
    public void refillProducts(final Map<Product, Integer> productMap)
    {
        try
        {
            productManager.insertProductsInStock(productMap);
        }
        catch (final ProductManagerException ex)
        {
            throw new VendingMachineException(ex.getMessage(), ex);
        }
    }

    @Override
    public void refillCash(final Map<Cash, Integer> cashMap)
    {
        try
        {
            cashManager.insertCashItemsInStock(cashMap);
        }
        catch (final ProductManagerException ex)
        {
            throw new VendingMachineException(ex.getMessage(), ex);
        }
    }

    @Override
    public Map<Product, Integer> getProductStockStatus()
    {
        return productManager.getProductStock();
    }

    @Override
    public Map<Cash, Integer> getCashStockStatus()
    {
        return cashManager.getCashStock();
    }

    @Override
    public Map<Cash, Integer> refund()
    {
        Map<Cash, Integer> refund = null;
        if (cashManager.hasCurrentBalance())
        {
            refund = cashManager.calculateRefund();
        }
        return refund;
    }

    @Override
    public double selectProduct(final Product product)
    {
        if (productManager.isProductItemAllowed(product))
        {
            throw new VendingMachineException("A valid product item should be provided");
        }

        if (productManager.isProductItemAvailable(product))
        {
            throw new VendingMachineException("Product " + product + " is not available in stock");
        }

        productManager.setSelectedProduct(product);
        return productManager.getPriceOfSelectedProduct();
    }

    @Override
    public double insertCash(final Cash cash)
    {
        try
        {
            return cashManager.receiveCash(cash);
        }
        catch (final CashManagerException e)
        {
            throw new VendingMachineException("A valid cash item should be provided");
        }

        // OR
//        if (cashManager.isCashItemAllowed(cash))
//        {
//            throw new VendingMachineException("A valid cash item should be provided");
//        }
//
//        return cashManager.receiveCash(cash);
    }

    @Override
    public Pair<Product, Map<Cash, Integer>> confirmPurchase()
    {
        if (productManager.isProductSelected())
        {
            throw new VendingMachineException("Before confirm a purchase one product should be selected first");
        }

        final Product selectedProduct = productManager.getSelectedProduct();
        final double productPrice = selectedProduct.getPrice();
        final BalanceResult balanceResult = cashManager.isPossibleToPurchaseProduct(productPrice);
        Pair<Product, Map<Cash, Integer>> purchaseResult = null;
        switch (balanceResult)
        {
            case BELOW_AMOUNT:
            {
                // not enough money
                throw new VendingMachineException("Current balance is not enough to buy Product " + selectedProduct.getName());
            }
            case EXACT_AMOUNT:
            {
                // balance equal to price
                purchaseResult = createPurchaseResponse(selectedProduct, Collections.emptyMap());
                break;
            }
            case ABOVE_AMOUNT:
            {
                // more than enough
                final double changeToRefund = cashManager.calculateRemainingChange(productPrice);
                final Map<Cash, Integer> change = cashManager.getCashItemsForChange(changeToRefund);
                if (change == null)
                {
                    throw new VendingMachineException(" Not sufficient change to provide");
                }
                purchaseResult = createPurchaseResponse(selectedProduct, change);
                break;
            }

        }
        return purchaseResult;
    }

    private Pair<Product, Map<Cash, Integer>> createPurchaseResponse(final Product selectedProduct, final Map<Cash, Integer> change)
    {
        final Pair<Product, Map<Cash, Integer>> purchaseResult;
        productManager.removeProductItemFromStock(selectedProduct);
        productManager.resetSelectedProduct();
        cashManager.removeCashItemsFromStock(change);
        cashManager.resetCurrentBalance();
        purchaseResult = new Pair<>(selectedProduct, change);
        return purchaseResult;
    }

    @Override
    public List<Cash> getAllowedCashItems()
    {
        return cashManager.getValidCashItems();
    }

    @Override
    public List<Product> getAvailableProducts()
    {
        return productManager.getAvailableProducts();
    }
}
