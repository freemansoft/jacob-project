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
    		//System.out.println("Variant Date Test passed");
    	}
    	
    	for ( int i = 0; i < 30000; i++){
	    	Variant dateVariant = new Variant(now);
	    	retrievedNow = dateVariant.getJavaDate();
	    	if (!now.equals(retrievedNow)){
	    		System.out.println("Variant Date Test (1) failed " +now+ " != " +retrievedNow );
	    	} else {
	    		//System.out.println("Variant Date Test (1) passed");
	    	}
	    	// verify auto typecasting works
	    	retrievedNow = (Date)dateVariant.toJavaObject();
	    	if (!now.equals(retrievedNow)){
	    		System.out.println("Variant Date Test (2) failed " +now+ " != " +retrievedNow );
	    	} else {
	    		//System.out.println("Variant Date Test (2) passed "+retrievedNow);
	    	}
	
	    	Variant intVariant = new Variant(4);
	    	Object variantReturn = intVariant.toJavaObject();
	    	// degenerate test to make sure date isn't always returned
	    	if (variantReturn instanceof Date ){
	    		System.out.println("int variant returned date");
	    	}
    	}
    	System.out.print("Test finished.  All tests passed if no errors before this line");
		
    }
    
}
