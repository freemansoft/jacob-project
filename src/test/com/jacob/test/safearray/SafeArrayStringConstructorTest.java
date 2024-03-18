package com.jacob.test.safearray;

import com.jacob.com.SafeArray;
import com.jacob.test.BaseTestCase;

/**
 * Test case provided #41 Fix for SafeArray(String) constructor
 * 
 * In the current release of Jacob, SafeArray.java contains a constructor which
 * takes a string as a single argument. The documentation claims that this
 * method converts a string to a VT_UI1 array. Using this method as written
 * always causes a ComFailException, because it attempts to create a SafeArray
 * from Java chars, which are 16-bit unsigned integers (which would be VT_UI2).
 */
public class SafeArrayStringConstructorTest extends BaseTestCase {
	public void testStringConstructor() {
		// The line below will throw ComFailException using jacob 1.17-M2
		// without the patch.
		SafeArray safeArrayFromString = new SafeArray("This is a string.");
		String convertBack = safeArrayFromString.asString();
		assertEquals("This is a string.", convertBack);
	}

}
