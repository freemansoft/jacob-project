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
package com.jacob.com;

import java.lang.reflect.Array;

/**
 * Object represents MS level dispatch object. 
 * Each instance of this points at some data structure on the
 * MS windows side.   
 * 
 * 
 * <p>You're going to live here a lot
 */
public class Dispatch extends JacobObject
{
    static {
        System.loadLibrary("jacob");
    }

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
    /**
     * This is public because Dispatch.cpp knows its name and accesses
     * it directly to get the disptach id.  You really can't rename
     * it or make it private
     */
    public int m_pDispatch;
	/** program Id passed in by ActiveX components in their constructor */
    private String programId = null;
  

    /**
     * Dummy empty array used one doesn't have to be created on every 
     * invocation
     */
    private final static Object[] NO_OBJECT_ARGS = new Object[0];
    /**
     * Dummy empty array used one doesn't have to be created on every 
     * invocation
     */
    private final static Variant[] NO_VARIANT_ARGS = new Variant[0];
    /**
     * Dummy empty array used one doesn't have to be created on every 
     * invocation
     */
    private final static int[] NO_INT_ARGS = new int[0];


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
     * native call createInstance only used by the constructor with the same parm
     * type. This probably should be private
     * <P>
     * This ends up calling CoCreate down in the JNI layer
     * 
     * @param progid
     */
    protected native void createInstance(String progid);

    /**
     * return a different interface by IID string
     * <p>
     * Once you have a Dispatch object, you can navigate to the other 
     * interfaces of a COM object by calling QueryInterafce. 
     * The argument is an IID string in the format: 
     * "{9BF24410-B2E0-11D4-A695-00104BFF3241}". You typically get 
     * this string from the idl file (it's called uuid in there). 
     * Any interface you try to use must be derived from IDispatch. T
     * The atl example uses this.
     * <p>
     * The Dispatch instance resulting from this query is instanciated in the
     * JNI code.
     * 
     * @param iid
     * @return Dispatch a disptach that matches ??
     */
    public native Dispatch QueryInterface(String iid);

    /**
     * Constructor that only gets called from JNI
     * QueryInterface calls JNI code that looks up the object 
     * for the key passed in.  The JNI CODE then creates a new dispatch object
     * using this constructor
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
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() {
        safeRelease();
    }
    
    /*
     *  (non-Javadoc)
     * @see com.jacob.com.JacobObject#safeRelease()
     */
    public void safeRelease()
    {
        super.safeRelease();
        if (m_pDispatch != 0){
            release();
            m_pDispatch = 0;
        } else {
            // looks like a double release
            if (isDebugEnabled()){debug(this.getClass().getName()+":"+this.hashCode()+" double release");}
        }
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
        if (o instanceof Short)
            return new Variant(((Short) o).shortValue());
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
     *  now private so only this object can asccess
     *  was: call this to explicitly release the com object before gc
     * 
     */
    private native void release();

    /**
     * not implemented yet
     * @param dispatchTarget
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
    /**
     * @param dispatchTarget
     * @param name
     * @param dispID
     * @param lcid
     * @param wFlags
     * @param vArg
     * @param uArgErr
     */
    public static void invokeSubv(Dispatch dispatchTarget, String name, int dispID,
            int lcid, int wFlags, Variant[] vArg, int[] uArgErr) {
        invokev(dispatchTarget, name, dispID, lcid, wFlags, vArg, uArgErr);
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param wFlags
     * @param vArg
     * @param uArgErr
     */
    public static void invokeSubv(Dispatch dispatchTarget, String name, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        invokev(dispatchTarget, name, 0, Dispatch.LOCALE_SYSTEM_DEFAULT, wFlags, vArg, uArgErr);
    }

    /**
     * @param dispatchTarget
     * @param dispID
     * @param wFlags
     * @param vArg
     * @param uArgErr
     */
    public static void invokeSubv(Dispatch dispatchTarget, int dispID, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        invokev(dispatchTarget, null, dispID, Dispatch.LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    /**
     * not implemented yet
     * @param dispatchTarget
     * @param name
     * @param values
     * @return never returns anything because throws ClassCastException
     * because not implemented yet
     */
    public static Variant callN_CaseSensitive(Dispatch dispatchTarget, String name,
            Object[] values) {
        throw new ClassCastException("not implemented yet");
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param args
     */
    public static void callSubN(Dispatch dispatchTarget, String name, Object[] args) {
        invokeSubv(dispatchTarget, name, Dispatch.Method | Dispatch.Get, obj2variant(args),
                new int[args.length]);
    }

    /**
     * @param dispatchTarget
     * @param dispID
     * @param args
     */
    public static void callSubN(Dispatch dispatchTarget, int dispID, Object[] args) {
        invokeSubv(dispatchTarget, dispID, Dispatch.Method | Dispatch.Get, obj2variant(args),
                new int[args.length]);
    }

    /*============================================================
     * start of the getIdsOfNames section
     * ===========================================================
     */
    /**
     * @param dispatchTarget
     * @param name
     * @return int id for the passed in name
     */
    public static int getIDOfName(Dispatch dispatchTarget, String name) {
        int ids[] = getIDsOfNames(dispatchTarget, Dispatch.LOCALE_SYSTEM_DEFAULT,
                new String[] { name });
        return ids[0];
    }

    /**
     * @param dispatchTarget
     * @param lcid
     * @param names
     * @return int[] in id array for passed in names
     */
    // eliminated _Guid argument
    public static native int[] getIDsOfNames(Dispatch dispatchTarget, int lcid,
            String[] names);

    /**
     * @param dispatchTarget
     * @param names
     * @return int[] int id array for passed in names
     */
    // eliminated _Guid argument
    public static int[] getIDsOfNames(Dispatch dispatchTarget, String[] names) {
        return getIDsOfNames(dispatchTarget, 
                Dispatch.LOCALE_SYSTEM_DEFAULT, names);
    }

    /*============================================================
     * start of the invokev section
     * ===========================================================
     */
    /**
     * @param dispatchTarget
     * @param name
     * @param args
     * @return Variant returned by call
     */
    public static Variant callN(Dispatch dispatchTarget, String name, Object[] args) {
        return invokev(dispatchTarget, name, 
                Dispatch.Method | Dispatch.Get, obj2variant(args),
                new int[args.length]);
    }

    /**
     * @param dispatchTarget
     * @param dispID
     * @param args
     * @return Variant returned by call
     */
    public static Variant callN(Dispatch dispatchTarget, int dispID, Object[] args) {
        return invokev(dispatchTarget, dispID, 
                Dispatch.Method | Dispatch.Get, obj2variant(args),
                new int[args.length]);
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param dispID
     * @param lcid
     * @param wFlags
     * @param oArg
     * @param uArgErr
     * @return Variuant returned by invoke
     */
    public static Variant invoke(Dispatch dispatchTarget, String name, int dispID,
            int lcid, int wFlags, Object[] oArg, int[] uArgErr) {
        return invokev(dispatchTarget, name, dispID, lcid, wFlags, obj2variant(oArg),
                uArgErr);
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param wFlags
     * @param oArg
     * @param uArgErr
     * @return Variuant returned by invoke
     */
    public static Variant invoke(Dispatch dispatchTarget, String name, int wFlags,
            Object[] oArg, int[] uArgErr) {
        return invokev(dispatchTarget, name, wFlags, obj2variant(oArg), uArgErr);
    }

    /**
     * @param dispatchTarget
     * @param dispID
     * @param wFlags
     * @param oArg
     * @param uArgErr
     * @return Variuant returned by invoke
     */
    public static Variant invoke(Dispatch dispatchTarget, int dispID, int wFlags,
            Object[] oArg, int[] uArgErr) {
        return invokev(dispatchTarget, dispID, wFlags, obj2variant(oArg), uArgErr);
    }
    
    /*============================================================
     * start of the callN section
     * ===========================================================
     */

    /**
     * @param dispatchTarget
     * @param name
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name) {
        return callN(dispatchTarget, name, NO_VARIANT_ARGS);
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param a1
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name, Object a1) {
        return callN(dispatchTarget, name, new Object[] { a1 });
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2) {
        return callN(dispatchTarget, name, new Object[] { a1, a2 });
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3 });
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3, a4 });
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5 });
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        return callN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     * @param a8
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        return callN(dispatchTarget, name,
                new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid) {
        return callN(dispatchTarget, dispid, NO_VARIANT_ARGS);
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1) {
        return callN(dispatchTarget, dispid, new Object[] { a1 });
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2 });
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3 });
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4 });
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5 });
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     * @param a8
     * @return Variant retuned by underlying callN
     */
    public static Variant call(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        return callN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7,
                a8 });
    }

    /*============================================================
     * start of the invoke section
     * ===========================================================
     */
    /**
     * @param dispatchTarget
     * @param name
     * @param val
     */
    public static void put(Dispatch dispatchTarget, String name, Object val) {
        invoke(dispatchTarget, name, Dispatch.Put, new Object[] { val }, new int[1]);
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param val
     */
    public static void put(Dispatch dispatchTarget, int dispid, Object val) {
        invoke(dispatchTarget, dispid, Dispatch.Put, new Object[] { val }, new int[1]);
    }

    /*============================================================
     * start of the invokev section
     * ===========================================================
     */
    // removed _Guid argument
    /**
     * @param dispatchTarget
     * @param name
     * @param dispID
     * @param lcid
     * @param wFlags
     * @param vArg
     * @param uArgErr
     * @return Variant returned by underlying invokev
     */
    public static native Variant invokev(Dispatch dispatchTarget, String name, int dispID,
            int lcid, int wFlags, Variant[] vArg, int[] uArgErr);

    /**
     * @param dispatchTarget
     * @param name
     * @param wFlags
     * @param vArg
     * @param uArgErr
     * @return Variant returned by underlying invokev
     */
    public static Variant invokev(Dispatch dispatchTarget, String name, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        if (!(dispatchTarget instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        return invokev(dispatchTarget, name, 0, Dispatch.LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    /**
     * @param dispatchTarget
     * @param name
     * @param wFlags
     * @param vArg
     * @param uArgErr
     * @param wFlagsEx
     * @return Variant returned by underlying invokev
     */
    public static Variant invokev(Dispatch dispatchTarget, String name, int wFlags,
            Variant[] vArg, int[] uArgErr, int wFlagsEx) {
        if (!(dispatchTarget instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        // do not implement IDispatchEx for now
        return invokev(dispatchTarget, name, 0, Dispatch.LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    /**
     * @param dispatchTarget
     * @param dispID
     * @param wFlags
     * @param vArg
     * @param uArgErr
     * @return Variant returned by underlying invokev
     */
    public static Variant invokev(Dispatch dispatchTarget, int dispID, int wFlags,
            Variant[] vArg, int[] uArgErr) {
        if (!(dispatchTarget instanceof Dispatch))
            throw new ClassCastException("Dispatch object expected");
        return invokev(dispatchTarget, null, dispID, 
                Dispatch.LOCALE_SYSTEM_DEFAULT, wFlags, vArg,
                uArgErr);
    }

    /*============================================================
     * start of the invokeSubv section
     * ===========================================================
     */
    
    // removed _Guid argument
    /**
     * @param dispatchTarget
     * @param name
     * @param dispid
     * @param lcid
     * @param wFlags
     * @param oArg
     * @param uArgErr
     */
    public static void invokeSub(Dispatch dispatchTarget, String name, int dispid,
            int lcid, int wFlags, Object[] oArg, int[] uArgErr) {
        invokeSubv(dispatchTarget, name, dispid, lcid, wFlags, obj2variant(oArg), uArgErr);
    }

    /*============================================================
     * start of the invokeSub section
     * ===========================================================
     */
    /**
     * @param dispatchTarget
     * @param name
     * @param wFlags
     * @param oArg
     * @param uArgErr
     */
    public static void invokeSub(Dispatch dispatchTarget, String name, int wFlags,
            Object[] oArg, int[] uArgErr) {
        invokeSub(dispatchTarget, name, 0, Dispatch.LOCALE_SYSTEM_DEFAULT, wFlags, oArg, uArgErr);
    }

    /**
     * @param dispatchTarget
     * @param dispid
     * @param wFlags
     * @param oArg
     * @param uArgErr
     */
    public static void invokeSub(Dispatch dispatchTarget, int dispid, int wFlags,
            Object[] oArg, int[] uArgErr) {
        invokeSub(dispatchTarget, null, dispid, Dispatch.LOCALE_SYSTEM_DEFAULT, wFlags, oArg,
                uArgErr);
    }

    /*============================================================
     * start of the callSubN section
     * ===========================================================
     */
    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     */
    public static void callSub(Dispatch dispatchTarget, String name) {
        callSubN(dispatchTarget, name, NO_OBJECT_ARGS);
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     * @param a1
     */
    public static void callSub(Dispatch dispatchTarget, String name, Object a1) {
        callSubN(dispatchTarget, name, new Object[] { a1 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     */
    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     */
    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     */
    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     */
    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     */
    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     */
    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param name
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     * @param a8
     */
    public static void callSub(Dispatch dispatchTarget, String name, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        callSubN(dispatchTarget, name, new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     */
    public static void callSub(Dispatch dispatchTarget, int dispid) {
        callSubN(dispatchTarget, dispid, NO_OBJECT_ARGS);
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     * @param a1
     */
    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1) {
        callSubN(dispatchTarget, dispid, new Object[] { a1 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     */
    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     */
    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     */
    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     */
    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     */
    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     */
    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7 });
    }

    /**
     * makes call to native callSubN
     * @param dispatchTarget
     * @param dispid
     * @param a1
     * @param a2
     * @param a3
     * @param a4
     * @param a5
     * @param a6
     * @param a7
     * @param a8
     */
    public static void callSub(Dispatch dispatchTarget, int dispid, Object a1, Object a2,
            Object a3, Object a4, Object a5, Object a6, Object a7, Object a8) {
        callSubN(dispatchTarget, dispid, new Object[] { a1, a2, a3, a4, a5, a6, a7, a8 });
    }

    /*============================================================
     * start of the invokev section
     * ===========================================================
     */
    /**
     * Cover for call to underlying invokev()
     * @param dispatchTarget
     * @param name
     * @return Variant returned by the request for retrieval of parameter
     */
    public static Variant get(Dispatch dispatchTarget, String name) {
        return invokev(dispatchTarget, name, Dispatch.Get, NO_VARIANT_ARGS, NO_INT_ARGS);
    }

    /**
     * Cover for call to underlying invokev()
     * @param dispatchTarget
     * @param dispid
     * @return Variant returned by the request for retrieval of parameter
     */
    public static Variant get(Dispatch dispatchTarget, int dispid) {
        return invokev(dispatchTarget, dispid, Dispatch.Get, NO_VARIANT_ARGS, NO_INT_ARGS);
    }

    /*============================================================
     * start of the invoke section
     * ===========================================================
     */
    /**
     * cover for underlying call to invoke
     * @param dispatchTarget
     * @param name
     * @param val
     */
    public static void putRef(Dispatch dispatchTarget, String name, Object val) {
        invoke(dispatchTarget, name, Dispatch.PutRef, new Object[] { val }, new int[1]);
    }

    /**
     * cover for underlying call to invoke
     * @param dispatchTarget
     * @param dispid
     * @param val
     */
    public static void putRef(Dispatch dispatchTarget, int dispid, Object val) {
        invoke(dispatchTarget, dispid, Dispatch.PutRef, new Object[] { val }, new int[1]);
    }

    /**
     * not implemented , will throw class cast exception
     * @param dispatchTarget
     * @param name
     * @return Variant never returned
     */
    public static Variant get_CaseSensitive(Dispatch dispatchTarget, String name) {
        throw new ClassCastException("not implemented yet");
    }
    
}