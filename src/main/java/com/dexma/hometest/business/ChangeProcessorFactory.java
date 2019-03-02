package com.dexma.hometest.business;

/**
 * ChangeProcessorFactory class - Factory class that contains all the available strategies to calculate change.
 */
public class ChangeProcessorFactory
{
    private final GreedyChangeProcessor greedyChangeProcessor = new GreedyChangeProcessor();

    ChangeProcessor getChangeProcessor()
    {
        return greedyChangeProcessor;
    }
}
