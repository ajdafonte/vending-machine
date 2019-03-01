package com.dexma.hometest.business;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * ChangeProcessorFactoryTest class - ChangeProcessorFactory test class.
 */
public class ChangeProcessorFactoryTest
{
    private ChangeProcessorFactory processorFactory;

    @BeforeEach
    public void setUp()
    {
        this.processorFactory = new ChangeProcessorFactory();
    }

    @Test
    public void given_whenGetSpecificProcessor_thenReturnCorrectProcessor()
    {
        // given

        // when + then
        final ChangeProcessor changeProcessor = processorFactory.getChangeProcessor();
        assertThat(changeProcessor, instanceOf(GreedyChangeProcessor.class));
    }
}
