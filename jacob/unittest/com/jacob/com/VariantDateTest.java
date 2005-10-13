package com.jacob.com;

import java.util.Date;

import com.jacob.com.Variant;

/**
 * test cases that should exercise the new date conversion code
 * run this test with options
 * <code>-Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=true -Dcom.jacob.debug=false</code>
 * @author joe
 *
 */
public class VariantDateTest {

    public static void main(String[] args) 
    {
    	Date now = new Date();
    	Variant holder = new Variant();
    	holder.putDate(now);
    	Date retrievedNow = holder.getJavaDate();
    	if (!now.equals(retrievedNow)){
    		System.out.println("Variant Date Test failed " +now+ " != " +retrievedNow );
    	} else {
    		System.out.println("Variant Date Test passed");
    	}
    }
    
}
