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
    /**
     * This is public because Dispatch.cpp knows its name and accesses
     * it directly to get the disptach id.  You really can't rename
     * it or make it private
     */
    public int m_pDispatch;
	/** program Id passed in by ActiveX components in their constructor */
    private String programId = null;
  
  public static final int LOCALE_SYSTEM_DEFAULT = 2048;
  public static final int Method = 1;
  public static final int Get = 2;
  public static final int Put = 4;
  public static final int PutRef = 8;
  public static final int fdexNameCaseSensitive = 1;
  public static final int DISPID_UNKNOWN = -1;
  public static final int DISPID_VALUE = 0;
  public static final int DISPID_PROPERTYPUT = -3;
  public static final int DISPID_NEWENUM = -4;
  public static final int DISPID_EVALUATE = -5;
  public static final int DISPID_CONSTRUCTOR = -6;
  public static final int DISPID_DESTRUCTOR = -7;
  public static final int DISPID_COLLECT = -8;
  public static final int DISPID_AUTOSIZE = -500;
  public static final int DISPID_BACKCOLOR = -501;
  public static final int DISPID_BACKSTYLE = -502;
  public static final int DISPID_BORDERCOLOR = -503;
  public static final int DISPID_BORDERSTYLE = -504;
  public static final int DISPID_BORDERWIDTH = -505;
  public static final int DISPID_DRAWMODE = -507;
  public static final int DISPID_DRAWSTYLE = -508;
  public static final int DISPID_DRAWWIDTH = -509;
  public static final int DISPID_FILLCOLOR = -510;
  public static final int DISPID_FILLSTYLE = -511;
  public static final int DISPID_FONT = -512;
  public static final int DISPID_FORECOLOR = -513;
  public static final int DISPID_ENABLED = -514;
  public static final int DISPID_HWND = -515;
  public static final int DISPID_TABSTOP = -516;
  public static final int DISPID_TEXT = -517;
  public static final int DISPID_CAPTION = -518;
  public static final int DISPID_BORDERVISIBLE = -519;
  public static final int DISPID_APPEARANCE = -520;
  public static final int DISPID_MOUSEPOINTER = -521;
  public static final int DISPID_MOUSEICON = -522;
  public static final int DISPID_PICTURE = -523;
  public static final int DISPID_VALID = -524;
  public static final int DISPID_READYSTATE = -525;
  public static final int DISPID_REFRESH = -550;
  public static final int DISPID_DOCLICK = -551;
  public static final int DISPID_ABOUTBOX = -552;
  public static final int DISPID_CLICK = -600;
  public static final int DISPID_DBLCLICK = -601;
  public static final int DISPID_KEYDOWN = -602;
  public static final int DISPID_KEYPRESS = -603;
  public static final int DISPID_KEYUP = -604;
  public static final int DISPID_MOUSEDOWN = -605;
  public static final int DISPID_MOUSEMOVE = -606;
  public static final int DISPID_MOUSEUP = -607;
  public static final int DISPID_ERROREVENT = -608;
  public static final int DISPID_READYSTATECHANGE = -609;
  public static final int DISPID_AMBIENT_BACKCOLOR = -701;
  public static final int DISPID_AMBIENT_DISPLAYNAME = -702;
  public static final int DISPID_AMBIENT_FONT = -703;
  public static final int DISPID_AMBIENT_FORECOLOR = -704;
  public static final int DISPID_AMBIENT_LOCALEID = -705;
  public static final int DISPID_AMBIENT_MESSAGEREFLECT = -706;
  public static final int DISPID_AMBIENT_SCALEUNITS = -707;
  public static final int DISPID_AMBIENT_TEXTALIGN = -708;
  public static final int DISPID_AMBIENT_USERMODE = -709;
  public static final int DISPID_AMBIENT_UIDEAD = -710;
  public static final int DISPID_AMBIENT_SHOWGRABHANDLES = -711;
  public static final int DISPID_AMBIENT_SHOWHATCHING = -712;
  public static final int DISPID_AMBIENT_DISPLAYASDEFAULT = -713;
  public static final int DISPID_AMBIENT_SUPPORTSMNEMONICS = -714;
  public static final int DISPID_AMBIENT_AUTOCLIP = -715;
  public static final int DISPID_AMBIENT_APPEARANCE = -716;
  public static final int DISPID_AMBIENT_CODEPAGE = -725;
  public static final int DISPID_AMBIENT_PALETTE = -726;
  public static final int DISPID_AMBIENT_CHARSET = -727;
  public static final int DISPID_AMBIENT_TRANSFERPRIORITY = -728;


  // map args based on msdn doc
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
     * native call createIstnace only used by the constructor with the same parm
     * type could this be private?
     * 
     * @param progid
     */
    protected native void createInstance(String progid);

    public String getProgramId(){
        return programId;
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
    public static void put_Casesensitive(Object disp, String name, Object val) {
        throw new ClassCastException("not implemented yet");
    }

    // eliminate _Guid arg
    public static void invokeSubv(Object disp, String name, int dispID,
            int lcid, int wFlags, Variant[] vArg, int[] uArgErr) {
        invokev(disp, name, dispID, lcid, wFlags, vArg, uArgErr);
    }

    public static void invokeSubv(Object disp, String name, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        invokev(disp, name, 0, LOCALE_SYSTEM_DEFAULT, wFlags, vArg, uArgErr);
    }

    public static void invokeSubv(Object disp, int dispID, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        invokev(disp, null, dispID, LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    public static Variant callN_CaseSensitive(Object disp, String name,
            Object[] values) {
        throw new ClassCastException("not implemented yet");
    }

    public static void callSubN(Object disp, String name, Object[] args) {
        invokeSubv(disp, name, Method | Get, obj2variant(args),
                new int[args.length]);
    }

    public static void callSubN(Object disp, int dispID, Object[] args) {
        invokeSubv(disp, dispID, Method | Get, obj2variant(args),
                new int[args.length]);
    }

    public static int getIDOfName(Object disp, String name) {
        int ids[] = getIDsOfNames(disp, LOCALE_SYSTEM_DEFAULT,
                new String[] { name });
        return ids[0];
    }

    // eliminated _Guid argument
    public static native int[] getIDsOfNames(Object disp, int lcid,
            String[] names);

    // eliminated _Guid argument
    public static int[] getIDsOfNames(Object disp, String[] names) {
        return getIDsOfNames(disp, LOCALE_SYSTEM_DEFAULT, names);
    }

    public static Variant callN(Object disp, String name, Object[] args) {
        return invokev(disp, name, Method | Get, obj2variant(args),
                new int[args.length]);
    }

    public static Variant callN(Object disp, int dispID, Object[] args) {
        return invokev(disp, dispID, Method | Get, obj2variant(args),
                new int[args.length]);
    }

    public static Variant invoke(Object disp, String name, int dispID,
            int lcid, int wFlags, Object[] oArg, int[] uArgErr) {
        return invokev(disp, name, dispID, lcid, wFlags, obj2variant(oArg),
                uArgErr);
    }

    public static Variant invoke(Object disp, String name, int wFlags,
            Object[] oArg, int[] uArgErr) {
        return invokev(disp, name, wFlags, obj2variant(oArg), uArgErr);
    }

    public static Variant invoke(Object disp, int dispID, int wFlags,
            Object[] oArg, int[] uArgErr) {
        return invokev(disp, dispID, wFlags, obj2variant(oArg), uArgErr);
    }

    public static Variant call(Object disp, String name) {
        return callN(disp, name, new Variant[0]);
    }

    public static Variant call(Object disp, String name, Object a1) {
        return callN(disp, name, new Object[] { a1 });
    }

    public static Variant call(Object disp, String name, Object a1, Object a2) {
        return callN(disp, name, new Object[] { a1, a2 });
    }

    public static Variant call(Object disp, String name, Object a1, Object a2,
            Object a3) {
        return callN(disp, name, new Object[] { a1, a2, a3 });
    }

    public static Variant call(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4) {
        return callN(disp, name, new Object[] { a1, a2, a3, a4 });
    }

    public static Variant call(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        return callN(disp, name, new Object[] { a1, a2, a3, a4, a5 });
    }

    public static Variant call(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        return callN(disp, name, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    public static Variant call(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        return callN(disp, name, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    public static Variant call(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        return callN(disp, name,
                new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    public static Variant call(Object disp, int dispid) {
        return callN(disp, dispid, new Variant[0]);
    }

    public static Variant call(Object disp, int dispid, Object a1) {
        return callN(disp, dispid, new Object[] { a1 });
    }

    public static Variant call(Object disp, int dispid, Object a1, Object a2) {
        return callN(disp, dispid, new Object[] { a1, a2 });
    }

    public static Variant call(Object disp, int dispid, Object a1, Object a2,
            Object a3) {
        return callN(disp, dispid, new Object[] { a1, a2, a3 });
    }

    public static Variant call(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4) {
        return callN(disp, dispid, new Object[] { a1, a2, a3, a4 });
    }

    public static Variant call(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        return callN(disp, dispid, new Object[] { a1, a2, a3, a4, a5 });
    }

    public static Variant call(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        return callN(disp, dispid, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    public static Variant call(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        return callN(disp, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    public static Variant call(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        return callN(disp, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7,
                a8 });
    }

    public static void put(Object disp, String name, Object val) {
        invoke(disp, name, Put, new Object[] { val }, new int[1]);
    }

    public static void put(Object disp, int dispid, Object val) {
        invoke(disp, dispid, Put, new Object[] { val }, new int[1]);
    }

    // removed _Guid argument
    public static native Variant invokev(Object disp, String name, int dispID,
            int lcid, int wFlags, Variant[] vArg, int[] uArgErr);

    public static Variant invokev(Object disp, String name, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        if (!(disp instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        return invokev(disp, name, 0, LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    public static Variant invokev(Object disp, String name, int wFlags,
            Variant[] vArg, int[] uArgErr, int wFlagsEx) {
        if (!(disp instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        // do not implement IDispatchEx for now
        return invokev(disp, name, 0, LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    public static Variant invokev(Object disp, int dispID, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        if (!(disp instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        return invokev(disp, null, dispID, LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    // removed _Guid argument
    public static void invokeSub(Object disp, String name, int dispid,
            int lcid, int wFlags, Object[] oArg, int[] uArgErr) {
        invokeSubv(disp, name, dispid, lcid, wFlags, obj2variant(oArg), uArgErr);
    }

    public static void invokeSub(Object disp, String name, int wFlags,
            Object[] oArg, int[] uArgErr) {
        invokeSub(disp, name, 0, LOCALE_SYSTEM_DEFAULT, wFlags, oArg, uArgErr);
    }

    public static void invokeSub(Object disp, int dispid, int wFlags,
            Object[] oArg, int[] uArgErr) {
        invokeSub(disp, null, dispid, LOCALE_SYSTEM_DEFAULT, wFlags, oArg,
                uArgErr);
    }

    public static void callSub(Object disp, String name) {
        callSubN(disp, name, new Object[0]);
    }

    public static void callSub(Object disp, String name, Object a1) {
        callSubN(disp, name, new Object[] { a1 });
    }

    public static void callSub(Object disp, String name, Object a1, Object a2) {
        callSubN(disp, name, new Object[] { a1, a2 });
    }

    public static void callSub(Object disp, String name, Object a1, Object a2,
            Object a3) {
        callSubN(disp, name, new Object[] { a1, a2, a3 });
    }

    public static void callSub(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4) {
        callSubN(disp, name, new Object[] { a1, a2, a3, a4 });
    }

    public static void callSub(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        callSubN(disp, name, new Object[] { a1, a2, a3, a4, a5 });
    }

    public static void callSub(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        callSubN(disp, name, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    public static void callSub(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        callSubN(disp, name, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    public static void callSub(Object disp, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        callSubN(disp, name, new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    public static void callSub(Object disp, int dispid) {
        callSubN(disp, dispid, new Object[0]);
    }

    public static void callSub(Object disp, int dispid, Object a1) {
        callSubN(disp, dispid, new Object[] { a1 });
    }

    public static void callSub(Object disp, int dispid, Object a1, Object a2) {
        callSubN(disp, dispid, new Object[] { a1, a2 });
    }

    public static void callSub(Object disp, int dispid, Object a1, Object a2,
            Object a3) {
        callSubN(disp, dispid, new Object[] { a1, a2, a3 });
    }

    public static void callSub(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4) {
        callSubN(disp, dispid, new Object[] { a1, a2, a3, a4 });
    }

    public static void callSub(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        callSubN(disp, dispid, new Object[] { a1, a2, a3, a4, a5 });
    }

    public static void callSub(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        callSubN(disp, dispid, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    public static void callSub(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        callSubN(disp, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    public static void callSub(Object disp, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        callSubN(disp, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    public static Variant get(Object disp, String name) {
        return invokev(disp, name, Get, new Variant[0], new int[0]);
    }

    public static Variant get(Object disp, int dispid) {
        return invokev(disp, dispid, Get, new Variant[0], new int[0]);
    }

    public static void putRef(Object disp, String name, Object val) {
        invoke(disp, name, PutRef, new Object[] { val }, new int[1]);
    }

    public static void putRef(Object disp, int dispid, Object val) {
        invoke(disp, dispid, PutRef, new Object[] { val }, new int[1]);
    }

    public static Variant get_CaseSensitive(Object disp, String name) {
        throw new ClassCastException("not implemented yet");
    }

    static {
        System.loadLibrary("jacob");
    }

    protected void finalize() {
        //System.out.println("Dispatch finalize start");
        if (m_pDispatch != 0)
            release();
        //System.out.println("Dispatch finalize end");
    }
}