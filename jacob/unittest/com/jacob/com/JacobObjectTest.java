package com.jacob.com;

import com.jacob.test.BaseTestCase;

/**
 * This will eventually be changed to a unit test.
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class JacobObjectTest extends BaseTestCase {

	/**
	 * verify the build version and date functions work correctly
	 */
	public void testBuildVersion() {
		System.out.println("build version is " + JacobReleaseInfo.getBuildVersion());
		System.out.println("build date is " + JacobReleaseInfo.getBuildDate());
	}

}
