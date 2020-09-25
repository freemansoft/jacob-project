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
#include "EventProxy.h"
#include "Variant.h"

// hook myself up as a listener for delegate
EventProxy::EventProxy(JNIEnv *env, 
		jobject aSinkObj, 
        CComPtr<IConnectionPoint> pConn,
        IID eid, 
        CComBSTR mName[], 
        DISPID mID[], 
        int mNum) :
   // initialize some variables
   m_cRef(0), pCP(pConn), 
   eventIID(eid), MethNum(mNum), MethName(mName), 
   MethID(mID)
{
	// keep a pointer to the sink
	javaSinkObj = env->NewGlobalRef(aSinkObj);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}

	// we need this to attach to the event invocation thread
	env->GetJavaVM(&jvm);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
	AddRef();
    Connect(env);
}

void EventProxy::Connect(JNIEnv *env) {
  HRESULT hr = pCP->Advise(this, &dwEventCookie);
  if (SUCCEEDED(hr)) {
        connected = 1;
  } else {
        connected = 0;
        // SF 3435567 added debug info to advise failed message
        char tmp[256];
        sprintf_s( tmp, 256, "Advise failed with %x (CONNECT_E_ADVISELIMIT is %x)", (int)hr, (int)(CONNECT_E_ADVISELIMIT) );
        ThrowComFail(env, tmp, hr);
  }
}

// unhook myself up as a listener and get rid of delegate
EventProxy::~EventProxy()
{
	JNIEnv *env;
    Disconnect();
    jint vmConnectionStatus = JNI_EVERSION ;
    jint attachReturnStatus = -1; // AttachCurrentThread return status.. negative numbers are failure return codes.
    
	// attach to the current running thread -- JDK 1.4 jni.h has two param cover for 3 param call
    vmConnectionStatus = jvm->GetEnv((void **)&env, JNI_VERSION_1_2);
		if ((env != NULL)&& env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
   	if (vmConnectionStatus == JNI_EDETACHED){
		//printf("Unhook: Attaching to current thread using JNI Version 1.2 (%d)\n",vmConnectionStatus);
		JavaVMAttachArgs attachmentArgs; 
        attachmentArgs.version = JNI_VERSION_1_2;  
        attachmentArgs.name = NULL; 
        attachmentArgs.group = NULL; 
        attachReturnStatus = jvm->AttachCurrentThread((void **)&env, &attachmentArgs); 
		if ((env != NULL) && env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
   	} else {
   		// should really look for JNI_OK versus an error because it could have been JNI_EVERSION
   		// started method with thread hooked to VM so no need to attach again
   		//printf("Unhook:  No need to attach because already attached %d\n",vmConnectionStatus);
   	}

   	// we should always have an env by this point but lets be paranoid and check
	if (env != NULL){
		env->DeleteGlobalRef(javaSinkObj);
			if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
	}
	if (MethNum) {
	    delete [] MethName;
	    delete [] MethID;
	}
	// detach from thread only if we attached to it in this function
	if (attachReturnStatus == 0){
		jvm->DetachCurrentThread();
		//printf("Unhook: Detached\n");
	} else {
		//printf("Unhook: No need to detatch because attached prior to method\n");
	}
  	//fflush(stdout);
}

void EventProxy::Disconnect() {
    if (connected) {
        // insure we don't call Unadvise twice
        connected = 0;
        pCP->Unadvise(dwEventCookie);
    }
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
// But however sometimes it is called, so do the mapping as defined
// by the API Patch #42 provided by abp-futura
STDMETHODIMP EventProxy::GetIDsOfNames(REFIID riid,
    OLECHAR **rgszNames, UINT cNames, LCID lcid, DISPID *rgDispID)
{
  HRESULT result = S_OK;
  for (UINT n = 0; n < cNames; n++) {
    rgDispID[n] = DISPID_UNKNOWN;
    USES_CONVERSION;
    const char *current = W2A((OLECHAR *) rgszNames[n]);
    // map name to dispID
    for(int i=0; i<MethNum; i++)
    {
      const char *found = W2A((OLECHAR *) MethName[i]);
      if (strcmp(current, found) == 0) {
        rgDispID[n] = MethID[i];
      }
    }
    if (rgDispID[n] == DISPID_UNKNOWN) {
      result = DISP_E_UNKNOWNNAME;
    }
  }

  return result;
}

// The actual callback from the connection point arrives here
STDMETHODIMP EventProxy::Invoke(DISPID dispID, REFIID riid,
    LCID lcid, unsigned short wFlags, DISPPARAMS *pDispParams,
    VARIANT *pVarResult, EXCEPINFO *pExcepInfo, UINT *puArgErr)
{
  	const char 	*eventMethodName = NULL; //Sourceforge report 1394001 
  	JNIEnv      *env = NULL;

  // map dispID to jmethodID
  for(int i=0;i<MethNum;i++) 
  {
    if (MethID[i] == dispID) { 
		USES_CONVERSION;
		eventMethodName = W2A((OLECHAR *)MethName[i]);
    	}
  }
  // added 1.12
  if (!eventMethodName) {
  	// just bail if can't find signature.  no need to attach
	// printf("Invoke: didn't find method name for dispatch id %d\n",dispID);
   	return S_OK;
  }
  if (DISPATCH_METHOD & wFlags) 
  {
        
    // attach to the current running thread
	//printf("Invoke: Attaching to current thread using JNI Version 1.2\n");
		JavaVMAttachArgs attachmentArgs; 
        attachmentArgs.version = JNI_VERSION_1_2;  
        attachmentArgs.name = NULL; 
        attachmentArgs.group = NULL; 
        jvm->AttachCurrentThread((void **)&env, &attachmentArgs); 
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}

	if (!eventMethodName) 
  	{
	    // could not find this signature in list
  		// printf("Invoke: didn't find method name for dispatch id %d\n",dispID);
  		// this probably leaves a native thread attached to the vm when we don't want it
  		ThrowComFail(env, "Event method received was not defined as part of callback interface", -1);
  		
  		// should we detatch before returning?? We probably never get here if we ThrowComFail()
	    // jvm->DetachCurrentThread();
    	return S_OK;
	  }

	// find the class of the InvocationHandler
   	jclass javaSinkClass = env->GetObjectClass(javaSinkObj);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
	//printf("Invoke: Got sink class\n");
    jmethodID invokeMethod;
    invokeMethod = env->GetMethodID(javaSinkClass, "invoke", "(Ljava/lang/String;[Lcom/jacob/com/Variant;)Lcom/jacob/com/Variant;");
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
    jstring eventMethodNameAsString = env->NewStringUTF(eventMethodName);
	//printf("Invoke: Got method name\n");
	// now do what we need for the variant
    jmethodID getVariantMethod = env->GetMethodID(javaSinkClass, "getVariant", "()Lcom/jacob/com/Variant;");
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
	//printf("Invoke: Found way too getVariant\n");
    jobject aVariantObj = env->CallObjectMethod(javaSinkObj, getVariantMethod); 
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
	//printf("Invoke: Made Variant\n");
   	jclass variantClass = env->GetObjectClass(aVariantObj);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}

	// create the variant parameter array
    // how many params
    int numVariantParams = pDispParams->cArgs;
    // make an array of them
    jobjectArray varr = env->NewObjectArray(numVariantParams, variantClass, 0);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
	//printf("Invoke: Created Array\n");
    int i,j;
    for(i=numVariantParams-1,j=0;i>=0;i--,j++) 
    {
      // construct a java variant holder
	  jobject arg = env->CallObjectMethod(javaSinkObj, getVariantMethod); 
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
	//printf("Invoke: Filled Array\n");
    // Set up the return value
    jobject ret;

    ret = env->CallObjectMethod(javaSinkObj, invokeMethod, 
		eventMethodNameAsString, varr); 
	//printf("Invoke: Invoked callback\n");
    if (!env->ExceptionOccurred() && ret != NULL) {
        VariantCopy(pVarResult, extractVariant(env,ret));
    }
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}
	// don't need the first variant we created to get the class
	// SF 1689061 change not accepted but put in as comment for later reminder
    //Java_com_jacob_com_Variant_release(env, aVariantObj);
	env->DeleteLocalRef(aVariantObj);
		if (env->ExceptionOccurred()) { env->ExceptionDescribe(); env->ExceptionClear();}

    // Begin code from Jiffie team that copies parameters back from java to COM
    for(i=numVariantParams-1,j=0;i>=0;i--,j++) 
    {
		jobject arg = env->GetObjectArrayElement(varr, j);
		VARIANT *java = extractVariant(env, arg);
		VARIANT *com = &pDispParams->rgvarg[i];
		convertJavaVariant(java, com);
		// SF 1689061 change not accepted but put in as comment for later reminder
		// note that a related fix has been submitted in SF 3435567 to do this in zeroVariant() method
		//Java_com_jacob_com_Variant_release(env, arg);
		zeroVariant(env, arg);
		env->DeleteLocalRef(arg);
    }
    // End code from Jiffie team that copies parameters back from java to COM
    // detach from thread
	//printf("Invoke: Detatching\n");
	jvm->DetachCurrentThread();
  	//fflush(stdout);
    return S_OK;
  }
  return E_NOINTERFACE;
}

void EventProxy::convertJavaVariant(VARIANT *java, VARIANT *com) {

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
}
