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
#include "stdafx.h"
#include <objbase.h>
#include "Dispatch.h"
// Win32 support for Ole Automation
#include <wchar.h>
#include <string.h>
#include <atlbase.h>
#include <oleauto.h>
#include <olectl.h>
#include "util.h"

/**
 * An implementation of IEnumVariant based on code submitted by
 * Thomas Hallgren (mailto:Thomas.Hallgren@eoncompany.com)
 */
extern "C"
{

// extract a IDispatch from a jobject
IEnumVARIANT* extractEnumVariant(JNIEnv* env, jobject arg)
{
  jfieldID FID_pIEnumVARIANT = 0;
  jclass clazz = env->GetObjectClass(arg);
  FID_pIEnumVARIANT = env->GetFieldID(clazz, "m_pIEnumVARIANT", "I");
  return (IEnumVARIANT*)env->GetIntField(arg, FID_pIEnumVARIANT);
}

JNIEXPORT jint JNICALL
Java_com_jacob_com_EnumVariant_Next(JNIEnv* env, jobject _this, jobjectArray vars)
{
  IEnumVARIANT* self = extractEnumVariant(env, _this);
	printf("self=%x\n", self);
  if(self == NULL)
    return 0;

  ULONG count = (ULONG)env->GetArrayLength(vars);
  if(count == 0)
    return 0;

  VARIANT* sink = (VARIANT*)CoTaskMemAlloc(count * sizeof(VARIANT));
  ULONG fetchCount = 0;

  HRESULT hr = self->Next(count, sink, &fetchCount);
  if(FAILED(hr))
  {
    CoTaskMemFree(sink);
    ThrowComFail(env, "IEnumVARIANT::Next", hr);
    return 0;
  }

  // prepare a new return value array
  //
  jclass    clazz = env->FindClass("com/jacob/com/Variant");
  jmethodID ctor  = env->GetMethodID(clazz, "<init>", "()V");

  for(ULONG idx = 0; idx < fetchCount; ++idx)
  {
    // construct a variant to return
    //
    jobject newVariant = env->NewObject(clazz, ctor);
    VARIANT* v = extractVariant(env, newVariant);
    VariantCopy(v, sink + idx);
    env->SetObjectArrayElement(vars, idx, newVariant);
    env->DeleteLocalRef(newVariant);
  }
  CoTaskMemFree(sink);
  return (jint)fetchCount;
}

JNIEXPORT void JNICALL
Java_com_jacob_com_EnumVariant_release(JNIEnv* env, jobject _this)
{
  IEnumVARIANT* self = extractEnumVariant(env, _this);
  if(self != NULL)
  {
    self->Release();
    jfieldID FID_pIEnumVARIANT = 0;
    jclass clazz = env->GetObjectClass(_this);
    FID_pIEnumVARIANT = env->GetFieldID(clazz, "m_pIEnumVARIANT", "I");
    env->SetIntField(_this, FID_pIEnumVARIANT, (unsigned int)0);
  }
}

JNIEXPORT void JNICALL
Java_com_jacob_com_EnumVariant_Reset(JNIEnv* env, jobject _this)
{
  IEnumVARIANT* self = extractEnumVariant(env, _this);
  if(self == NULL)
    return;

  HRESULT hr = self->Reset();
  if(FAILED(hr))
    ThrowComFail(env, "IEnumVARIANT::Reset", hr);
}

JNIEXPORT void JNICALL
Java_com_jacob_com_EnumVariant_Skip(JNIEnv* env, jobject _this, jint count)
{
  IEnumVARIANT* self = extractEnumVariant(env, _this);
  if(self == NULL)
    return;

  HRESULT hr = self->Skip((ULONG)count);
  if(FAILED(hr))
    ThrowComFail(env, "IEnumVARIANT::Skip", hr);
}

}
