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

import java.util.Date;

/**
 * The multi-format data type used for all call backs and most communications
 * between Java and COM. It provides a single class that can handle all data
 * types.
 * <p>
 * This object no longer implements Serializable because serialization is broken 
 * (and has been since 2000/xp).  The underlying
 * marshalling/unmarshalling code is broken in the JNI layer.
 */
public class Variant extends JacobObject {

	/**
     * Use this constant for optional parameters
     */
    public final static com.jacob.com.Variant DEFAULT;

    /**
     * Same than {@link #DEFAULT}
     */
    public final static com.jacob.com.Variant VT_MISSING;
    
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

    /*
     * do the run time definition of DEFAULT and MISSING
     */
    static {
        com.jacob.com.Variant vtMissing = new com.jacob.com.Variant();
        vtMissing.noParam();
        DEFAULT = vtMissing;
        VT_MISSING = vtMissing;
    }
    
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

    /** 
     * @return the value of this variant as an int 
     * (after possible conversion)
     */
    public native int toInt();

    /** 
     * @return the value of this variant as a date 
     * (after possible conversion)
     */
    public native double toDate();

    /**
     * Returns the windows time contained in this Variant to a Java Date
     * should return null if this is not a date Variant
     * SF 959382.
     * 
     * This method added 12/2005 for possible use by jacobgen instead of its conversion code
     * @return java.util.Date
     * (after possible conversion)
     */
    public Date toJavaDate(){
    	double windowsDate = toDate();
    	if (windowsDate == 0){
    		return null;
    	} else {
    		return DateUtilities.convertWindowsTimeToDate(getDate());
    	}
    }
    
    
    /** 
     * @return the value of this variant as boolean (after possible conversion) 
     */
    public native boolean toBoolean();

    /** @return the value of this variant as an enumeration (java style) */
    public native EnumVariant toEnumVariant();

    /**
     * This method would have returned null if the type was VT_NULL.
     * But because we return null if the data is not of the right type,
     *  this method should have always returned null
     *  @deprecated method never did anything
     */
    public void getNull() {};

    /** 
     * Set this Variant's type to VT_NULL (the VB equivalent of NULL) 
     * */
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
     * @param in doesn't matter because this method does nothing
     * @throws com.jacob.com.NotImplementedException
     */
    public void putVariantArray(Variant[] in) {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * @deprecated superceded by SafeArray
     * @return never returns anything
     * @throws com.jacob.com.NotImplementedException
     */
    public Variant[] getVariantArray() {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * Exists to support jacobgen.
     * This would be deprecated if it weren't for jacobgen
     * @return this same object
     */
    public Variant toVariant() { return this; }
    
    /**
     * @deprecated superceded by SafeArray
     * @param in doesn't matter because this method does nothing
     * @throws com.jacob.com.NotImplementedException
     */
    public void putByteArray(Object in) {
        throw new NotImplementedException("Not implemented");
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
     * converts a java date to a windows time and calls putDateRef(double)
     * SF 959382 
     * @throws IllegalArgumentException if inDate = null
     * @param inDate a Java date to be converted
     */
    public void putDateRef(Date inDate){
    	if (inDate == null){
    		throw new IllegalArgumentException("Cannot put null in as windows date");
    		// do nothing
    	} else {
    		putDateRef(DateUtilities.convertDateToWindowsTime(inDate));
    	}
    }
    
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
     * returns the windows time contained in this Variant to a Java Date
     * should return null if this is not a date reference Variant
     * SF 959382 
     * @return java.util.Date
     */
    public Date getJavaDateRef(){
    	double windowsDate = getDateRef();
    	if (windowsDate == 0){
    		return null;
    	} else {
    		return DateUtilities.convertWindowsTimeToDate(getDate());
    	}
    }
    
    /**
     * get the content of this variant as a string
     * @return String
     */
    public native String getStringRef();

    /**
     * @deprecated superceded by SafeArray
     * @return never returns anything
     * @throws com.jacob.com.NotImplementedException
     */
    public Object toCharArray() {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * Clear the content of this variant
     */
    public native void VariantClear();

    /**
     * @return the content of this variant as a Dispatch object
     */
    public Dispatch toDispatch(){ return toDispatchObject(); }
    
    /**
     * native method used by toDispatch()
     * @return
     */
    private native Dispatch toDispatchObject();

    /**
     * this returns null
     * @return ?? comment says null?
     */
    public native Object clone();

    /**
     * Attempts to return the content of this variant as a string
     * Will convert the underlying data type to a string(!)
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
     * returns the windows time contained in this Variant to a Java Date
     * should return null if this is not a date Variant
     * SF 959382 
     * @return java.util.Date
     */
    public Date getJavaDate(){
    	double windowsDate = getDate();
    	if (windowsDate == 0){
    		return null;
    	} else {
    		return DateUtilities.convertWindowsTimeToDate(getDate());
    	}
    }
    
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
     * converts a java date to a windows time and calls putDate(double)
     * SF 959382 
     * @throws IllegalArgumentException if inDate = null
     * @param inDate a Java date to be converted
     */
    public void putDate(Date inDate){
    	if (inDate == null){
    		throw new IllegalArgumentException("Cannot put null in as windows date");
    		// do nothing
    	} else {
    		putDate(DateUtilities.convertDateToWindowsTime(inDate));
    	}
    }
    
    /** 
     * attempts to return the content of this variant as a double 
     * (after possible conversion)
     * @return byte
     */
    public native byte toByte();

    /** 
     * same as {@link #toDispatch()}
     * This is different than the other get methods.
     * It calls toDispatch wich will do type conversion.
     * Most getXXX() methods will return null if the data is not of 
     * the requested type
     * @return this object as a dispatch (Why isn't this typed as type Dispatch?)
     */
    public Dispatch getDispatch() {
        return toDispatch();
    }

    /** 
     * This acts a cover for 
     * same as @link #putObject(Object)
     * 
     * Why isn't this typed as type Dispatch?
     * @param in
     */
    public void putDispatch(Dispatch in) {
        putDispatchObject(in);
    }

    /**
     * 
     * @return the value in this Variant as a boolean, null if not a boolean
     */
    public native boolean getBoolean();

    /**
     * 
     * @return the value in this Variant as a byte, null if not a byte
     */
    public native byte getByte();

    public native void putBoolean(boolean in);

    public native void putByte(byte in);

    public native int toError();

    /**
     * Acts a a cover for toDispatch.
     * This primarily exists to support jacobgen.
     * This should be deprecated.
     * @return Object returned by toDispatch()
     * @see Variant#toDispatch() instead
     */
    public Object toObject() {
        return toDispatch();
    }

    /**
     * Pointless method that was put here so that putEmpty() has a get method.
     * This would have returned null if the value was VT_EMPTY
     * or if it wasn't so it would have always returned the same value.
     * @deprecated method never did anything
     */
    public void getEmpty() {};

    /**
     * Sets the type to VariantEmpty.  No values needed 
     */
    public native void putEmpty();
    
    /**
     * Sets the type to VariantDispatch and sets teh value to null
     * Equivalent to VB's nothing
     */
    public native void putNothing();

    public native int getError();

    public native void putError(int in);

    public native double getDouble();

    public native void putCurrency(long in);

    /** 
     * Puts an object into the Variant -- converts to Dispatch.
     * Acts as a cover for putDispatchObject();
     * This primarily exists to support jacobgen.
     * This should be deprecated.
     * @see Variant#putDispatch(Dispatch)  
     * */
    public void putObject(Object in){ 
    	// this should verify in instanceof Dispatch
    	putDispatchObject(in); }
    
    /**
     * a cover for putDispatch() but is named differently so that 
     * we can screen the incoming dispatches in putDispatch() before this
     * is invoked
     * @param in
     */
    private native void putDispatchObject(Object in);

    public native void putDouble(double in);

    /**
     * 
     * @return the value in this Variant as a long, null if not a long
     */
    public native long getCurrency();

    public native void putFloatRef(float in);

    public native void putCurrencyRef(long in);

    public native void putErrorRef(int in);

    public native void putBooleanRef(boolean in);

    /**
     * Just a cover for putObject().
     * We shouldn't accept any old random object.
     * This has been left in to support jacobgen.
     * This should be deprecated.
     * @param in
     */
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

    public native byte getByteRef();

    /**
     * attempts to return the contents of this variant as a float
     * (after possible conversion)
     * @return float
     */
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

    /**
     * sets the type to VT_ERROR and the error message to DISP_E_PARAMNOTFOIUND
     * */
    public native void noParam();

    /**
     * @deprecated superceded by SafeArray
     * @throws com.jacob.com.NotImplementedException
     */ 
    public void putCharArray(Object in) {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * 
     * @return returns the value as a float if the type is of type float
     */
    public native float getFloat();

    /**
     * fills the Variant with a float and sets the type to float
     * @param in
     */
    public native void putFloat(float in);

    /**
     * Dispatch and dispatchRef are treated the same
     * This is a cover for putDispatch().  
     * Dispatch and dispatchRef are treated the same
     * @param in
     */
    public void putDispatchRef(Dispatch in) {
        putDispatchObject(in);
    }

    /**
     * Dispatch and dispatchRef are treated the same
     * This is just a cover for getDispatch()
     * @return the results of getDispatch()
     */
    public Dispatch getDispatchRef() {
        return getDispatch();
    }

    /**
     * @deprecated superceded by SafeArray
     * @throws com.jacob.com.NotImplementedException
     */ 
    public void putVariantArrayRef(Variant[] in) {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * @deprecated superceded by SafeArray
     * @throws com.jacob.com.NotImplementedException
     */ 
    public Variant[] getVariantArrayRef() {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * Convertes variant to the passed in type by converting the underlying 
     * windows variant structure
     * @param in the desired resulting type
     */
    public native void changeType(short in);

    /**
     * cover for changeType(short)
     * @param in type to convert this variant too
     */
    public void changeType(int in) {
        changeType((short) in);
    }

    /**
     * I don't know what this is.  Is it some legacy (pre 1.8) thing?
     * @deprecated
     * @return this object as a dispatch object by calling toDispatch()
     */
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
            debug("Variant: " +	"create " + this );
        }
    }

    /**
     * Constructor that accepts a primitive rather than an object
     * @param in
     */
    public Variant(int in) {
        init();
        putInt(in);
    }

    /**
     * Constructor that accepts a primitive rather than an object
     * @param in
     */
    public Variant(double in) {
        init();
        putDouble(in);
    }

    /**
     * Constructor that accepts a primitive rather than an object
     * @param in
     */
    public Variant(boolean in) {
        init();
        putBoolean(in);
    }

    /** 
     * Convenience constructor that calls the main one with
     * a byRef value of false
     * @param in object to be made into variant
     */
    public Variant(Object in) {
        this(in, false);
    }

    /**
     * constructor that accepts the data object and informaton about
     * whether this is by reference or not.
     * @param pValueObject a null object sets this to "empty"
     * @param fByRef
     */
    public Variant(Object pValueObject, boolean fByRef) {
        init();
        if (pValueObject == null) {
            putEmpty();
        } else if (pValueObject instanceof Integer) {
            if (fByRef)
                putIntRef(((Integer) pValueObject).intValue());
            else
                putInt(((Integer) pValueObject).intValue());
        } else if (pValueObject instanceof Short) {
            if (fByRef)
                putShortRef(((Short) pValueObject).shortValue());
            else
                putShort(((Short) pValueObject).shortValue());
        } else if (pValueObject instanceof String) {
            if (fByRef)
                putStringRef((String) pValueObject);
            else
                putString((String) pValueObject);
        } else if (pValueObject instanceof Boolean) {
            if (fByRef)
                putBooleanRef(((Boolean) pValueObject).booleanValue());
            else
                putBoolean(((Boolean) pValueObject).booleanValue());
        } else if (pValueObject instanceof Double) {
            if (fByRef)
                putDoubleRef(((Double) pValueObject).doubleValue());
            else
                putDouble(((Double) pValueObject).doubleValue());
        } else if (pValueObject instanceof Float) {
            if (fByRef)
                putFloatRef(((Float) pValueObject).floatValue());
            else
                putFloat(((Float) pValueObject).floatValue());
        }  else if (pValueObject instanceof Byte){
        	if (fByRef){
        		putByteRef(((Byte)pValueObject).byteValue());
        	} else {
        		putByte(((Byte)pValueObject).byteValue());
        	}
        } else if (pValueObject instanceof Date){
        	if (fByRef){
        		putDateRef((Date) pValueObject);
        	} else {
        		putDate((Date)pValueObject);
        	}
        } else if (pValueObject instanceof SafeArray) {
            if (fByRef)
                putSafeArrayRef((SafeArray) pValueObject);
            else
                putSafeArray((SafeArray) pValueObject);
        } else if (pValueObject instanceof Dispatch){
        	if (fByRef)
        		putDispatchRef((Dispatch)pValueObject);
        	else
        		putDispatch((Dispatch)pValueObject);
        } else {
        	// should really throw an illegal argument exception if its an invalid type
            if (fByRef)
                putObjectRef(pValueObject);
            else
                putObject(pValueObject);
        }
    }


    public native short getvt();

    /**
     * attempts to return the contents of this Variant as a short
     * (after possible conversion)
     * @return short
     */
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
    		safeRelease();
    }

    /** 
     * returns true if the passed in Variant is a constant that should not be freed
     * @param pVariant
     * @return
     */
    protected boolean objectIsAConstant(Variant pVariant){
    	if (pVariant == VT_FALSE ||
    			pVariant == VT_TRUE ||
    			pVariant == VT_MISSING ||
    			pVariant == DEFAULT){
    		return true;
    	} else {
    		return false;
    	}
    		
    }
    
    /**
     * This will release the "C" memory for the Variant 
     * unless this Variant is one of the constants in which case
     * we don't want to release the memory.
     * <p>
     * @see com.jacob.com.JacobObject#safeRelease()
     */
    public void safeRelease() {
        // The well known constants should not be released.
        // Unfortunately this doesn't fix any other classes that are
        // keeping constants around in their static ivars.
        // those will still be busted.
    	//
		// The only inconcistency here is that we leak
		// when this class is unloaded because we won't
		// free the memory even if the constants are being
		// finalized.  this is not a big deal at all.
		// another way around this would be to create the constants
		// in their own thread so that they would never be released
    	if (!objectIsAConstant(this)){
	        super.safeRelease();
	        if (m_pVariant != 0) {
	            release();
	            m_pVariant = 0;
	        } else {
	            // looks like a double release
	            // this should almost always happen due to gc
	            // after someone has called ComThread.Release
	            if (isDebugEnabled()) {
	                debug("Variant: " + this.hashCode()
	                        + " double release");
	                //Throwable x = new Throwable();
	                //x.printStackTrace();
	            }
	        }
    	} else {
            if (isDebugEnabled()) {
                debug("Variant: " + this.hashCode()
                        + " don't want to release a constant");
            }
    	}
    }

    /**
     * @deprecated superceded by SafeArray
     * @return nothing because this method is not implemented
     * @throws com.jacob.com.NotImplementedException
     */
    public Variant[] toVariantArray() {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * @deprecated superceded by SafeArray
     * @return nothing because this method is not implemented
     * @throws com.jacob.com.NotImplementedException
     */
    public Object toByteArray() {
        throw new NotImplementedException("Not implemented");
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
        	byte[] ourBytes = SerializationWriteToBytes();
        	int count = ourBytes.length;
        	if (JacobObject.isDebugEnabled()){
        		JacobObject.debug("writing out "+count+" bytes");
        	}
        	oos.writeInt(count);
        	oos.write(ourBytes);
            //Save(oos);
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
        	// Load will do this if we don't but lets do it
        	// from here so that the variant is set up exactly
        	// the same as the ones not created from a stream
        	init();
        	int numBytes = ois.readInt();
        	byte[] ourBytes = new byte[numBytes];
        	if (JacobObject.isDebugEnabled()){
        		JacobObject.debug("reading in "+numBytes+" bytes");
        	}
        	ois.read(ourBytes);
        	SerializationReadFromBytes(ourBytes);
            //Load(ois);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  is the variant null or empty or error or null dispatch
     * @return true if it is null or false if not
     */ 
    public native boolean isNull();
    
    /**
     * this is supposed to create a byte array that represents the underlying
     * variant object struct
     */
    public native byte[] SerializationWriteToBytes();
    
    /**
     * this is supposed to cause the underlying variant object struct to 
     * be rebuilt from a previously serialized byte array.
     * @param ba
     */
    public native void SerializationReadFromBytes(byte[] ba);
    
    /*=====================================================================
     * 
     * 
     *=====================================================================*/
    /**
	 * Convert a JACOB Variant value to a Java object (type conversions).
     * provided in Sourceforge feature request 959381
	 *
	 * @return Corresponding Java type object.
	 * @throws Exception if conversion failed.
	 */
	protected Object toJavaObject() throws JacobException {
	    Object result = null;
	
	    short type = this.getvt(); //variant type
	
	    if (type >= Variant.VariantArray) { //array returned?
		    SafeArray array = null;
		    type = (short) (type - Variant.VariantArray);
		    array = this.toSafeArray(false);
		    //result = toJava(array);
		    result = array;
	    } else { //non-array object returned
		    switch (type) {
		    case Variant.VariantEmpty : //0
		    case Variant.VariantNull : //1
		    break;
		    case Variant.VariantShort : //2
		    result = new Short(this.getShort());
		    break;
		    case Variant.VariantInt : //3
		    result = new Integer(this.getInt());
		    break;
		    case Variant.VariantFloat : //4
		    result = new Float(this.getFloat());
		    break;
		    case Variant.VariantDouble : //5
		    result = new Double(this.getDouble());
		    break;
		    case Variant.VariantCurrency : //6
		    result = new Long(this.getCurrency());
		    break;
		    case Variant.VariantDate : //7
		    result = this.getJavaDate();
		    break;
		    case Variant.VariantString : //8
		    result = this.getString();
		    break;
		    case Variant.VariantDispatch : //9
		    result = this.getDispatchRef();
		    break;
		    case Variant.VariantError : //10
		    result = new NotImplementedException("Not implemented: VariantError");
		    break;
		    case Variant.VariantBoolean : //11
		    result = new Boolean(this.getBoolean());
		    break;
		    case Variant.VariantVariant : //12
		    result = new NotImplementedException("Not implemented: VariantVariant");
		    break;
		    case Variant.VariantObject : //13
		    result = new NotImplementedException("Not implemented: VariantObject");
		    break;
		    case Variant.VariantByte : //17
		    result = new Byte(this.getByte());
		    //result = new NotImplementedException("Not implemented: VariantByte");
		    break;
		    case Variant.VariantTypeMask : //4095
		    result = new NotImplementedException("Not implemented: VariantTypeMask");
		    break;
		    case Variant.VariantArray : //8192
		    result = new NotImplementedException("Not implemented: VariantArray");
		    break;
		    case Variant.VariantByref : //16384
		    result = new NotImplementedException("Not implemented: VariantByref");
		    break;
		    default :
		    result = new NotImplementedException("Unknown return type: " + type);
		    result = this;
		    break;
	    }//switch (type)
	
	    if (result instanceof JacobException) {
	    	throw (JacobException) result;
	    }
	    }
	
	    return result;
    }//toJava()    
}