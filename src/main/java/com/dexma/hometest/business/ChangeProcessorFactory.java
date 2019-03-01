package com.dexma.hometest.business;

/**
 *
 */
public class ChangeProcessorFactory
{
    GreedyChangeProcessor greedyChangeProcessor = new GreedyChangeProcessor();

    public ChangeProcessor getChangeProcessor()
    {
        return greedyChangeProcessor;
    }

//    public List<Double> processChange(final Map<Double, Integer> currentStock, final double amount)
//    {
//
//    }

//    public int coinChange(final int[] coins, final int amount)
//    {
//        if (amount == 0)
//        {
//            return 0;
//        }
//
//        final int[] dp = new int[amount + 1];
//
//        Arrays.fill(dp, Integer.MAX_VALUE);
//        dp[0] = 0;
//
//        for (int i = 1; i <= amount; i++)
//        {
//            for (final int coin : coins)
//            {
//                if (i == coin)
//                {
//                    dp[i] = 1;
//                }
//                else if (i > coin)
//                {
//                    if (dp[i - coin] == Integer.MAX_VALUE)
//                    {
//                        continue;
//                    }
//                    dp[i] = Math.min(dp[i - coin] + 1, dp[i]);
//                }
//            }
//        }
//
//        if (dp[amount] == Integer.MAX_VALUE)
//        {
//            return -1;
//        }
//
//        return dp[amount];
//    }
//
//    int minCoins(final int[] coins, final int m, final int V)
//    {
//        // base case
//        if (V == 0)
//        {
//            return 0;
//        }
//
//        // Initialize result
//        int res = Integer.MAX_VALUE;
//
//        // Try every coin that has smaller value than V
//        for (int i = 0; i < m; i++)
//        {
//            if (coins[i] <= V)
//            {
//                final int sub_res = minCoins(coins, m, V - coins[i]);
//
//                // Check for INT_MAX to avoid overflow and see if
//                // result can minimized
//                if (sub_res != Integer.MAX_VALUE && sub_res + 1 < res)
//                {
//                    res = sub_res + 1;
//                }
//            }
//        }
//        return res;
//    }
//
//    // works
//    public ArrayList<Integer> greedyChange(final Integer[] d, int coinToChange)
//    {
//        printArray(d);
//        Arrays.sort(d, Comparator.reverseOrder());
//        printArray(d);
//        System.out.println("Coin to Change: " + coinToChange);
//        final ArrayList<Integer> change = new ArrayList<>();            // create an array list to store changes bc dynamic
//
//        for (int i = 0; i < d.length; i++)        // iterate over each denomination
//        {
//            while (d[i] <= coinToChange)
//            {
//                System.out.print("Change: " + coinToChange + " - " + d[i]);
//                coinToChange = coinToChange - d[i];
//                System.out.print(" = " + coinToChange + "\n");
//                change.add(d[i]);
//            }
//            if (coinToChange == 0)        // no need of extra iterations
//            {
//                break;
//            }
//        }
//        System.out.println("Min No. of Coins: " + change.size() + "" + change);
//        return change;
//    }
//
//    public Map<Integer, Integer> greedyChange2(final Map<Integer, Integer> d, int coinToChange)
//    {
//        final TreeMap<Integer, Integer> treeMap = new TreeMap<>(Comparator.reverseOrder());
//        treeMap.putAll(d);
////        Arrays.sort(d, Comparator.reverseOrder());
//        System.out.println("Coin to Change: " + coinToChange);
//        final Map<Integer, Integer> change = new HashMap<>();            // create an array list to store changes bc dynamic
//
//        for (final Map.Entry<Integer, Integer> entry : treeMap.entrySet())
//        {
//            int cnt = 0;
//            final Integer cashItem = entry.getKey();
//            Integer quantity = entry.getValue();
//            while (cashItem <= coinToChange && quantity > 0)
//            {
//                System.out.print("Change: " + coinToChange + " - " + cashItem);
//                coinToChange = coinToChange - cashItem;
//                System.out.print(" = " + coinToChange + "\n");
//                change.put(cashItem, ++cnt);
//                quantity--;
//            }
//            if (coinToChange == 0)        // no need of extra iterations
//            {
//                break;
//            }
//        }
//
//        // check if change calculated if enough -- with value of coinToChange if not zero
//        final int changeFinal = change.keySet()
//            .stream()
//            .reduce(0, Integer::sum);
//
//        System.out.println("Min No. of Coins: " + change.size() + "" + change);
//        if (changeFinal == coinToChange)
//        {
//            return change;
//        }
//        else
//        {
//            return null;
//        }
//    }
//
//    private void printArray(final Integer[] d)
//    {
//        for (final Integer i : d)
//        {
//            System.out.print(i + " ");
//        }
//        System.out.println();
//    }
}
