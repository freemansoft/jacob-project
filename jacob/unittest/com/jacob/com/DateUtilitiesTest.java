package com.jacob.com;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

/**
 * test cases that should exercise the new date conversion code
 * <p>
 * This test does not require any command line options because it is only a
 * utility test
 */

public class DateUtilitiesTest extends TestCase {

	/**
	 * verify date conversion to and from java
	 */
	public void testDateUtilities() {
		Date now = new Date();
		double comTimeForNow = DateUtilities.convertDateToWindowsTime(now);
		Date retrievedNow = DateUtilities
				.convertWindowsTimeToDate(comTimeForNow);
		if (!now.equals(retrievedNow)) {
			fail("DateUtilities Date Test failed " + now + " != "
					+ retrievedNow);
		} else {
			System.out.println("DateUtilities Date Test passed");
		}

	}

	/**
	 * Verify that the start of time is when we think it is.
	 */
	public void testBeginningOfWindowsTime() {
		// this is a magic time in the windows world
		Date beginningOfWindowsTime = new GregorianCalendar(1899,
				Calendar.DECEMBER, 30).getTime();
		double comTimeForBeginningOfWindowsTime = DateUtilities
				.convertDateToWindowsTime(beginningOfWindowsTime);
		if (comTimeForBeginningOfWindowsTime > 0) {
			fail("Beginning of windows time test failed "
					+ comTimeForBeginningOfWindowsTime);
		} else {
			System.out.println("Beginning of windows time test passed");
		}

	}

}
