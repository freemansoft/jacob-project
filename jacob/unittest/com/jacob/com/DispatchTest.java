package com.jacob.com;

import java.util.Date;

import com.jacob.test.BaseTestCase;

/**
 * Test some of the Dispatch utility methods
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class DispatchTest extends BaseTestCase {

	/**
	 * verify that disatch can convert from object to variant and that the
	 * variant holds the right value
	 */
	public void testDispatch() {
		Date testDate = new Date();
		Variant fromDate = Dispatch.obj2variant(testDate);
		Date returnedDate = fromDate.getJavaDate();
		// System.out.println("test date is "+testDate);
		// System.out.println("VariantDate is "+fromDate.getJavaDate());
		assertTrue("Could not call obj2variant(Date) and get it to work",
				testDate.equals(returnedDate));
		Long someMoney = new Long(12349876L);
		Variant fromMoney = Dispatch.obj2variant(someMoney);
		long someMoneyConverted = fromMoney.getCurrency();
		assertTrue("Could not call obj2variant(Long) and get it to work",
				someMoney.equals(someMoneyConverted));
		System.out.println("currency returned was: " + someMoneyConverted);

	}
}
