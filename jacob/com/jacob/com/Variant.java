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
 * The multi-format data type used for all call backs and most
 * communications between Java and COM
 */
public class Variant extends JacobObject implements java.io.Serializable
{
  int m_pVariant = 0;

  public static final short VariantEmpty = 0;
  public static final short VariantNull = 1;
  public static final short VariantShort = 2;
  public static final short VariantInt = 3;
  public static final short VariantFloat = 4;
  public static final short VariantDouble = 5;
  public static final short VariantCurrency = 6;
  public static final short VariantDate = 7;
  public static final short VariantString = 8;
  public static final short VariantDispatch = 9;
  public static final short VariantError = 10;
  public static final short VariantBoolean = 11;
  public static final short VariantVariant = 12;
  public static final short VariantObject = 13;
  public static final short VariantByte = 17;
  public static final short VariantTypeMask = 4095;
  public static final short VariantArray = 8192;
  public static final short VariantByref = 16384;

  public native int toInt();
  public native double toDate();
  public native boolean toBoolean();
  public native EnumVariant toEnumVariant();

  public native void getNull();
  public native void putNull();

  public native Variant cloneIndirect();
  public native double toDouble();
  public native long toCurrency();

  // superceded by SafeArray
  public void putVariantArray(Variant[] in)
  {
    throw new ComFailException("Not implemented");
  }

  // superceded by SafeArray
  public Variant[] getVariantArray()
  {
    throw new ComFailException("Not implemented");
  }

  // superceded by SafeArray
  public void putByteArray(Object in)
  {
    throw new ComFailException("Not implemented");
  }

  public native void putShortRef(short in);
  public native void putIntRef(int in);
  public native void putDoubleRef(double in);
  public native void putDateRef(double in);
  public native void putStringRef(String in);
  public native short getShortRef();
  public native int getIntRef();
  public native void putShort(short in);
  public native short getShort();
  public native double getDoubleRef();
  public native double getDateRef();
  public native String getStringRef();

  // superceded by SafeArray
  public Object toCharArray()
  {
    throw new ComFailException("Not implemented");
  }

  public native void VariantClear();
  public native Dispatch toDispatch();
  public native Object clone();
  public native String toString();
  public native int getInt();
  public native double getDate();
  public native void putInt(int in);
  public native void putDate(double in);
  public native byte toByte();

  public Object getDispatch() { return toDispatch(); }
  public void putDispatch(Object in) { putObject(in); }

  public native boolean getBoolean();
  public native byte getByte();
  public native void putBoolean(boolean in);
  public native void putByte(byte in);
  public native int toError();

  public Object toObject()
  {
    return toDispatch();
  }

  public native void getEmpty();
  public native void putEmpty();
  public native int getError();
  public native void putError(int in);
  public native double getDouble();
  public Object getObject()
  {
    return toDispatch();
  }

  public native void putCurrency(long in);

  public native void putObject(Object in);

  public native void putDouble(double in);
  public native long getCurrency();
  public native void putFloatRef(float in);
  public native void putCurrencyRef(long in);
  public native void putErrorRef(int in);
  public native void putBooleanRef(boolean in);

  public void putObjectRef(Object in)
  {
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
   * By default toSafeArray makes a deep copy due to the fact
   * that this Variant owns the embedded SafeArray and will
   * destroy it when it gc's
   */
  public SafeArray toSafeArray()
  {
    return toSafeArray(true);
  }

  public native SafeArray toSafeArray(boolean deepCopy);
  public native void putSafeArrayRef(SafeArray in);
  public native void putSafeArray(SafeArray in);

  public native void noParam();

  // superceded by SafeArray
  public void putCharArray(Object in)
  {
    throw new ComFailException("Not implemented");
  }

  public native float getFloat();
  public native void putFloat(float in);

  public void putDispatchRef(Object in) { putDispatch(in); }
  public Object getDispatchRef() { return getDispatch(); }

  // superceded by SafeArray
  public void putVariantArrayRef(Variant[] in)
  {
    throw new ClassCastException("Not implemented");
  }

  // superceded by SafeArray
  public Variant[] getVariantArrayRef()
  {
    throw new ClassCastException("Not implemented");
  }

  public native void changeType(short in);

  public void changeType(int in)
  {
    changeType((short)in);
  }

  public Object toScriptObject() { return toDispatch(); }

  public Variant()
  {
    init();
    putEmpty();
  }

  public Variant(int in)
  {
    init();
    putInt(in);
  }

  public Variant(double in)
  {
    init();
    putDouble(in);
  }

  public Variant(boolean in)
  {
    init();
    putBoolean(in);
  }

  public Variant(String in)
  {
    init();
    putString(in);
  }

  public Variant(SafeArray in,boolean fByRef)
  {
    init();
    if (fByRef) {
      putSafeArrayRef(in);
    } else {
      putSafeArray(in);
    }
  }

  public Variant(Object in)
  {
    this(in, false);
  }

  public Variant(Object o,boolean fByRef)
  {
    init();
    if (o == null) {
      putEmpty();
    } else if (o instanceof Integer) {
      if (fByRef) putIntRef(((Integer)o).intValue());
      else putInt(((Integer)o).intValue());
    } else if (o instanceof String) {
      if (fByRef) putStringRef((String)o);
      else putString((String)o);
    } else if (o instanceof Boolean) {
      if (fByRef) putBooleanRef(((Boolean)o).booleanValue());
      else putBoolean(((Boolean)o).booleanValue());
    } else if (o instanceof Double) {
      if (fByRef) putDoubleRef(((Double)o).doubleValue());
      else putDouble(((Double)o).doubleValue());
    } else if (o instanceof Float) {
      if (fByRef) putFloatRef(((Float)o).floatValue());
      else putFloat(((Float)o).floatValue());
    } else if (o instanceof SafeArray) {
      if (fByRef) putSafeArrayRef((SafeArray)o); 
      else putSafeArray((SafeArray)o);
    } else {
      if (fByRef) putObjectRef(o); else putObject(o);
    }
  }

  //weird constructors
  public Variant(int in,int in1)
  {
    throw new ComFailException("Not implemented");
  }

  public Variant(int in,boolean in1)
  {
    throw new ComFailException("Not implemented");
  }

  public Variant(int in,double in1)
  {
    throw new ComFailException("Not implemented");
  }

  public Variant(int in,Object in1)
  {
    throw new ComFailException("Not implemented");
  }

  public native short getvt();
  public native short toShort();

  // call this to explicitly release the com object before gc
  public native void release();

  protected native void init();

  protected void finalize()
  {
    //System.out.println("Variant finalize start:"+m_pVariant);
    if (m_pVariant != 0) release();
    //System.out.println("Variant finalize end");
  }


  // superceded by SafeArray
  public Variant[] toVariantArray()
  {
    throw new ClassCastException("Not implemented");
  }

  // superceded by SafeArray
  public Object toByteArray()
  {
    throw new ClassCastException("Not implemented");
  }

  static {
    System.loadLibrary("jacob");
  }

  // serialization support
  private void writeObject(java.io.ObjectOutputStream oos)
  {
    try {
      Save(oos);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void readObject(java.io.ObjectInputStream ois)
  {
    try {
      Load(ois);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // is the variant null or empty or error or null disp 
  public native boolean isNull();

  public native void Save(java.io.OutputStream os) 
    throws java.io.IOException;

  public native void Load(java.io.InputStream is) 
    throws java.io.IOException;

}
