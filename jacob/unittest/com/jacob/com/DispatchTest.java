package com.jacob.com;

import java.util.Date;

/**
 * Test some of the Dispatch utility methods 
 * May need to run with some command line options (including from inside Eclipse).  
 * If so, then try these
 * <pre>
 *      -Djava.library.path=d:/jacob/release/x86 
 *      -Dcom.jacob.autogc=false 
 *      -Dcom.jacob.debug=false 
 *      -Xcheck:jni
 *  </pre>
 */
public class DispatchTest {

    public static void main(String[] args) 
    {
    	Date testDate = new Date();
    	Variant fromDate = Dispatch.obj2variant(testDate);
    	Date returnedDate = fromDate.getJavaDate();
    	System.out.println("test date is "+testDate);
    	System.out.println("VariantDate is "+fromDate.getJavaDate());
    	if (testDate.equals(returnedDate)){
    		
    	} else {
    		System.out.println("Could not call obj2variant(Date) and get it to work");
    	}
    }
}
