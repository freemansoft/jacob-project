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
#include "Variant.h"

// hook myself up as a listener for delegate
EventProxy::EventProxy(JNIEnv *env, 
		jobject aSinkObj, 
		jobject aVariantObj,
        CComPtr<IConnectionPoint> pConn,
        IID eid, 
        CComBSTR mName[], 
        DISPID mID[], 
        int mNum) :
   // initialize some variables
   m_cRef(0), pCP(pConn), 
   eventIID(eid), MethNum(mNum), MethName(mName), 
   MethID(mID), JMethID(NULL), 
   javaSinkClass(NULL)
{
	// don't really need the variant object but we keep a reference 
	// anyway
  javaSinkObj = env->NewGlobalRef(aSinkObj);
	if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
  javaVariantObj = env->NewGlobalRef(aVariantObj);
	if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}

	// we need this to attach to the event invocation thread
  env->GetJavaVM(&jvm);
	if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
  AddRef();
  HRESULT hr = pCP->Advise(this, &dwEventCookie);
  if (SUCCEEDED(hr)) {

    // create a mapping from the DISPID's to jmethodID's by using
    // the method names I extracted from the classinfo
    JMethID = new jmethodID[MethNum];

    javaSinkClass = env->GetObjectClass(javaSinkObj);
		if (javaSinkClass == NULL){ printf("can't figure out java sink class"); }
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}

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
	if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
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
  //Visual C++ 6.0 recognized this as an unused variable
  //HRESULT     hr;
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


    // get variant class
	jclass vClass = NULL;
	// do this in a JACOB 1.8 backwards compatable way
	// this succeeds if the class was loaded from the bootstrap class loader
    vClass = env->FindClass("com/jacob/com/Variant");
    // this is guarenteed to work so there really isn't any need for the line above
    // but I don't want to bust anything so we leave it in
    // the following code exists to support launchers like JWS where jacob isn't 
    // in the system classloader so we wouldn't be able to create a variant class
	if (vClass == NULL){
		// there will be a stored up exception if FindClass failed so lets clear it
		if (env->ExceptionOccurred()) { env->ExceptionClear(); }
		//printf("using variant class passed in via constructor\n");
	    jclass javaVariantClass = env->GetObjectClass(javaVariantObj);
			if (javaVariantClass == NULL){ printf("can't figure out java Variant class"); }
			if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
		vClass = javaVariantClass;
		if (vClass == NULL){
			printf("This application is probably running from a launcher where system class loader knows not Jacob\n");
			printf("And for some reason we couldn't find the variant class from the passed in variant\n");
		}
	}

    // how many params
    int num = pDispParams->cArgs;
    // and the constructor
    jmethodID vCons = env->GetMethodID(vClass, "<init>", "()V");
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
    // make an array of them
    jobjectArray varr = env->NewObjectArray(num, vClass, 0);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
    int i,j;
    for(i=num-1,j=0;i>=0;i--,j++) 
    {
      // construct a java variant holder
      jobject arg = env->NewObject(vClass, vCons);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
      // get the empty variant from it
      VARIANT *va = extractVariant(env, arg);
      // copy the value
      VariantCopy(va, &pDispParams->rgvarg[i]);
      // put it in the array
      env->SetObjectArrayElement(varr, j, arg);
			env->DeleteLocalRef(arg);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
    }
    // call the method
    env->CallVoidMethod(javaSinkObj, meth, varr); 
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}


    // Begin code from Jiffie team that copies parameters back from java to COM
    for(i=num-1,j=0;i>=0;i--,j++) 
    {
	  jobject arg = env->GetObjectArrayElement(varr, j);
      VARIANT *java = extractVariant(env, arg);
	  VARIANT *com = &pDispParams->rgvarg[i];

	  switch (com->vt)
	  {	  
		case VT_DISPATCH:
		{
			switch (java->vt)
			{
				case VT_DISPATCH:
				{
					V_DISPATCH(com) = V_DISPATCH(java);
					break;
				}

				case VT_DISPATCH | VT_BYREF:
				{
					V_DISPATCH(com) = *V_DISPATCHREF(java);
					break;
				}
			}
			break;
		}

		case VT_DISPATCH | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_DISPATCH:
				{
					*V_DISPATCHREF(com) = V_DISPATCH(java);
					break;
				}

				case VT_DISPATCH | VT_BYREF:
				{
					*V_DISPATCHREF(com) = *V_DISPATCHREF(java);
					break;
				}
			}
			break;
		}

		case VT_BOOL:
		{
			switch (java->vt)
			{
				case VT_BOOL:
				{
					V_BOOL(com) = V_BOOL(java);
					break;
				}

				case VT_BOOL | VT_BYREF:
				{
					V_BOOL(com) = *V_BOOLREF(java);
					break;
				}
			}
			break;
		}

		case VT_BOOL | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_BOOL:
				{
					*V_BOOLREF(com) = V_BOOL(java);
					break;
				}

				case VT_BOOL | VT_BYREF:
				{
					*V_BOOLREF(com) = *V_BOOLREF(java);
					break;
				}
			}
			break;
		}

		case VT_UI1:
		{
			switch (java->vt)
			{
				case VT_UI1:
				{
					V_UI1(com) = V_UI1(java);
					break;
				}

				case VT_UI1 | VT_BYREF:
				{
					V_UI1(com) = *V_UI1REF(java);
					break;
				}
			}
			break;
		}

		case VT_UI1 | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_UI1:
				{
					*V_UI1REF(com) = V_UI1(java);
					break;
				}

				case VT_UI1 | VT_BYREF:
				{
					*V_UI1REF(com) = *V_UI1REF(java);
					break;
				}
			}
			break;
		}


		case VT_I2:
		{
			switch (java->vt)
			{
				case VT_I2:
				{
					V_I2(com) = V_I2(java);
					break;
				}

				case VT_I2 | VT_BYREF:
				{
					V_I2(com) = *V_I2REF(java);
					break;
				}
			}
			break;
		}

		case VT_I2 | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_I2:
				{
					*V_I2REF(com) = V_I2(java);
					break;
				}

				case VT_I2 | VT_BYREF:
				{
					*V_I2REF(com) = *V_I2REF(java);
					break;
				}
			}
			break;
		}

		case VT_I4:
		{
			switch (java->vt)
			{
				case VT_I4:
				{
					V_I4(com) = V_I4(java);
					break;
				}

				case VT_I4 | VT_BYREF:
				{
					V_I4(com) = *V_I4REF(java);
					break;
				}
			}
			break;
		}

		case VT_I4 | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_I4:
				{
					*V_I4REF(com) = V_I4(java);
					break;
				}

				case VT_I4 | VT_BYREF:
				{
					*V_I4REF(com) = *V_I4REF(java);
					break;
				}
			}
			break;
		}

		case VT_R4:
		{
			switch (java->vt)
			{
				case VT_R4:
				{
					V_R4(com) = V_R4(java);
					break;
				}

				case VT_R4 | VT_BYREF:
				{
					V_R4(com) = *V_R4REF(java);
					break;
				}
			}
			break;
		}

		case VT_R4 | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_R4:
				{
					*V_R4REF(com) = V_R4(java);
					break;
				}

				case VT_R4 | VT_BYREF:
				{
					*V_R4REF(com) = *V_R4REF(java);
					break;
				}
			}
			break;
		}

		case VT_R8:
		{
			switch (java->vt)
			{
				case VT_R8:
				{
					V_R8(com) = V_R8(java);
					break;
				}

				case VT_R8 | VT_BYREF:
				{
					V_R8(com) = *V_R8REF(java);
					break;
				}
			}
			break;
		}

		case VT_R8 | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_R8:
				{
					*V_R8REF(com) = V_R8(java);
					break;
				}

				case VT_R8 | VT_BYREF:
				{
					*V_R8REF(com) = *V_R8REF(java);
					break;
				}
			}
			break;
		}

				case VT_I1:
		{
			switch (java->vt)
			{
				case VT_I1:
				{
					V_I1(com) = V_I1(java);
					break;
				}

				case VT_I1 | VT_BYREF:
				{
					V_I1(com) = *V_I1REF(java);
					break;
				}
			}
			break;
		}

		case VT_I1 | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_I1:
				{
					*V_I1REF(com) = V_I1(java);
					break;
				}

				case VT_I1 | VT_BYREF:
				{
					*V_I1REF(com) = *V_I1REF(java);
					break;
				}
			}
			break;
		}

				case VT_UI2:
		{
			switch (java->vt)
			{
				case VT_UI2:
				{
					V_UI2(com) = V_UI2(java);
					break;
				}

				case VT_UI2 | VT_BYREF:
				{
					V_UI2(com) = *V_UI2REF(java);
					break;
				}
			}
			break;
		}

		case VT_UI2 | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_UI2:
				{
					*V_UI2REF(com) = V_UI2(java);
					break;
				}

				case VT_UI2 | VT_BYREF:
				{
					*V_UI2REF(com) = *V_UI2REF(java);
					break;
				}
			}
			break;
		}

				case VT_UI4:
		{
			switch (java->vt)
			{
				case VT_UI4:
				{
					V_UI4(com) = V_UI4(java);
					break;
				}

				case VT_UI4 | VT_BYREF:
				{
					V_UI4(com) = *V_UI4REF(java);
					break;
				}
			}
			break;
		}

		case VT_UI4 | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_UI4:
				{
					*V_UI4REF(com) = V_UI4(java);
					break;
				}

				case VT_UI4 | VT_BYREF:
				{
					*V_UI4REF(com) = *V_UI4REF(java);
					break;
				}
			}
			break;
		}

				case VT_INT:
		{
			switch (java->vt)
			{
				case VT_INT:
				{
					V_INT(com) = V_INT(java);
					break;
				}

				case VT_INT | VT_BYREF:
				{
					V_INT(com) = *V_INTREF(java);
					break;
				}
			}
			break;
		}

		case VT_INT | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_INT:
				{
					*V_INTREF(com) = V_INT(java);
					break;
				}

				case VT_INT | VT_BYREF:
				{
					*V_INTREF(com) = *V_INTREF(java);
					break;
				}
			}
			break;
		}

				case VT_UINT:
		{
			switch (java->vt)
			{
				case VT_UINT:
				{
					V_UINT(com) = V_UINT(java);
					break;
				}

				case VT_UINT | VT_BYREF:
				{
					V_UINT(com) = *V_UINTREF(java);
					break;
				}
			}
			break;
		}

		case VT_UINT | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_UINT:
				{
					*V_UINTREF(com) = V_UINT(java);
					break;
				}

				case VT_UINT | VT_BYREF:
				{
					*V_UINTREF(com) = *V_UINTREF(java);
					break;
				}
			}
			break;
		}

				case VT_CY:
		{
			switch (java->vt)
			{
				case VT_CY:
				{
					V_CY(com) = V_CY(java);
					break;
				}

				case VT_CY | VT_BYREF:
				{
					V_CY(com) = *V_CYREF(java);
					break;
				}
			}
			break;
		}

		case VT_CY | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_CY:
				{
					*V_CYREF(com) = V_CY(java);
					break;
				}

				case VT_CY | VT_BYREF:
				{
					*V_CYREF(com) = *V_CYREF(java);
					break;
				}
			}
			break;
		}

				case VT_DATE:
		{
			switch (java->vt)
			{
				case VT_DATE:
				{
					V_DATE(com) = V_DATE(java);
					break;
				}

				case VT_DATE | VT_BYREF:
				{
					V_DATE(com) = *V_DATEREF(java);
					break;
				}
			}
			break;
		}

		case VT_DATE | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_DATE:
				{
					*V_DATEREF(com) = V_DATE(java);
					break;
				}

				case VT_DATE | VT_BYREF:
				{
					*V_DATEREF(com) = *V_DATEREF(java);
					break;
				}
			}
			break;
		}

				case VT_BSTR:
		{
			switch (java->vt)
			{
				case VT_BSTR:
				{
					V_BSTR(com) = V_BSTR(java);
					break;
				}

				case VT_BSTR | VT_BYREF:
				{
					V_BSTR(com) = *V_BSTRREF(java);
					break;
				}
			}
			break;
		}

		case VT_BSTR | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_BSTR:
				{
					*V_BSTRREF(com) = V_BSTR(java);
					break;
				}

				case VT_BSTR | VT_BYREF:
				{
					*V_BSTRREF(com) = *V_BSTRREF(java);
					break;
				}
			}
			break;
		}

				case VT_DECIMAL:
		{
			switch (java->vt)
			{
				case VT_DECIMAL:
				{
					V_DECIMAL(com) = V_DECIMAL(java);
					break;
				}

				case VT_DECIMAL | VT_BYREF:
				{
					V_DECIMAL(com) = *V_DECIMALREF(java);
					break;
				}
			}
			break;
		}

		case VT_DECIMAL | VT_BYREF:
		{
			switch (java->vt)
			{
				case VT_DECIMAL:
				{
					*V_DECIMALREF(com) = V_DECIMAL(java);
					break;
				}

				case VT_DECIMAL | VT_BYREF:
				{
					*V_DECIMALREF(com) = *V_DECIMALREF(java);
					break;
				}
			}
			break;
		}


	  }
      zeroVariant(env, arg);
      env->DeleteLocalRef(arg);
    }
    // End code from Jiffie team that copies parameters back from java to COM

	// detach from thread
    jvm->DetachCurrentThread();
    return S_OK;
  }
  return E_NOINTERFACE;
}
