/*
 * Copyright (c) 1999-2004 Sourceforge JACOB Project.
 * All rights reserved. Originator: Dan Adler (http://danadler.com).
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution. 
 * 3. Redistributions in any form must be accompanied by information on
 *    how to obtain complete source code for the JACOB software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jacob.activeX;

import com.jacob.com.*;

/**
 * This class simulates com.ms.activeX.ActiveXComponent only as it used for
 * creating Dispatch objects
 */
public class ActiveXComponent extends Dispatch {
    /**
     * @param progid
     */
    public ActiveXComponent(String progid) {
        super(progid);
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
     * @return actually returns this bject
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
     * returns the property as a Variant
     * @param name
     * @return variant value of property
     */
    public Variant getProperty(String name)
    {
      return Dispatch.get(this, name);
    }

    /**
     * returns the selected proeprty as a ActiveXComponent
     * @param propertyName
     * @return Dispatch representing the object under the property name
     */
    public ActiveXComponent getPropertyAsComponent(String propertyName){
        return new ActiveXComponent(Dispatch.get(this,propertyName).toDispatch());
        
    }
    
    /**
     * 
     * @param propertyName property we are looking up
     * @return boolean value of property
     */
    public boolean getPropertyAsBoolean(String propertyName){
        return Dispatch.get(this, propertyName).toBoolean();
    }

    /**
     * 
     * @param propertyName property we are looking up
     * @return byte value of property
     */
    public byte getPropertyAsByte(String propertyName){
        return Dispatch.get(this, propertyName).toByte();
    }

    /**
     * returns the property as a stirng
     * @param propertyName
     * @return String value of property
     */
    public String getPropertyAsString(String propertyName){
        return Dispatch.get(this, propertyName).toString();

    }
    
    /**
     * 
     * @param propertyName
     * @return the property value as an int
     */
    public int getPropertyAsInt(String propertyName){
        return Dispatch.get(this,propertyName).toInt();
    }

    /**
     * sets a property on this object
     * @param name property name
     * @param arg variant value to be set
     */
    public void setProperty(String name, Variant arg)
    {
      Dispatch.put(this, name, arg);
    }

    /**
     * sets a property on this object
     * @param name property name
     * @param arg variant value to be set
     */
    public void setProperty(String name, Dispatch arg)
    {
      Dispatch.put(this, name, arg);
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
     * @param propName
     * @param propValue the boolean value we want the prop set to
     */
    public void setProperty(String propName, boolean propValue){
        this.setProperty(propName, new Variant(propValue));        
    }

    /**
     * sets a property as a boolean value
     * @param propName
     * @param propValue the boolean value we want the prop set to
     */
    public void setProperty(String propName, byte propValue){
        this.setProperty(propName, new Variant(propValue));        
    }

    /**
     * sets teh property as an int value
     * @param propName
     * @param propValue the int value we want the prop to be set to.
     */
    public void setProperty(String propName, int propValue){
        this.setProperty(propName, new Variant(propValue));        
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
     * @return
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
     * @return
     */
    public Variant invoke(String name, Variant[] args)
    {
      return Dispatch.callN(this, name, args);
    }

    
}