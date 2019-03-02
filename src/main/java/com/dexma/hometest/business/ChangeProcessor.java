package com.dexma.hometest.business;

import java.math.BigDecimal;
import java.util.Map;

import com.dexma.hometest.domain.Cash;


/**
 * ChangeProcessor interface - Describes all methods related with change processing.
 */
public interface ChangeProcessor
{
    /**
     * Method that process change for {@code amount} considering the current {@code cashStock}.
     *
     * @param cashStock - represent the stock of the available cash
     * @param amount - value about which the change should be calculated
     * @return a map containing the change (cash item and respective quantity) or null if no change available
     */
    Map<Cash, Integer> processChange(final Map<Cash, Integer> cashStock, BigDecimal amount);
}
