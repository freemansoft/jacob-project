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
#include "EventProxy.h"

// hook myself up as a listener for delegate
EventProxy::EventProxy(JNIEnv *env, jobject aSinkObj, 
        CComPtr<IConnectionPoint> pConn,
        IID eid, CComBSTR mName[], DISPID mID[], int mNum) :
   m_cRef(0), pCP(pConn), 
   eventIID(eid), MethNum(mNum), MethName(mName), 
   MethID(mID), JMethID(NULL), javaSinkClass(NULL)
{
  javaSinkObj = env->NewGlobalRef(aSinkObj);
	// we need this to attach to the event invocation thread
  env->GetJavaVM(&jvm);
  AddRef();
  HRESULT hr = pCP->Advise(this, &dwEventCookie);
  if (SUCCEEDED(hr)) {
    // create a mapping from the DISPID's to jmethodID's by using
    // the method names I extracted from the classinfo
    JMethID = new jmethodID[MethNum];
    javaSinkClass = env->GetObjectClass(javaSinkObj);
    const char *method;
    for(int i=0;i<MethNum;i++) 
    {
      // look for a method with the specified name, that takes an
      // array of variants and return void
      USES_CONVERSION;
      method = W2A((OLECHAR *)MethName[i]);
      JMethID[i] = env->GetMethodID(javaSinkClass, method, "([Lcom/jacob/com/Variant;)V");
      // need to clear exceptions because GetMethodID above may fail
      // if the user didn't implement all the methods
      env->ExceptionClear();
    }
  } else {
    ThrowComFail(env, "Advise failed", hr);
  }
}

// unhook myself up as a listener and get rid of delegate
EventProxy::~EventProxy()
{
  pCP->Unadvise(dwEventCookie);
	JNIEnv *env;
  // attach to the current running thread
  jvm->AttachCurrentThread((void **)&env, jvm);

  env->DeleteGlobalRef(javaSinkObj);
  if (MethNum) {
    delete [] MethName;
    delete [] MethID;
    if (JMethID) delete [] JMethID;
  }
	// detach from thread
  jvm->DetachCurrentThread();
}

// I only support the eventIID interface which was passed in
// by the DispatchEvent wrapper who looked it up as the
// source object's default source interface
STDMETHODIMP EventProxy::QueryInterface(REFIID rid, void **ppv)
{
  if (rid == IID_IUnknown || rid == eventIID || rid == IID_IDispatch) 
  {
    *ppv = this;
    AddRef();
    return S_OK;
  }
  return E_NOINTERFACE;
}

// This should never get called - the event source fires events
// by dispid's, not by name
STDMETHODIMP EventProxy::GetIDsOfNames(REFIID riid,
    OLECHAR **rgszNames, UINT cNames, LCID lcid, DISPID *rgDispID)
{
  return E_UNEXPECTED;
}

// The actual callback from the connection point arrives here
STDMETHODIMP EventProxy::Invoke(DISPID dispID, REFIID riid,
    LCID lcid, unsigned short wFlags, DISPPARAMS *pDispParams,
    VARIANT *pVarResult, EXCEPINFO *pExcepInfo, UINT *puArgErr)
{
  HRESULT     hr;
  jmethodID meth = 0;
  JNIEnv      *env = NULL;


  // map dispID to jmethodID
  for(int i=0;i<MethNum;i++) 
  {
    if (MethID[i] == dispID) { meth = JMethID[i]; break; }
  }

  if (!meth) 
  {
    // user did not implement this method
    return S_OK;
  }

  if (DISPATCH_METHOD & wFlags) 
  {
    // attach to the current running thread
    jvm->AttachCurrentThread((void**)&env, jvm);

    // how many params
    int num = pDispParams->cArgs;
    // get variant class
    jclass vClass = env->FindClass("com/jacob/com/Variant");
    // and the constructor
    jmethodID vCons = env->GetMethodID(vClass, "<init>", "()V");
    // make an array of them
    jobjectArray varr = env->NewObjectArray(num, vClass, 0);
    int i,j;
    for(i=num-1,j=0;i>=0;i--,j++) 
    {
      // construct a java variant holder
      jobject arg = env->NewObject(vClass, vCons);
      // get the empty variant from it
      VARIANT *va = extractVariant(env, arg);
      // copy the value
      VariantCopy(va, &pDispParams->rgvarg[i]);
      // put it in the array
      env->SetObjectArrayElement(varr, j, arg);
			env->DeleteLocalRef(arg);
    }
    // call the method
    env->CallVoidMethod(javaSinkObj, meth, varr); 

		// detach from thread
    jvm->DetachCurrentThread();
    return S_OK;
  }
  return E_NOINTERFACE;
}
