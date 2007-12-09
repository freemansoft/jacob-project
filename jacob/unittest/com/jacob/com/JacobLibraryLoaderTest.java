package com.jacob.com;

import junit.framework.TestCase;

/**
 * Tests Library loader architecture methods
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 * 
 * @author clay_shooter
 * 
 */
public class JacobLibraryLoaderTest extends TestCase {

	/**
	 * verify the architecture switches work
	 */
	public void testArchitectureVersions() {
		System.out.println("running on 32Bit? VM"
				+ JacobLibraryLoader.shouldLoad32Bit());
		System.out.println("running on 64Bit? VM"
				+ JacobLibraryLoader.shouldLoad64Bit());
		assertTrue("should never be both 32 bit and 64 bit", JacobLibraryLoader
				.shouldLoad32Bit() != JacobLibraryLoader.shouldLoad64Bit());
	}

	/**
	 * Verifies that we get a preferred DLL name with X86 since we really only
	 * run the unit tests on 32 bit platforms.
	 */
	public void testDLLNameContainsProcessorAndVersion() {
		System.out.println(JacobLibraryLoader.getPreferredDLLName());
		// we build the package and run the unit tests on X86
		assertTrue(JacobLibraryLoader.getPreferredDLLName().contains("X86"));
	}
}
