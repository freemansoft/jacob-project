/*
 * ClassGenerator.java
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

import java.io.*;
import java.util.*;

/**
 * The standard class generator for DLL entities of type 
 * TKIND_COCLASS and TKIND_DISPATCH
 * 
 * @version $Id$
 *
 */
class ClassGenerator extends AbstractGenerator {

	public static String[] javaKeywords = { "goto", "default", "volatile", "import",
		"new", "abstract"};

	protected boolean containsDate = false;

	protected ClassGenerator( String filename, String typelibName, String destinationPackage,
		String className, String baseClass, Vector classFields, Vector classMethods ) {
		super( filename, typelibName, destinationPackage, className, baseClass,
				classFields, classMethods );
	}

	protected void writeClassDeclaration() throws IOException {
		w.write( "public class " + className + " extends " + baseClass + " {\n\n" );
	}

	protected void writeFields() throws IOException {
		w.write("\tpublic static final String componentName = \"" + typelibName + "." +
			className + "\";\n\n");
	}

	protected void writeConstructors() throws IOException {
		if( baseClass.equals("Dispatch") ) {
			writeConstructor1();
			writeConstructor2();
			writeConstructor4();
		} else
			writeConstructor3();
	}

	protected void writeConstructor1() throws IOException {
		w.write( "\tpublic " + className +
			"() {\n\t\tsuper(componentName);\n\t}\n\n");
	}

	protected void writeConstructor2()  throws IOException {
		w.write("\t/**\n");
		w.write("\t* This constructor is used instead of a case operation to\n");
		w.write("\t* turn a Dispatch object into a wider object - it must exist\n");
		w.write("\t* in every wrapper class whose instances may be returned from\n");
		w.write("\t* method calls wrapped in VT_DISPATCH Variants.\n");
		w.write("\t*/\n");
		w.write("\tpublic " + className + "(Dispatch d) {\n");
		w.write("\t\t// take over the IDispatch pointer\n");
		w.write("\t\tm_pDispatch = d.m_pDispatch;\n");
		w.write("\t\t// null out the input's pointer\n");
		w.write("\t\td.m_pDispatch = 0;\n");
		w.write("\t}\n\n");
	}

	protected void writeConstructor4() throws IOException {
		w.write( "\tpublic " + className +
			"(String compName) {\n\t\tsuper(compName);\n\t}\n\n");
	}

	protected void writeConstructor3() throws IOException {
		w.write( "\tpublic " + className +
			"() {\n\t\tsuper(componentName);\n\t}\n\n");
		w.write("\tpublic " + className + "(Dispatch d) {\n"+
		"\t\tsuper(d);\n\t}\n");
	}

	protected void writeMethods() throws IOException {
		Enumeration e = classMethods.elements();
		while( e.hasMoreElements() ) {
			MethodItem mi = (MethodItem)e.nextElement();

			if( mi.getMethodType() == MethodItem.METHODTYPE_FUNCTION ||
				mi.getMethodType() == MethodItem.METHODTYPE_PROPERTYGET ||
				mi.getMethodType() == MethodItem.METHODTYPE_PROPERTYPUT ||
				mi.getMethodType() == MethodItem.METHODTYPE_PROPERTYPUTREF ) {

				writeMethod( mi );
				if( mi.isAdditionalMethodRequired() )
					writeByRefMethod( mi );
			}
		}
	}

	protected void writeByRefMethod( MethodItem mi ) throws IOException {
		ParameterItem[] parameters = mi.getParameters();

		writeMethodDeclaration( mi, parameters.length, false );
		writeMethodBody( mi, parameters.length, false );
	}

	protected void writeMethod( MethodItem mi ) throws IOException {
		ParameterItem[] parameters = mi.getParameters();

		writeMethodDeclaration( mi, parameters.length, true );
		writeMethodBody( mi, parameters.length, true );

		//Try to guess which overloaded methods to write
		for( int i = parameters.length-1; i>=0; i-- ) {
			ParameterItem p = parameters[i];

			if( p.isOptional() )  {
				writeMethodDeclaration( mi, i, true );
				writeMethodBody( mi, i, true );
			}
		}
	}

  /**
   * This Method writes a short javadoc-comment in front of the method.
   * Maybe in a future version the helpstring could be extracted an used
   * instead the current weak description.
   * @param mi        the MethodItem which should be documented
   * @param paramNum  the count of parameters
   * @param baseTypes is this a comment for method with only base types
   *                  or is this a comment for method with output-parameters
   * @throws IOException the used Writer could throw an IOException
   */
  protected void writeMethodComment( MethodItem mi, int paramNum,
                                     boolean baseTypes ) throws IOException {
    // Short description of the method
    w.write( "\t/**\n" );
    if ( baseTypes ) {
      w.write( "\t * Wrapper for calling the ActiveX-Method with input-parameter(s).\n" );
    } else {
      w.write( "\t * Wrapper for calling the ActiveX-Method and receiving the output-parameter(s).\n" );
    }

    // descriptions of the parameters
    ParameterItem[] parameters = mi.getParameters();
    for ( int i = 0; i < paramNum; i++ ) {
      ParameterItem p = parameters[i];
      if ( baseTypes || p.getDirection() == ParameterItem.DIRECTION_IN ) {
        w.write( "\t * @param " + p.getJavaName() + " an input-parameter of type " + p.getType() + "\n" );
      } else {
        // this is only necessary if we want to comment non-basetypes and if it is an output-parameter
        char[] achParamSpace = new char[p.getJavaName().length()];
        Arrays.fill(achParamSpace, ' ');
        // don't want to put a char array in a write() statement
        String achParamSpaceString = new String(achParamSpace);
        w.write( "\t * @param " + p.getJavaName() + " is an one-element array which sends the input-parameter\n"
               + "\t *        " + achParamSpaceString   + " to the ActiveX-Component and receives the output-parameter\n" );
      }
    }

    // and the return-value
    if( !mi.getReturnType().equals("void") ) {
      w.write( "\t * @return the result is of type " + mi.getReturnType() + "\n");
    }

    w.write( "\t */\n" );
  }

	protected void writeMethodDeclaration( MethodItem mi, int paramNum,
						boolean	baseTypes ) throws IOException {
		String[] objectMethods = { "clone", "equals", "finalize", "getClass"
			, "hashCode", "notify", "notifyAll", "toString", "wait" };

    writeMethodComment(mi, paramNum, baseTypes);

		w.write( "\tpublic " );
		if( mi.getMethodType() == MethodItem.METHODTYPE_PROPERTYGET
			&& mi.getReturnType().trim().length() == 0 )
			w.write( "Variant" );
		else if( mi.getReturnType().trim().length() == 0 )
			w.write( "void" );
		else
			w.write( mi.getReturnType() );

		w.write( " " );

		StringBuffer methodName = new StringBuffer();

		//Change Method name if it is a property get or set
		switch( mi.getMethodType() ) {
			case MethodItem.METHODTYPE_FUNCTION:
				if( checkForJavaKeywords( mi.getName() ) )
					methodName.append( "m_" );
				methodName.append( mi.getName().substring(0,1).toLowerCase() );
				methodName.append( mi.getName().substring(1) );
				break;

			case MethodItem.METHODTYPE_PROPERTYGET:
				methodName.append( "get" );
				methodName.append( mi.getName() );
				break;

			case MethodItem.METHODTYPE_PROPERTYPUT:
			case MethodItem.METHODTYPE_PROPERTYPUTREF:
				methodName.append( "set" );
				methodName.append( mi.getName() );
				break;
		}

		for( int i=0; i<objectMethods.length; i++ ) {
			if( objectMethods[i].equals( methodName.toString() ) ) {
				methodName.append("1");
				break;
			}
		}

		w.write( methodName.toString() );
		w.write( "(" );

		ParameterItem[] parameters = mi.getParameters();
		for( int i = 0; i < paramNum; i++ ) {
			ParameterItem p = parameters[i];

			//w.write( p.getType() + "_" + p.getNativeType() );
			w.write( p.getType(baseTypes) + " " + p.getJavaName() );

			if( i < paramNum -1 )
				w.write( ", " );
		}

		w.write( ") {\n" );
	}

	protected void writeMethodBody( MethodItem mi, int paramNum,
					boolean	baseTypes ) throws IOException {

		switch( mi.getMethodType() ) {
			case MethodItem.METHODTYPE_FUNCTION:
				if ( baseTypes ) {
					writeFunctionMethodBody( mi, paramNum );
				} else {
					writeOutFunctionMethodBody( mi, paramNum );
				}
				break;

			case MethodItem.METHODTYPE_PROPERTYGET:
				// For zero parameters get property methods I can call
				// Dispatch.get, otherwise switch to Dispatch.call
				if( paramNum == 0 ) {
					w.write( "\t\treturn " );

					if( mi.isCustomReturnType() )
						w.write("new " + mi.getReturnType() + "(");

					w.write( "Dispatch.get(this, \"" + mi.getName() + "\")" +
					 mi.getReturnConversionMethodCode() );

					if( mi.isCustomReturnType() )
						w.write(".toDispatch())");

					w.write(";\n");
				} else {
				  if ( baseTypes ) {
					writeFunctionMethodBody( mi, paramNum );
				  } else {
					writeOutFunctionMethodBody( mi, paramNum );
				  }
				}
				break;

			case MethodItem.METHODTYPE_PROPERTYPUT:
			case MethodItem.METHODTYPE_PROPERTYPUTREF:
				//w.write( "\t\tDispatch.put(this, \"" + mi.getName() + "\", " +
				//	computeParamType( params[0] ) + ");\n" );

        // ---- special processing should be done in the functions write*MethodBody
/*				if( paramNum == 1 ) {
					w.write( "\t\tDispatch.put(this, \"" + mi.getName() + "\", " +
						params[0].getParameterCallingCode() + ");\n" );
				} else {*/
				if ( baseTypes ) {
					writeFunctionMethodBody( mi, paramNum );
				} else {
					writeOutFunctionMethodBody( mi, paramNum );
				}
				//}
				break;
		}

		w.write( "\t}\n\n" );
	}

  /**
   * This Method writes the method body for a method with output-parameters.
   * @param mi        the MethodItem which should be documented
   * @param paramNum  the count of parameters
   * @throws IOException the used Writer could throw an IOException
   */
  protected void writeOutFunctionMethodBody(MethodItem mi, int paramNum) throws IOException {
		ParameterItem[] parameters = mi.getParameters();
		
		// If we are using paramters with return values we have to initialize
		// the Varaints with put*Ref(). This has to be done before the call.
		// It should look like the following: Variant param1 = new Variant();
		//                                    param1.putIntRef(lastParam[0]);
		for( int i = 0; i < paramNum; i++ ) {
			ParameterItem p = parameters[i];
			// this is only necessary if it is an output-parameter
			//if ( p.getDirection() == ParameterItem.DIRECTION_OUT && !p.getType().equals("SafeArray") ) {
			if ( p.getDirection() == ParameterItem.DIRECTION_OUT ) {
				w.write("\t\tVariant " + p.getVariantName() + " = new Variant();\n");
				w.write("\t\tif( "+p.getJavaName()+" == null || "+p.getJavaName()+".length == 0 )\n");
				w.write("\t\t\t"+p.getVariantName()+".noParam();\n");
				w.write("\t\telse\n");
				// 12/2005 hack to get get this to generate compileable code.  
				// Hopefully someone will look at this later and make sure it is correct
				if (p.getVariantPutMethod().equals("putVariantRef")){
					w.write("\t\t\t"+ p.getVariantName() + " = " + p.getJavaName() + "[0];\n\n");
				} else if (p.getVariantPutMethod().equals("java.util.Date")) {
					w.write("\t\t\t" + p.getVariantName() + ".putDate(" + p.getJavaName() + "[0]);\n\n");
				} else {
					w.write("\t\t\t" + p.getVariantName() + "." + p.getVariantPutMethod() + "(" + p.getJavaName() + "[0]);\n\n");
				}
			}
		}

		w.write("\t\t");
		if( !mi.getReturnType().equals("void") ) {
			w.write(mi.getReturnType() + " result_of_" + mi.getName() + " = ");

			if( mi.isCustomReturnType() )
				w.write("new " + mi.getReturnType() + "(");

			if( mi.getReturnType().equals("java.util.Date") ) {
				w.write("javaDateToComDate(");
				containsDate = true;
			}
		}

		w.write("Dispatch.call");
		if( paramNum > 8 )
			w.write("N");
		w.write("(this, \"" + mi.getName() + "\"");
		if( paramNum > 0 )
			w.write(", ");

		if( paramNum > 8 )
			w.write( "new Object[] { " );

		for( int i = 0; i < paramNum; i++ ) {
			if( i!=0 )
				w.write( ", " );

			ParameterItem p = parameters[i];
			if ( p.getDirection() == ParameterItem.DIRECTION_IN ) {
				// if it is an input-parameter we use the normal behaviour
				w.write( p.getParameterCallingCode() );
			} else {
				// if it is an output-parameter we use the variant-variable
				w.write( p.getVariantName() );
			}
		}

		if( paramNum > 8 )
			w.write( "}" );

		w.write( ")" );
		if( !mi.getReturnType().equals("void") )
			w.write( mi.getReturnConversionMethodCode() );

		if( mi.isCustomReturnType() )
			w.write( ".toDispatch())" );

		if( mi.getReturnType().equals("java.util.Date") )
			w.write( ")" );

		w.write( ";\n\n" );

		// If we are using paramters with return values we have to retrieve this
		// values from the Varaints with to*(). This has to be done after the call.
		// It should look like the following: lastParam[0] = param1.toInt();
		for( int i = 0; i < paramNum; i++ ) {
			ParameterItem p = parameters[i];
			// this is only necessary if it is an output-parameter
			if ( p.getDirection() == ParameterItem.DIRECTION_OUT ) {
				w.write("\t\tif( "+p.getJavaName()+" != null && "+p.getJavaName()+".length > 0 )\n");
				if (p.getVariantGetMethod().equals("toVariant")){
					// 12/2005 hack to get get this to generate compileable code.  
					// Hopefully someone will look at this later and make sure it is correct
					w.write("\t\t\t" +p.getJavaName() + "[0] = " +p.getVariantName() + ";\n");
				} else {
					w.write("\t\t\t" + p.getJavaName() + "[0] = " + p.getVariantName() + "." + p.getVariantGetMethod() + "();\n");
				}
			}
		}

		// Now it is time to return the result
		if( !mi.getReturnType().equals("void") ) {
			w.write("\n\t\treturn result_of_" + mi.getName() + ";\n");
		}
	}

	protected void writeFunctionMethodBody( MethodItem mi, int paramNum ) throws IOException {
		w.write("\t\t");

		if( !mi.getReturnType().equals("void") ) {
			w.write("return ");

			if( mi.isCustomReturnType() )
				w.write("new " + mi.getReturnType() + "(");
		}

		w.write("Dispatch.call");
		if( paramNum > 8 )
			w.write("N");
		w.write("(this, \"" + mi.getName() + "\"");
		if( paramNum > 0 )
			w.write(", ");

		ParameterItem[] parameters = mi.getParameters();
		if( paramNum > 8 )
			w.write( "new Object[] { " );

		for( int i = 0; i < paramNum; i++ ) {
			if( i!=0 )
				w.write( ", " );

			ParameterItem p = parameters[i];
			w.write( p.getParameterCallingCode() );
		}

		if( paramNum > 8 )
			w.write( "}" );

		w.write( ")" );
		if( !mi.getReturnType().equals("void") )
			w.write( mi.getReturnConversionMethodCode() );

		if( mi.isCustomReturnType() )
			w.write( ".toDispatch())" );
		
		w.write( ";\n" );
	}

	protected void writeDateConversionCode() throws IOException {
		w.write( "\tstatic long zoneOffset" );
		w.write( "\t= java.util.Calendar.getInstance().get(java.util.Calendar.ZONE_OFFSET);\n\n" );
/*
  		w.write( "\tstatic java.util.Date comDateToJavaDate(double comDate) {\n");
		w.write( "\t\tcomDate = comDate - 25569D;\n");
		w.write( "\t\tlong millis = Math.round(86400000L * comDate) - zoneOffset;\n\n");
		w.write( "\t\tjava.util.Calendar cal = java.util.Calendar.getInstance();\n");
		w.write( "\t\tcal.setTime(new java.util.Date(millis));\n");
		w.write( "\t\tmillis -= cal.get(java.util.Calendar.DST_OFFSET);\n\n");
		w.write( "\t\treturn new java.util.Date(millis);\n");
		w.write( "\t}\n\n");
*/
		w.write( "\tstatic double javaDateToComDate(java.util.Date javaDate) {\n\n");
		w.write( "\t\tjava.util.Calendar cal = java.util.Calendar.getInstance();\n");
		w.write( "\t\tcal.setTime(javaDate);\n");
		w.write( "\t\tlong gmtOffset = (cal.get(java.util.Calendar.ZONE_OFFSET) + cal.get(java.util.Calendar.DST_OFFSET));\n\n");
		w.write( "\t\tlong millis = javaDate.getTime() + gmtOffset;\n");
		w.write( "\t\treturn 25569D+millis/86400000D;\n");
		w.write( "\t}\n\n");
	}

	protected void writeEndings() throws IOException {
		if( containsDate )
			writeDateConversionCode();
	}

	/*
	protected String computeParamType( ParameterItem pi ) {
		String result = "new Variant(" + pi.getName() + ")";
		if( pi.getType().equals("String" ) || pi.isObject() || pi.isCustomType() )
			result = pi.getName();

		return result;
	}
	*/

	/**
	 * Change contents of StringBuffer if contains a Java Keyword. It should
	 * prevent compile errors.
	 */
	public static boolean checkForJavaKeywords( String s ) {
		boolean result = false;
		for( int i=0; i<javaKeywords.length; i++)
			if( s.equalsIgnoreCase( javaKeywords[i] ) ) {
				result = true;
				break;
			}

		return result;
	}
}

