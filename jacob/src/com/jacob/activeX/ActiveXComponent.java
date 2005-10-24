/*
 * Copyright (c) 1999-2004 Sourceforge JACOB Project.
 * All rights reserved. Originator: Dan Adler (http://danadler.com).
 * Get more information about JACOB at www.sourceforge.net/jacob-project
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
package com.jacob.activeX;

import com.jacob.com.*;

/**
 * This class provides a higher level, more object like, wrapper for
 * top of the Dispatch object.  The Dispatch class's method essentially 
 * directly map to Microsoft C API including the first parameter that is
 * almost always the target of the message.
 * ActiveXComponent assumes the target of every message is the MS
 * COM object behind the ActiveXComponent.  This removes the need
 * to pass the Dispatch object into every method.
 * <p>
 * It is really up to the developer as to whether they want to 
 * use the Dispatch interface or the ActiveXComponent interface.
 * <p>
 * This class simulates com.ms.activeX.ActiveXComponent only in
 * the senese that it is used for creating Dispatch objects
 */
public class ActiveXComponent extends Dispatch {
    /**
     * Normally used to create a new connection to a microsoft application.
     * The passed in parameter is the name of the program as registred
     * in the registry.  It can also be the object name.
     * <pre>new ActiveXComponent("ScriptControl");</pre>
     * @param programId
     */
    public ActiveXComponent(String programId) {
        super(programId);
    }

    /**
     * Creates an active X component that is built on top of the
     * COM pointers held in the passed in dispatch.
     * This widends the Dispatch object to pick up the ActiveXComponent API
     * 
     * @param dispatchToBeWrapped
     */
    public ActiveXComponent(Dispatch dispatchToBeWrapped) {
        super(dispatchToBeWrapped);
    }

    /**
     * Probably was a cover for something else in the past.
     * Should be deprecated.
     * @return Now it actually returns this exact same object.
     */
    public Dispatch getObject() {
        return this;
    }

    /**
     * @see com.jacob.com.Dispatch#finalize()
     */
    protected void finalize() {
        super.finalize();
    }

    static {
        System.loadLibrary("jacob");
    }
    
    
    
    /*============================================================
     * 
     * start of instance based calls to the COM layer
     * ===========================================================
     */

    /**
     * retrieves a property and returns it as a Variant
     * @param propertyName
     * @return variant value of property
     */
    public Variant getProperty(String propertyName)
    {
      return Dispatch.get(this, propertyName);
    }

    /**
     * retrieves a property and returns it as an ActiveX component
     * @param propertyName
     * @return Dispatch representing the object under the property name
     */
    public ActiveXComponent getPropertyAsComponent(String propertyName){
        return new ActiveXComponent(Dispatch.get(this,propertyName).toDispatch());
        
    }
    
    /**
     * retrieves a property and returns it as a Boolean
     * @param propertyName property we are looking up
     * @return boolean value of property
     */
    public boolean getPropertyAsBoolean(String propertyName){
        return Dispatch.get(this, propertyName).toBoolean();
    }

    /**
     * retrieves a property and returns it as a byte
     * @param propertyName property we are looking up
     * @return byte value of property
     */
    public byte getPropertyAsByte(String propertyName){
        return Dispatch.get(this, propertyName).toByte();
    }

    /**
     * retrieves a property and returns it as a String
     * @param propertyName
     * @return String value of property
     */
    public String getPropertyAsString(String propertyName){
        return Dispatch.get(this, propertyName).toString();

    }
    
    /**
     * retrieves a property and returns it as a int
     * @param propertyName
     * @return the property value as an int
     */
    public int getPropertyAsInt(String propertyName){
        return Dispatch.get(this,propertyName).toInt();
    }

    /**
     * sets a property on this object
     * @param propertyName property name
     * @param arg variant value to be set
     */
    public void setProperty(String propertyName, Variant arg)
    {
      Dispatch.put(this, propertyName, arg);
    }

    /**
     * sets a property on this object
     * @param propertyName property name
     * @param arg variant value to be set
     */
    public void setProperty(String propertyName, Dispatch arg)
    {
      Dispatch.put(this, propertyName, arg);
    }

    
    /**
     * sets a property to be the value of the string
     * @param propertyName
     * @param propertyValue
     */
    public void setProperty(String propertyName, String propertyValue){
        this.setProperty(propertyName, new Variant(propertyValue));
    }

    /**
     * sets a property as a boolean value
     * @param propertyName
     * @param propValue the boolean value we want the prop set to
     */
    public void setProperty(String propertyName, boolean propValue){
        this.setProperty(propertyName, new Variant(propValue));        
    }

    /**
     * sets a property as a boolean value
     * @param propertyName
     * @param propValue the boolean value we want the prop set to
     */
    public void setProperty(String propertyName, byte propValue){
        this.setProperty(propertyName, new Variant(propValue));        
    }

    /**
     * sets the property as an int value
     * @param propertyName
     * @param propValue the int value we want the prop to be set to.
     */
    public void setProperty(String propertyName, int propValue){
        this.setProperty(propertyName, new Variant(propValue));        
    }

    /*-------------------------------------------------------
     * Listener logging helpers
     *-------------------------------------------------------
     */
    
    /**
     * This boolean determines if callback events should be logged
     */
    public static boolean shouldLogEvents = false;
    
    /**
     * used by the doc and application listeners to get intelligent logging
     * @param description event description
     * @param args args passed in (variants)
     * 
     */
    public void logCallbackEvent(String description, Variant[] args ) {
        String argString = "";
        if (args!=null && ActiveXComponent.shouldLogEvents){
            if (args.length > 0){
                argString+=" args: ";
            }
            for ( int i = 0; i < args.length; i++){
                short argType = args[i].getvt();
                argString+=",["+i+"]";
                // break out the byref bits if they are on this
                if ((argType & Variant.VariantByref) == Variant.VariantByref){
                    // show the type and the fact that its byref
                    argString += "("+(args[i].getvt() & ~Variant.VariantByref)+
                        "/"+Variant.VariantByref+")";
                } else {
                    // show the type
                    argString += "("+argType+")";
                }
                argString += "=";
                if (argType == Variant.VariantDispatch){
                    Dispatch foo = (Dispatch)args[i].getDispatch();
                    argString+=foo;
                } else if ((argType & Variant.VariantBoolean) == 
                            Variant.VariantBoolean){
                    // do the boolean thing
                    if ((argType & Variant.VariantByref) ==
                            Variant.VariantByref){
                            // boolean by ref
                            argString += args[i].getBooleanRef();
                        } else {
                            // boolean by value
                            argString += args[i].getBoolean();
                        }
                } else if ((argType & Variant.VariantString) == 
                    Variant.VariantString){
                    // do the string thing
                    if ((argType & Variant.VariantByref) ==
                            Variant.VariantByref){
                            // string by ref
                            argString += args[i].getStringRef();
                        } else {
                            // string by value
                            argString += args[i].getString();
                        }
                } else {
                    argString+=args[i].toString();
                }
            }
            System.out.println(description +argString);
        }
    }
    
    /*==============================================================
     * 
     * covers for dispatch call methods
     *=============================================================*/

    /**
     * makes a dispatch call for the passed in action and no parameter
     * @param callAction
     * @return ActiveXComponent representing the results of the call
     */
    public ActiveXComponent invokeGetComponent(String callAction){
        return new ActiveXComponent(invoke(callAction).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter
     * @return ActiveXComponent representing the results of the call
     */
    public ActiveXComponent invokeGetComponent(String callAction, 
            Variant parameter){
        return new ActiveXComponent(invoke(callAction, parameter).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @return ActiveXComponent representing the results of the call
     */
    public ActiveXComponent invokeGetComponent(String callAction, 
            Variant parameter1, Variant parameter2){
        return new ActiveXComponent(invoke(callAction, 
                parameter1, parameter2).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @param parameter3
     * @return ActiveXComponent representing the results of the call
     */
    public ActiveXComponent invokeGetComponent(String callAction, 
            Variant parameter1, 
            Variant parameter2, 
            Variant parameter3){
        return new ActiveXComponent(invoke(callAction, 
                parameter1, parameter2, parameter3).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @param parameter3
     * @param parameter4
     * @return ActiveXComponent representing the results of the call
     */
    public ActiveXComponent invokeGetComponent(String callAction, 
            Variant parameter1, 
            Variant parameter2, 
            Variant parameter3,
            Variant parameter4){
        return new ActiveXComponent(invoke(callAction, 
                parameter1, parameter2, parameter3, parameter4)
                .toDispatch());
    }
    
    /**
     * invokes a single parameter call on this dispatch
     * that returns no value
     * @param actionCommand
     * @param parameter
     * @return a Variant but that may be null for some calls
     */
    public Variant invoke(String actionCommand, String parameter){
        return Dispatch.call(this, actionCommand, parameter);
    }
    
    /**
     * makes a dispatch call to the passed in action with a single boolean parameter
     * @param actionCommand
     * @param parameter
     * @return Variant result
     */
    public Variant invoke(String actionCommand, boolean parameter){
        return Dispatch.call(this, actionCommand, new Variant(parameter));
    }

    /**
     * makes a dispatch call to the passed in action with a single int parameter
     * @param actionCommand
     * @param parameter
     * @return Variant result of the invoke (Dispatch.call)
     */
    public Variant invoke(String actionCommand, int parameter){
        return Dispatch.call(this, actionCommand, new Variant(parameter));
    }
    
    /**
     * makes a dispatch call to the passed in action with a string and integer parameter
     * (this was put in for some application)
     * @param actionCommand
     * @param parameter1
     * @param parameter2
     * @return Variant result
     */
    public Variant invoke(String actionCommand, String parameter1, int parameter2){
        return Dispatch.call(this, actionCommand, parameter1, new Variant(parameter2));
    }

    /**
     * makes a dispatch call to the passed in action with two 
     * integer parameters
     * (this was put in for some application)
     * @param actionCommand
     * @param parameter1
     * @param parameter2
     * @return a Variant but that may be null for some calls
     */
    public Variant invoke(String actionCommand, 
            int parameter1, int parameter2){
        return Dispatch.call(this, actionCommand, 
                new Variant(parameter1),new Variant(parameter2));
    }
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter
     * @return a Variant but that may be null for some calls
     */
    public Variant invoke(String callAction, Variant parameter){
        return Dispatch.call(this,callAction, parameter);
    }
    
    /**
     * makes a dispatch call for the passed in action and two parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @return a Variant but that may be null for some calls
     */
    public Variant invoke(String callAction, 
            Variant parameter1, 
            Variant parameter2){
        return Dispatch.call(this,callAction, parameter1,parameter2);
    }
    
    /**
     * makes a dispatch call for the passed in action and two parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @param parameter3
     * @return Variant result data
     */
    public Variant invoke(String callAction, 
            Variant parameter1, 
            Variant parameter2, 
            Variant parameter3){
        return Dispatch.call(this,callAction, 
                parameter1, parameter2, parameter3);
    }


    /**
     * calls call() with 4 variant parameters
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @param parameter3
     * @param parameter4
     * @return Variant result data
     */
    public Variant invoke(String callAction, 
            Variant parameter1, 
            Variant parameter2, 
            Variant parameter3,
            Variant parameter4){
        return Dispatch.call(this,callAction, 
                parameter1, parameter2, parameter3, parameter4);
    }
    
    
    /**
     * makes a dispatch call for the passed in action and no parameter
     * @param callAction
     * @return a Variant but that may be null for some calls
     */
    public Variant invoke(String callAction){
        return Dispatch.call(this,callAction);
    }
    
    /**
     * This is really a cover for call(String,Variant[]) that should be eliminated
     * call with a variable number of args mainly used for quit.
     * @param name
     * @param args
     * @return Variant returned by the invoke (Dispatch.callN)
     */
    public Variant invoke(String name, Variant[] args)
    {
      return Dispatch.callN(this, name, args);
    }

    
}