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
 * The multi-format data type used for all call backs and most communications
 * between Java and COM. It provides a single class that can handle all data
 * types
 */
public class Variant extends JacobObject implements java.io.Serializable {
    /**
     * Use this constant for optional parameters
     */
    public final static com.jacob.com.Variant DEFAULT;

    /**
     * Same than {@link #DEFAULT}
     */
    public final static com.jacob.com.Variant VT_MISSING;
    static {
        com.jacob.com.Variant vtMissing = new com.jacob.com.Variant();
        vtMissing.noParam();
        DEFAULT = vtMissing;
        VT_MISSING = vtMissing;
    }

    /**
     * Use for true/false variant parameters
     */
    public final static com.jacob.com.Variant VT_TRUE = new com.jacob.com.Variant(
            true);

    /**
     * Use for true/false variant parameters
     */
    public final static com.jacob.com.Variant VT_FALSE = new com.jacob.com.Variant(
            false);

    /**
     * Pointer to MS struct.
     */
    int m_pVariant = 0;

    /** variant's type is empty : equivalent to VB Nothing */
    public static final short VariantEmpty = 0;

    /** variant's type is null : equivalent to VB Null */
    public static final short VariantNull = 1;

    /** variant's type is short */
    public static final short VariantShort = 2;

    /** variant's type is int */
    public static final short VariantInt = 3;

    /** variant's type is float */
    public static final short VariantFloat = 4;

    /** variant's type is double */
    public static final short VariantDouble = 5;

    /** variant's type is currency */
    public static final short VariantCurrency = 6;

    /** variant's type is date */
    public static final short VariantDate = 7;

    /** variant's type is string */
    public static final short VariantString = 8;

    /** variant's type is dispatch */
    public static final short VariantDispatch = 9;

    /** variant's type is error */
    public static final short VariantError = 10;

    /** variant's type is boolean */
    public static final short VariantBoolean = 11;

    /** variant's type is variant it encapsulate another variant */
    public static final short VariantVariant = 12;

    /** variant's type is object */
    public static final short VariantObject = 13;

    /** variant's type is byte */
    public static final short VariantByte = 17;

    /** @todo */
    public static final short VariantTypeMask = 4095;

    /** variant's type is array */
    public static final short VariantArray = 8192;

    /** variant's type is a reference (to IDispatch?) */
    public static final short VariantByref = 16384;

    /** @return the value of this variant as an int */
    public native int toInt();

    /** @return the value of this variant as a date */
    public native double toDate();

    /** @return the value of this variant as boolean */
    public native boolean toBoolean();

    /** @return the value of this variant as an enumeration (java style) */
    public native EnumVariant toEnumVariant();

    /** As far as I can tell : this does absolutely nothing */
    public native void getNull();

    /** Set this Variant's type to VT_NULL (the VB equivalent of NULL) */
    public native void putNull();

    /**
     * @deprecated No longer used
     * @return null !
     */
    public native Variant cloneIndirect();

    /** @return the content of this variant as a double */
    public native double toDouble();

    /**
     * @return the content of this variant as a long reprensenting a monetary
     *         amount
     */
    public native long toCurrency();

    /**
     * @deprecated superceded by SafeArray
     * @param in
     *            doesn't matter because this method does nothing
     * @throws com.jacob.com.ComFailException
     */
    public void putVariantArray(Variant[] in) {
        throw new ComFailException("Not implemented");
    }

    /**
     * @deprecated superceded by SafeArray
     * @return never returns
     * @throws com.jacob.com.ComFailException
     */
    public Variant[] getVariantArray() {
        throw new ComFailException("Not implemented");
    }

    /**
     * @deprecated superceded by SafeArray
     * @param in
     *            doesn't matter because this method does nothing
     * @throws com.jacob.com.ComFailException
     */
    public void putByteArray(Object in) {
        throw new ComFailException("Not implemented");
    }

    /**
     * set the content of this variant to a short (VT_I2|VT_BYREF)
     * @param in
     */
    public native void putShortRef(short in);

    /**
     * set the content of this variant to an int (VT_I4|VT_BYREF)
     * @param in
     */
    public native void putIntRef(int in);

    /**
     * set the content of this variant to a double (VT_R8|VT_BYREF)
     * @param in
     */
    public native void putDoubleRef(double in);

    /**
     * set the content of this variant to a date (VT_DATE|VT_BYREF)
     * @param in
     */
    public native void putDateRef(double in);

    /**
     * set the content of this variant to a string (VT_BSTR|VT_BYREF)
     * @param in
     */
    public native void putStringRef(String in);

    /**
     * get the content of this variant as a short
     * @return short
     */
    public native short getShortRef();

    /**
     * get the content of this variant as an int
     * @return int
     */
    public native int getIntRef();

    /**
     * set the content of this variant to a short (VT_I2)
     * @param in
     */
    public native void putShort(short in);

    /**
     * get the content of this variant as a short
     * @return short
     */
    public native short getShort();

    /**
     * get the content of this variant as a double
     * @return double
     */
    public native double getDoubleRef();

    /**
     * get the content of this variant as a double representing a date
     * @return double
     */
    public native double getDateRef();

    /**
     * get the content of this variant as a string
     * @return String
     */
    public native String getStringRef();

    /**
     * @return never returns anything
     * @throws com.jacob.com.ComFailException
     * @deprecated superceded by SafeArray
     */
    public Object toCharArray() {
        throw new ComFailException("Not implemented");
    }

    /**
     * Clear the content of this variant
     */
    public native void VariantClear();

    /**
     * @return the content of this variant as a Dispatch object
     */
    public native Dispatch toDispatch();

    /**
     * this returns null
     * @return ?? comment says null?
     */
    public native Object clone();

    /**
     * attempts to return the content of this variant as a string
     * @return String
     */
    public native String toString();

    /**
     * return the int value held in this variant (fails on other types?)
     * @return int
     */
    public native int getInt();

    /**
     * return the date (as a double) value held in this variant (fails on other
     * types?)
     * @return double
     */
    public native double getDate();

    /** 
     * set the value of this variant 
     * @param in
     */
    public native void putInt(int in);

    /** 
     * set the value of this variant 
     * @param in
     */
    public native void putDate(double in);

    /** 
     * attempts to return the content of this variant as a double 
     * @return byte
     */
    public native byte toByte();

    /** 
     * same as {@link #toDispatch()} 
     * @return this object as a dispatch
     */
    public Object getDispatch() {
        return toDispatch();
    }

    /** 
     * same as {@link #putObject(Object)} 
     * @param in
     */
    public void putDispatch(Object in) {
        putObject(in);
    }

    public native boolean getBoolean();

    public native byte getByte();

    public native void putBoolean(boolean in);

    public native void putByte(byte in);

    public native int toError();

    public Object toObject() {
        return toDispatch();
    }

    public native void getEmpty();

    public native void putEmpty();

    public native int getError();

    public native void putError(int in);

    public native double getDouble();

    public Object getObject() {
        return toDispatch();
    }

    public native void putCurrency(long in);

    /** puts an object into the */
    public native void putObject(Object in);

    public native void putDouble(double in);

    public native long getCurrency();

    public native void putFloatRef(float in);

    public native void putCurrencyRef(long in);

    public native void putErrorRef(int in);

    public native void putBooleanRef(boolean in);

    public void putObjectRef(Object in) {
        putObject(in);
    }

    public native void putByteRef(byte in);

    public native String getString();

    public native void putString(String in);

    public native float getFloatRef();

    public native long getCurrencyRef();

    public native int getErrorRef();

    public native boolean getBooleanRef();

    public native Object getObjectRef();

    public native byte getByteRef();

    public native float toFloat();

    /**
     * By default toSafeArray makes a deep copy due to the fact that this
     * Variant owns the embedded SafeArray and will destroy it when it gc's
     */
    public SafeArray toSafeArray() {
        return toSafeArray(true);
    }

    public native SafeArray toSafeArray(boolean deepCopy);

    public native void putSafeArrayRef(SafeArray in);

    public native void putSafeArray(SafeArray in);

    public native void noParam();

    /**
     * superceded by SafeArray
     * @throws com.jacob.com.ComFailException
     */ 
    public void putCharArray(Object in) {
        throw new ComFailException("Not implemented");
    }

    public native float getFloat();

    public native void putFloat(float in);

    public void putDispatchRef(Object in) {
        putDispatch(in);
    }

    public Object getDispatchRef() {
        return getDispatch();
    }

    /**
     * superceded by SafeArray
     * @throws com.jacob.com.ComFailException
     */ 
    public void putVariantArrayRef(Variant[] in) {
        throw new ClassCastException("Not implemented");
    }

    /**
     * superceded by SafeArray
     * @throws com.jacob.com.ComFailException
     */ 
    public Variant[] getVariantArrayRef() {
        throw new ClassCastException("Not implemented");
    }

    public native void changeType(short in);

    public void changeType(int in) {
        changeType((short) in);
    }

    public Object toScriptObject() {
        return toDispatch();
    }

    /**
     * public constructor
     */
    public Variant() {
        init();
        putEmpty();
        if (isDebugEnabled()) {
            debug("create " + getClass().getName() + " in thread "
                    + Thread.currentThread().getName());
        }
    }

    /**
     * constructor that calls init() and then putXXX()
     * @param in
     */
    public Variant(int in) {
        init();
        putInt(in);
    }

    /**
     * constructor that calls init() and then putXXX()
     * @param in
     */
    public Variant(double in) {
        init();
        putDouble(in);
    }

    /**
     * constructor that calls init() and then putXXX()
     * @param in
     */
    public Variant(boolean in) {
        init();
        putBoolean(in);
    }

    /**
     * constructor that calls init() and then putXXX()
     * @param in
     */
    public Variant(String in) {
        init();
        putString(in);
    }

    /**
     * constructor that calls init() and then putSafeArrayXXX()
     * @param in
     * @param fByRef is this data by reference or not?
     */
    public Variant(SafeArray in, boolean fByRef) {
        init();
        if (fByRef) {
            putSafeArrayRef(in);
        } else {
            putSafeArray(in);
        }
    }

    /** 
     * constructor that calls two parameter constructor
     * with 1st parameter as object and 2nd parameter as false
     * @param in
     */
    public Variant(Object in) {
        this(in, false);
    }

    /**
     * constructor that accepts the data object and informaton about
     * whether this is by reference or not
     * @param o
     * @param fByRef
     */
    public Variant(Object o, boolean fByRef) {
        init();
        if (o == null) {
            putEmpty();
        } else if (o instanceof Integer) {
            if (fByRef)
                putIntRef(((Integer) o).intValue());
            else
                putInt(((Integer) o).intValue());
        } else if (o instanceof String) {
            if (fByRef)
                putStringRef((String) o);
            else
                putString((String) o);
        } else if (o instanceof Boolean) {
            if (fByRef)
                putBooleanRef(((Boolean) o).booleanValue());
            else
                putBoolean(((Boolean) o).booleanValue());
        } else if (o instanceof Double) {
            if (fByRef)
                putDoubleRef(((Double) o).doubleValue());
            else
                putDouble(((Double) o).doubleValue());
        } else if (o instanceof Float) {
            if (fByRef)
                putFloatRef(((Float) o).floatValue());
            else
                putFloat(((Float) o).floatValue());
        } else if (o instanceof SafeArray) {
            if (fByRef)
                putSafeArrayRef((SafeArray) o);
            else
                putSafeArray((SafeArray) o);
        } else {
            if (fByRef)
                putObjectRef(o);
            else
                putObject(o);
        }
    }

    /**
     * wierd constructor that is no longer supported
     * @param in
     * @param in1
     * @throws com.jacob.com.ComFailException
     */ 
    public Variant(int in, int in1) {
        throw new ComFailException("Not implemented");
    }

    /**
     * wierd constructor that is no longer supported
     * @param in
     * @param in1
     * @throws com.jacob.com.ComFailException
     */ 
    public Variant(int in, boolean in1) {
        throw new ComFailException("Not implemented");
    }

    /**
     * wierd constructor that is no longer supported
     * @param in
     * @param in1
     * @throws com.jacob.com.ComFailException
     */ 
    public Variant(int in, double in1) {
        throw new ComFailException("Not implemented");
    }

    /**
     * wierd constructor that is no longer supported
     * @param in
     * @param in1
     * @throws com.jacob.com.ComFailException
     */ 
    public Variant(int in, Object in1) {
        throw new ComFailException("Not implemented");
    }

    public native short getvt();

    public native short toShort();

    /**
     * now private so only this object can asccess was: call this to explicitly
     * release the com object before gc
     *  
     */
    private native void release();

    protected native void init();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    protected void finalize() {
        super.safeRelease();
        safeRelease();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jacob.com.JacobObject#safeRelease()
     */
    public void safeRelease() {
        super.safeRelease();
        if (m_pVariant != 0) {
            release();
            m_pVariant = 0;
        } else {
            // looks like a double release
            // this should almost always happen due to gc
            // after someone has called ComThread.Release
            if (isDebugEnabled()) {
                debug(this.getClass().getName() + ":" + this.hashCode()
                        + " double release");
                //Throwable x = new Throwable();
                //x.printStackTrace();
            }
        }
    }

    // superceded by SafeArray
    public Variant[] toVariantArray() {
        throw new ClassCastException("Not implemented");
    }

    // superceded by SafeArray
    public Object toByteArray() {
        throw new ClassCastException("Not implemented");
    }

    static {
        System.loadLibrary("jacob");
    }

    /**
     * custom serialization support
     * @param oos
     */
    private void writeObject(java.io.ObjectOutputStream oos) {
        try {
            Save(oos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * custom serialization support
     * @param ois
     */
    private void readObject(java.io.ObjectInputStream ois) {
        try {
            Load(ois);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  is the variant null or empty or error or null disp
     * @return true if it is null or false if not
     */ 
    public native boolean isNull();

    public native void Save(java.io.OutputStream os) throws java.io.IOException;

    public native void Load(java.io.InputStream is) throws java.io.IOException;

}