/*
 * TypeLibInspector.java
 * Copyright (C) 2000-2002 Massimiliano Bigatti
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
package it.bigatti.jacobgen;

public class TypeLibInspector {

	public native byte[] queryInterface( String filename );
	
	static {
		System.loadLibrary("JacobgenDll");
	}
	
	public static void main(String[] argv) {
		byte buf[];
		
		TypeLibInspector dll = new TypeLibInspector();
		
		if( argv.length > 0 ) {
			buf = dll.queryInterface( argv[0] );
			
			for( int i=0; i<buf.length; i++ ) {
				System.out.print( (char)buf[i] );
			}
		} else
			System.out.println("TypeLibInspector <tlbfilename>");
	}
	
}
