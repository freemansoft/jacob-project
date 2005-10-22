/*
 * FieldItem.java
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
package com.jacob.jacobgen;

import java.util.*;

/**
 * Represents a class method
 *
 * @version $Id$
 * @author  Massimiliano Bigatti
 */
public class FieldItem {
	
	private String name;
	private String type;
	private String nativeType;
	private int fieldType;
	private String defaultValue;

	public static final int FIELDTYPE_UNKNOWN = -1;
	public static final int FIELDTYPE_CONST = 0;
	
	protected FieldItem(String line) throws IllegalFormatException {
		extractData( line );
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Extract information from a well formatted line
	 */
	protected void extractData( String line ) throws IllegalFormatException {
		StringTokenizer st = new StringTokenizer( line, ";" );
		
		try {
			//Extract field type
			computeFieldType( st.nextToken() );
			
			//Extract variable name
			name = st.nextToken();
			
			//Compute variable type
			nativeType = st.nextToken();
			computeFieldVartype( nativeType );

			//Extract default value
			defaultValue = st.nextToken();
			
		} catch( NoSuchElementException ns ) {
			System.err.println("Seems that line " + line + " is not well " +
			"formed, report to support.");
			ns.printStackTrace();
		}
		
	}
	
	protected void computeFieldType( String type ) {
		fieldType = FIELDTYPE_UNKNOWN;
		if( type.equals( "VAR_CONST" ) )
			fieldType = FIELDTYPE_CONST;
	}
	
	protected void computeFieldVartype( String fieldVartype ) {
		type = "";
		if( nativeType.equals("VT_I4") )
			type = "int";
	}
}

