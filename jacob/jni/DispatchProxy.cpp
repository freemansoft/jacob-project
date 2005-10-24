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
