/*
 * Copyright (c) 1999-2004 Sourceforge JACOB Project.
 * All rights reserved. Originator: Dan Adler (http://danadler.com).
 * Get more information about JACOB at http://sourceforge.net/projects/jacob-project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.jacob.com;

/**
 * Utility class to centralize the way in which the jacob JNI library is loaded.
 * 
 * <p>
 * If system property {@link #JACOB_DLL_PATH} is defined, the file located there
 * will be loaded as the jacob dll. If the system property is not defined,
 * normal library paths will be used to load the jacob dll. This means it
 * defaults to the previous behavior for existing applications.
 * <p>
 * The standard behavior for most applications is that LoadLibrary() will be
 * called to load the dll. LoadLibary searches directories specified in the
 * variable java.library.path . This is why most test cases specify
 * -Djava.library.path in their command line arguments
 * <p>
 * Submitted sourceforge ticket 1493647 Platform and release modifiers added
 * release 1.14
 * 
 * @author Scott Dickerson (sjd78)
 */
public final class JacobLibraryLoader {
	/**
	 * Name of system property (currently <tt>jacob.dll.path</tt>) that may
	 * contain an absolute path to the JNI library.
	 */
	public static final String JACOB_DLL_PATH = "jacob.dll.path";

	/**
	 * Appended to "jacob" when building DLL name This string must EXACTLY match
	 * the string in the build.xml file
	 */
	public static final String DLL_NAME_MODIFIER_32_BIT = "X86";
	/**
	 * Appended to "jacob" when building DLL name This string must EXACTLY match
	 * the string in the build.xml file
	 */
	public static final String DLL_NAME_MODIFIER_64_BIT = "AMD64";

	/**
	 * Load the jacob dll either from an completely qualified absolute path
	 * defined in system property {@link #JACOB_DLL_PATH} or as a general
	 * library called "<tt>jacob</tt>".
	 * <p>
	 * The fully qualified path option exists to help support Applets and JWS
	 * clients.
	 * 
	 * @throws UnsatisfiedLinkError
	 *             if the library does not exist.
	 */
	public static void loadJacobLibrary() {
		String path = System.getProperty(JACOB_DLL_PATH);
		if (path != null) {
			System.load(path);
		} else {
			try {
				System.loadLibrary(getPreferredDLLName());
			} catch (UnsatisfiedLinkError ule) {
				System.err.println("Unable to load DLL: "
						+ getPreferredDLLName());
				throw ule;
			}
		}
	}

	/**
	 * Developer note: This method MUST be synchronized with the DLL names
	 * created as part of the build process in build.xml
	 * <p>
	 * The DLL name is "jacob\<PLATFORM\>.release"
	 * 
	 * @return the preferred name of the DLL adjusted for this platform and
	 *         version without the ".dll" extension
	 */
	public static String getPreferredDLLName() {
		if (shouldLoad64Bit()) {
			return "jacob" + DLL_NAME_MODIFIER_64_BIT + "."
					+ JacobReleaseInfo.getBuildVersion();
		} else {
			return "jacob" + DLL_NAME_MODIFIER_32_BIT + "."
					+ JacobReleaseInfo.getBuildVersion();
		}
	}

	/**
	 * Determines if we are running in 32 bit mode using strings because there
	 * is no API.
	 * 
	 * @return true if 32 bit
	 */
	protected static boolean shouldLoad32Bit() {
		String model = System.getProperty("sun.arch.data.model");
		if (model != null && model.contains("32")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines if we are running in 64 bit mode using strings because there
	 * is no API.
	 * 
	 * @return true if 64 bit
	 */
	protected static boolean shouldLoad64Bit() {
		String model = System.getProperty("sun.arch.data.model");
		if (model != null && model.contains("64")) {
			return true;
		} else {
			return false;
		}
	}

} // LibraryLoader
