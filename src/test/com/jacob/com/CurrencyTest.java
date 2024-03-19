/*
 * Copyright (c) Esmond Pitt, 2024.
 * All rights reserved.
 */
package com.jacob.com;

import static junit.framework.TestCase.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 *
 * @author Esmond Pitt
 */
public class CurrencyTest {
    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        System.out.println("CurrencyTest." + testName.getMethodName());
    }

    /**
     * Test of longValue method, of class Currency.
     */
    @Test
    public void testLongValue() {
        Currency instance = new Currency(1234567890123456L);
        long expResult = 1234567890123456L;
        long result = instance.longValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class Currency.
     */
    @Test
    public void testCompareTo_Currency() {
        Currency anotherCurrency = new Currency(1234567890123456L);
        Currency instance = new Currency(1234567890000000L);
        int expResult = -1;
        int result = instance.compareTo(anotherCurrency);
        assertEquals(expResult, result);
    }

    // /**
    // * Test of compareTo method, of class Currency.
    // * This test should not compile.
    // */
    // @Test
    // public void testCompareTo_Object()
    // {
    // Object o = new Object();
    // Currency instance = new Currency(1234567890123456L);
    // int expResult = 0;
    // int result = instance.compareTo(o);
    // assertEquals(expResult, result);
    // }

    /**
     * Test of equals method, of class Currency.
     */
    @Test
    public void testEquals() {
        Object o = new Object();
        Currency instance = new Currency(1234567890123456L);
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
    }

}