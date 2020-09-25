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
/* Header for class Dispatch */

#ifndef _Included_Dispatch
#define _Included_Dispatch
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_jacob_com_Dispatch
 * Method:    QueryInterface
 * Signature: (Ljava/lang/String;)Lcom/jacob/com/Dispatch;
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Dispatch_QueryInterface
  (JNIEnv *, jobject, jstring);

/*
 * Class:     Dispatch
 * Method:    createInstance
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_createInstanceNative
  (JNIEnv *, jobject, jstring);

/*
 * Class:     Dispatch
 * Method:    getActiveInstance
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_getActiveInstanceNative
  (JNIEnv *, jobject, jstring);

/*
 * Class:     Dispatch
 * Method:    coCreateInstance
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_coCreateInstanceNative
  (JNIEnv *, jobject, jstring);

/*
 * Class:     Dispatch
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_release
  (JNIEnv *, jobject);

/*
 * Class:     Dispatch
 * Method:    getIDsOfNames
 * Signature: (Ljava/lang/Object;I[Ljava/lang/String;)[I
 */
JNIEXPORT jintArray JNICALL Java_com_jacob_com_Dispatch_getIDsOfNames
  (JNIEnv *, jclass, jobject, jint, jobjectArray);

/*
 * Class:     Dispatch
 * Method:    invokev
 * Signature: (Ljava/lang/Object;Ljava/lang/String;III[LVariant;[I)LVariant;
 */
JNIEXPORT jobject JNICALL Java_com_jacob_com_Dispatch_invokev
  (JNIEnv *, jclass, jobject, jstring, jint, jint, jint, jobjectArray, jintArray);

/*
 * Class:     Dispatch
 * Method:    wait
 * Signature: (Ljava/lang/Object;I;)I
 */
JNIEXPORT jint JNICALL Java_com_jacob_com_Dispatch_hasExited
	(JNIEnv *, jclass, jobject, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
