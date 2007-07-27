package com.jacob.com;

import com.jacob.test.BaseTestCase;

/**
 * Test armoring of dispatch static methods
 * <p> 
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
public class DispatchValidDispatchTest extends BaseTestCase {

    public void testThrowIllegalArgumentException() {
    	try {
    	Dispatch.call(null, 0);
    	fail("Failed to throw IllegalArgumentException");
    	} catch (IllegalArgumentException iae){
    		System.out.println("Caught correct IllegalArgumentException: "+iae);
    	}
    }
    public void testThrowIllegalStateException() {
    	try {
    		Dispatch foo = new Dispatch();
    		Dispatch.call(foo, 0);
    		fail("Failed to throw IllegalStateException");
    	} catch (IllegalStateException ise){
    		System.out.println("Caught correct IllegalStateException "+ise);
    	}
    }
}
