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
package com.jacob.com;

/**
 * Object represents MS level dispatch object. You're going to live here
 * a lot
 */
public class Dispatch extends JacobObject
{
    /**
     * This is public because Dispatch.cpp knows its name and accesses
     * it directly to get the disptach id.  You really can't rename
     * it or make it private
     */
    public int m_pDispatch;
	/** program Id passed in by ActiveX components in their constructor */
    private String programId = null;
  


    /**
     * zero argument constructor that sets the dispatch pointer to 0
     */
    public Dispatch() {
        m_pDispatch = 0;
    }

    /**
     * Constructor that calls createInstance with progid. This is the
     * constructor used by the ActiveXComponent
     * 
     * @param requestedProgramId
     */
    public Dispatch(String requestedProgramId) {
        programId = requestedProgramId;
        createInstance(requestedProgramId);
    }

    /**
     * native call createIstnace only used by the constructor with the same parm
     * type could this be private?
     * 
     * @param progid
     */
    protected native void createInstance(String progid);

    /**
     * return a different interface by IID string
     * 
     * @param iid
     * @return Dispatch a disptach that matches ??
     */
    public native Dispatch QueryInterface(String iid);

    /**
     * Constructor that only gets called from JNI
     * 
     * @param pDisp
     */
    protected Dispatch(int pDisp) {
        m_pDispatch = pDisp;
    }

    /**
     * Constructor to be used by subclass that want to swap themselves
     * in for the default Dispatch class.  Usually you will have a
     * class like WordDocument that is a subclass of Dispatch and it
     * will have a constructor public WordDocument(Dispatch).  That
     * constructor should just call this constructor as super(Dispatch)
     * 
     * @param dispatchToBeDisplaced 
     */
    public Dispatch(Dispatch dispatchToBeDisplaced) {
        //TAKE OVER THE IDispatch POINTER
        this.m_pDispatch = dispatchToBeDisplaced.m_pDispatch;
        //NULL OUT THE INPUT POINTER
        dispatchToBeDisplaced.m_pDispatch = 0;
    }
    
    /**
     * returns the program id if an activeX component created this
     * otherwise it returns null. This was added to aid in debugging
     * @return the program id an activeX component was created against
     */
    public String getProgramId(){
        return programId;
    }
    
    static {
        System.loadLibrary("jacob");
    }

    /**
     * overrides superclass's dispatch to do a release if needed
     */
    protected void finalize() {
        //System.out.println("Dispatch finalize start");
        if (m_pDispatch != 0)
            release();
        //System.out.println("Dispatch finalize end");
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
      return DispatchNative.get(this, name);
    }

    /**
     * returns the selected proeprty as a dispatch
     * @param propertyName
     * @return Dispatch representing the object under the property name
     */
    public Dispatch getPropertyAsDispatch(String propertyName){
        return new Dispatch(DispatchNative.get(this,propertyName).toDispatch());
        
    }
    
    /**
     * 
     * @param propertyName property we are looking up
     * @return boolean value of property
     */
    public boolean getPropertyAsBoolean(String propertyName){
        return DispatchNative.get(this, propertyName).toBoolean();
    }

    /**
     * 
     * @param propertyName property we are looking up
     * @return byte value of property
     */
    public byte getPropertyAsByte(String propertyName){
        return DispatchNative.get(this, propertyName).toByte();
    }

    /**
     * returns the property as a stirng
     * @param propertyName
     * @return String value of property
     */
    public String getPropertyAsString(String propertyName){
        return DispatchNative.get(this, propertyName).toString();

    }
    
    /**
     * 
     * @param propertyName
     * @return the property value as an int
     */
    public int getPropertyAsInt(String propertyName){
        return DispatchNative.get(this,propertyName).toInt();
    }

    /**
     * sets a property on this object
     * @param name property name
     * @param arg variant value to be set
     */
    public void setProperty(String name, Variant arg)
    {
      DispatchNative.put(this, name, arg);
    }

    /**
     * sets a property on this object
     * @param name property name
     * @param arg variant value to be set
     */
    public void setProperty(String name, Dispatch arg)
    {
      DispatchNative.put(this, name, arg);
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
        if (args!=null && Dispatch.shouldLogEvents){
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
     * @return Dispatch representing the results of the call
     */
    public Dispatch callGetDispatch(String callAction){
        return new Dispatch(DispatchNative.call(this,callAction).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter
     * @return Dispatch representing the results of the call
     */
    public Dispatch callGetDispatch(String callAction, Variant parameter){
        return new Dispatch(DispatchNative.call(this,callAction, parameter).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @return Dispatch representing the results of the call
     */
    public Dispatch callGetDispatch(String callAction, Variant parameter1, Variant parameter2){
        return new Dispatch(DispatchNative.call(this,callAction, parameter1, parameter2).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @param parameter3
     * @return Dispatch representing the results of the call
     */
    public Dispatch callGetDispatch(String callAction, Variant parameter1, Variant parameter2, Variant parameter3){
        return new Dispatch(DispatchNative.call(this,callAction, parameter1, parameter2, parameter3).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @param parameter3
     * @param parameter4
     * @return Dispatch representing the results of the call
     */
    public Dispatch callGetDispatch(String callAction, 
            Variant parameter1, 
            Variant parameter2, 
            Variant parameter3,
            Variant parameter4){
        return new Dispatch(DispatchNative.call(this,callAction, 
                parameter1, parameter2, parameter3, parameter4).toDispatch());
    }
    
    /**
     * invokes a single parameter call on this dispatch
     * that returns no value
     * @param actionCommand
     * @param parameter
     * @return a Variant but that may be null for some calls
     */
    public Variant call(String actionCommand, String parameter){
        return DispatchNative.call(this, actionCommand, parameter);
    }
    
    public Variant call(String actionCommand, boolean parameter){
        return DispatchNative.call(this, actionCommand, new Variant(parameter));
    }

    public Variant call(String actionCommand, String parameter1, int parameter2){
        return DispatchNative.call(this, actionCommand, parameter1, new Variant(parameter2));
    }
    /**
     * makes a dispatch call to the passed in action with two 
     * integer parameters
     * @param actionCommand
     * @param parameter1
     * @param parameter2
     * @return a Variant but that may be null for some calls
     */
    public Variant call(String actionCommand, int parameter1, int parameter2){
        return DispatchNative.call(this, actionCommand, 
                new Variant(parameter1),new Variant(parameter2));
    }
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter
     * @return a Variant but that may be null for some calls
     */
    public Variant call(String callAction, Variant parameter){
        return DispatchNative.call(this,callAction, parameter);
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter
     * @return a Variant but that may be null for some calls
     */
    public Variant call(String callAction, Variant parameter1, Variant parameter2){
        return DispatchNative.call(this,callAction, parameter1,parameter2);
    }
    
    /**
     * makes a dispatch call for the passed in action and no parameter
     * @param callAction
     * @return a Variant but that may be null for some calls
     */
    public Variant call(String callAction){
        return DispatchNative.call(this,callAction);
    }
    
    /**
     * cover for callN (call with N params?)
     * @param name
     * @param args array of Variants that are the parameters
     * @return the Variant return code
     */
    public Variant call(String name, Variant[] args)
    {
      return DispatchNative.callN(this, name, args);
    }

    /**
     * call with a variable number of args ainly used for quit.
     * @param name
     * @param args
     * @return Variant result of the invoke
     */
    public Variant invoke(String name, Variant[] args) {
        return DispatchNative.callN(this, name, args);
    }


    
}