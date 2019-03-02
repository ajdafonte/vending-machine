package com.dexma.hometest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Product;

import javafx.util.Pair;


/**
 * VendingMachineUserOperations interface - Describes all the user operations in a Vending Machine.
 */
public interface VendingMachineUserOperations
{
    Map<Cash, Integer> refund();

    BigDecimal selectProduct(Product product);

    BigDecimal insertCash(Cash cash);

    Pair<Product, Map<Cash, Integer>> confirmPurchase();

    List<Cash> getAllowedCashItems();

    List<Product> getAvailableProducts();
}
