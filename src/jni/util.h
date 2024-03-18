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
#include <objbase.h>
extern "C" {
  VARIANT *extractVariant(JNIEnv *env, jobject arg);
  void ThrowComFail(JNIEnv *env, const char* desc, jint hr);
  void ThrowComFailUnicode(JNIEnv *env, const wchar_t* desc, jint hr);
  IDispatch *extractDispatch(JNIEnv *env, jobject arg);
  SAFEARRAY *extractSA(JNIEnv *env, jobject arg);
  void setSA(JNIEnv *env, jobject arg, SAFEARRAY *sa, int copy);
  SAFEARRAY *copySA(SAFEARRAY *psa);
}
