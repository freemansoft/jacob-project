package com.jacob.com;

import java.lang.reflect.Array;

/**
 * This class holds all the native calls that formerly existed in 
 * the Dispatch class.  They were moved here to split the very Java
 * OO calls from the more procedural JNI related calls
 * <p>
 * Callers should really use the instance based call in Dispatch
 */
public class DispatchNative extends JacobObject {

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
            v.putObject((DispatchNative) o);
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
