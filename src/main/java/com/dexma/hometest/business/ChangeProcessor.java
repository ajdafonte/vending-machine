package com.dexma.hometest.business;

import java.math.BigDecimal;
import java.util.Map;

import com.dexma.hometest.domain.Cash;


/**
 *
 */
public interface ChangeProcessor
{
    Map<Cash, Integer> processChange(final Map<Cash, Integer> cashStock, BigDecimal amount);
}
