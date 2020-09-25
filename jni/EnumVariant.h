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
#ifndef _Included_EnumVariant
#define _Included_EnumVariant

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_jacob_com_EnumVariant
 * Method:    Next
 * Signature: ([Lcom/jacob/com/Variant;)I
 */
JNIEXPORT jint JNICALL Java_com_jacob_com_EnumVariant_Next
  (JNIEnv *, jobject, jobjectArray);

/*
 * Class:     com_jacob_com_EnumVariant
 * Method:    Release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_EnumVariant_release
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_EnumVariant
 * Method:    Reset
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_EnumVariant_Reset
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_EnumVariant
 * Method:    Skip
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_EnumVariant_Skip
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
