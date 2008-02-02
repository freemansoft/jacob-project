package com.jacob.com;

import java.util.Date;

import com.jacob.test.BaseTestCase;

/**
 * test cases that should exercise the new date conversion code
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class VariantDateTest extends BaseTestCase {

	/**
	 * verify the conversion of Variants into java dates
	 */
	public void testVariantDate() {
		Date now = new Date();
		Variant holder = new Variant();
		holder.putDate(now);
		Date retrievedNow = holder.getJavaDate();
		if (!now.equals(retrievedNow)) {
			fail("Variant Date Test failed " + now + " != " + retrievedNow);
		} else {
			System.out.println("Variant Date Test passed");
		}

	}

	/**
	 * verify that the Variant constructor accepts Java dates and converts them
	 * correctly
	 */
	public void testVariantDateToJavaObject() {
		Date now = new Date();
		Variant holder = new Variant(now);
		for (int i = 0; i < 30000; i++) {
			Variant dateVariant = new Variant(now);
			Date retrievedNow = holder.getJavaDate();
			retrievedNow = dateVariant.getJavaDate();
			if (!now.equals(retrievedNow)) {
				fail("Variant Date Test (1) failed " + now + " != "
						+ retrievedNow);
			} else {
				// System.out.println("Variant Date Test (1) passed");
			}
			// verify auto typecasting works
			retrievedNow = (Date) dateVariant.toJavaObject();
			if (!now.equals(retrievedNow)) {
				fail("Variant Date Test (2) failed " + now + " != "
						+ retrievedNow);
			} else {
				// System.out.println("Variant Date Test (2) passed
				// "+retrievedNow);
			}

			Variant intVariant = new Variant(4);
			Object variantReturn = intVariant.toJavaObject();
			// degenerate test to make sure date isn't always returned
			if (variantReturn instanceof Date) {
				System.out.println("int variant returned date");
			}
		}
		System.out.print("Test finished.  All tests passed.");

	}

}
