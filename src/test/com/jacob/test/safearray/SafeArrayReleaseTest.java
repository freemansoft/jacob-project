package com.jacob.test.safearray;

import com.jacob.com.ComThread;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 * <p>
 * SF 1085370 In my understatnding, an instance of SafeArray java class has a
 * value of a pointer to VARIANT structure that contains a pointer to a
 * SAFEARRAY strucuture.
 * 
 * On the other hand, we can create a Variant object from the SafeArray object
 * like this: SafeArray sa = ...; Variant val = new Variant(sa); the val object
 * has a pointer to another VARIANT structure that contains a pointer to the
 * same SAFEARRAY structure.
 * 
 * In this case, the val object has a pointer to another VARIANT that contains a
 * pointer to the same SAFEARRAY like this:
 * 
 * +-----------+ |SafeArray | +------------+ | m_pV--->VARIANT(a) |
 * +-----------+ | VT_ARRAY| +---------+ | parray---->SAFEARRAY| +------------+
 * +^--------+ | +-----------+ | |Variant | +------------+ | |
 * m_pVariant--->VARIANT(b) | | +-----------+ | VT_ARRAY| | | parray-----+
 * +------------+
 * 
 * When previous objects are rereased by ComThread.Release(), first the
 * VARIANT(a) is released by VariantClear() function, and second the VARIANT(b)
 * is released by VariantClear() function too. But the SAFEARRAY was already
 * released by the VARIANT(a).
 * 
 * So, in my enviroment (WinXP + J2SDK 1.4.1) the following java program is
 * sometimes crash with EXCEPTION_ACCESS_VIOLATION.
 * 
 * 
 * To solve this problem, it is nessesary to copy the SAFEARRAY like this:
 * 
 * +-----------+ |Variant | +------------+ | m_pVariant--->VARIANT(a) |
 * +-----------+ | VT_ARRAY| +---------+ | parray---->SAFEARRAY| +------------+
 * +|--------+ | +-----------+ | copySA() |SafeArray | +------------+ | |
 * m_pV--->VARIANT(b) | V +-----------+ | VT_ARRAY| +---------+ |
 * parray---->SAFEARRAY| +------------+ +---------+
 * 
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */

public class SafeArrayReleaseTest extends BaseTestCase {
	final static int MAX = 300;

	/**
	 * verifies the release works on SafeArray
	 */
	public void testSaveArrayRelease() {
		int count;
		System.out.println("Starting test for max = " + MAX);
		for (count = 1; count < MAX; count++) {
			int i = 0;
			try {
				ComThread.InitMTA();
				for (i = 0; i < count; i++) {
					SafeArray a1 = new SafeArray(Variant.VariantVariant, 2);
					a1.setVariant(0, new Variant("foo"));
					a1.setVariant(1, new Variant("bar"));
					Variant v = new Variant(a1);
					SafeArray a2 = v.toSafeArray(true);
					if (a2 == null) {
						System.out.println("got null back from toSafeArray()");
					}
				}
				ComThread.Release();
				System.gc();
				// System.out.print(".");
			} catch (Exception e) {
				fail("Test fails with i = " + i + " (max = " + MAX + ")");
			}
		}
		System.gc();
		System.out.println("\nTest ends with count = " + count + " (max = "
				+ MAX + ")");
	}
}