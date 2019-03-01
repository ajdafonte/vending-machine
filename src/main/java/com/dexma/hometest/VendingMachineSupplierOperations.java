package com.dexma.hometest;

import java.util.Map;

import com.dexma.hometest.domain.Cash;
import com.dexma.hometest.domain.Product;


/**
 *
 */
public interface VendingMachineSupplierOperations
{
    void refillProducts(Map<Product, Integer> product);

    void refillCash(Map<Cash, Integer> cash);

    Map<Product, Integer> getProductStockStatus();

    Map<Cash, Integer> getCashStockStatus();
}
