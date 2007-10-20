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
 * Submitted sourceforge ticket 1493647
 * 
 * @author Scott Dickerson (sjd78)
 */
public final class LibraryLoader {
	/**
	 * Name of system property (currently <tt>jacob.dll.path</tt>) that may
	 * contain an absolute path to the JNI library.
	 */
	public static final String JACOB_DLL_PATH = "jacob.dll.path";

	/**
	 * Load the jacob dll either from an absolute path defined in system
	 * property {@link #JACOB_DLL_PATH} or as a general library called "<tt>jacob</tt>".
	 * 
	 * @throws UnsatisfiedLinkError
	 *             if the library does not exist.
	 */
	public static void loadJacobLibrary() {
		String path = System.getProperty(JACOB_DLL_PATH);
		if (path != null) {
			System.load(path);
		} else {
			System.loadLibrary("jacob");
		}
	}

} // LibraryLoader
