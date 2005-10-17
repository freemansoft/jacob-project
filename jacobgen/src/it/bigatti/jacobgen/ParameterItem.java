/*
 * ParameterItem.java
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

/**
 * Represent a method parameter
 *
 * @author  Massimiliano Bigatti
 */
public class ParameterItem {

	private String name;
	private String nativeType;
	private String type;
	private String objectType;
	private String arrayType;
	private int direction;
	private boolean optional;
	private boolean object;
	private boolean customType;

	public static final int DIRECTION_UNKNOWN = 0;
	public static final int DIRECTION_IN = 1;
	public static final int DIRECTION_OUT = 2;
	public static final int DIRECTION_RETVAL = 3;

	private	String[] types = { "void", "Object", "short", "int", "float", "double",
							"long", "Date", "String", "Dispatch", "Error",
							"boolean", "Variant", "byte" };

	private	String[] otypes = { "void", "Object", "Short", "Int", "Float", "Double",
							"Long", "Date", "String", "Dispatch", "Error",
							"Boolean", "Variant", "Byte" };

	public ParameterItem( String name, String nativeType, String type, int direction, boolean optional) {
		this.name = name.substring(0,1).toLowerCase() + name.substring(1);
		this.nativeType = nativeType;
		this.type = type;
		this.direction = direction;
		this.optional = optional;
		object = false;

		computeCustomType();
		computeObjectType();
	}

	public String getName() {
		return name;
	}

  /**
   * Returns the name for the parameter used by the java component.
   * If it is the same as a Java-Keyword it will be preceeded by a <code>p_</code>.
   * @return a String which is the name of the parameter without any
   *         conflicts with Java-Keywords
   */
  public String getJavaName() {
    if ( ClassGenerator.checkForJavaKeywords(this.name) ) {
      return "p_" + this.name;
    } else {
      return this.name;
    }
  }

  /**
   * Returns the name for the parameter as it is used by the Variant-variable
   * which is necessary for calls with out-parameters.
   * @return a String which is the name of the Variant-variable of this parameter
   */
  public String getVariantName() {
    return "vnt_" + this.name;
  }

  /**
   * Returns the name for the put*Ref-method for this parameter as it is used
   * by the Variant-variable which is necessary for calls with out-parameters.
   * @return a String which is the name of the put*Ref-method of this parameter
   */
  public String getVariantPutMethod() {
    return "put" + getObjectType() + "Ref";
  }

  /**
   * Returns the name for the to*-method for this parameter as it is used
   * by the Variant-variable which is necessary for calls with out-parameters.
   * @return a String which is the name of the to*-method of this parameter
   */
  public String getVariantGetMethod() {
    return "to" + getObjectType();
  }

  /**
   * Returns the type for the parameter as an array.
   * @return a String which is the type of this parameter as an array
   */
  public String getArrayType() {
    return this.type + "[]";
  }

  /**
   * Returns the type for this parameter. Also if non-basetypes are
   * requested it returns ab basetye if it is a pure input parameter.
   * @return a String which is the type of this parameter
   */
  public String getType(boolean	baseType) {
    if ( baseType || (direction == DIRECTION_IN) ) {
      return getType();
    } else {
      return getArrayType();
    }
  }

	public String getType() {
		return type;
	}

	private String getObjectType() {
		return objectType;
	}

	public String getNativeType() {
		return nativeType;
	}

	public int getDirection() {
		return direction;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isObject() {
		return object;
	}

	public boolean isCustomType() {
		return customType;
	}


	/**
     * Return the method required to pass this parameter to the Dispatch
	 * call method.
	 */
	protected String getParameterCallingCode() {
		String result = name;
		String paramName = name;

		if( ClassGenerator.checkForJavaKeywords( name ) )
			paramName = "p_" + name;

		result = Jacobgen.getInstance().getExternalClass(paramName)==null ?
			paramName : Jacobgen.getInstance().getExternalClass(paramName);

		if( nativeType.equals("VT_EMPTY") )		//Pending
			result = "";
		else if( nativeType.equals("VT_NULL") ) //Pending
			result = "";
		else if( nativeType.equals("VT_I2") )
			result = "new Variant(" + paramName + ")";
		else if( nativeType.equals("VT_I4") || nativeType.equals("VT_INT") )
			result = "new Variant(" + paramName + ")";
		else if( nativeType.equals("VT_R4") )
			result = "new Variant(" + paramName + ")";
		else if( nativeType.equals("VT_R8") )
			result = "new Variant(" + paramName + ")";
		else if( nativeType.equals("VT_CY") )		//Currency
			result = "new Variant(" + paramName + ")";
		else if( nativeType.equals("VT_DATE") )
			result = "new Variant(" + paramName + ")";
		else if( nativeType.equals("VT_BSTR") )
			result = paramName;
		else if( nativeType.equals("VT_DISPATCH") )
			result = paramName;
		else if( nativeType.equals("VT_ERROR") )
			result = "new Variant(" + paramName + ")";
		else if( nativeType.equals("VT_BOOL") )
			result = "new Variant(" + paramName + ")";
		else if( nativeType.equals("VT_HRESULT") )
			result = "new Variant(" + paramName + ")";

		//For variant use class type
		//else if( nativeType.equals("VT_VARIANT") )

		//JACOB wrapper ADO.Field.getDataFormat() maps UNKNOWN as Variant.
		//I hope it is correct
		else if( nativeType.equals("VT_UNKNOWN") )
			result = paramName;
		else if( nativeType.equals("VT_UI1") )		//Pending
			result = "new Variant(" + paramName + ")";

		//Simple Enums
		if( Jacobgen.getInstance().isEnum(nativeType) )
			result = "new Variant(" + paramName + ")";

		//RGB VB Internal type
		if( nativeType.equals( "MsoRGBType" ) )
			result = "new Variant(" + paramName + ")";

		return result;
	}

	protected void computeCustomType() {
		customType = true;
		if( type.trim().length() == 0 )
			customType = false;
		else
			for( int i=0; i<types.length; i++ ) {
				if( types[i].equals( type.trim() ) ) {
					customType = false;
					break;
				}
			}
	}

	protected void computeObjectType() {
		for( int i=0; i<types.length; i++ )
			if( types[i].equals( type ) ) {
				objectType = otypes[i];
				break;
			}

		if( objectType == null )
			objectType = type;
	}
}


