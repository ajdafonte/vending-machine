package com.dexma.hometest.business;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.dexma.hometest.domain.Cash;


/**
 *
 */
public class GreedyChangeProcessor implements ChangeProcessor
{
    @Override
    public Map<Cash, Integer> processChange(final Map<Cash, Integer> cashStock, double amount)
    {
        final TreeMap<Cash, Integer> treeMap = new TreeMap<>(Comparator.comparingDouble(Cash::getValue));
        treeMap.putAll(cashStock);
//        Arrays.sort(d, Comparator.reverseOrder());
        System.out.println("Coin to Change: " + amount);
        final Map<Cash, Integer> change = new HashMap<>();            // create an array list to store changes bc dynamic

        for (final Map.Entry<Cash, Integer> entry : treeMap.entrySet())
        {
            int cnt = 0;
            final Cash cashItem = entry.getKey();
            final double cashValue = cashItem.getValue();
            Integer quantity = entry.getValue();
            while (cashValue <= amount && quantity > 0)
            {
                System.out.print("Change: " + amount + " - " + cashItem);
                amount -= cashValue;
                System.out.print(" = " + amount + "\n");
                change.put(cashItem, ++cnt);
                quantity--;
            }
            if (amount == 0)        // no need of extra iterations
            {
                break;
            }
        }

        // check if change calculated if enough -- with value of amount if not zero
        System.out.println("Min No. of Coins: " + change.size() + "" + change);
        return amount == 0 ? change : null;
    }
}
