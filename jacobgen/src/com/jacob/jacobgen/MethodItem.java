/*
 * MethodItem.java
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
 * @author  Massimiliano Bigatti
 */
public class MethodItem {
	
	private String name;
	private String returnType;
	private String nativeReturnType;
	private int methodType;
	private Vector parametersList;
	private boolean customReturnType;
	private boolean additionalMethodRequired;

	public static final int METHODTYPE_UNKNOWN = -1;
	public static final int METHODTYPE_FUNCTION = 1;
	public static final int METHODTYPE_PROPERTYGET = 2;
	public static final int METHODTYPE_PROPERTYPUT = 3;
	public static final int METHODTYPE_PROPERTYPUTREF = 4;
	
	public MethodItem(String line) throws IllegalFormatException {
		parametersList = new Vector();
		extractData( line );
	}
	
	/**
	 * Extract information from a well formatted line
	 */
	protected void extractData( String line ) throws IllegalFormatException {
		StringTokenizer st = new StringTokenizer( line, ";" );
		
		try {
			//Extract method type
			methodType = computeMethodType( st.nextToken() );
			
			//Extract return type
			nativeReturnType = st.nextToken().trim();
			if( nativeReturnType.length() > 0 )
				returnType = computeVarType( nativeReturnType );
			else
				returnType = "";

			//Extract name
			name = st.nextToken();
			
			//Extract parameters
			extractParameters( st.nextToken() );
			
			//Computes if the return type is a standard type
			computeCustomReturnType();
			
		} catch( NoSuchElementException ns ) {
			System.err.println("Seems that line " + line + " is not well " +
			"formed, report to support.");
			ns.printStackTrace();
		}
	}
	
	protected void extractParameters( String parameters ) throws IllegalFormatException {
		additionalMethodRequired = false;
		
		//Strip trailing and ending []
		if( !parameters.startsWith("[") || !parameters.endsWith("]") )
			throw new IllegalFormatException("Parameters format error : "
			+ parameters );
			
		parameters = parameters.substring( 1, parameters.length()-1 );

		StringTokenizer st = new StringTokenizer( parameters, "," );
		while( st.hasMoreTokens() ) {
			String param = st.nextToken();

			//Extract parameters data
			StringTokenizer st1 = new StringTokenizer( param );
			String options = st1.nextToken("{}");
			String type = st1.nextToken(" ");
			String name = st1.nextToken();

			type = type.substring( 1 ).trim();
      
			//Extract options
			int direction = ParameterItem.DIRECTION_UNKNOWN;
			boolean optional = false;
			StringTokenizer st2 = new StringTokenizer( options, "-" );
			while( st2.hasMoreTokens() ) {
				String option = st2.nextToken();
				if( option.equals("in") )
					direction = ParameterItem.DIRECTION_IN;
				else if( option.equals("out") )
					direction = ParameterItem.DIRECTION_OUT;
				else if( option.equals("retval") )
					direction = ParameterItem.DIRECTION_RETVAL;
				else if( option.equals("optional") )
					optional = true;
			}
			
			ParameterItem pi = new ParameterItem( name, 
				type, computeVarType( type ), direction, optional );
			
			//This parameter is a Java base class
			//if( pi.getObjectType() != pi.getType() )
			if( optional || direction == ParameterItem.DIRECTION_OUT)
				additionalMethodRequired = true;
			
			parametersList.addElement( pi );
		}
		
	}
	
	public String getName() {
		return name;
	}
	
	public String getReturnType() {
		return returnType;
	}
	
	public int getMethodType() {
		return methodType;
	}
	
	public boolean isAdditionalMethodRequired() {
		return additionalMethodRequired;
	}
	
	protected void computeCustomReturnType() {
		if( nativeReturnType.equals("VT_SAFEARRAY") ) {
			customReturnType = false;
			return;
		}
		
		String[] types = { "void", "Object", "short", "int", "float", "double",
							"long", "java.util.Date", "String", "Dispatch",
							"boolean", "Variant", "byte" };

		customReturnType = true;
		if( returnType.trim().length() == 0 )
			customReturnType = false;
		else
			for( int i=0; i<types.length; i++ ) {
				if( types[i].equals( returnType.trim() ) ) {
					customReturnType = false;
					break;
				}
			}
	}
	
	public boolean isCustomReturnType() {
		return customReturnType;
	}
	
	public ParameterItem[] getParameters() {
		ParameterItem[] parameters = new ParameterItem[ parametersList.size() ];
		parametersList.copyInto( parameters );
		return parameters;
	}
	
	public static int computeMethodType( String type ) throws IllegalFormatException {
		int result = METHODTYPE_UNKNOWN;
		
		if( type.equals("INVOKE_FUNC") )
			result = METHODTYPE_FUNCTION;
		else if( type.equals("INVOKE_PROPERTYGET") )
			result = METHODTYPE_PROPERTYGET;
		else if( type.equals("INVOKE_PROPERTYPUT") )
			result = METHODTYPE_PROPERTYPUT;
		else if( type.equals("INVOKE_PROPERTYPUTREF") )
			result = METHODTYPE_PROPERTYPUTREF;
		else
			throw new IllegalFormatException("Unknown method type: "
			+ type );

		return result;
	}
	
	public static String computeVarType( String type ) throws IllegalFormatException {
		String result = "";
		
		if( type.equals("VT_EMPTY") )		//Pending
			result = "void";
		else if( type.equals("VT_NULL") )
			result = "Object";
		else if( type.equals("VT_I2") )
			result = "short";
		else if( type.equals("VT_I4") || type.equals("VT_INT") )
			result = "int";
		else if( type.equals("VT_R4") )
			result = "float";
		else if( type.equals("VT_R8") )
			result = "double";
		else if( type.equals("VT_CY") )		//Currency
			result = "long";
		else if( type.equals("VT_DATE") )
			result = "java.util.Date";
		else if( type.equals("VT_BSTR") )
			result = "String";
		else if( type.equals("VT_DISPATCH") )		//???
			//result = "Dispatch";
			result = "Object";
		else if( type.equals("VT_ERROR") )		//Pending
			result = "int";
		else if( type.equals("VT_BOOL") )
			result = "boolean";
		else if( type.equals("VT_VARIANT") )
			result = "Variant";
		//ADO.Field.getDataFormat() maps UNKNOWN as Variant. I hope it is
		// correct
		else if( type.equals("VT_UNKNOWN") )
			result = "Variant";
		else if( type.equals("VT_I1") )
			result = "byte";
		else if( type.equals("VT_UI1") )
			result = "byte";
		else if( type.equals("VT_UI2") )
			result = "short";
		else if( type.equals("VT_UI4") )
			result = "int";
		else if( type.equals("VT_I8") )
			result = "long";
		else if( type.equals("VT_UI8") )
			result = "long";
		else if( type.equals("VT_INT") )
			result = "int";
		else if( type.equals("VT_UINT") )
			result = "int";
		else if( type.equals("VT_VOID") )
			result = "void";
		//Simple HRESULT managment
		else if( type.equals("VT_HRESULT") )
			result = "int";
		else if( type.equals("VT_SAFEARRAY") )
			result = "SafeArray";
		//RGB VB Internal type
		else if( type.equals( "MsoRGBType" ) )
			result = "int";
		else 											//Custom type
			result = Jacobgen.getInstance().getExternalClass(type) == null ? 
				type : Jacobgen.getInstance().getExternalClass(type);
		
		//Simple enumeration
		if( Jacobgen.getInstance().isEnum(type) )
			result = "int";
		
		return result;
	}

	/**
	 * Return the code required to perform the correct conversion between
	 * Variant and return type
	 */
	protected String getReturnConversionMethodCode() {
		String result = "";

		if( nativeReturnType.equals("VT_EMPTY") )		//Pending
			result = "";
		else if( nativeReturnType.equals("VT_NULL") )
			result = ".toObject()";
		else if( nativeReturnType.equals("VT_I2") )
			result = ".toShort()";
		else if( nativeReturnType.equals("VT_I4") || nativeReturnType.equals("VT_INT") )
			result = ".toInt()";
		else if( nativeReturnType.equals("VT_R4") )
			result = ".toFloat()";
		else if( nativeReturnType.equals("VT_R8") )
			result = ".toDouble()";
		else if( nativeReturnType.equals("VT_CY") )		//Currency
			result = ".toCurrency()";
		else if( nativeReturnType.equals("VT_DATE") )
			result = ".toDate()";
		else if( nativeReturnType.equals("VT_BSTR") )
			result = ".toString()";
		else if( nativeReturnType.equals("VT_DISPATCH") )	//???
			//result = ".toDispatch()";
			result = "";
		else if( nativeReturnType.equals("VT_ERROR") )		//Pending
			result = ".toError()";
		else if( nativeReturnType.equals("VT_BOOL") )
			result = ".toBoolean()";
		else if( nativeReturnType.equals("VT_VARIANT") )
			result = "";
		//ADO.Field.getDataFormat() maps UNKNOWN as Variant. I hope it is
		// correct
		else if( nativeReturnType.equals("VT_UNKNOWN") )
			result = "";
		else if( nativeReturnType.equals("VT_UI1") )
			result = ".toByte()";
		//Simple HRESULT managment
		else if( nativeReturnType.equals("VT_HRESULT") )
			result = ".toInt()";
		else if( nativeReturnType.equals("VT_SAFEARRAY") )
			result = ".toSafeArray()";
			
		//Simple enumeration
		if( Jacobgen.getInstance().isEnum(nativeReturnType) )
			result = ".toInt()";
			
		//RGB VB Internal type
		if( nativeReturnType.equals( "MsoRGBType" ) )
			result = ".toInt()";
		
		return result;
	}
	
}

