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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The superclass of all Jacob objects.  It is used to
 * create a standard API framework and to facillitate memory management
 * for Java and COM memory elements.
 * <p>
 * All instances of this class and subclasses are automatically manged
 * by the ROT. This means the ROT cannot be a subclass of JacobObject.
 * <p>
 * All COM object created by JACOB extend this class so that we can
 * automatically release them when the thread is detached from COM - if we leave
 * it to the finalizer it will call the release from another thread, which may
 * result in a segmentation violation.
 */
public class JacobObject {

	/**
	 * holds the build version as retrieved from the version.properties
	 * file that exists in the JAR.
	 * This can be retrived by calling the static method getBuildVersion()
	 * @see getBuildVersion()
	 */
    private static String buildVersion = "";
	/**
	 * holds the build date as retrieved from the version.properties
	 * file that exists in the JAR
	 * This can be retrived by calling the static method getBuildDate()
	 * @see getBuildDate()
	 */
    private static String buildDate = "";

    /**
     *  Standard constructor that adds this JacobObject
     * to the memory management pool.
     */
    public JacobObject() {
        ROT.addObject(this);
    }

    
    /**
     * 
     * loads the jacob library dll
     *
     */
    protected static void loadJacobLibrary(){
        System.loadLibrary("jacob");
    }

    /**
     * Loads version information from version.properties that was 
     * built as part of this release.
     *
     */
    private static void loadVersionProperties(){
        Properties versionProps = new Properties();
        // can't use system class loader cause won't work in jws
        InputStream stream = 
           JacobObject.class.getClassLoader().getResourceAsStream("version.properties");
        try {
        versionProps.load(stream);
        stream.close();
        buildVersion = (String)versionProps.get("version");
        buildDate = (String)versionProps.get("build.date");
        } catch (IOException ioe){
            System.out.println("blah, couldn't load props");
        }
    }
    
    /**
     * loads version.properties and returns the value of versin in it
     * @return String value of version in version.properties or "" if none
     */
    public static String getBuildDate(){
        if (buildDate.equals("")){
            loadVersionProperties();
        }
        return buildDate;
    }

    /**
     * loads version.properties and returns the value of versin in it
     * @return String value of version in version.properties or "" if none
     */
    public static String getBuildVersion(){
        if (buildVersion.equals("")){
            loadVersionProperties();
        }
        return buildVersion;
    }
    
    /**
     *  Finalizers call this method.
     *  This method should release any COM data structures in a way
     *  that it can be called multiple times.
     *  This can happen if someone manually calls this and then
     *  a finalizer calls it.
     */
    public void safeRelease() {
        // currently does nothing - subclasses may do something
        if (isDebugEnabled()){
        	// this used to do a toString() but that is bad for SafeArray
            debug("SafeRelease: "+this.getClass().getName());
        }
    }
    
    /**
     * When things go wrong, it is usefull to be able to debug the ROT.
     */
    private static final boolean DEBUG = 
        //true;
        "true".equalsIgnoreCase(System.getProperty("com.jacob.debug"));
    
    protected static boolean isDebugEnabled(){
        //return true;
        return DEBUG;
    }
    /**
     * Very basic debugging fucntion.
     * @param istrMessage
     */
    protected static void debug(String istrMessage)
    {
        if(isDebugEnabled())
        {
            System.out.println(istrMessage 
            		+ " in thread "+ Thread.currentThread().getName());
        }
    }

    /**
     * force the jacob DLL to be loaded whenever this class is referenced
     */
    static {
    	JacobObject.loadJacobLibrary();
    }
    
    
}