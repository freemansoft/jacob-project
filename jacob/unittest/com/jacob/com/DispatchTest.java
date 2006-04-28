package com.jacob.com;

import java.util.Date;

/**
 * Test some of the Dispatch utility methods 
 * <code>-Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=true -Dcom.jacob.debug=false</code>
 * @author joe

 * @author joe
 *
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
