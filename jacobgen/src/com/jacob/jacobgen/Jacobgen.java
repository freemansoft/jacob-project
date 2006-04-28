/*
 * Jacobgen.java
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

import com.samskivert.viztool.clenum.*;

/**
 * This is the root class for Jacobgen.  It generates 
 * Jacob wrapper class for windows DLLs.  Run this class
 * with no command line parameters to get a list of the valid command line options
 * 
 * <code>
 * %JRE% -Xint com.jacob.jacobgen.Jacobgen %1 %2 %3 %4 %5
 * </code>
 *
 * @version $Id$
 * @author  Massimiliano Bigatti
 */
public class Jacobgen {

	public static final String version = "0.4";
	public static Jacobgen instance;
	protected Vector methodsToAvoid;		
	
	/**
	 * Package where to put generated classes
	 */
	protected String destinationPackage = "";
	
	protected String destinationPath = "";
	
	protected String typelibFilename = null;
	
	protected String tempFileName = null;
	
	protected Vector internalEnums = new Vector();
	
	protected Hashtable externalClasses;
	
	private Jacobgen() {
		methodsToAvoid = new Vector();
		methodsToAvoid.addElement( "QueryInterface" );
		methodsToAvoid.addElement( "AddRef" );
		methodsToAvoid.addElement( "Release" );
		methodsToAvoid.addElement( "GetTypeInfoCount" );
		methodsToAvoid.addElement( "GetTypeInfo" );
		methodsToAvoid.addElement( "GetIDsOfNames" );
		methodsToAvoid.addElement( "Invoke" );
	}
	
	protected void loadExternalClasses() {
        String classpath = System.getProperty("java.class.path");
        ClassEnumerator aClassEnumerator = new ClassEnumerator(classpath);

        // print out the warnings
        ClassEnumerator.Warning[] warnings = aClassEnumerator.getWarnings();
        for (int i = 0; i < warnings.length; i++) {
            System.out.println("External Class Loader Warning: " + warnings[i].reason);
        }

		externalClasses = new Hashtable();
        while (aClassEnumerator.hasNext()) {
			String s = (String)aClassEnumerator.next();
			int p = s.lastIndexOf('.');
            externalClasses.put(s.substring(p+1), s);
        }
	}
	
	protected String getExternalClass( String className ) {
		return (String)externalClasses.get( className );
	}
	
	public boolean isEnum( String className ) {
		String fullClassName = null;
		boolean result = internalEnums.contains( className.toLowerCase() );
		
		//Check for pointed JARs. We assume that all enums are implemented
		//as interfaces.
		if( !result ) {
			fullClassName = getExternalClass( className );			
			if( fullClassName != null )
				try {
					Class clazz = Class.forName( fullClassName );
					result = clazz.isInterface();				//Little hack
					//if( result )
					//	System.out.println("isEnum : " + fullClassName );
		
				} catch( ClassNotFoundException ex1 ) {
				} catch( NoClassDefFoundError ex2 ) {
				}
		}
		
		return result;
	}
	
	public void generate() throws IOException {

		System.out.println("JACOBGEN " + version + ". Copyright 2000-2002 Massimiliano Bigatti. Relased under GNU GPL License");
		System.out.println("starting ..." );
		
		//Create a list of external classes
		loadExternalClasses();
		System.out.println("creating TypeLibInspector");
		//Query TypeLib informations
		TypeLibInspector inspector = new TypeLibInspector();
		System.out.println("calling TypeLibInspector.queryInterface with "+typelibFilename);
		byte[] typelibinfo = inspector.queryInterface(typelibFilename);
		
		//Convert byte array to a vector of lines
		LineNumberReader reader = new LineNumberReader( new StringReader( new
			String( typelibinfo ) ) );
		
		Vector lines = new Vector();
		while( true ) {
			String line = reader.readLine();
			if( line == null )
				break;
			
			lines.addElement( line );
		}
		
		if( tempFileName != null ) {
			try {
				File out = new File(tempFileName);
				FileWriter fw = new FileWriter( out );
				
				Enumeration e = lines.elements();
				while( e.hasMoreElements() ) {
					fw.write( (String)e.nextElement() );
					fw.write( '\n' );
				}
				
				fw.close();
				
			} catch( IOException io ) {
				System.err.println("Unable to generate temporary output file\n");
			}
		}
			
		generateClasses( lines );
	}
		
	protected void generateClasses( Vector lines ) throws IOException {
		int count = 0;
		boolean startClass = false;
		Vector classMethods = null;
		Vector classFields = null;
		String className = "<invalid>";
		String classType = "<unknown>";
		String typelibName = "<unknown>";
		String baseClass = "<unknown>";
		
		int enums = 0;
		System.out.print("finding ENUMS ("+lines.size()+")... " );
		for( int i=0; i<lines.size(); i++ ) {
			String line = (String)lines.elementAt( i );
			if( line.startsWith( "CLASS" ) ) {
				int p = line.indexOf(' ');
				int f = line.indexOf(';');
				classType = line.substring(f+1).trim();
				className = line.substring(p,f).trim().toLowerCase();
				
				if( classType.equals("TKIND_ENUM") ) {
					internalEnums.addElement( className );
					enums++;
				}
				
				if( classType.equals("TKIND_ALIAS") ) {
					
					//Pick up the next row and determine the superclass
					i++;
					String derivedAlias = (String)lines.elementAt( i );
					derivedAlias = derivedAlias.substring(
						derivedAlias.indexOf(';')+1).trim();
						
					//Search for pointed to alias
					for( int k=0; k<lines.size(); k++ ) {
						line = (String)lines.elementAt( k );
						if( line.startsWith( "CLASS" ) ) {
							int p1 = line.indexOf(' ');
							int f1 = line.indexOf(';');
							String className1 = line.substring(p1,f1).trim();
							
							if( className1.trim().equals( derivedAlias ) ) {
								internalEnums.addElement( className );
								enums++;
								break;
							}
						}
					}
				}
			}
		}
		System.out.println("done ("+enums+")" );
		
		for( int i=0; i<internalEnums.size(); i++ ) {
			System.out.println( (String)internalEnums.elementAt( i ) );
		}
		className = "<invalid>";
		classType = "<unknown>";
		
		System.out.println("generating classes ... " );
		for( int i=0; i<lines.size(); i++ ) {
			String line = (String)lines.elementAt( i );

			if( line.startsWith( "TYPELIB" ) ) {
				typelibName = line.substring(8);
			}
			
			if( line.startsWith( "CLASS" ) ) {
				if( startClass ) {
					//Previous class definition ends, commit data to file
					createSourceFile( typelibName, className, classType, baseClass, 
						classFields, classMethods );
				}
				baseClass = "<none>";
				classMethods = new Vector();
				classFields = new Vector();

				int p = line.indexOf(' ');
				int f = line.indexOf(';');

				className = line.substring(p,f).trim();
				classType = line.substring(f+1).trim();
				startClass = true;
				count++;
			} else {
				if( startClass ) {

					if( line.startsWith( "EXTENDS" ) ) {
						
						int f = line.indexOf(';');
						if( !line.substring(f+1).trim().endsWith("Events") ) {
							baseClass = line.substring(f+1).trim();
							if( baseClass.equals("IDispatch") )
								baseClass = "Dispatch";
						}
						
					} else {
						try {
							if( classType.equals("TKIND_ENUM") ) {
								FieldItem fi = new FieldItem( line );
								classFields.addElement( fi );
							} else if( classType.equals("TKIND_DISPATCH") ) {
								MethodItem mi = new MethodItem( line );
								if( !methodsToAvoid.contains( mi.getName() ) )
									classMethods.addElement( mi );
							}
						} catch( IllegalFormatException ife ) {
							System.err.println("Class " + className + ", method:" +
							line + " not parsed due to a format error");
							ife.printStackTrace();
						}
					}
				}
			}
		}
		
		//Parse last CLASS definition
		if( startClass ) {
			//Previous class definition ends, commit data to file
			createSourceFile( typelibName, className, classType, baseClass, classFields, classMethods );
		}		
		System.out.println("done (" + count +" classes)");
		
	}

	protected void createSourceFile( String typelibName, String className, 
		String classType, String baseClass, Vector classFields, Vector classMethods ) throws IOException {
		
		AbstractGenerator g;
		String filename;
		String directory = "";

		if( destinationPath.length() > 0 )
			if( !destinationPath.endsWith( File.separator ) )
				destinationPath += File.separator;
				
		if( destinationPackage.length() > 0 ){
			directory = convertPackageToDir(destinationPackage)	+ File.separator;
		}

		filename = destinationPath + directory + className + ".java";

		System.out.println("Creating " + filename + " ..." );

		File file = new File( destinationPath + directory );
		if( !file.exists() ){
			if( !file.mkdirs() ){
				System.err.println("Unable to create directories (" + destinationPath + directory + ") !" );
			}
		}
		
		g = null;
		if( classType.equals("TKIND_ENUM") ) {
			g = new EnumGenerator( filename, typelibName, destinationPackage, className, baseClass, classFields, null );
		} else if( classType.equals("TKIND_COCLASS") ) {
			g = new ClassGenerator( filename, typelibName, destinationPackage, className, baseClass, null, classMethods );
		} else if( classType.equals("TKIND_DISPATCH") ) {
			g = new ClassGenerator( filename, typelibName, destinationPackage, className, baseClass, null, classMethods );
		} else if( classType.equals("TKIND_ALIAS") ) {
			g = new AliasGenerator( filename, typelibName, destinationPackage, className, baseClass );
		} else {
			//System.err.println("Unrecognized class type "+classType);
		}
		
		if( g != null )
			g.generate();
		
	}
	
	protected String convertPackageToDir( String packageName ) {
		StringTokenizer st = new StringTokenizer( packageName, "." );
		StringBuffer buffer = new StringBuffer();
		
		while( st.hasMoreTokens() ) {
			buffer.append( st.nextToken() );
			if( st.hasMoreTokens() )
				buffer.append( File.separator );
		}
		
		return buffer.toString();
	}
	
	protected Vector readFile(String filename) throws FileNotFoundException, IOException {
		Vector result = new Vector();
		
		FileReader fr = new FileReader( filename );
		LineNumberReader reader = new LineNumberReader( fr );
		
		while( true ) {
			String line = reader.readLine();
			if( line == null )
				break;
				
			result.addElement( line );
		}

		return result;
	}
	
	public static Jacobgen getInstance() {
		if( instance == null )
			instance = new Jacobgen();
		
		return instance;
	}
	
	public void parseOptions( String[] args ) {
		for( int i=0; i<args.length; i++ ) {
			if( args[i].startsWith("-package") )
				destinationPackage = args[i].substring(9);
			else if( args[i].startsWith("-destdir") )
				destinationPath = args[i].substring(9);
			else if( args[i].startsWith("-listfile") )
				tempFileName = args[i].substring(10);
			else
				typelibFilename = args[i];
		}
	}
	
	public static void main(String[] args) {
		if( args.length == 0 ) {
			System.out.println("JacobGen [options] typelibfile\n");
			System.out.println("Options:");
			System.out.println("\t-package:<destination package name>");
			System.out.println("\t-destdir:<root dir for classes>");
			System.out.println("\t-listfile:<listing file>");
			System.out.println();
			System.exit(0);
		} else {
			Jacobgen g = getInstance();
			try {
				g.parseOptions( args );
				if( g.typelibFilename == null )
					System.out.println("Jacobgen you need to specify an input file");
				else
					g.generate();
				//g.generate( argv[0], argv[1] );
			} catch( IOException ex2 ) {
				System.err.println("Jacobgen: I/O error (file " + g.typelibFilename +")" );
			}
		}
	}
}
