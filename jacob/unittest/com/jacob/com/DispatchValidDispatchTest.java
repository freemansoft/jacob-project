package com.jacob.com;

/**
 * Test armoring of dispatch static methods 
 * May need to run with some command line options (including from inside Eclipse).  
 * If so, then try these
 * <pre>
 *      -Djava.library.path=d:/jacob/release/x86 
 *      -Dcom.jacob.autogc=false 
 *      -Dcom.jacob.debug=false 
 *      -Xcheck:jni
 *  </pre>
 */
public class DispatchValidDispatchTest {

    public static void main(String[] args) 
    {
    	try {
    	Dispatch.call(null, 0);
    	System.out.println("Failed to throw IllegalArgumentException");
    	} catch (IllegalArgumentException iae){
    		System.out.println("Caught correct IllegalArgumentException: "+iae);
    	}
    	try {
    		Dispatch foo = new Dispatch();
    		Dispatch.call(foo, 0);
    	} catch (IllegalStateException ise){
    		System.out.println("Caught correct IllegalStateException "+ise);
    	}
    }
}
