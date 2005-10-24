package com.jacob.com;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.jacob.com.DateUtilities;

/**
 * test cases that should exercise the new date conversion code
 * run this test with options
 * <code>-Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=true -Dcom.jacob.debug=false</code>
 * @author joe
 *
 */

public class DateUtilitiesTest {

    public static void main(String[] args) 
    {
    	Date now = new Date();
    	double comTimeForNow = DateUtilities.convertDateToWindowsTime(now);
    	Date retrievedNow = DateUtilities.convertWindowsTimeToDate(comTimeForNow);
    	if (!now.equals(retrievedNow)){
    		System.out.println("Variant Date Test failed " +now+ " != " +retrievedNow );
    	} else {
    		System.out.println("Variant Date Test passed");
    	}
    	
    	// this is a magic time in the windows world
    	Date beginningOfWindowsTime = 
    		new GregorianCalendar(1899, Calendar.DECEMBER, 30).getTime();
    	double comTimeForBeginningOfWindowsTime = 
    		DateUtilities.convertDateToWindowsTime(beginningOfWindowsTime);
    	if (comTimeForBeginningOfWindowsTime > 0){
    		System.out.println("Beginning of windows time test failed "
    					+comTimeForBeginningOfWindowsTime);
    	} else {
    		System.out.println("Beginning of windows time test passed");
    	}
    }
    

}
