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
#include "stdafx.h"
#include "Variant.h"
// Win32 support for Ole Automation
#include <wchar.h>
#include <string.h>
#include <atlbase.h>
#include <objbase.h>
#include <oleauto.h>
#include <olectl.h>
#include "util.h"

extern "C" 
{

#define VARIANT_FLD "m_pVariant"

// extract a VARIANT from a Variant object
VARIANT *extractVariant(JNIEnv *env, jobject arg)
{
  jclass argClass = env->GetObjectClass(arg);
  jfieldID ajf = env->GetFieldID( argClass, VARIANT_FLD, "J");
  jlong anum = env->GetLongField(arg, ajf);
  VARIANT *v = (VARIANT *)anum;
  return v;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_release
  (JNIEnv *env, jobject _this)
{
  jclass clazz = env->GetObjectClass(_this);
  jfieldID jf = env->GetFieldID(clazz, VARIANT_FLD, "J");
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    // fix byref leak 
    if (V_VT(v) & VT_BYREF)        // is this a reference
    {
      void  *pMem = V_BSTRREF(v);    // get allocated memory
      if (pMem)
      {
        if (V_VT(v) == (VT_BYREF|VT_BSTR))
        {
          BSTR  *pBstr = (BSTR*)pMem;
          if (*pBstr)
            SysFreeString(*pBstr);// release bstr
        }
        CoTaskMemFree(pMem);
      }
    }
    VariantClear(v);
    delete v;
    env->SetLongField(_this, jf, 0ll);
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_init
  (JNIEnv *env, jobject _this)
{
  jclass clazz = env->GetObjectClass(_this);
  jfieldID jf = env->GetFieldID( clazz, VARIANT_FLD, "J");
  VARIANT *v = new VARIANT();
  VariantInit(v);
  env->SetLongField(_this, jf, (jlong)v);
}


/*
 * Class:     com_jacob_com_Variant
 * Method:    zeroVariant
 * Signature: ()V
 *
 * This should only be used on variant objects created by the
 * com layer as part of a call through EventProxy.
 * This zeros out the variant pointer in the Variant object
 * so that the calling COM program can free the memory.
 * instead of both the COM program and the Java GC doing it.
 */
void zeroVariant(JNIEnv *env, jobject _this)
{
  // sf 3435567 Fix a memory leak (detected with Glowcode) in Variant.cpp/zeroVariant function
  // does this code conflict with the function/method documentation now?
  // note that a related fix was proposed in SF 1689061 in EventProxy.cpp but never accepted
  VARIANT *v = extractVariant(env, _this);
  delete v;

  jclass clazz = env->GetObjectClass(_this);
  jfieldID jf = env->GetFieldID(clazz, VARIANT_FLD, "J");
  env->SetLongField(_this, jf, 0ll);
}


/**
 * This is the core of the old Save method.
 * It copies this variant to a byte stream.
 * The unmarshalling part of this doesn't work but it was left in 
 * with the hope that someone will want to fix this later
 **/
JNIEXPORT jbyteArray JNICALL Java_com_jacob_com_Variant_SerializationWriteToBytes
  (JNIEnv *env, jobject _this){
  VARIANT *v = extractVariant(env, _this);
	  if (v) 
	  {
	    DWORD flags = MSHCTX_LOCAL;
	    jint size = VARIANT_UserSize(&flags, 0L, v);
	    // allocate a byte array of the right length
	    jbyte* pBuf = new jbyte[size];
	    // clear it out
	    ZeroMemory(pBuf, size);
	    // marshall the Variant into the buffer
	    VARIANT_UserMarshal(&flags, (unsigned char *)pBuf, v);
	    // need to convert the buffer to a java byte ba[]
	    jbyteArray ba = env->NewByteArray(size);
	    env->SetByteArrayRegion(ba, 0, size, pBuf);
	    // and delete the original memory
	    delete [] pBuf;
		return ba;
	  } else {
	    jbyteArray ba = env->NewByteArray(0);
	    return ba;
	  }
  }
  
/**
 * This is the core of the old Load method.  It is broken because the 
 * unmarshalling code doesn't work under 2000/XP.
 * 
 * It probably needs a custom handler.
 **/
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_SerializationReadFromBytes
  (JNIEnv *env, jobject _this, jbyteArray ba){

  VARIANT *v = extractVariant(env, _this);
	if (v){
		// get a buffer from it
		jbyte *pBuf = env->GetByteArrayElements(ba, 0);
		// unmarshall the Variant from the buffer
		DWORD flags = MSHCTX_LOCAL;
		printf("about to unmarshall array elements\n");
		VARIANT_UserUnmarshal(&flags, (unsigned char *)pBuf, v);
		// release the byte array
	    printf("about to release array elements\n");
		env->ReleaseByteArrayElements(ba, pBuf, 0);
	}
  }

/**
 * Converts the data to a Enum Variant object and then returns it as a Dispatch
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_toEnumVariant
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) 
  {
    HRESULT hr;
    if (FAILED(hr = VariantChangeType(v, v, 0, VT_UNKNOWN))) {
      ThrowComFail(env, "VariantChangeType failed", hr);
      return NULL;
    }
    jclass autoClass = env->FindClass("com/jacob/com/EnumVariant");
    jmethodID autoCons =
    env->GetMethodID(autoClass, "<init>", "(J)V");
    // construct an Unknown object to return
    IUnknown *unk = V_UNKNOWN(v);
    IEnumVARIANT *ie;
    hr = unk->QueryInterface(IID_IEnumVARIANT, (void **)&ie);
    if (FAILED(hr)) {
      ThrowComFail(env, "[toEnumVariant]: Object does not implement IEnumVariant", hr);
      return NULL;
    }
    // I am copying the pointer to java
    // SF-1674179 fix EnumVariants memory leak
    // AJ: yes, but the QueryInterface call above already incremented the reference
    //if (ie) ie->AddRef();
    // jacobproject/bug/132
    jobject newAuto = env->NewObject(autoClass, autoCons, (jlong)(uintptr_t)ie);
    return newAuto;
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantNull
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_NULL;
  }
}

JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_cloneIndirect
  (JNIEnv *env, jobject _this)
{
  return NULL;
}


JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantShortRef
  (JNIEnv *env, jobject _this, jshort s)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    short *ps = (short *)CoTaskMemAlloc(sizeof(short));
    *ps = s;
    V_VT(v) = VT_I2|VT_BYREF;
    V_I2REF(v) = ps;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantIntRef
  (JNIEnv *env, jobject _this, jint s)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    long *ps = (long *)CoTaskMemAlloc(sizeof(long));
    *ps = s;
    V_VT(v) = VT_I4|VT_BYREF;
    V_I4REF(v) = ps;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDoubleRef
  (JNIEnv *env, jobject _this, jdouble s)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    double *ps = (double *)CoTaskMemAlloc(sizeof(double));
    *ps = s;
    V_VT(v) = VT_R8|VT_BYREF;
    V_R8REF(v) = ps;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDateRef
  (JNIEnv *env, jobject _this, jdouble s)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    double *ps = (double *)CoTaskMemAlloc(sizeof(double));
    *ps = s;
    V_VT(v) = VT_DATE|VT_BYREF;
    V_DATEREF(v) = ps;
  }
}

// SF 1065533  added unicode support
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantStringRef
  (JNIEnv *env, jobject _this, jstring s)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before

	const jchar *cStr = env->GetStringChars(s,NULL);
	// SF 1314116 
	// DBeck: 2005-09-23: changed CComBSTR c-tor to accept
	//	Unicode string (no terminating NULL) provided by GetStringChars
	const jsize numChars = env->GetStringLength(s);
    //CComBSTR bs(cStr);
	CComBSTR bs( numChars, (LPCOLESTR)cStr ); // SR cast SF 1689061

    BSTR *pbs = (BSTR *)CoTaskMemAlloc(sizeof(BSTR));
    bs.CopyTo(pbs);
    V_VT(v) = VT_BSTR|VT_BYREF;
    V_BSTRREF(v) = pbs;

    env->ReleaseStringChars(s,cStr);  }
}

JNIEXPORT jshort JNICALL Java_com_jacob_com_Variant_getVariantShortRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_I2|VT_BYREF)) {
      return NULL;
    }
    return (jshort)*V_I2REF(v);
  }
  return NULL;
}

JNIEXPORT jint JNICALL Java_com_jacob_com_Variant_getVariantIntRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_I4|VT_BYREF)) {
      return NULL;
    }
    return (jint)*V_I4REF(v);
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantShort
  (JNIEnv *env, jobject _this, jshort s)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_I2;
    V_I2(v) = (short)s;
  }
}

JNIEXPORT jshort JNICALL Java_com_jacob_com_Variant_getVariantShort
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  VT_I2) {
      return NULL;
    }
    return (jshort)V_I2(v);
  }
  return NULL;
}

JNIEXPORT jdouble JNICALL Java_com_jacob_com_Variant_getVariantDoubleRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_R8|VT_BYREF)) {
      return NULL;
    }
    return (jdouble)*V_R8REF(v);
  }
  return NULL;
}

JNIEXPORT jdouble JNICALL Java_com_jacob_com_Variant_getVariantDateRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_DATE|VT_BYREF)) {
      return NULL;
    }
    return (jdouble)*V_DATEREF(v);
  }
  return NULL;
}

JNIEXPORT jstring JNICALL Java_com_jacob_com_Variant_getVariantStringRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_BSTR|VT_BYREF)) {
      return NULL;
    }
    BSTR *bs = V_BSTRREF(v);
    jstring js = env->NewString((jchar*)*bs, SysStringLen(*bs)); // SR cast SF 1689061
    return js;
  }
  return NULL;
}

/**
 * cover for underlying C VariantClear function
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_VariantClear
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v);
  }
}

/**
 * Converts the data to a Dispatch object and then returns it as a Dispatch
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_toVariantDispatch
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    HRESULT hr;
    if (FAILED(hr = VariantChangeType(v, v, 0, VT_DISPATCH))) {
      ThrowComFail(env, "VariantChangeType failed", hr);
      return NULL;
    }
    jclass autoClass = env->FindClass("com/jacob/com/Dispatch");
    jmethodID autoCons =
    env->GetMethodID(autoClass, "<init>", "(J)V");
    // construct a Dispatch object to return
    IDispatch *disp = V_DISPATCH(v);
    // I am copying the pointer to java
    if (disp) disp->AddRef();
    // jacobproject/bug/132
    jobject newAuto = env->NewObject(autoClass, autoCons, (jlong)(uintptr_t)disp);
    return newAuto;
  }
  return NULL;
}

JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_clone
  (JNIEnv *env, jobject _this)
{
  return NULL;
}

/**
 * Returns the value of this int as a Boolea if it is of that type.
 * Otherwise it will return null (no conversion done)
 */

JNIEXPORT jint JNICALL Java_com_jacob_com_Variant_getVariantInt
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  VT_I4) {
      return NULL;
    }
    return (jint)V_I4(v);
  }
  return NULL;
}

/**
 * Returns the value of this Date as a Boolea if it is of that type.
 * Otherwise it will return null (no conversion done)
 */

JNIEXPORT jdouble JNICALL Java_com_jacob_com_Variant_getVariantDate
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  VT_DATE) {
      return NULL;
    }
    return (jdouble)V_DATE(v);
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantInt
  (JNIEnv *env, jobject _this, jint i)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_I4;
    V_I4(v) = (int)i;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDate
  (JNIEnv *env, jobject _this, jdouble date)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_DATE;
    V_DATE(v) = date;
  }
}

/**
 * Returns the value of this Variant as a Boolea if it is of that type.
 * Otherwise it will return null (no conversion done)
 */
JNIEXPORT jboolean JNICALL Java_com_jacob_com_Variant_getVariantBoolean
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_BOOL)) {
      return NULL;
    }
    return (jboolean)V_BOOL(v);
  }
  return NULL;
}

/**
 * Returns the value of this Variant as a Byte if it is of that type.
 * Otherwise it will return null (no conversion done)
 */
JNIEXPORT jbyte JNICALL Java_com_jacob_com_Variant_getVariantByte
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_UI1)) {
      return NULL;
    }
    return (jbyte)V_UI1(v);
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantBoolean
  (JNIEnv *env, jobject _this, jboolean b)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_BOOL;
    V_BOOL(v) = b == JNI_TRUE ? VARIANT_TRUE : VARIANT_FALSE;
  }
  else ThrowComFail(env, "putVariantBoolean failed", -1);
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantByte
  (JNIEnv *env, jobject _this, jbyte b)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_UI1;
    V_UI1(v) = b;
  }
  else ThrowComFail(env, "putVariantByte failed", -1);
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantEmpty
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_EMPTY;
  }
}

/**
 * Sets the variant type to dispatch with no value object
 **/
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantNothing
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_DISPATCH;
  }
}

JNIEXPORT jint JNICALL Java_com_jacob_com_Variant_getVariantError
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  VT_ERROR) {
      return NULL;
    }
    return (jint)V_ERROR(v);
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantError
  (JNIEnv *env, jobject _this, jint i)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_ERROR;
    V_ERROR(v) = (int)i;
  }
}


/**
 * Returns the value of this Variant as a double if it is of that type.
 * Otherwise it will return null (no conversion done)
 */
JNIEXPORT jdouble JNICALL Java_com_jacob_com_Variant_getVariantDouble
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  VT_R8) {
      return NULL;
    }
    return (jdouble)V_R8(v);
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantCurrency
  (JNIEnv *env, jobject _this, jlong cur)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    CY pf;
    pf.int64 = (LONGLONG)cur;
    V_VT(v) = VT_CY;
    V_CY(v) = pf;
  } else ThrowComFail(env, "putVariantCurrency failed", -1);
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantLong
  (JNIEnv *env, jobject _this, jlong longValue)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_I8;
    V_I8(v) = (LONGLONG)longValue;
  } else ThrowComFail(env, "putVariantLong failed", -1);
}

/**
 * Accepts a dispatch object and sets the type to VT_DISPATCH.
 * There is currently no way to pass NULL into this method 
 * to create something like "NOTHING" from VB
 * */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDispatch
  (JNIEnv *env, jobject _this, jobject _that)
{
  VARIANT *v = extractVariant(env, _this);
  IDispatch *disp = extractDispatch(env, _that);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_DISPATCH;
    V_DISPATCH(v) = disp;
    // I am handing the pointer to COM
    // SF 3435567 support null dispatch pointer
    if( disp )
      disp->AddRef();
  } else ThrowComFail(env, "putObject failed", -1);
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDouble
  (JNIEnv *env, jobject _this, jdouble d)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_R8;
    V_R8(v) = (double)d;
  }
}

/**
 * Returns the value of this Variant as a long if it is of that type.
 * Otherwise it will return null (no conversion done)
 */
JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantCurrency
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  VT_CY) {
      return NULL;
    }
    CY cy;
    cy = V_CY(v);
    jlong jl;
    memcpy(&jl, &cy, sizeof(jl)); // was 64. should be sizeof(x) SF 1690420
    return jl;
  }
  return NULL;
}

JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantLong
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  VT_I8) {
      return NULL;
    }
    return (jlong)V_I8(v);
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantFloatRef
  (JNIEnv *env, jobject _this, jfloat val)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    float *pf = (float *)CoTaskMemAlloc(sizeof(float));
    *pf = val;
    V_VT(v) = VT_R4|VT_BYREF;
    V_R4REF(v) = pf;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantCurrencyRef
  (JNIEnv *env, jobject _this, jlong cur)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    CY *pf = (CY *)CoTaskMemAlloc(sizeof(CY));
    memcpy(pf, &cur, sizeof(*pf)); // was 64. should be sizeof(x) SF 1690420
    V_VT(v) = VT_BYREF|VT_CY;
    V_CYREF(v) = pf;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantLongRef
  (JNIEnv *env, jobject _this, jlong longValue)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    LONGLONG *ps = (LONGLONG *)CoTaskMemAlloc(sizeof(LONGLONG));
    *ps = longValue;
    V_VT(v) = VT_I8|VT_BYREF;
    V_I8REF(v) = ps;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantErrorRef
  (JNIEnv *env, jobject _this, jint i)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_ERROR|VT_BYREF;
    V_ERROR(v) = (int)i;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantBooleanRef
  (JNIEnv *env, jobject _this, jboolean b)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    VARIANT_BOOL *br = (VARIANT_BOOL *)CoTaskMemAlloc(sizeof(VARIANT_BOOL));
    *br = b ? VARIANT_TRUE : VARIANT_FALSE;
    V_VT(v) = VT_BOOL|VT_BYREF;
    V_BOOLREF(v) = br;
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantByteRef
  (JNIEnv *env, jobject _this, jbyte b)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    unsigned char *br = (unsigned char *)CoTaskMemAlloc(sizeof(char));
    *br = b;
    V_VT(v) = VT_UI1|VT_BYREF;
    V_UI1REF(v) = br;
  }
}

/**
 * Returns the value of this Variant as a String if it is of that type.
 * Otherwise it will return null (no conversion done)
 */
JNIEXPORT jstring JNICALL Java_com_jacob_com_Variant_getVariantString
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  VT_BSTR) {
      return NULL;
    }
    BSTR bs = V_BSTR(v);
    jstring js = env->NewString((jchar*)bs, SysStringLen(bs));// SR cast SF 1689061
    return js;
  }
  return NULL;
}

/**
 * SF 1065533  added unicode support
 * */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantString
  (JNIEnv *env, jobject _this, jstring s)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_BSTR;

    const jchar *cStr = env->GetStringChars(s,NULL);
    // SF 1314116 
	// DBeck: 2005-09-23: changed CComBSTR c-tor to accept
	//	Unicode string (no terminating NULL) provided by GetStringChars
	const jsize numChars = env->GetStringLength(s);
    //CComBSTR bs(cStr);
	CComBSTR bs( numChars, (LPCOLESTR)cStr ); // SR cast SF 1689061

    V_VT(v) = VT_BSTR;
    V_BSTR(v) = bs.Copy();
    
    env->ReleaseStringChars(s,cStr);
  }
}

JNIEXPORT jfloat JNICALL Java_com_jacob_com_Variant_getVariantFloatRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_R4|VT_BYREF)) {
      return NULL;
    }
    return (jfloat)*V_R4REF(v);
  }
  return NULL;
}

JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantCurrencyRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_CY|VT_BYREF)) {
      return NULL;
    }
    CY *cy;
    cy = V_CYREF(v);
    jlong jl;
    memcpy(&jl, cy, sizeof(jl)); // was 64. should be sizeof(x) SF 1690420
    return jl;
  }
  return NULL;
}

JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantLongRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_I8|VT_BYREF)) {
      return NULL;
    }
    return (jlong)*V_I8REF(v);
  }
  return NULL;
}

JNIEXPORT jint JNICALL Java_com_jacob_com_Variant_getVariantErrorRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_ERROR|VT_BYREF)) {
      return NULL;
    }
    return (jint)V_ERROR(v);
  }
  return NULL;
}

JNIEXPORT jboolean JNICALL Java_com_jacob_com_Variant_getVariantBooleanRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_BOOL|VT_BYREF)) {
      return NULL;
    }
    return (jboolean)*V_BOOLREF(v);
  }
  return NULL;
}


JNIEXPORT jbyte JNICALL Java_com_jacob_com_Variant_getVariantByteRef
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_UI1|VT_BYREF)) {
      return NULL;
    }
    return (jbyte)*V_UI1REF(v);
  }
  return NULL;
}

/**
 * Converts the data to a Safe Array object and then returns it as a Dispatch
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_toVariantSafeArray
  (JNIEnv *env, jobject _this, jboolean deepCopy)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if ((V_VT(v) & VT_ARRAY) == 0) 
    {
      ThrowComFail(env, "Variant not array", -1);
      return NULL;
    }
    // prepare a new sa obj
    jclass saClass = env->FindClass("com/jacob/com/SafeArray");
    jmethodID saCons = env->GetMethodID(saClass, "<init>", "()V");
    // construct an SA to return
    jobject newSA = env->NewObject(saClass, saCons);
    // pass in the deep copy indicator
    setSA(env, newSA, V_ARRAY(v), deepCopy == JNI_TRUE ? 1 : 0);
    return newSA;
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantSafeArrayRef
  (JNIEnv *env, jobject _this, jobject sa)
{
  SAFEARRAY *psa = extractSA(env, sa);
  if (psa) 
  {
    VARIANT *v = extractVariant(env, _this);
    if (v) {
      VARTYPE vt;
      SAFEARRAY **sa = (SAFEARRAY **)CoTaskMemAlloc(sizeof(SAFEARRAY*));
      *sa = psa;
      SafeArrayGetVartype(psa, &vt);
      V_VT(v) = VT_ARRAY | vt | VT_BYREF;
      V_ARRAYREF(v) = sa;
      return;
    }
    ThrowComFail(env, "Can't get variant pointer", -1);
    return;
  }
  ThrowComFail(env, "Can't get sa pointer", -1);
  return;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantSafeArray
  (JNIEnv *env, jobject _this, jobject sa)
{
  SAFEARRAY *psa = extractSA(env, sa);
  if (psa) 
  {
    VARIANT *v = extractVariant(env, _this);
    if (v) {
      VARTYPE vt;
      SafeArrayGetVartype(psa, &vt);
      V_VT(v) = VT_ARRAY | vt;
      V_ARRAY(v) = copySA(psa);
      return;
    }
    ThrowComFail(env, "Can't get variant pointer", -1);
    return;
  }
  ThrowComFail(env, "Can't get sa pointer", -1);
  return;
}

/**
 * sets the type to VT_ERROR and the error message to DISP_E_PARAMNOTFOIUND
 * */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantNoParam
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
  	// SF 3377279 clear variable to fix leak
    VariantClear(v);
    V_VT(v) = VT_ERROR;
    V_ERROR(v) = DISP_E_PARAMNOTFOUND;
  }
}

/**
 * Returns the value of this Variant as a Float if it is of that type.
 * Otherwise it will return null (no conversion done)
 */
JNIEXPORT jfloat JNICALL Java_com_jacob_com_Variant_getVariantFloat
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    if (V_VT(v) !=  (VT_R4)) {
      return NULL;
    }
    return (jfloat)V_R4(v);
  }
  return NULL;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantFloat
  (JNIEnv *env, jobject _this, jfloat val)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantClear(v); // whatever was there before
    V_VT(v) = VT_R4;
    V_R4(v) = val;
  }
}

/**
 * changes the type of the underlying variant data
 * */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_changeVariantType
  (JNIEnv *env, jobject _this, jshort t)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    VariantChangeType(v, v, 0, t);
  }
}

/**
 * returns the variant type if it is set, otherwise 
 * returns null
 * */
JNIEXPORT jshort JNICALL Java_com_jacob_com_Variant_getVariantType
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (v) {
    return (jshort)V_VT(v);
  }
  return NULL;
}

// removed Java_com_jacob_com_Variant_putSafeArrayRefHelper

/**
 * this is a big cover method that returns TRUE if 
 * the variant type is 
 * 	VT_EMPTY, VT_NULL, VT_ERROR or VT_DISPATCH with no dispatch object
 * */
JNIEXPORT jboolean JNICALL Java_com_jacob_com_Variant_isVariantConsideredNull
  (JNIEnv *env, jobject _this)
{
  VARIANT *v = extractVariant(env, _this);
  if (!v) return JNI_TRUE;
  if ((V_VT(v) & VT_ARRAY)) 
  {
    // is it a null safearray
    // prior to 4 Dec 2005 the squiggle brackets were missing 
    // so this did the wrong thing for the else statement
    if ((V_VT(v) & VT_BYREF)) {
       if (!V_ARRAYREF(v)) return JNI_TRUE;
    } else {
       if (!V_ARRAY(v)) return JNI_TRUE;
    }
  }
  switch (V_VT(v))
  {
    case VT_EMPTY:
    case VT_NULL:
    case VT_ERROR:
            return JNI_TRUE;
    // is it a null dispatch (Nothing in VB)
    case VT_DISPATCH:
            if (!V_DISPATCH(v)) return JNI_TRUE;
  }
  return JNI_FALSE;
}

/**
 * Puts a variant into a the Variant as its data and sets the type 
 * to VT_VARIANT|VT_BYREF.
 * Added 1.12 pre 6
 * 
 * */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantVariant
  (JNIEnv *env, jobject _this, jobject var)
{

  VARIANT *vVar = extractVariant(env, var);
  VARIANT *v = extractVariant(env, _this);

  if (v) {
    VariantClear(v); // whatever was there before

    V_VT(v) = VT_VARIANT|VT_BYREF;
    V_VARIANTREF(v) = vVar;
  }

}

/**
 * retrieves the enclosed variant when they are of type VT_VARIANT
 * Added 1.12 pre 6
 * 
 * */
JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantVariant
(JNIEnv *env, jobject _this) 
{

  VARIANT *v = extractVariant(env, _this);
  if (v) {

    if (V_VT(v) != (VT_VARIANT|VT_BYREF)) {
      return NULL;
    }

	VARIANT *refVar = V_VARIANTREF(v);

	// we could have made a copy of refV here but we aren't every going to free 
	// it outside of the scope of the enclosing context so we will just used the
	// enclosed.  This relies on the java layer to zero out its ref to this
	// enclosed variant before the gc can come along and free the memory out from
	// under this enclosing variant.
	return (jlong)refVar;
  }

  return NULL;
}

 /** 
  * puts a VT_DECIMAL by reference
  * Added 1.13M4
  * */
 JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDecRef
    (JNIEnv *env, jobject _this, jint signum, jbyte scale, jint lo, jint mid, jint hi)
  {
    VARIANT *v = extractVariant(env, _this);
    if (v) {
      VariantClear(v); // whatever was there before
      DECIMAL *pd = (DECIMAL *)CoTaskMemAlloc(sizeof(DECIMAL));
      pd->scale = scale;
      if (signum == 1 || signum == 0){
    	  pd->sign = 0;
      } else {
    	  pd->sign = 0x80;
      }
      pd->Hi32 = hi;
      pd->Mid32 = mid;
      pd->Lo32 = lo;
      V_VT(v) = VT_DECIMAL | VT_BYREF;
      V_DECIMALREF(v) = pd;
    }
  }


 /**
  * puts a VT_DECIMAL
  * Added 1.13M4
  * */
 JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDec
    (JNIEnv *env, jobject _this, jint signum, jbyte scale, jint lo, jint mid, jint hi)
  {
    VARIANT *v = extractVariant(env, _this);
    DECIMAL *d;
    if (v) {
      VariantClear(v); // whatever was there before
      d = (DECIMAL*)v;
      d->scale = scale;
      if (signum == 1 || signum == 0){
    	  d->sign = 0;
      } else {
    	  d->sign = 0x80;
      }
      d->Hi32 = hi;
      d->Mid32 = mid;
      d->Lo32 = lo;
      V_VT(v) = VT_DECIMAL;
    }
  }

/**
 * utility method used by the getVariantXXX() methods to convert VT_DECIMAL to BigDecimal
 * */
jobject extractDecimal
     (JNIEnv *env, DECIMAL* d)
  {
     jclass bigIntegerClass;
     jclass bigDecimalClass;
     jobject integer;
     jmethodID bigIntegerConstructor;
     jmethodID bigDecimalConstructor;
     jbyteArray bArray;
     jobject result = NULL;
     jbyte* buffer; 

     bigIntegerClass = env->FindClass("java/math/BigInteger");
     if (bigIntegerClass == NULL)
         return NULL;    
      bigDecimalClass = env->FindClass("java/math/BigDecimal");
      if (bigDecimalClass == NULL) {
         env->DeleteLocalRef(bigIntegerClass);
         return NULL;
      }
          
      bigIntegerConstructor = env->GetMethodID(bigIntegerClass, "<init>", "(I[B)V");
      if (bigIntegerConstructor == NULL) {
         env->DeleteLocalRef(bigIntegerClass);
         env->DeleteLocalRef(bigDecimalClass);
         return NULL;
      }
      bigDecimalConstructor = env->GetMethodID(bigDecimalClass, "<init>", "(Ljava/math/BigInteger;I)V");
      if (bigIntegerConstructor == NULL) {
         env->DeleteLocalRef(bigIntegerClass);
         env->DeleteLocalRef(bigDecimalClass);
         return NULL;
      }
      bArray = env->NewByteArray(12);
      if (bArray == NULL) {
         env->DeleteLocalRef(bigIntegerClass);
         env->DeleteLocalRef(bigDecimalClass);
         return NULL;
      }
      /* Unfortunately the byte ordering is completely wrong, so we remap it into buffer */
      buffer = (jbyte*)malloc(12);
      buffer[11] = (byte)(d->Lo32 & 255);
      buffer[10] = (byte)((d->Lo32 >> 8) & 255);
      buffer[9] =  (byte)((d->Lo32 >> 16) & 255);
      buffer[8] =  (byte)((d->Lo32 >> 24) & 255);
      buffer[7] =  (byte)((d->Mid32) & 255);
      buffer[6] =  (byte)((d->Mid32 >> 8) & 255);
      buffer[5] =  (byte)((d->Mid32 >> 16) & 255);
      buffer[4] =  (byte)((d->Mid32 >> 24) & 255);
      buffer[3] =  (byte)((d->Hi32) & 255);
      buffer[2] =  (byte)((d->Hi32 >> 8) & 255);
      buffer[1] =  (byte)((d->Hi32 >> 16) & 255);
      buffer[0] =  (byte)((d->Hi32 >> 24) & 255);
      /* Load buffer into the actual array */
      env->SetByteArrayRegion(bArray, 0, 12, buffer);
      /* then clean up the C array */
      free(buffer);

      /* instantiate the BigInteger */
      integer = env->NewObject(bigIntegerClass, bigIntegerConstructor, d->sign == 0x80?-1:1, bArray);

      result = env->NewObject(bigDecimalClass, bigDecimalConstructor, integer, (jint)(d->scale));    
      
      /* Clean up the Java global references */
      env->DeleteLocalRef(bArray);
      env->DeleteLocalRef(integer);
      env->DeleteLocalRef(bigIntegerClass);      
      return result;
  }

/**
  * gets a VT_DECIMAL by ref as a BigDecimal
  * Added 1.13M4
  * */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_getVariantDecRef
    (JNIEnv *env, jobject _this)
  {
    VARIANT *v = extractVariant(env, _this);
    if (v) {
      if (V_VT(v) !=  (VT_DECIMAL|VT_BYREF)) {
        return NULL;
      }
      return extractDecimal(env, v->pdecVal);
    }
    return NULL;
  }

/**
  * gets a VT_DECIMAL as a BigDecimal
  * Added 1.13M4
  * */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_getVariantDec
    (JNIEnv *env, jobject _this)
  {
    VARIANT *v = extractVariant(env, _this); 
    if (v) {
      if (V_VT(v) !=  VT_DECIMAL) {
        return NULL;
      }
      return extractDecimal(env, (DECIMAL*)v);
    }
    return NULL;
  }

}
