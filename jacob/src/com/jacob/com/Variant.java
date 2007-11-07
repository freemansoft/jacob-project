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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * The multi-format data type used for all call backs and most communications
 * between Java and COM. It provides a single class that can handle all data
 * types.
 * <p>
 * Just loading this class creates 3 variants that get added to the ROT
 * <p>
 * PROPVARIANT introduces new types so eventually Variant will need to be
 * upgraded to support PropVariant types.
 * http://blogs.msdn.com/benkaras/archive/2006/09/13/749962.aspx
 * <p>
 * This object no longer implements Serializable because serialization is broken
 * (and has been since 2000/xp). The underlying marshalling/unmarshalling code
 * is broken in the JNI layer.
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
	 * Do the run time definition of DEFAULT and MISSING. Have to use static
	 * block because of the way the initialization is done via two calls instead
	 * of just a constructor for this type.
	 */
	static {
		com.jacob.com.Variant vtMissing = new com.jacob.com.Variant();
		vtMissing.putVariantNoParam();
		DEFAULT = vtMissing;
		VT_MISSING = vtMissing;
	}

	/**
	 * Pointer to MS struct.
	 */
	int m_pVariant = 0;

	/** variant's type is empty : equivalent to VB Nothing and VT_EMPTY */
	public static final short VariantEmpty = 0;

	/** variant's type is null : equivalent to VB Null and VT_NULL */
	public static final short VariantNull = 1;

	/** variant's type is short VT_I2 */
	public static final short VariantShort = 2;

	/** variant's type is int VT_I4, a Long in VC */
	public static final short VariantInt = 3;

	/** variant's type is float VT_R4 */
	public static final short VariantFloat = 4;

	/** variant's type is double VT_R8 */
	public static final short VariantDouble = 5;

	/** variant's type is currency VT_CY */
	public static final short VariantCurrency = 6;

	/** variant's type is date VT_DATE */
	public static final short VariantDate = 7;

	/** variant's type is string also known as VT_BSTR */
	public static final short VariantString = 8;

	/** variant's type is dispatch VT_DISPATCH */
	public static final short VariantDispatch = 9;

	/** variant's type is error VT_ERROR */
	public static final short VariantError = 10;

	/** variant's type is boolean VT_BOOL */
	public static final short VariantBoolean = 11;

	/** variant's type is variant it encapsulate another variant VT_VARIANT */
	public static final short VariantVariant = 12;

	/** variant's type is object VT_UNKNOWN */
	public static final short VariantObject = 13;

	/** variant's type is object VT_DECIMAL */
	public static final short VariantDecimal = 14;

	/** variant's type is byte VT_UI1 */
	public static final short VariantByte = 17;

	/**
	 * variant's type is 64 bit long integer VT_I8 - not yet implemented in
	 * Jacob because we have to decide what to do with Currency and because its
	 * only supported on XP and later. No win2k, NT or 2003 server.
	 */
	public static final short VariantLongInt = 20;

	/** what is this? */
	public static final short VariantTypeMask = 4095;

	/** variant's type is array */
	public static final short VariantArray = 8192;

	/** variant's type is a reference (to IDispatch?) */
	public static final short VariantByref = 16384;

	/**
	 * @deprecated should use changeType() followed by getInt()
	 * @return the value of this variant as an int (after possible conversion)
	 */
	@Deprecated
	public int toInt() {
		changeType(VariantInt);
		return getInt();
	}

	/**
	 * @deprecated should use changeType() followed by getDate()
	 * @return the value of this variant as a date (after possible conversion)
	 */
	@Deprecated
	public double toDate() {
		changeType(VariantDate);
		return getDate();
	}

	/**
	 * Returns the windows time contained in this Variant as a Java Date
	 * converts to a date like many of the other toXXX() methods SF 959382.
	 * <p>
	 * This method added 12/2005 for possible use by jacobgen instead of its
	 * conversion code
	 * <p>
	 * This does not convert the data
	 * 
	 * @deprecated callers should use getDate()
	 * @return java.util.Date version of this variant if it is a date, otherwise
	 *         null
	 * 
	 */
	@Deprecated
	public Date toJavaDate() {
		changeType(Variant.VariantDate);
		return getJavaDate();
	}

	/**
	 * @deprecated should be replaced by changeType() followed by getBoolean()
	 * @return the value of this variant as boolean (after possible conversion)
	 */
	@Deprecated
	public boolean toBoolean() {
		changeType(Variant.VariantBoolean);
		return getBoolean();
	}

	/** @return the value of this variant as an enumeration (java style) */
	public native EnumVariant toEnumVariant();

	/**
	 * This method would have returned null if the type was VT_NULL. But because
	 * we return null if the data is not of the right type, this method should
	 * have always returned null
	 * 
	 * @deprecated method never did anything
	 */
	@Deprecated
	public void getNull() {
	};

	/**
	 * Set this Variant's type to VT_NULL (the VB equivalent of NULL)
	 */
	private native void putVariantNull();

	/**
	 * Set this Variant's type to VT_NULL (the VB equivalent of NULL)
	 */
	public void putNull() {
		// verify we aren't released yet
		getvt();
		putVariantNull();
	}

	/**
	 * @deprecated No longer used
	 * @return null !
	 */
	@Deprecated
	public native Variant cloneIndirect();

	/**
	 * @deprecated should call changeType() then getDouble()
	 * @return the content of this variant as a double (after possible
	 *         conversion)
	 */
	@Deprecated
	public double toDouble() {
		changeType(Variant.VariantDouble);
		return getDouble();
	}

	/**
	 * @deprecated should be replaced by changeType() followed by getCurrency
	 * @return the content of this variant as a long reprensenting a monetary
	 *         amount
	 */
	@Deprecated
	public long toCurrency() {
		changeType(Variant.VariantCurrency);
		return getCurrency();
	}

	/**
	 * @deprecated superseded by SafeArray
	 * @param in
	 *            doesn't matter because this method does nothing
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public void putVariantArray(Variant[] in) {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * @deprecated superseded by SafeArray
	 * @return never returns anything
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public Variant[] getVariantArray() {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * Exists to support jacobgen. This would be deprecated if it weren't for
	 * jacobgen
	 * 
	 * @deprecated superseded by "this"
	 * @return this same object
	 */
	@Deprecated
	public Variant toVariant() {
		return this;
	}

	/**
	 * @deprecated superseded by SafeArray
	 * @param in
	 *            doesn't matter because this method does nothing
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public void putByteArray(Object in) {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * set the content of this variant to a short (VT_I2|VT_BYREF)
	 * 
	 * @param in
	 */
	private native void putVariantShortRef(short in);

	/**
	 * set the content of this variant to a short (VT_I2|VT_BYREF)
	 * 
	 * @param in
	 */
	public void putShortRef(short in) {
		// verify we aren't released
		getvt();
		putVariantShortRef(in);
	}

	/**
	 * set the content of this variant to an int (VT_I4|VT_BYREF)
	 * 
	 * @param in
	 */
	private native void putVariantIntRef(int in);

	/**
	 * set the content of this variant to an int (VT_I4|VT_BYREF)
	 * 
	 * @param in
	 */
	public void putIntRef(int in) {
		// verify we aren't released
		getvt();
		putVariantIntRef(in);
	}

	/**
	 * private JNI method called by putDecimalRef
	 * 
	 * @param signum
	 *            sign
	 * @param scale
	 *            BigDecimal's scale
	 * @param lo
	 *            low 32 bits
	 * @param mid
	 *            middle 32 bits
	 * @param hi
	 *            high 32 bits
	 */
	private native void putVariantDecRef(int signum, byte scale, int lo,
			int mid, int hi);

	/**
	 * Set the content of this variant to an decimal (VT_DECIMAL|VT_BYREF) This
	 * may throw exceptions more often than the caller expects because most
	 * callers don't manage the scale of their BigDecimal objects.
	 * 
	 * @param in
	 *            the BigDecimal that will be converted to VT_DECIMAL
	 * @throws IllegalArgumentException
	 *             if the scale is > 28, the maximum for VT_DECIMAL
	 */
	public void putDecimalRef(BigDecimal in) {
		// verify we aren't released
		getvt();
		if (in.scale() > 28) {
			// should this really cast to a string and call putStringRef()?
			throw new IllegalArgumentException(
					"VT_DECIMAL only supports a scale of 28 and the passed"
							+ " in value has a scale of " + in.scale());
		} else {
			int sign = in.signum();
			// MS decimals always have positive values with just the sign
			// flipped
			if (in.signum() < 0) {
				in = in.negate();
			}
			byte scale = (byte) in.scale();
			BigInteger unscaled = in.unscaledValue();
			BigInteger shifted = unscaled.shiftRight(32);
			putVariantDecRef(sign, scale, unscaled.intValue(), shifted
					.intValue(), shifted.shiftRight(32).intValue());
		}
	}

	/**
	 * set the content of this variant to a double (VT_R8|VT_BYREF)
	 * 
	 * @param in
	 */
	private native void putVariantDoubleRef(double in);

	/**
	 * set the content of this variant to a double (VT_R8|VT_BYREF)
	 * 
	 * @param in
	 */
	public void putDoubleRef(double in) {
		// verify we aren't released
		getvt();
		putVariantDoubleRef(in);
	}

	/**
	 * set the content of this variant to a date (VT_DATE|VT_BYREF)
	 * 
	 * @param in
	 */
	private native void putVariantDateRef(double in);

	/**
	 * set the content of this variant to a date (VT_DATE|VT_BYREF)
	 * 
	 * @param in
	 */
	public void putDateRef(double in) {
		// verify we aren't released
		getvt();
		putVariantDateRef(in);
	}

	/**
	 * converts a java date to a windows time and calls putDateRef(double) SF
	 * 959382
	 * 
	 * @param inDate
	 *            a Java date to be converted
	 * @throws IllegalArgumentException
	 *             if inDate = null
	 */
	public void putDateRef(Date inDate) {
		if (inDate == null) {
			throw new IllegalArgumentException(
					"Cannot put null in as windows date");
			// do nothing
		} else {
			putDateRef(DateUtilities.convertDateToWindowsTime(inDate));
		}
	}

	/**
	 * set the content of this variant to a string (VT_BSTR|VT_BYREF)
	 * 
	 * @param in
	 */
	private native void putVariantStringRef(String in);

	/**
	 * set the content of this variant to a string (VT_BSTR|VT_BYREF)
	 * 
	 * @param in
	 */
	public void putStringRef(String in) {
		// verify we aren't released
		getvt();
		putVariantStringRef(in);
	}

	/**
	 * Puts a variant into this variant making it type VT_VARIANT. Added 1.12
	 * pre 6
	 * 
	 * @param objectToBeWrapped
	 *            A object that is to be referenced by this variant. If
	 *            objectToBeWrapped is already of type Variant, then it is used.
	 *            If objectToBeWrapped is not Variant then
	 *            <code>new Variant(objectToBeWrapped)</code> is called and
	 *            the result is passed into the com layer
	 * @throws IllegalArgumentException
	 *             if inVariant = null or if inVariant is a Varint
	 */
	public void putVariant(Object objectToBeWrapped) {
		if (objectToBeWrapped == null) {
			throw new IllegalArgumentException(
					"Cannot put null in as a variant");
		} else if (objectToBeWrapped instanceof Variant) {
			throw new IllegalArgumentException(
					"Cannot putVariant() only accepts non jacob objects.");
		} else {
			Variant inVariant = new Variant(objectToBeWrapped);
			putVariantVariant(inVariant);
			// This could be done in Variant.cpp
			if (JacobObject.isDebugEnabled()) {
				JacobObject
						.debug("Zeroing out enclosed Variant's ref to windows memory");
			}
			inVariant.m_pVariant = 0;
		}
	}

	/**
	 * All VariantVariant type variants are BYREF.
	 * 
	 * Set the content of this variant to a string (VT_VARIANT|VT_BYREF).
	 * 
	 * Added 1.12 pre 6 - VT_VARIANT support is at an alpha level
	 * 
	 * @param in
	 *            variant to be wrapped
	 * 
	 */
	private native void putVariantVariant(Variant in);

	/**
	 * Used to get the value from a windows type of VT_VARIANT or a jacob
	 * Variant type of VariantVariant. Added 1.12 pre 6 - VT_VARIANT support is
	 * at an alpha level
	 * 
	 * @return Object a java Object that represents the content of the enclosed
	 *         Variant
	 */
	public Object getVariant() {
		if ((this.getvt() & VariantVariant) == VariantVariant
				&& (this.getvt() & VariantByref) == VariantByref) {
			if (JacobObject.isDebugEnabled()) {
				JacobObject.debug("About to call getVariantVariant()");
			}
			Variant enclosedVariant = new Variant();
			int enclosedVariantMemory = getVariantVariant();
			enclosedVariant.m_pVariant = enclosedVariantMemory;
			Object enclosedVariantAsJava = enclosedVariant.toJavaObject();
			// zero out the reference to the underlying windows memory so that
			// it is still only owned in one place by one java object
			// (this object of type VariantVariant)
			// enclosedVariant.putEmpty(); // don't know if this would have had
			// side effects
			if (JacobObject.isDebugEnabled()) {
				JacobObject
						.debug("Zeroing out enclosed Variant's ref to windows memory");
			}
			enclosedVariant.m_pVariant = 0;
			return enclosedVariantAsJava;
		} else {
			throw new IllegalStateException(
					"getVariant() only legal on Variants of type VariantVariant, not "
							+ this.getvt());
		}
	}

	/**
	 * Returns the variant type via a native method call. Added 1.12 pre 6 -
	 * VT_VARIANT support is at an alpha level
	 * 
	 * @return Variant one of the VT_Variant types
	 */
	private native int getVariantVariant();

	/**
	 * get the content of this variant as a short
	 * 
	 * @return short
	 */
	private native short getVariantShortRef();

	/**
	 * get the content of this variant as an int
	 * 
	 * @return int
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public short getShortRef() {
		if ((this.getvt() & VariantShort) == VariantShort
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantShortRef();
		} else {
			throw new IllegalStateException(
					"getShortRef() only legal on byRef Variants of type VariantShort, not "
							+ this.getvt());
		}
	}

	/**
	 * get the content of this variant as an int
	 * 
	 * @return int
	 */
	private native int getVariantIntRef();

	/**
	 * get the content of this variant as an int
	 * 
	 * @return int
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public int getIntRef() {
		if ((this.getvt() & VariantInt) == VariantInt
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantIntRef();
		} else {
			throw new IllegalStateException(
					"getIntRef() only legal on byRef Variants of type VariantInt, not "
							+ this.getvt());
		}
	}

	/**
	 * set the content of this variant to a short (VT_I2)
	 * 
	 * @param in
	 */
	private native void putVariantShort(short in);

	/**
	 * set the content of this variant to a short (VT_I2)
	 * 
	 * @param in
	 */
	public void putShort(short in) {
		// verify we aren't released
		getvt();
		putVariantShort(in);
	}

	/**
	 * get the content of this variant as a short
	 * 
	 * @return short
	 */
	private native short getVariantShort();

	/**
	 * return the int value held in this variant (fails on other types?)
	 * 
	 * @return int
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public short getShort() {
		if (this.getvt() == VariantShort) {
			return getVariantShort();
		} else {
			throw new IllegalStateException(
					"getShort() only legal on Variants of type VariantShort, not "
							+ this.getvt());
		}
	}

	/**
	 * get the content of this variant as a double
	 * 
	 * @return double
	 */
	private native double getVariantDoubleRef();

	/**
	 * 
	 * @return returns the double value, throws exception if not a Double type
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public double getDoubleRef() {
		if ((this.getvt() & VariantDouble) == VariantDouble
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantDoubleRef();
		} else {
			throw new IllegalStateException(
					"getDoubleRef() only legal on byRef Variants of type VariantDouble, not "
							+ this.getvt());
		}
	}

	/**
	 * get the content of this variant as a double representing a date
	 * 
	 * @return double
	 */
	private native double getVariantDateRef();

	/**
	 * 
	 * @return returns the date value as a double, throws exception if not a
	 *         date type
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public double getDateRef() {
		if ((this.getvt() & VariantDate) == VariantDate
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantDateRef();
		} else {
			throw new IllegalStateException(
					"getDateRef() only legal on byRef Variants of type VariantDate, not "
							+ this.getvt());
		}
	}

	/**
	 * returns the windows time contained in this Variant to a Java Date should
	 * return null if this is not a date reference Variant SF 959382
	 * 
	 * @return java.util.Date
	 */
	public Date getJavaDateRef() {
		double windowsDate = getDateRef();
		if (windowsDate == 0) {
			return null;
		} else {
			return DateUtilities.convertWindowsTimeToDate(windowsDate);
		}
	}

	/**
	 * get the content of this variant as a string
	 * 
	 * @return String
	 */
	private native String getVariantStringRef();

	/**
	 * gets the content of the variant as a string ref
	 * 
	 * @return String retrieved from the COM area.
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public String getStringRef() {
		if ((this.getvt() & VariantString) == VariantString
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantStringRef();
		} else {
			throw new IllegalStateException(
					"getStringRef() only legal on byRef Variants of type VariantString, not "
							+ this.getvt());
		}
	}

	/**
	 * @deprecated superseded by SafeArray
	 * @return never returns anything
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public Object toCharArray() {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * Clear the content of this variant
	 */
	public native void VariantClear();

	/**
	 * @return the content of this variant as a Dispatch object (after possible
	 *         conversion)
	 */
	public Dispatch toDispatch() {
		// now make the native call
		return toVariantDispatch();
	}

	/**
	 * native method used by toDispatch()
	 * 
	 * @return
	 */
	private native Dispatch toVariantDispatch();

	/**
	 * this returns null
	 * 
	 * @return ?? comment says null?
	 */
	public native Object clone();

	/**
	 * This method now correctly implements java toString() semantics Attempts
	 * to return the content of this variant as a string
	 * <ul>
	 * <li>"not initialized" if not initialized
	 * <li>"null" if VariantEmpty,
	 * <li>"null" if VariantError
	 * <li>"null" if VariantNull
	 * <li>the value if we know how to describe one of that type
	 * <li>three question marks if can't convert
	 * 
	 * @return String value conversion,
	 * @throws IllegalStateException
	 *             if there is no underlying windows data structure
	 */
	public String toString() {
		try {
			// see if we are in a legal state
			getvt();
		} catch (IllegalStateException ise) {
			return "";
		}
		if (getvt() == VariantEmpty || getvt() == VariantError
				|| getvt() == VariantNull) {
			return "null";
		}
		if (getvt() == VariantString) {
			return getString();
		}
		try {
			Object foo = toJavaObject();
			// rely on java objects to do the right thing
			return foo.toString();
		} catch (NotImplementedException nie) {
			// some types do not generate a good description yet
			return "Description not available for type: " + getvt();
		}
	}

	/**
	 * return the int value held in this variant (fails on other types?)
	 * 
	 * @return int
	 */
	private native int getVariantInt();

	/**
	 * return the int value held in this variant if it is an int or a short.
	 * Throws for other types.
	 * 
	 * @return int contents of the windows memory
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public int getInt() {
		if (this.getvt() == VariantInt) {
			return getVariantInt();
		} else if (this.getvt() == VariantShort) {
			return getVariantShort();
		} else {
			throw new IllegalStateException(
					"getInt() only legal on Variants of type VariantInt, not "
							+ this.getvt());
		}
	}

	/**
	 * @return double return the date (as a double) value held in this variant
	 *         (fails on other types?)
	 */
	private native double getVariantDate();

	/**
	 * @return double return the date (as a double) value held in this variant
	 *         (fails on other types?)
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public double getDate() {
		if (this.getvt() == VariantDate) {
			return getVariantDate();
		} else {
			throw new IllegalStateException(
					"getDate() only legal on Variants of type VariantDate, not "
							+ this.getvt());
		}
	}

	/**
	 * returns the windows time contained in this Variant to a Java Date. should
	 * return null if this is not a date Variant SF 959382
	 * 
	 * @return java.util.Date returns the date if this is a VariantDate != 0,
	 *         null if it is a VariantDate == 0 and throws an
	 *         IllegalStateException if this isn't a date.
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public Date getJavaDate() {
		Date returnDate = null;
		if (getvt() == VariantDate) {
			double windowsDate = getDate();
			if (windowsDate != 0) {
				returnDate = DateUtilities.convertWindowsTimeToDate(getDate());
			}
		} else {
			throw new IllegalStateException(
					"getJavaDate() only legal on Variants of type VariantDate, not "
							+ this.getvt());
		}
		return returnDate;
	}

	/**
	 * set the value of this variant and set the type
	 * 
	 * @param in
	 */
	private native void putVariantInt(int in);

	/**
	 * set the value of this variant and set the type
	 * 
	 * @param in
	 */
	public void putInt(int in) {
		// verify we aren't released yet
		getvt();
		putVariantInt(in);
	}

	/**
	 * @return the value in this Variant as a decimal, null if not a decimal
	 */
	private native Object getVariantDec();

	/**
	 * @return the value in this Variant (byref) as a decimal, null if not a
	 *         decimal
	 */
	private native Object getVariantDecRef();

	/**
	 * return the BigDecimal value held in this variant (fails on other types)
	 * 
	 * @return BigDecimal
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public BigDecimal getDecimal() {
		if (this.getvt() == VariantDecimal) {
			return (BigDecimal) (getVariantDec());
		} else {
			throw new IllegalStateException(
					"getDecimal() only legal on Variants of type VariantDecimal, not "
							+ this.getvt());
		}
	}

	/**
	 * return the BigDecimal value held in this variant (fails on other types)
	 * 
	 * @return BigDecimal
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public BigDecimal getDecimalRef() {
		if ((this.getvt() & VariantDecimal) == VariantDecimal
				&& (this.getvt() & VariantByref) == VariantByref) {
			return (BigDecimal) (getVariantDecRef());
		} else {
			throw new IllegalStateException(
					"getDecimalRef() only legal on byRef Variants of type VariantDecimal, not "
							+ this.getvt());
		}
	}

	/**
	 * private JNI method called by putDecimal
	 * 
	 * @param signum
	 *            sign
	 * @param scale
	 *            BigDecimal's scale
	 * @param lo
	 *            low 32 bits
	 * @param mid
	 *            middle 32 bits
	 * @param hi
	 *            high 32 bits
	 */
	private native void putVariantDec(int signum, byte scale, int lo, int mid,
			int hi);

	/**
	 * Set the value of this variant and set the type. This may throw exceptions
	 * more often than the caller expects because most callers don't manage the
	 * scale of their BigDecimal objects.
	 * 
	 * @param in
	 *            the big decimal that will convert to the VT_DECIMAL type
	 * @throws IllegalArgumentException
	 *             if the scale is > 28, the maximum for VT_DECIMAL
	 */
	public void putDecimal(BigDecimal in) {
		// verify we aren't released yet
		getvt();
		if (in.scale() > 28) {
			// should this really cast to a string and call putStringRef()?
			throw new IllegalArgumentException(
					"VT_DECIMAL only supports a scale of 28 and the passed"
							+ " in value has a scale of " + in.scale());
		} else {
			int sign = in.signum();
			// MS decimals always have positive values with just the sign
			// flipped
			if (in.signum() < 0) {
				in = in.negate();
			}
			byte scale = (byte) in.scale();
			BigInteger unscaled = in.unscaledValue();
			BigInteger shifted = unscaled.shiftRight(32);
			putVariantDec(sign, scale, unscaled.intValue(), shifted.intValue(),
					shifted.shiftRight(32).intValue());
		}
	}

	/**
	 * set the value of this variant
	 * 
	 * @param in
	 */
	private native void putVariantDate(double in);

	/**
	 * puts a windows date double into the variant and sets the type
	 * 
	 * @param in
	 */
	public void putDate(double in) {
		// verify we aren't released yet
		getvt();
		putVariantDate(in);
	}

	/**
	 * converts a java date to a windows time and calls putDate(double) SF
	 * 959382
	 * 
	 * @param inDate
	 *            a Java date to be converted
	 * @throws IllegalArgumentException
	 *             if inDate = null
	 */
	public void putDate(Date inDate) {
		if (inDate == null) {
			throw new IllegalArgumentException(
					"Cannot put null in as windows date");
			// do nothing
		} else {
			putDate(DateUtilities.convertDateToWindowsTime(inDate));
		}
	}

	/**
	 * attempts to return the content of this variant as a double (after
	 * possible conversion)
	 * 
	 * @deprecated should be replaced by changeType() followed by getByte()
	 * @return byte
	 */
	@Deprecated
	public byte toByte() {
		changeType(Variant.VariantByte);
		return getByte();
	}

	/**
	 * cover for {@link #toDispatch()} This method now matches other getXXX()
	 * methods. It throws an IllegalStateException if the object is not of type
	 * VariantDispatch
	 * 
	 * @return this object as a dispatch
	 * @throws IllegalStateException
	 *             if wrong variant type
	 */
	public Dispatch getDispatch() {
		if ((this.getvt() & VariantDispatch) == VariantDispatch) {
			return toDispatch();
		} else {
			throw new IllegalStateException(
					"getDispatch() only legal on Variants of type VariantDispatch, not "
							+ this.getvt());
		}
	}

	/**
	 * This acts a cover for same as
	 * 
	 * Why isn't this typed as type Dispatch?
	 * 
	 * @param in
	 */
	public void putDispatch(Dispatch in) {
		putVariantDispatch(in);
	}

	/**
	 * 
	 * @return the value in this Variant as a boolean, null if not a boolean
	 */
	private native boolean getVariantBoolean();

	/**
	 * 
	 * @return returns the value as a boolean, throws an exception if its not.
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public boolean getBoolean() {
		if (this.getvt() == VariantBoolean) {
			return getVariantBoolean();
		} else {
			throw new IllegalStateException(
					"getBoolean() only legal on Variants of type VariantBoolean, not "
							+ this.getvt());
		}
	}

	/**
	 * 
	 * @return the value in this Variant as a byte, null if not a byte
	 */
	private native byte getVariantByte();

	/**
	 * 
	 * @return returns the value as a boolean, throws an exception if its not.
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public byte getByte() {
		if (this.getvt() == VariantByte) {
			return getVariantByte();
		} else {
			throw new IllegalStateException(
					"getByte() only legal on Variants of type VariantByte, not "
							+ this.getvt());
		}
	}

	/**
	 * puts a boolean into the variant and sets it's type
	 * 
	 * @param in
	 *            the new value
	 */
	private native void putVariantBoolean(boolean in);

	/**
	 * puts a boolean into the variant and sets it's type
	 * 
	 * @param in
	 *            the new value
	 */
	public void putBoolean(boolean in) {
		// verify we aren't released yet
		getvt();
		putVariantBoolean(in);
	}

	private native void putVariantByte(byte in);

	/**
	 * pushes a byte into the varaint and sets the type
	 * 
	 * @param in
	 */
	public void putByte(byte in) {
		// verify we aren't released yet
		getvt();
		putVariantByte(in);
	}

	/**
	 * converts to an error type and returns the error
	 * 
	 * @deprecated should use changeType() followed by getError()
	 * @return the error as an int (after conversion)
	 */
	@Deprecated
	public int toError() {
		changeType(Variant.VariantError);
		return getError();
	}

	/**
	 * Acts a a cover for toDispatch. This primarily exists to support jacobgen.
	 * 
	 * @deprecated this is a cover for toDispatch();
	 * @return Object returned by toDispatch()
	 * @see Variant#toDispatch() instead
	 */
	@Deprecated
	public Object toObject() {
		return toDispatch();
	}

	/**
	 * Pointless method that was put here so that putEmpty() has a get method.
	 * This would have returned null if the value was VT_EMPTY or if it wasn't
	 * so it would have always returned the same value.
	 * 
	 * @deprecated method never did anything
	 */
	@Deprecated
	public void getEmpty() {
	};

	/**
	 * Sets the type to VariantEmpty. No values needed
	 */
	private native void putVariantEmpty();

	/**
	 * sets the type to VariantEmpty
	 * 
	 */
	public void putEmpty() {
		// verify we aren't released yet
		getvt();
		putVariantEmpty();
	}

	/**
	 * Sets the type to VariantDispatch and sets the value to null Equivalent to
	 * VB's nothing
	 */
	private native void putVariantNothing();

	/**
	 * Sets the type to VariantDispatch and sets the value to null Equivalent to
	 * VB's nothing
	 */
	public void putNothing() {
		// verify we aren't released yet
		getvt();
		putVariantNothing();
	}

	private native int getVariantError();

	/**
	 * @return double return the error value held in this variant (fails on
	 *         other types?)
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public int getError() {
		if (this.getvt() == VariantError) {
			return getVariantError();
		} else {
			throw new IllegalStateException(
					"getError() only legal on Variants of type VariantError, not "
							+ this.getvt());
		}
	}

	private native void putVariantError(int in);

	/**
	 * puts an error code (I think) into the variant and sets the type
	 * 
	 * @param in
	 */
	public void putError(int in) {
		// verify we aren't released yet
		getvt();
		putVariantError(in);
	}

	private native double getVariantDouble();

	/**
	 * @return double return the double value held in this variant (fails on
	 *         other types?)
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public double getDouble() {
		if (this.getvt() == VariantDouble) {
			return getVariantDouble();
		} else {
			throw new IllegalStateException(
					"getDouble() only legal on Variants of type VariantDouble, not "
							+ this.getvt());
		}
	}

	private native void putVariantCurrency(long in);

	/**
	 * Puts a value in as a currency and sets the variant type. MS Currency
	 * objects are 64 bit fixed point numbers with 15 digits to the left and 4
	 * to the right of the decimal place.
	 * 
	 * @param in
	 *            the long that will be put into the 64 bit currency object.
	 */
	public void putCurrency(long in) {
		// verify we aren't released yet
		getvt();
		putVariantCurrency(in);
	}

	/**
	 * Puts an object into the Variant -- converts to Dispatch. Acts as a cover
	 * for putVariantDispatch(); This primarily exists to support jacobgen. This
	 * should be deprecated.
	 * 
	 * @param in
	 *            the object we are putting into the Variant, assumes a
	 * @see Variant#putDispatch(Dispatch)
	 * @deprecated should use putDispatch()
	 */
	@Deprecated
	public void putObject(Object in) {
		// this should verify in instanceof Dispatch
		putVariantDispatch(in);
	}

	/**
	 * the JNI implementation for putDispatch() so that we can screen the
	 * incoming dispatches in putDispatch() before this is invoked
	 * 
	 * @param in
	 *            should be a dispatch object
	 */
	private native void putVariantDispatch(Object in);

	private native void putVariantDouble(double in);

	/**
	 * wraps this Variant around the passed in double.
	 * 
	 * @param in
	 */
	public void putDouble(double in) {
		// verify we aren't released yet
		getvt();
		putVariantDouble(in);
	}

	/**
	 * 
	 * @return the value in this Variant as a long, null if not a long
	 */
	private native long getVariantCurrency();

	/**
	 * MS Currency objects are 64 bit fixed point numbers with 15 digits to the
	 * left and 4 to the right of the decimal place.
	 * 
	 * @return returns the currency value as a long, throws exception if not a
	 *         currency type..
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public long getCurrency() {
		if (this.getvt() == VariantCurrency) {
			return getVariantCurrency();
		} else {
			throw new IllegalStateException(
					"getCurrency() only legal on Variants of type VariantCurrency, not "
							+ this.getvt());
		}
	}

	private native void putVariantFloatRef(float in);

	/**
	 * pushes a float into the variant and sets the type
	 * 
	 * @param in
	 */
	public void putFloatRef(float in) {
		// verify we aren't released yet
		getvt();
		putVariantFloatRef(in);
	}

	private native void putVariantCurrencyRef(long in);

	/**
	 * Pushes a long into the variant as currency and sets the type. MS Currency
	 * objects are 64 bit fixed point numbers with 15 digits to the left and 4
	 * to the right of the decimal place.
	 * 
	 * @param in
	 *            the long that will be put into the 64 bit currency object
	 */
	public void putCurrencyRef(long in) {
		// verify we aren't released yet
		getvt();
		putVariantCurrencyRef(in);
	}

	private native void putVariantErrorRef(int in);

	/**
	 * pushes an error code into the variant by ref and sets the type
	 * 
	 * @param in
	 */
	public void putErrorRef(int in) {
		// verify we aren't released yet
		getvt();
		putVariantErrorRef(in);
	}

	private native void putVariantBooleanRef(boolean in);

	/**
	 * pushes a boolean into the variant by ref and sets the type of the variant
	 * to boolean
	 * 
	 * @param in
	 */
	public void putBooleanRef(boolean in) {
		// verify we aren't released yet
		getvt();
		putVariantBooleanRef(in);
	}

	/**
	 * Just a cover for putObject(). We shouldn't accept any old random object.
	 * This has been left in to support jacobgen. This should be deprecated.
	 * 
	 * @param in
	 * @deprecated
	 */
	@Deprecated
	public void putObjectRef(Object in) {
		putObject(in);
	}

	private native void putVariantByteRef(byte in);

	/**
	 * pushes a byte into the variant by ref and sets the type
	 * 
	 * @param in
	 */
	public void putByteRef(byte in) {
		// verify we aren't released yet
		getvt();
		putVariantByteRef(in);
	}

	/**
	 * Native method that actually extracts a string value from the variant
	 * 
	 * @return
	 */
	private native String getVariantString();

	/**
	 * 
	 * @return string contents of the variant.
	 * @throws IllegalStateException
	 *             if this variant is not of type String
	 */
	public String getString() {
		if (getvt() == Variant.VariantString) {
			return getVariantString();
		} else {
			throw new IllegalStateException(
					"getString() only legal on Variants of type VariantString, not "
							+ this.getvt());
		}
	}

	private native void putVariantString(String in);

	/**
	 * put a string into the variant and set its type
	 * 
	 * @param in
	 */
	public void putString(String in) {
		// verify we aren't released yet
		getvt();
		putVariantString(in);
	}

	private native float getVariantFloatRef();

	/**
	 * 
	 * @return returns the float value, throws exception if not a Float type
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public float getFloatRef() {
		if ((this.getvt() & VariantFloat) == VariantFloat
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantFloatRef();
		} else {
			throw new IllegalStateException(
					"getFloatRef() only legal on byRef Variants of type VariantFloat, not "
							+ this.getvt());
		}
	}

	private native long getVariantCurrencyRef();

	/**
	 * MS Currency objects are 64 bit fixed point numbers with 15 digits to the
	 * left and 4 to the right of the decimal place.
	 * 
	 * @return returns the currency value as a long, throws exception if not a
	 *         currency type
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public long getCurrencyRef() {
		if ((this.getvt() & VariantCurrency) == VariantCurrency
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantCurrencyRef();
		} else {
			throw new IllegalStateException(
					"getCurrencyRef() only legal on byRef Variants of type VariantCurrency, not "
							+ this.getvt());
		}
	}

	private native int getVariantErrorRef();

	/**
	 * 
	 * @return returns the error value as an int, throws exception if not a
	 *         Error type
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public int getErrorRef() {
		if ((this.getvt() & VariantError) == VariantError
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantErrorRef();
		} else {
			throw new IllegalStateException(
					"getErrorRef() only legal on byRef Variants of type VariantError, not "
							+ this.getvt());
		}
	}

	private native boolean getVariantBooleanRef();

	/**
	 * public cover for native method
	 * 
	 * @return the boolean from a booleanRef
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public boolean getBooleanRef() {
		if ((this.getvt() & VariantBoolean) == VariantBoolean
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantBooleanRef();
		} else {
			throw new IllegalStateException(
					"getBooleanRef() only legal on byRef Variants of type VariantBoolean, not "
							+ this.getvt());
		}
	}

	private native byte getVariantByteRef();

	/**
	 * public cover for native method
	 * 
	 * @return the byte from a booleanRef
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public byte getByteRef() {
		if ((this.getvt() & VariantByte) == VariantByte
				&& (this.getvt() & VariantByref) == VariantByref) {
			return getVariantByteRef();
		} else {
			throw new IllegalStateException(
					"getByteRef() only legal on byRef Variants of type VariantByte, not "
							+ this.getvt());
		}
	}

	/**
	 * attempts to return the contents of this variant as a float (after
	 * possible conversion)
	 * 
	 * @deprecated should use changeType() and getFloat() instead
	 * @return float
	 */
	@Deprecated
	public float toFloat() {
		changeType(Variant.VariantFloat);
		return getFloat();
	}

	private native SafeArray toVariantSafeArray(boolean deepCopy);

	/**
	 * By default toSafeArray makes a deep copy due to the fact that this
	 * Variant owns the embedded SafeArray and will destroy it when it gc's
	 * calls toSafeArray(true).
	 * 
	 * @return the object converted to a SafeArray
	 */
	public SafeArray toSafeArray() {
		// verify we haven't been released yet
		getvt();
		return toSafeArray(true);
	}

	/**
	 * This lets folk turn into a safe array without a deep copy. Should this
	 * API be public?
	 * 
	 * @param deepCopy
	 * @return SafeArray constructed
	 */
	public SafeArray toSafeArray(boolean deepCopy) {
		// verify we haven't been released yet
		getvt();
		return toVariantSafeArray(deepCopy);
	}

	private native void putVariantSafeArrayRef(SafeArray in);

	/**
	 * have no idea...
	 * 
	 * @param in
	 */
	public void putSafeArrayRef(SafeArray in) {
		// verify we haven't been released yet
		getvt();
		putVariantSafeArrayRef(in);
	}

	private native void putVariantSafeArray(SafeArray in);

	/**
	 * have no idea...
	 * 
	 * @param in
	 */
	public void putSafeArray(SafeArray in) {
		// verify we haven't been released yet
		getvt();
		putVariantSafeArray(in);
	}

	/**
	 * sets the type to VT_ERROR and the error message to DISP_E_PARAMNOTFOIUND
	 */
	private native void putVariantNoParam();

	/**
	 * sets the type to VT_ERROR and the error message to DISP_E_PARAMNOTFOIUND
	 */
	public void putNoParam() {
		// verify we aren't released yet
		getvt();
		putVariantNoParam();
	}

	/**
	 * sets the type to VT_ERROR and the error message to DISP_E_PARAMNOTFOIUND
	 * 
	 * @deprecated replaced by putNoParam()
	 */
	@Deprecated
	public void noParam() {
		putNoParam();
	}

	/**
	 * @param in
	 *            the object that would be wrapped by the Variant if this method
	 *            was implemented
	 * @deprecated superseded by SafeArray
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public void putCharArray(Object in) {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * 
	 * @return returns the value as a float if the type is of type float
	 */
	private native float getVariantFloat();

	/**
	 * @return returns the value as a float if the type is of type float
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public float getFloat() {
		if (this.getvt() == VariantFloat) {
			return getVariantFloat();
		} else {
			throw new IllegalStateException(
					"getFloat() only legal on Variants of type VariantFloat, not "
							+ this.getvt());
		}
	}

	/**
	 * fills the Variant with a float and sets the type to float
	 * 
	 * @param in
	 */
	private native void putVariantFloat(float in);

	/**
	 * fills the Variant with a float and sets the type to float
	 * 
	 * @param in
	 */
	public void putFloat(float in) {
		// verify we haven't been released yet
		getvt();
		putVariantFloat(in);
	}

	/**
	 * Dispatch and dispatchRef are treated the same This is a cover for
	 * putVariantDispatch(). Dispatch and dispatchRef are treated the same
	 * 
	 * @param in
	 */
	public void putDispatchRef(Dispatch in) {
		putVariantDispatch(in);
	}

	/**
	 * Dispatch and dispatchRef are treated the same This is just a cover for
	 * toDispatch() with a flag check
	 * 
	 * @return the results of toDispatch()
	 * @throws IllegalStateException
	 *             if variant is not of the requested type
	 */
	public Dispatch getDispatchRef() {
		if ((this.getvt() & VariantDispatch) == VariantDispatch
				&& (this.getvt() & VariantByref) == VariantByref) {
			return toDispatch();
		} else {
			throw new IllegalStateException(
					"getDispatchRef() only legal on byRef Variants of type VariantDispatch, not "
							+ this.getvt());
		}
	}

	/**
	 * @param in
	 *            the thing that would be come an array if this method was
	 *            implemented
	 * @deprecated superseded by SafeArray
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public void putVariantArrayRef(Variant[] in) {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * @return the Variant Array that represents the data in the Variant
	 * @deprecated superseded by SafeArray
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public Variant[] getVariantArrayRef() {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * Converts variant to the passed in type by converting the underlying
	 * windows variant structure. private so folks use public java method
	 * 
	 * @param in
	 *            the desired resulting type
	 */
	private native void changeVariantType(short in);

	/**
	 * Cover for native method so we can cover it.
	 * <p>
	 * This cannot convert an object to a byRef. It can convert from byref to
	 * not byref
	 * 
	 * @param in
	 *            type to convert this variant too
	 * @return Variant returns this same object so folks can change when
	 *         replacing calls toXXX() with changeType().getXXX()
	 */
	public Variant changeType(short in) {
		changeVariantType(in);
		return this;
	}

	/**
	 * I don't know what this is. Is it some legacy (pre 1.8) thing?
	 * 
	 * @deprecated
	 * @return this object as a dispatch object by calling toDispatch()
	 */
	@Deprecated
	public Object toScriptObject() {
		return toDispatch();
	}

	/**
	 * public constructor, initializes and sets type to VariantEmpty
	 */
	public Variant() {
		this(null, false);
	}

	/**
	 * Constructor that accepts a primitive rather than an object
	 * 
	 * @param in
	 */
	public Variant(int in) {
		this(new Integer(in));
	}

	/**
	 * Constructor that accepts a primitive rather than an object
	 * 
	 * @param in
	 */
	public Variant(double in) {
		this(new Double(in));
	}

	/**
	 * Constructor that accepts a primitive rather than an object
	 * 
	 * @param in
	 */
	public Variant(float in) {
		this(new Float(in));
	}

	/**
	 * Constructor that accepts a primitive rather than an object
	 * 
	 * @param in
	 */
	public Variant(long in) {
		this(new Long(in));
	}

	/**
	 * Constructor that accepts a primitive rather than an object
	 * 
	 * @param in
	 */
	public Variant(boolean in) {
		this(new Boolean(in));
	}

	/**
	 * Constructor that accepts a primitive rather than an object
	 * 
	 * @param in
	 */
	public Variant(short in) {
		this(new Short(in));
	}

	/**
	 * Constructor that accepts a primitive rather than an object
	 * 
	 * @param in
	 */
	public Variant(byte in) {
		this(new Byte(in));
	}

	/**
	 * Convenience constructor that calls the main one with a byRef value of
	 * false
	 * 
	 * @param in
	 *            object to be made into variant
	 */
	public Variant(Object in) {
		this(in, false);
	}

	/**
	 * Constructor that accepts the data object and information about whether
	 * this is by reference or not. It calls the JavaVariantConverter to
	 * actually push the data into the newly created Variant.
	 * 
	 * @param pValueObject
	 *            The value object that will pushed down into windows memory. A
	 *            null object sets this to "empty"
	 * @param fByRef
	 */
	public Variant(Object pValueObject, boolean fByRef) {
		init();
		VariantUtilities.populateVariant(this, pValueObject, fByRef);
	}

	/**
	 * Returns the variant type via a native method call
	 * 
	 * @return short one of the VT_xx types
	 */
	private native short getVariantType();

	/**
	 * Reports the type of the underlying Variant object
	 * 
	 * @return returns the variant type as a short, one of the Variantxxx values
	 *         defined as statics in this class. returns VariantNull if not
	 *         initialized
	 * @throws IllegalStateException
	 *             if there is no underlying windows data structure
	 */
	public short getvt() {
		if (m_pVariant != 0) {
			return getVariantType();
		} else {
			throw new IllegalStateException("uninitialized Variant");
		}
	}

	/**
	 * attempts to return the contents of this Variant as a short (after
	 * possible conversion)
	 * 
	 * @deprecated callers should use changeType() followed by getShort()
	 * @return short
	 */
	@Deprecated
	public short toShort() {
		this.changeType(Variant.VariantShort);
		return getShort();
	}

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
	 * returns true if the passed in Variant is a constant that should not be
	 * freed
	 * 
	 * @param pVariant
	 * @return boolean that is true if Variant is a type of constant, VT_FALSE,
	 *         VT_TRUE, VT_MISSING, DEFAULT
	 */
	protected boolean objectIsAConstant(Variant pVariant) {
		if (pVariant == VT_FALSE || pVariant == VT_TRUE
				|| pVariant == VT_MISSING || pVariant == DEFAULT) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * This will release the "C" memory for the Variant unless this Variant is
	 * one of the constants in which case we don't want to release the memory.
	 * <p>
	 * 
	 * @see com.jacob.com.JacobObject#safeRelease()
	 */
	public void safeRelease() {
		// The well known constants should not be released.
		// Unfortunately this doesn't fix any other classes that are
		// keeping constants around in their static ivars.
		// those will still be busted.
		//
		// The only inconsistency here is that we leak
		// when this class is unloaded because we won't
		// free the memory even if the constants are being
		// finalized. this is not a big deal at all.
		// another way around this would be to create the constants
		// in their own thread so that they would never be released
		if (!objectIsAConstant(this)) {
			super.safeRelease();
			if (m_pVariant != 0) {
				release();
				m_pVariant = 0;
			} else {
				// looks like a double release
				// this should almost always happen due to gc
				// after someone has called ComThread.Release
				if (isDebugEnabled()) {
					debug("Variant: " + this.hashCode() + " double release");
					// Throwable x = new Throwable();
					// x.printStackTrace();
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
	 * @deprecated superseded by SafeArray
	 * @return nothing because this method is not implemented
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public Variant[] toVariantArray() {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * @deprecated superseded by SafeArray
	 * @return nothing because this method is not implemented
	 * @throws com.jacob.com.NotImplementedException
	 */
	@Deprecated
	public Object toByteArray() {
		throw new NotImplementedException("Not implemented");
	}

	/**
	 * is the variant null or empty or error or null dispatch
	 * 
	 * @return true if it is null or false if not
	 */
	private native boolean isVariantConsideredNull();

	/**
	 * 
	 * @return returns true if the variant is considered null
	 * @throws IllegalStateException
	 *             if there is no underlying windows memory
	 */
	public boolean isNull() {
		getvt();
		return isVariantConsideredNull();
	}

	/**
	 * this is supposed to create a byte array that represents the underlying
	 * variant object structure
	 */
	protected native byte[] SerializationWriteToBytes();

	/**
	 * this is supposed to cause the underlying variant object struct to be
	 * rebuilt from a previously serialized byte array.
	 * 
	 * @param ba
	 */
	protected native void SerializationReadFromBytes(byte[] ba);

	/*
	 * =====================================================================
	 * 
	 * 
	 * =====================================================================
	 */

	/**
	 * Convert a JACOB Variant value to a Java object (type conversions).
	 * provided in Sourceforge feature request 959381. See
	 * JavaVariantConverter..convertVariantTJavaObject(Variant) for more
	 * information.
	 * 
	 * @return Corresponding Java object of the type matching the Variant type.
	 * @throws IllegalStateException
	 *             if no underlying windows data structure
	 * @throws NotImplementedException
	 *             if unsupported conversion is requested
	 * @throws JacobException
	 *             if the calculated result was a JacobObject usually as a
	 *             result of error
	 */
	public Object toJavaObject() throws JacobException {
		return VariantUtilities.variantToObject(this);
	}

}