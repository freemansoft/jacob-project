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
#include "ComThread.h"
// Win32 support for Ole Automation
#include <wchar.h>
#include <string.h>
#include <atlbase.h>
#include <oleauto.h>
#include <olectl.h>
#include "util.h"

extern "C" 
{

// extract a IStream from a jobject
IStream *extractStream(JNIEnv *env, jobject arg)
{
  jclass argClass = env->GetObjectClass(arg);
  jfieldID ajf = env->GetFieldID( argClass, "m_pStream", "I");
  jint anum = env->GetIntField(arg, ajf);
  IStream *v = (IStream *)anum;
  return v;
}

JNIEXPORT void JNICALL Java_com_jacob_com_DispatchProxy_MarshalIntoStream
  (JNIEnv *env, jobject _this, jobject disp)
{
  IDispatch *pIDispatch = extractDispatch(env, disp);
  if (!pIDispatch) return;
  IStream *ps; // this is the stream we will marshall into
  HRESULT hr = CoMarshalInterThreadInterfaceInStream(
                 IID_IDispatch, pIDispatch, &ps);
  if (!SUCCEEDED(hr))
  {
    ThrowComFail(env, "Could not Marshal Dispatch into IStream", hr);
    return;
  }
  // store the stream pointer on the object
  jclass argClass = env->GetObjectClass(_this);
  jfieldID ajf = env->GetFieldID( argClass, "m_pStream", "I");
  env->SetIntField(_this, ajf, (jint)ps);
}

JNIEXPORT jobject JNICALL Java_com_jacob_com_DispatchProxy_MarshalFromStream
  (JNIEnv *env, jobject _this)
{
  IStream *ps = extractStream(env, _this);
  if (!ps) 
  {
    ThrowComFail(env, "Could not get IStream from DispatchProxy", -1);
    return NULL;
  }
  IDispatch *pD;
  HRESULT hr = CoGetInterfaceAndReleaseStream(ps, IID_IDispatch, (void **)&pD);
  // zero out the stream pointer on the object
  // since the stream can only be read once
  jclass argClass = env->GetObjectClass(_this);
  jfieldID ajf = env->GetFieldID( argClass, "m_pStream", "I");
  env->SetIntField(_this, ajf, (unsigned int)0);

  if (!SUCCEEDED(hr))
  {
    ThrowComFail(env, "Could not Marshal Dispatch from IStream", hr);
    return NULL;
  }
  jclass autoClass = env->FindClass("com/jacob/com/Dispatch");
  jmethodID autoCons = env->GetMethodID(autoClass, "<init>", "(I)V");
  // construct a Dispatch object to return
  // I am copying the pointer to java
  if (pD) pD->AddRef();
  jobject newAuto = env->NewObject(autoClass, autoCons, pD);
  return newAuto;
}

JNIEXPORT void JNICALL Java_com_jacob_com_DispatchProxy_release
  (JNIEnv *env, jobject _this)
{
  IStream *ps =  extractStream(env, _this);
  if (ps) {
    ps->Release();
		jclass argClass = env->GetObjectClass(_this);
    jfieldID ajf = env->GetFieldID( argClass, "m_pStream", "I");
    env->SetIntField(_this, ajf, (unsigned int)0);
  }
}

}
