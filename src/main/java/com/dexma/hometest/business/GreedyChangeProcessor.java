package com.dexma.hometest.business;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.dexma.hometest.domain.Cash;


/**
 * GreedyChangeProcessor class - Implements a greedy strategy to process change.
 */
public class GreedyChangeProcessor implements ChangeProcessor
{
    @Override
    public Map<Cash, Integer> processChange(final Map<Cash, Integer> cashStock, BigDecimal amount)
    {
        if (isValid(cashStock) && isValid(amount))
        {
            // need to have stock of cash sorted in descending order (from biggest to smallest)
            final TreeMap<Cash, Integer> treeMap = new TreeMap<>(Comparator.comparing(Cash::getValue).reversed());
            treeMap.putAll(cashStock);

            final Map<Cash, Integer> change = new HashMap<>();
            for (final Map.Entry<Cash, Integer> entry : treeMap.entrySet())
            {
                int cnt = 0;
                final Cash cashItem = entry.getKey();
                final BigDecimal cashValue = cashItem.getValue();
                Integer quantity = entry.getValue();
                while (cashValue.compareTo(amount) <= 0 && quantity > 0)
                {
//                    System.out.print("Change: " + amount + " - " + cashItem);
                    amount = amount.subtract(cashValue);
//                    System.out.print(" = " + amount + "\n");
                    change.put(cashItem, ++cnt);
                    quantity--;
                }

                // no need for more iterations
                if (amount.compareTo(BigDecimal.ZERO) == 0)
                {
                    break;
                }
            }

            return amount.compareTo(BigDecimal.ZERO) == 0 ? change : null;
        }
        return null;
    }

    private boolean isValid(final Map<Cash, Integer> cashStock)
    {
        return cashStock != null && !cashStock.isEmpty();
    }

    private boolean isValid(final BigDecimal amount)
    {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
