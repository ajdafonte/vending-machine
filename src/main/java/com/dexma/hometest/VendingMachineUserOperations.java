package com.dexma.hometest;

import java.util.List;
import java.util.Map;

import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Product;

import javafx.util.Pair;


/**
 *
 */
public interface VendingMachineUserOperations
{
    Map<Cash, Integer> refund();

    double selectProduct(Product product);

    double insertCash(Cash cash);

    Pair<Product, Map<Cash, Integer>> confirmPurchase();

    List<Cash> getAllowedCashItems();

    List<Product> getAvailableProducts();
}
