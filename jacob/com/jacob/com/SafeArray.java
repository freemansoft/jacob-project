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

public class SafeArray extends JacobObject
{
  int m_pV = 0;

  public SafeArray() {}

  public SafeArray(int vt)
  {
    init(vt, new int[] {0}, new int[] {-1});
  }

  public SafeArray(int vt,int celems)
  {
    init(vt, new int[] {0}, new int[] {celems});
  }

  public SafeArray(int vt,int celems1,int celems2)
  {
    init(vt, new int[] {0,0}, new int[] {celems1, celems2});
  }

  public SafeArray(int vt,int lbounds[],int celems[])
  {
    init(vt, lbounds, celems);
  }

  // convert a string to a VT_UI1 array
  public SafeArray(String s)
  {
    char[] ca = s.toCharArray();
    init(Variant.VariantByte, new int[] {0}, new int[] {ca.length});
    fromCharArray(ca);
  }

  protected native void init(int vt, int lbounds[], int celems[]);

  // not impl
  public int getNumLocks() { return 0; }

  // convert a VT_UI1 array to string
  public String asString()
  {
    if (getvt() != Variant.VariantByte) return null;
    char ja[] = toCharArray();
    return new String(ja);
  }


  public native Object clone();
  // call this to explicitly release the com object before gc

  public void release()
  {
    destroy();
  }

  public native void destroy();
  public native int getvt();
  protected void finalize() 
  {
    //System.out.println("SafeArray finalize start");
    if (m_pV != 0) release(); 
    //System.out.println("SafeArray finalize end");
  }
  public native void reinit(SafeArray sa);
  public native void reinterpretType(int vt);

  public native int getLBound();
  public native int getLBound(int dim);
  public native int getUBound();
  public native int getUBound(int dim);

  public native int getNumDim();
  public native int getFeatures();
  public native int getElemSize();

  public native void fromCharArray(char ja[]);
  public native void fromIntArray(int ja[]);
  public native void fromShortArray(short ja[]);
  public native void fromDoubleArray(double ja[]);
  public native void fromStringArray(String ja[]);
  public native void fromByteArray(byte ja[]);
  public native void fromFloatArray(float ja[]);
  public native void fromBooleanArray(boolean ja[]);
  public native void fromVariantArray(Variant ja[]);

  public native char[] toCharArray();
  public native int[] toIntArray();
  public native short[] toShortArray();
  public native double[] toDoubleArray();
  public native String[] toStringArray();
  public native byte[] toByteArray();
  public native float[] toFloatArray();
  public native boolean[] toBooleanArray();
  public native Variant[] toVariantArray();

  // char access
  public native char getChar(int sa_idx);
  public native char getChar(int sa_idx1, int sa_idx2);
  public native void setChar(int sa_idx, char c);
  public native void setChar(int sa_idx1, int sa_idx2, char c);
  public native void getChars(int sa_idx, int nelems, char ja[], int ja_start);
  public native void setChars(int sa_idx, int nelems, char ja[], int ja_start);

  // int access
  public native int  getInt(int sa_idx);
  public native int  getInt(int sa_idx1, int sa_idx2);
  public native void setInt(int sa_idx, int c);
  public native void setInt(int sa_idx1, int sa_idx2, int c);
  public native void getInts(int sa_idx, int nelems, int ja[], int ja_start);
  public native void setInts(int sa_idx, int nelems, int ja[], int ja_start);

  // short access
  public native short  getShort(int sa_idx);
  public native short  getShort(int sa_idx1, int sa_idx2);
  public native void   setShort(int sa_idx, short c);
  public native void   setShort(int sa_idx1, int sa_idx2, short c);
  public native void   getShorts(int sa_idx, int nelems, short ja[], int ja_start);
  public native void   setShorts(int sa_idx, int nelems, short ja[], int ja_start);

  // double access
  public native double  getDouble(int sa_idx);
  public native double  getDouble(int sa_idx1, int sa_idx2);
  public native void    setDouble(int sa_idx, double c);
  public native void    setDouble(int sa_idx1, int sa_idx2, double c);
  public native void    getDoubles(int sa_idx, int nelems, double ja[], int ja_start);
  public native void    setDoubles(int sa_idx, int nelems, double ja[], int ja_start);

  // string access
  public native String  getString(int sa_idx);
  public native String  getString(int sa_idx1, int sa_idx2);
  public native void    setString(int sa_idx, String c);
  public native void    setString(int sa_idx1, int sa_idx2, String c);
  public native void    getStrings(int sa_idx, int nelems, String ja[], int ja_start);
  public native void    setStrings(int sa_idx, int nelems, String ja[], int ja_start);

  // byte access
  public native byte  getByte(int sa_idx);
  public native byte  getByte(int sa_idx1, int sa_idx2);
  public native void  setByte(int sa_idx, byte c);
  public native void  setByte(int sa_idx1, int sa_idx2, byte c);
  public native void  getBytes(int sa_idx, int nelems, byte ja[], int ja_start);
  public native void  setBytes(int sa_idx, int nelems, byte ja[], int ja_start);

  // float access
  public native float  getFloat(int sa_idx);
  public native float  getFloat(int sa_idx1, int sa_idx2);
  public native void   setFloat(int sa_idx, float c);
  public native void   setFloat(int sa_idx1, int sa_idx2, float c);
  public native void   getFloats(int sa_idx, int nelems, float ja[], int ja_start);
  public native void   setFloats(int sa_idx, int nelems, float ja[], int ja_start);

  // boolean access
  public native boolean  getBoolean(int sa_idx);
  public native boolean  getBoolean(int sa_idx1, int sa_idx2);
  public native void     setBoolean(int sa_idx, boolean c);
  public native void     setBoolean(int sa_idx1, int sa_idx2, boolean c);
  public native void     getBooleans(int sa_idx, int nelems, boolean ja[], int ja_start);
  public native void     setBooleans(int sa_idx, int nelems, boolean ja[], int ja_start);

  // variant access
  public native Variant  getVariant(int sa_idx);
  public native Variant  getVariant(int sa_idx1, int sa_idx2);
  public native void     setVariant(int sa_idx, Variant c);
  public native void     setVariant(int sa_idx1, int sa_idx2, Variant c);
  public native void     getVariants(int sa_idx, int nelems, Variant ja[], int ja_start);
  public native void     setVariants(int sa_idx, int nelems, Variant ja[], int ja_start);

  public String toString()
  {
    String s = "";
    int ndim = getNumDim();
    if (ndim == 1)
    {
      int ldim = getLBound();
      int udim = getUBound();
      for (int i = ldim; i <= udim; i++)
      {
        Variant v = getVariant(i);

        if (((v.getvt() & Variant.VariantTypeMask) | Variant.VariantArray) == v.getvt())
        {
          return s + "[" + v.toSafeArray().toString() + "]";
        }
        else
        {
          s += " " + v.toString();
        }
      }
    }
    else if (ndim == 2)
    {
      int ldim1 = getLBound(1);
      int udim1 = getUBound(1);

      int ldim2 = getLBound(2);
      int udim2 = getUBound(2);

      for (int i = ldim1; i <= udim1; i++)
      {
        for (int j = ldim2; j <= udim2; j++)
        {
          Variant v = getVariant(i, j);
          s += " " + v.toString();
        }
        s += "\n";
      }
    }
    return s;
  }

  static {
    System.loadLibrary("jacob");
  }

}
