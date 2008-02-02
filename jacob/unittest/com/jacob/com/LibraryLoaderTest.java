package com.jacob.com;

import junit.framework.TestCase;

/**
 * Tests Library loader architecture methods This test requires that jacob.jar
 * be compiled and added to the classpath. You will need to refresh the release
 * directory so that eclipse knows about jacob.jar. Otherwise you will get a
 * "jar not found" dialog.
 * 
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 * 
 * @author clay_shooter
 * 
 */
public class LibraryLoaderTest extends TestCase {

	/**
	 * verify the architecture switches work
	 */
	public void testArchitectureVersions() {
		System.out.println("running on 32Bit? VM"
				+ LibraryLoader.shouldLoad32Bit());
		// verify no null pointer is thrown
		LibraryLoader.shouldLoad32Bit();
	}

	/**
	 * verify LibraryLoader.JACOB_DLL_NAME is read by LibraryLoader
	 */
	public void testJacobDllNameSystemProperty() {
		// fill with bad dll name
		System.setProperty(LibraryLoader.JACOB_DLL_NAME, "xxx");
		try {
			LibraryLoader.loadJacobLibrary();
			fail("Should have been unable to load dll with name xxx");
		} catch (UnsatisfiedLinkError ule) {
			// yes, this is what we want to see when using a bad name
		}
		// no way to clear a system property once set so lets try setting to
		// default
		System.setProperty(LibraryLoader.JACOB_DLL_NAME, LibraryLoader
				.getPreferredDLLName());
		try {
			LibraryLoader.loadJacobLibrary();
		} catch (UnsatisfiedLinkError ule) {
			fail("Should have been able to load dll after setting "
					+ LibraryLoader.JACOB_DLL_NAME + " to "
					+ LibraryLoader.getPreferredDLLName() + " "
					+ ule.getMessage());
		}
	}

	/**
	 * Verifies that we get a preferred DLL name with X86 since we really only
	 * run the unit tests on 32 bit platforms.
	 */
	public void testDLLNameContainsProcessorAndVersion() {
		System.out.println(LibraryLoader.getPreferredDLLName());
		if (LibraryLoader.shouldLoad32Bit()) {
			// we build the package and run the unit tests on X86
			assertTrue(LibraryLoader.getPreferredDLLName()
					+ "should have contained "
					+ LibraryLoader.DLL_NAME_MODIFIER_32_BIT, LibraryLoader
					.getPreferredDLLName().contains(
							LibraryLoader.DLL_NAME_MODIFIER_32_BIT));
		} else {
			// we build the package and run the unit tests on X86
			assertTrue(LibraryLoader.getPreferredDLLName()
					+ "should have contained "
					+ LibraryLoader.DLL_NAME_MODIFIER_64_BIT, LibraryLoader
					.getPreferredDLLName().contains(
							LibraryLoader.DLL_NAME_MODIFIER_64_BIT));
		}
	}
}
