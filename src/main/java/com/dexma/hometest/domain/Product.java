package com.dexma.hometest.domain;

import java.math.BigDecimal;


/**
 * Product interface - describes all the characteristics that a certain Product item should have.
 */
public interface Product
{
    String getName();

    BigDecimal getPrice();
}
