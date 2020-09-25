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
#include <jni.h>
/* Header for class com_jacob_com_Variant */

#ifndef _Included_com_jacob_com_Variant
#define _Included_com_jacob_com_Variant
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_jacob_com_Variant
 * Method:    toEnumVariant
 * Signature: ()Lcom/jacob/com/EnumVariant;
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_toEnumVariant
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantNull
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantNull
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    cloneIndirect
 * Signature: ()Lcom_jacob_com_Variant;
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_cloneIndirect
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantShortRef
 * Signature: (S)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantShortRef
  (JNIEnv *, jobject, jshort);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantIntRef
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantIntRef
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantDoubleRef
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDoubleRef
  (JNIEnv *, jobject, jdouble);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantDateRef
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDateRef
  (JNIEnv *, jobject, jdouble);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantStringRef
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantStringRef
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantShortRef
 * Signature: ()S
 */
JNIEXPORT jshort JNICALL Java_com_jacob_com_Variant_getVariantShortRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantIntRef
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_jacob_com_Variant_getVariantIntRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantShort
 * Signature: (S)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantShort
  (JNIEnv *, jobject, jshort);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantShort
 * Signature: ()S
 */
JNIEXPORT jshort JNICALL Java_com_jacob_com_Variant_getVariantShort
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantDoubleRef
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_jacob_com_Variant_getVariantDoubleRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantDateRef
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_jacob_com_Variant_getVariantDateRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getStringRef
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_jacob_com_Variant_getVariantStringRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    com_jacob_com_VariantClear
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_VariantClear
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    toDispatch
 * Signature: ()LDispatch;
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_toVariantDispatch
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    clone
 * Signature: ()Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_clone
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantInt
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_jacob_com_Variant_getVariantInt
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantDate
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_jacob_com_Variant_getVariantDate
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantInt
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantInt
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantDate
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDate
  (JNIEnv *, jobject, jdouble);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantBoolean
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_jacob_com_Variant_getVariantBoolean
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantByte
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_com_jacob_com_Variant_getVariantByte
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantBoolean
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantBoolean
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantByte
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantByte
  (JNIEnv *, jobject, jbyte);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantEmpty
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantEmpty
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantNothing
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantNothing
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantError
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_jacob_com_Variant_getVariantError
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantError
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantError
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantDouble
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_jacob_com_Variant_getVariantDouble
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantCurrency
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantCurrency
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantLong
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantLong
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantDispatch
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDispatch
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantDouble
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDouble
  (JNIEnv *, jobject, jdouble);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantCurrency
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantCurrency
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantLong
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantLong
  (JNIEnv *, jobject);
/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantFloatRef
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantFloatRef
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantCurrencyRef
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantCurrencyRef
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantLongRef
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantLongRef
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantErrorRef
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantErrorRef
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantBooleanRef
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantBooleanRef
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putObjectRef
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putObjectRef
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantByteRef
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantByteRef
  (JNIEnv *, jobject, jbyte);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_jacob_com_Variant_getVariantString
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantString
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantString
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantFloatRef
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_com_jacob_com_Variant_getVariantFloatRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantCurrencyRef
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantCurrencyRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantLongRef
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantLongRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantErrorRef
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_jacob_com_Variant_getVariantErrorRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantBooleanRef
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_jacob_com_Variant_getVariantBooleanRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantByteRef
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_com_jacob_com_Variant_getVariantByteRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    toVariantSafeArray
 * Signature: (Z)Lcom/jacob/com/SafeArray;
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_toVariantSafeArray
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantSafeArrayRef
 * Signature: (LSafeArray;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantSafeArrayRef
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantSafeArray
 * Signature: (LSafeArray;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantSafeArray
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantNoParam
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantNoParam
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantFloat
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_com_jacob_com_Variant_getVariantFloat
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantFloat
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantFloat
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     com_jacob_com_Variant
 * Method:    changeVariantType
 * Signature: (S)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_changeVariantType
  (JNIEnv *, jobject, jshort);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantType
 * Signature: ()S
 */
JNIEXPORT jshort JNICALL Java_com_jacob_com_Variant_getVariantType
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_release
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_init
  (JNIEnv *, jobject);

JNIEXPORT jbyteArray JNICALL Java_com_jacob_com_Variant_SerializationWriteToBytes
  (JNIEnv *, jobject);

JNIEXPORT void JNICALL Java_com_jacob_com_Variant_SerializationReadFromBytes
  (JNIEnv *, jobject, jbyteArray);
  
/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantVariant
 * Signature: (Lcom/jacob/com/Variant;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantVariant
  (JNIEnv *, jobject, jobject);


/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantVariant
 * Signature: ()I
 */
JNIEXPORT jlong JNICALL Java_com_jacob_com_Variant_getVariantVariant
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantDecRef
 * Signature: (Ljava.math.BigDecimal;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDecRef
  (JNIEnv *env, jobject _this, jint signum, jbyte scale, jint lo, jint mid, jint hi);


/*
 * Class:     com_jacob_com_Variant
 * Method:    putVariantDec
 * Signature: (Ljava.math.BigDecimal;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Variant_putVariantDec
  (JNIEnv *env, jobject _this, jint signum, jbyte scale, jint lo, jint mid, jint hi);


/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantDecRef
 * Signature: ()Ljava.math.BigDecimal
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_getVariantDecRef
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    getVariantDec
 * Signature: ()Ljava.math.BigDecimal
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Variant_getVariantDec
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_Variant
 * Method:    isVariantConsideredNull
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_jacob_com_Variant_isVariantConsideredNull
  (JNIEnv *, jobject);

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
void zeroVariant (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
