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

import java.lang.reflect.Array;

/**
 * Object represents MS level dispatch object. You're going to live here
 * a lot
 */
public class Dispatch extends JacobObject
{
    static {
        System.loadLibrary("jacob");
    }

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
      return Dispatch.get(this, name);
    }

    /**
     * returns the selected proeprty as a dispatch
     * @param propertyName
     * @return Dispatch representing the object under the property name
     */
    public Dispatch getPropertyAsDispatch(String propertyName){
        return new Dispatch(Dispatch.get(this,propertyName).toDispatch());
        
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
        return new Dispatch(Dispatch.call(this,callAction).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter
     * @return Dispatch representing the results of the call
     */
    public Dispatch callGetDispatch(String callAction, Variant parameter){
        return new Dispatch(Dispatch.call(this,callAction, parameter).toDispatch());
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @return Dispatch representing the results of the call
     */
    public Dispatch callGetDispatch(String callAction, Variant parameter1, Variant parameter2){
        return new Dispatch(Dispatch.call(this,callAction, parameter1, parameter2).toDispatch());
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
        return new Dispatch(Dispatch.call(this,callAction, parameter1, parameter2, parameter3).toDispatch());
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
        return new Dispatch(Dispatch.call(this,callAction, 
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
        return Dispatch.call(this, actionCommand, parameter);
    }
    
    /**
     * makes a dispatch call to the passed in action with a single boolean parameter
     * @param actionCommand
     * @param parameter
     * @return Variant result
     */
    public Variant call(String actionCommand, boolean parameter){
        return Dispatch.call(this, actionCommand, new Variant(parameter));
    }

    /**
     * makes a dispatch call to teh passed in action with a string and integer parameter
     * @param actionCommand
     * @param parameter1
     * @param parameter2
     * @return Variant result
     */
    public Variant call(String actionCommand, String parameter1, int parameter2){
        return Dispatch.call(this, actionCommand, parameter1, new Variant(parameter2));
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
        return Dispatch.call(this, actionCommand, 
                new Variant(parameter1),new Variant(parameter2));
    }
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter
     * @return a Variant but that may be null for some calls
     */
    public Variant call(String callAction, Variant parameter){
        return Dispatch.call(this,callAction, parameter);
    }
    
    /**
     * makes a dispatch call for the passed in action and single parameter
     * @param callAction
     * @param parameter1
     * @param parameter2
     * @return a Variant but that may be null for some calls
     */
    public Variant call(String callAction, Variant parameter1, Variant parameter2){
        return Dispatch.call(this,callAction, parameter1,parameter2);
    }
    
    /**
     * makes a dispatch call for the passed in action and no parameter
     * @param callAction
     * @return a Variant but that may be null for some calls
     */
    public Variant call(String callAction){
        return Dispatch.call(this,callAction);
    }
    
    /**
     * call with a variable number of args mainly used for quit.
     * @param name
     * @param args
     * @return Variant result of the invoke
     */
    public Variant call(String name, Variant[] args) {
        return Dispatch.callN(this, name, args);
    }

    /**
     *  map args based on msdn doc
     * @param o
     * @return Variant that represents the object
     */
    protected static Variant obj2variant(Object o) {
        if (o == null)
            return new Variant();
        if (o instanceof Variant)
            return (Variant) o;
        if (o instanceof Integer)
            return new Variant(((Integer) o).intValue());
        if (o instanceof String)
            return new Variant((String) o);
        if (o instanceof Boolean)
            return new Variant(((Boolean) o).booleanValue());
        if (o instanceof Double)
            return new Variant(((Double) o).doubleValue());
        if (o instanceof Float)
            return new Variant(((Float) o).floatValue());
        if (o instanceof SafeArray)
            return new Variant((SafeArray) o);
        if (o instanceof Dispatch) {
            Variant v = new Variant();
            v.putObject((Dispatch) o);
            return v;
        }
        // automatically convert arrays using reflection
        Class c1 = o.getClass();
        SafeArray sa = null;
        if (c1.isArray()) {
            int len1 = Array.getLength(o);
            Object first = Array.get(o, 0);
            if (first.getClass().isArray()) {
                int max = 0;
                for (int i = 0; i < len1; i++) {
                    Object e1 = Array.get(o, i);
                    int len2 = Array.getLength(e1);
                    if (max < len2) {
                        max = len2;
                    }
                }
                sa = new SafeArray(Variant.VariantVariant, len1, max);
                for (int i = 0; i < len1; i++) {
                    Object e1 = Array.get(o, i);
                    for (int j = 0; j < Array.getLength(e1); j++) {
                        sa.setVariant(i, j, obj2variant(Array.get(e1, j)));
                    }
                }
            } else {
                sa = new SafeArray(Variant.VariantVariant, len1);
                for (int i = 0; i < len1; i++) {
                    sa.setVariant(i, obj2variant(Array.get(o, i)));
                }
            }
            return new Variant(sa);
        }
        throw new ClassCastException("cannot convert to Variant");
    }

    /**
     * same as above, for an array
     * @param o
     * @return Variant[] 
     */
    protected static Variant[] obj2variant(Object[] o) {
        Variant vArg[] = new Variant[o.length];
        for (int i = 0; i < o.length; i++) {
            vArg[i] = obj2variant(o[i]);
        }
        return vArg;
    }


    /**
     * call this to explicitly release the com object before gc
     */
    public native void release();

    /**
     * not implemented yet
     * 
     * @param disp
     * @param name
     * @param val
     * @throws ClassCastException
     *             because???
     */
    public static void put_Casesensitive(Dispatch dispatchTarget, String name, Object val) {
        throw new ClassCastException("not implemented yet");
    }

    /*============================================================
     * start of the invokev section
     * ===========================================================
     */
    // eliminate _Guid arg
    public static void invokeSubv(Dispatch dispatchTarget, String name, int dispID,
            int lcid, int wFlags, Variant[] vArg, int[] uArgErr) {
        invokev(dispatchTarget, name, dispID, lcid, wFlags, vArg, uArgErr);
    }

    public static void invokeSubv(Dispatch dispatchTarget, String name, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        invokev(dispatchTarget, name, 0, DispatchConstants.LOCALE_SYSTEM_DEFAULT, wFlags, vArg, uArgErr);
    }

    public static void invokeSubv(Dispatch dispatchTarget, int dispID, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        invokev(dispatchTarget, null, dispID, DispatchConstants.LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    public static Variant callN_CaseSensitive(Dispatch dispatchTarget, String name,
            Object[] values) {
        throw new ClassCastException("not implemented yet");
    }

    public static void callSubN(Dispatch dispatchTarget, String name, Object[] args) {
        invokeSubv(dispatchTarget, name, DispatchConstants.Method | DispatchConstants.Get, obj2variant(args),
                new int[args.length]);
    }

    public static void callSubN(Dispatch dispatchTarget, int dispID, Object[] args) {
        invokeSubv(dispatchTarget, dispID, DispatchConstants.Method | DispatchConstants.Get, obj2variant(args),
                new int[args.length]);
    }

    /*============================================================
     * start of the getIdsOfNames section
     * ===========================================================
     */
    public static int getIDOfName(Dispatch dispatchTarget, String name) {
        int ids[] = getIDsOfNames(dispatchTarget, DispatchConstants.LOCALE_SYSTEM_DEFAULT,
                new String[] { name });
        return ids[0];
    }

    // eliminated _Guid argument
    public static native int[] getIDsOfNames(Dispatch dispatchTarget, int lcid,
            String[] names);

    // eliminated _Guid argument
    public static int[] getIDsOfNames(Dispatch dispatchTarget, String[] names) {
        return getIDsOfNames(dispatchTarget, 
                DispatchConstants.LOCALE_SYSTEM_DEFAULT, names);
    }

    /*============================================================
     * start of the invokev section
     * ===========================================================
     */
    public static Variant callN(Dispatch dispatchTarget, String name, Object[] args) {
        return invokev(dispatchTarget, name, 
                DispatchConstants.Method | DispatchConstants.Get, obj2variant(args),
                new int[args.length]);
    }

    public static Variant callN(Dispatch dispatchTarget, int dispID, Object[] args) {
        return invokev(dispatchTarget, dispID, 
                DispatchConstants.Method | DispatchConstants.Get, obj2variant(args),
                new int[args.length]);
    }

    public static Variant invoke(Dispatch dispatchTarget, String name, int dispID,
            int lcid, int wFlags, Object[] oArg, int[] uArgErr) {
        return invokev(dispatchTarget, name, dispID, lcid, wFlags, obj2variant(oArg),
                uArgErr);
    }

    public static Variant invoke(Dispatch dispatchTarget, String name, int wFlags,
            Object[] oArg, int[] uArgErr) {
        return invokev(dispatchTarget, name, wFlags, obj2variant(oArg), uArgErr);
    }

    public static Variant invoke(Dispatch dispatchTarget, int dispID, int wFlags,
            Object[] oArg, int[] uArgErr) {
        return invokev(dispatchTarget, dispID, wFlags, obj2variant(oArg), uArgErr);
    }
    
    /*============================================================
     * start of the callN section
     * ===========================================================
     */

    public static Variant call(Dispatch dispatchTarget, String name) {
        return callN(dispatchTarget, name, new Variant[0]);
    }

    public static Variant call(Dispatch dispatchTarget, String name, Object a1) {
        return callN(dispatchTarget, name, new Object[] { a1 });
    }

    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2) {
        return callN(dispatchTarget, name, new Object[] { a1, a2 });
    }

    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3 });
    }

    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3, a4 });
    }

    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5 });
    }

    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        return callN(dispatchTarget, name,
                new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    public static Variant call(Dispatch dispatchTarget, int dispid) {
        return callN(dispatchTarget, dispid, new Variant[0]);
    }

    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1) {
        return callN(dispatchTarget, dispid, new Object[] { a1 });
    }

    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2 });
    }

    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3 });
    }

    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4 });
    }

    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5 });
    }

    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7,
                a8 });
    }

    /*============================================================
     * start of the invoke section
     * ===========================================================
     */
    public static void put(Dispatch dispatchTarget, String name, Object val) {
        invoke(dispatchTarget, name, DispatchConstants.Put, new Object[] { val }, new int[1]);
    }

    public static void put(Dispatch dispatchTarget, int dispid, Object val) {
        invoke(dispatchTarget, dispid, DispatchConstants.Put, new Object[] { val }, new int[1]);
    }

    /*============================================================
     * start of the invokev section
     * ===========================================================
     */
    // removed _Guid argument
    public static native Variant invokev(Dispatch dispatchTarget, String name, int dispID,
            int lcid, int wFlags, Variant[] vArg, int[] uArgErr);

    public static Variant invokev(Dispatch dispatchTarget, String name, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        if (!(dispatchTarget instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        return invokev(dispatchTarget, name, 0, DispatchConstants.LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    public static Variant invokev(Dispatch dispatchTarget, String name, int wFlags,
            Variant[] vArg, int[] uArgErr, int wFlagsEx) {
        if (!(dispatchTarget instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        // do not implement IDispatchEx for now
        return invokev(dispatchTarget, name, 0, DispatchConstants.LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    public static Variant invokev(Dispatch dispatchTarget, int dispID, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        if (!(dispatchTarget instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        return invokev(dispatchTarget, null, dispID, 
                DispatchConstants.LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    /*============================================================
     * start of the invokeSubv section
     * ===========================================================
     */
    
    // removed _Guid argument
    public static void invokeSub(Dispatch dispatchTarget, String name, int dispid,
            int lcid, int wFlags, Object[] oArg, int[] uArgErr) {
        invokeSubv(dispatchTarget, name, dispid, lcid, wFlags, obj2variant(oArg), uArgErr);
    }

    /*============================================================
     * start of the invokeSub section
     * ===========================================================
     */
    public static void invokeSub(Dispatch dispatchTarget, String name, int wFlags,
            Object[] oArg, int[] uArgErr) {
        invokeSub(dispatchTarget, name, 0, DispatchConstants.LOCALE_SYSTEM_DEFAULT, wFlags, oArg, uArgErr);
    }

    public static void invokeSub(Dispatch dispatchTarget, int dispid, int wFlags,
            Object[] oArg, int[] uArgErr) {
        invokeSub(dispatchTarget, null, dispid, DispatchConstants.LOCALE_SYSTEM_DEFAULT, wFlags, oArg,
                uArgErr);
    }

    /*============================================================
     * start of the callSubN section
     * ===========================================================
     */
    public static void callSub(Dispatch dispatchTarget, String name) {
        callSubN(dispatchTarget, name, new Object[0]);
    }

    public static void callSub(Dispatch dispatchTarget, String name, Object a1) {
        callSubN(dispatchTarget, name, new Object[] { a1 });
    }

    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2 });
    }

    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3 });
    }

    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4 });
    }

    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5 });
    }

    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    public static void callSub(Dispatch dispatchTarget, int dispid) {
        callSubN(dispatchTarget, dispid, new Object[0]);
    }

    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1) {
        callSubN(dispatchTarget, dispid, new Object[] { a1 });
    }

    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2 });
    }

    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3 });
    }

    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4 });
    }

    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5 });
    }

    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    /*============================================================
     * start of the invokev section
     * ===========================================================
     */
    public static Variant get(Dispatch dispatchTarget, String name) {
        return invokev(dispatchTarget, name, DispatchConstants.Get, new Variant[0], new int[0]);
    }

    public static Variant get(Dispatch dispatchTarget, int dispid) {
        return invokev(dispatchTarget, dispid, DispatchConstants.Get, new Variant[0], new int[0]);
    }

    /*============================================================
     * start of the invoke section
     * ===========================================================
     */
    public static void putRef(Dispatch dispatchTarget, String name, Object val) {
        invoke(dispatchTarget, name, DispatchConstants.PutRef, new Object[] { val }, new int[1]);
    }

    public static void putRef(Dispatch dispatchTarget, int dispid, Object val) {
        invoke(dispatchTarget, dispid, DispatchConstants.PutRef, new Object[] { val }, new int[1]);
    }

    public static Variant get_CaseSensitive(Dispatch dispatchTarget, String name) {
        throw new ClassCastException("not implemented yet");
    }
    
}