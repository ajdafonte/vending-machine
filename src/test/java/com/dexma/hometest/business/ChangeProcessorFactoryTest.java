package com.dexma.hometest.business;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * ChangeProcessorFactoryTest class - ChangeProcessorFactory test class.
 */
class ChangeProcessorFactoryTest
{
    private ChangeProcessorFactory processorFactory;

    @BeforeEach
    void setUp()
    {
        this.processorFactory = new ChangeProcessorFactory();
    }

    @Test
    void given_whenGetSpecificProcessor_thenReturnCorrectProcessor()
    {
        // given

        // when + then
        final ChangeProcessor changeProcessor = processorFactory.getChangeProcessor();
        assertThat(changeProcessor, instanceOf(GreedyChangeProcessor.class));
    }
}
