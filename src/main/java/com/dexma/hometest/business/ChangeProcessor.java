package com.dexma.hometest.business;

import java.util.Map;

import com.dexma.hometest.domain.Cash;


/**
 *
 */
public interface ChangeProcessor
{
    Map<Cash, Integer> processChange(final Map<Cash, Integer> cashStock, double amount);
}
