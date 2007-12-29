/*
 * TypeLibInspector.java
 * Copyright (C) 2000-2002 Massimiliano Bigatti
 * 				 2007 modified by Robert Searle
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jacob.jacobgen;

/**
 * The wrapper for the jni code that generates the tokenized representatation of
 * the DLL that is used by the java classes to generate the stubs
 * 
 * @version $Id$
 * 
 */
public class TypeLibInspector {

	static {
		System.loadLibrary("Jacobgen");
	}

	public static void main(final String[] argv) {
		byte buf[];

		final TypeLibInspector dll = new TypeLibInspector();

		if (argv.length > 0) {
			buf = dll.queryInterface(argv[0]);

			for (int i = 0; i < buf.length; i++) {
				System.out.print((char) buf[i]);
			}
		} else {
			System.out.println("TypeLibInspector <tlbfilename>");
		}
	}

	/**
	 * the front for the actual jni code that is the working guts of this thing
	 * 
	 * @param filename
	 * @return
	 */
	public native byte[] queryInterface(String filename);

}
