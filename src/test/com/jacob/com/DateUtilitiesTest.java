package com.jacob.com;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;


/**
 * test cases that should exercise the new date conversion code
 * <p>
 * This test does not require any command line options because it is only a
 * utility test
 */

public class DateUtilitiesTest {

	/**
	 * verify date conversion to and from java
	 */
	@Test
	public void testDateUtilities() {
		Date now = new Date();
		double comTimeForNow = DateUtilities.convertDateToWindowsTime(now);
		Date retrievedNow = DateUtilities
				.convertWindowsTimeToDate(comTimeForNow);
		if (!now.equals(retrievedNow)) {
			Assert.fail("DateUtilities Date Test failed " + now + " != "
					+ retrievedNow);
		} else {
			System.out.println("DateUtilities Date Test passed");
		}

	}

	/**
	 * Verify that the start of time is when we think it is.
	 */
	@Test
	public void testBeginningOfWindowsTime() {
		// this is a magic time in the windows world
		Date beginningOfWindowsTime = new GregorianCalendar(1899,
				Calendar.DECEMBER, 30).getTime();
		double comTimeForBeginningOfWindowsTime = DateUtilities
				.convertDateToWindowsTime(beginningOfWindowsTime);
		if (comTimeForBeginningOfWindowsTime > 0) {
			Assert.fail("Beginning of windows time test failed "
					+ comTimeForBeginningOfWindowsTime);
		} else {
			System.out.println("Beginning of windows time test passed");
		}

	}

}
