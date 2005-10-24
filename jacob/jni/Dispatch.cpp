/*
 * Copyright (c) 1999-2004 Sourceforge JACOB Project.
 * All rights reserved. Originator: Dan Adler (http://danadler.com).
 * Get more information about JACOB at www.sourceforge.net/jacob-project
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
#include "Dispatch.h"
// Win32 support for Ole Automation
#include <wchar.h>
#include <string.h>
#include <atlbase.h>
#include <oleauto.h>
#include <olectl.h>
#include "util.h"

extern "C" 
{

#define DISP_FLD "m_pDispatch"

// extract a IDispatch from a jobject
IDispatch *extractDispatch(JNIEnv *env, jobject arg)
{
  jclass argClass = env->GetObjectClass(arg);
  jfieldID ajf = env->GetFieldID( argClass, DISP_FLD, "I");
  jint anum = env->GetIntField(arg, ajf);
  IDispatch *v = (IDispatch *)anum;
  return v;
}

JNIEXPORT jobject JNICALL Java_com_jacob_com_Dispatch_QueryInterface
  (JNIEnv *env, jobject _this, jstring _iid)
{
  // get the current IDispatch
  IDispatch *pIDispatch = extractDispatch(env, _this);
  if (!pIDispatch) return NULL;
  const char *siid = env->GetStringUTFChars(_iid, NULL);
  USES_CONVERSION;
  LPOLESTR bsIID = A2W(siid);
  env->ReleaseStringUTFChars(_iid, siid);
  IID iid;
  HRESULT hr = IIDFromString(bsIID, &iid);
  if (FAILED(hr)) {
    ThrowComFail(env, "Can't get IID from String", hr);
    return NULL;
  }

  // try to call QI on the passed IID
  IDispatch *disp;
  hr = pIDispatch->QueryInterface(iid, (void **)&disp);
  if (FAILED(hr)) {
    ThrowComFail(env, "QI on IID from String Failed", hr);
    return NULL;
  }

  jclass autoClass = env->FindClass("com/jacob/com/Dispatch");
  jmethodID autoCons = env->GetMethodID(autoClass, "<init>", "(I)V");
  // construct a Dispatch object to return
  // I am copying the pointer to java
  // jacob-msg 1817 - SF 1053871 :  QueryInterface already called AddRef!!
  //if (disp) disp->AddRef();
  jobject newAuto = env->NewObject(autoClass, autoCons, disp);
  return newAuto;
}

JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_createInstance
  (JNIEnv *env, jobject _this, jstring _progid)
{
  jclass clazz = env->GetObjectClass(_this);
  jfieldID jf = env->GetFieldID( clazz, DISP_FLD, "I");

  const char *progid = env->GetStringUTFChars(_progid, NULL);
  CLSID clsid;
  HRESULT hr;
  IUnknown *punk = NULL;
  IDispatch *pIDispatch;
  USES_CONVERSION;
  LPOLESTR bsProgId = A2W(progid);
  if (strchr(progid,':')) 
  {
     env->ReleaseStringUTFChars(_progid, progid);
     // it's a moniker
     hr = CoGetObject(bsProgId, NULL, IID_IUnknown, (LPVOID *)&punk);
     if (FAILED(hr)) {
       ThrowComFail(env, "Can't find moniker", hr);
       return;
     }
     IClassFactory *pIClass;
     // if it was a clsid moniker, I may have a class factory
     hr = punk->QueryInterface(IID_IClassFactory, (void **)&pIClass);
     if (!SUCCEEDED(hr)) goto doDisp;
     punk->Release();
     // try to create an instance
     hr = pIClass->CreateInstance(NULL, IID_IUnknown, (void **)&punk);
     if (FAILED(hr)) {
       ThrowComFail(env, "Can't create moniker class instance", hr);
       return;
     }
     pIClass->Release();
     goto doDisp;
  }
  env->ReleaseStringUTFChars(_progid, progid);
  // Now, try to find an IDispatch interface for progid
  hr = CLSIDFromProgID(bsProgId, &clsid);
  if (FAILED(hr)) {
    ThrowComFail(env, "Can't get object clsid from progid", hr);
    return;
  }
  // standard creation
  hr = CoCreateInstance(clsid,NULL,CLSCTX_LOCAL_SERVER|CLSCTX_INPROC_SERVER,IID_IUnknown, (void **)&punk);
  if (!SUCCEEDED(hr)) {
     ThrowComFail(env, "Can't co-create object", hr);
     return;
  }
doDisp:

  // now get an IDispatch pointer from the IUnknown
  hr = punk->QueryInterface(IID_IDispatch, (void **)&pIDispatch);
  if (!SUCCEEDED(hr)) {
    ThrowComFail(env, "Can't QI object for IDispatch", hr);
    return;
  }
  // CoCreateInstance called AddRef
  punk->Release();
  env->SetIntField(_this, jf, (unsigned int)pIDispatch);
}

JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_release
  (JNIEnv *env, jobject _this)
{
  jclass clazz = env->GetObjectClass(_this);
  jfieldID jf = env->GetFieldID( clazz, DISP_FLD, "I");
  jint num = env->GetIntField(_this, jf);

  IDispatch *disp = (IDispatch *)num;
  if (disp) {
    disp->Release();
    env->SetIntField(_this, jf, (unsigned int)0);
  }
}

static HRESULT
name2ID(IDispatch *pIDispatch, const char *prop, DISPID *dispid, long lcid)
{
  HRESULT     hresult;
  USES_CONVERSION;
  LPOLESTR propOle = A2W(prop);
  hresult = pIDispatch->GetIDsOfNames(IID_NULL,(LPOLESTR*)&propOle,1,lcid,dispid);
  return hresult;
}

JNIEXPORT jintArray JNICALL Java_com_jacob_com_Dispatch_getIDsOfNames
  (JNIEnv *env, jclass clazz, jobject disp, jint lcid, jobjectArray names)
{
  IDispatch *pIDispatch = extractDispatch(env, disp);
  if (!pIDispatch) return NULL;

  int l = env->GetArrayLength(names);
  int i;
  LPOLESTR *lps = (LPOLESTR *)CoTaskMemAlloc(l * sizeof(LPOLESTR));
  DISPID *dispid = (DISPID *)CoTaskMemAlloc(l * sizeof(DISPID));
  for(i=0;i<l;i++) 
  {
    USES_CONVERSION;
    jstring s = (jstring)env->GetObjectArrayElement(names, i);
    const char *nm = env->GetStringUTFChars(s, NULL);
    LPOLESTR nmos = A2W(nm);
    env->ReleaseStringUTFChars(s, nm);
    lps[i] = nmos;
    env->DeleteLocalRef(s);
  }
  HRESULT hr = pIDispatch->GetIDsOfNames(IID_NULL,lps,l,lcid,dispid);
  if (FAILED(hr)) {
    CoTaskMemFree(lps);
    CoTaskMemFree(dispid);
    char buf[1024];
    strcpy(buf, "Can't map names to dispid:");
    for(i=0;i<l;i++) 
    {
      USES_CONVERSION;
      jstring s = (jstring)env->GetObjectArrayElement(names, i);
      const char *nm = env->GetStringUTFChars(s, NULL);
      strcat(buf, nm);
      env->ReleaseStringUTFChars(s, nm);
      env->DeleteLocalRef(s);
    }
    ThrowComFail(env, buf, hr);
    return NULL;
  }
  jintArray iarr = env->NewIntArray(l);
  env->SetIntArrayRegion(iarr, i, l, dispid);
  CoTaskMemFree(lps);
  CoTaskMemFree(dispid);
  return iarr;
}

static char* BasicToCharString(const BSTR inBasicString)
{
    char* charString = NULL;
    const size_t charStrSize = ::SysStringLen(inBasicString) + 1;
    if (charStrSize > 1)
    {
        charString = new char[charStrSize];
        size_t len = ::wcstombs(charString, inBasicString, charStrSize);
    }
    else
        charString = ::strdup("");

    return charString;
}

static char* CreateErrorMsgFromResult(HRESULT inResult)
{
  char* msg = NULL;
  ::FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER |
      FORMAT_MESSAGE_FROM_SYSTEM, NULL, inResult,MAKELANGID(LANG_NEUTRAL,
      SUBLANG_DEFAULT), (LPTSTR) &msg, 0, NULL);
  if (msg == NULL)
  msg = ::strdup("An unknown COM error has occured.");

  return msg;
}

static char* CreateErrorMsgFromInfo(HRESULT inResult, EXCEPINFO* ioInfo,
 const char* methName)
{
  char* msg = NULL;

  // If this is a dispatch exception (triggered by an Invoke message),
  // then we have to take some additional steps to process the error
  // message.
  if  (inResult == DISP_E_EXCEPTION)
  {
    // Check to see if the server deferred filling in the exception
    // information.  If so, make the call to populate the structure.
    if (ioInfo->pfnDeferredFillIn != NULL)
        (*(ioInfo->pfnDeferredFillIn))(ioInfo);

    // Build the error message from exception information content.
    char* source = ::BasicToCharString(ioInfo->bstrSource);
    char* desc = ::BasicToCharString(ioInfo->bstrDescription);
    const size_t MSG_LEN = ::strlen(methName) + ::strlen(source) + ::strlen(desc) + 128;
    msg = new char[MSG_LEN];
    ::strncpy(msg, "Invoke of: ", MSG_LEN);
    ::strncat(msg, methName, MSG_LEN);
    ::strncat(msg, "\nSource: ", MSG_LEN);
    ::strncat(msg, source, MSG_LEN);
    ::strncat(msg, "\nDescription: ", MSG_LEN);
    ::strncat(msg, desc, MSG_LEN);
    ::strncat(msg, "\n", MSG_LEN);
    delete source;
    delete desc;
  }
  else
  {
    char* msg2 = CreateErrorMsgFromResult(inResult);
    const size_t MSG_LEN = ::strlen(methName) + ::strlen(msg2) + 128;
    msg = new char[MSG_LEN];
    ::strncpy(msg, "A COM exception has been encountered:\n"
        "At Invoke of: ", MSG_LEN);
    ::strncat(msg, methName, MSG_LEN);
    ::strncat(msg, "\nDescription: ", MSG_LEN);
    ::strncat(msg, msg2, MSG_LEN);
    // jacob-msg 1075 - SF 1053872 : Documentation says "use LocalFree"!! 
    //delete msg2;
	LocalFree(msg2); 
  }
  return msg;
}


#define SETDISPPARAMS(dp, numArgs, pvArgs, numNamed, pNamed) \
        {\
           (dp).cArgs  = numArgs; \
           (dp).rgvarg = pvArgs; \
           (dp).cNamedArgs = numNamed; \
           (dp).rgdispidNamedArgs = pNamed; \
        }

#define SETNOPARAMS(dp) SETDISPPARAMS(dp, 0, NULL, 0, NULL)

JNIEXPORT jobject JNICALL Java_com_jacob_com_Dispatch_invokev
  (JNIEnv *env, jclass clazz,
  jobject disp, jstring name, jint dispid,
  jint lcid, jint wFlags, jobjectArray vArg, jintArray uArgErr)
{
  DISPPARAMS  dispparams;
  EXCEPINFO   excepInfo;

  IDispatch *pIDispatch = extractDispatch(env, disp);
  if (!pIDispatch) return NULL;

  int dispID = dispid;
  if (name != NULL) 
  {
    const char *nm = env->GetStringUTFChars(name, NULL);
    HRESULT hr;
    if (FAILED(hr = name2ID(pIDispatch, nm, (long *)&dispID, lcid))) {
      char buf[1024];
      sprintf(buf, "Can't map name to dispid: %s", nm);
      ThrowComFail(env, buf, -1);
      return NULL;
    }
    env->ReleaseStringUTFChars(name, nm);
  }

  int num_args = env->GetArrayLength(vArg);
  int i, j;
  VARIANT *varr = NULL;
  if (num_args) 
  {
    varr = (VARIANT *)CoTaskMemAlloc(num_args*sizeof(VARIANT));
    /* reverse args for dispatch */
    for(i=num_args-1,j=0;0<=i;i--,j++) 
    {
      VariantInit(&varr[j]);
      jobject arg = env->GetObjectArrayElement(vArg, i);
      VARIANT *v = extractVariant(env, arg);
      // no escape from copy?
      VariantCopy(&varr[j], v);
      env->DeleteLocalRef(arg);
    }
  }
  // prepare a new return value
  jclass variantClass = env->FindClass("com/jacob/com/Variant");
  jmethodID variantCons = 
      env->GetMethodID(variantClass, "<init>", "()V");
  // construct a variant to return
  jobject newVariant = env->NewObject(variantClass, variantCons);
  // get the VARIANT from the newVariant
  VARIANT *v = extractVariant(env, newVariant);
  DISPID  dispidPropertyPut = DISPID_PROPERTYPUT;

  // determine how to dispatch
  switch (wFlags) 
  {
    case DISPATCH_PROPERTYGET: // GET
    case DISPATCH_METHOD: // METHOD
    case DISPATCH_METHOD|DISPATCH_PROPERTYGET:
      {
        SETDISPPARAMS(dispparams, num_args, varr, 0, NULL);
        break;
      }
    case DISPATCH_PROPERTYPUT:
    case DISPATCH_PROPERTYPUTREF: // jacob-msg 1075 - SF 1053872
      {
        SETDISPPARAMS(dispparams, num_args, varr, 1, &dispidPropertyPut);
        break;
      }
  }

  HRESULT hr = 0;
  jint count = env->GetArrayLength(uArgErr);
  if ( count != 0 )
  {
	  jint *uAE = env->GetIntArrayElements(uArgErr, NULL);
	  hr = pIDispatch->Invoke(dispID,IID_NULL,
		lcid,wFlags,&dispparams,v,&excepInfo,(unsigned int *)uAE);
	  env->ReleaseIntArrayElements(uArgErr, uAE, 0);
  }
  else
  {
	  hr = pIDispatch->Invoke(dispID,IID_NULL,
		lcid,wFlags,&dispparams,v,&excepInfo, NULL);
  }
  if (num_args) 
  {
    // to account for inouts, I need to copy the inputs back to
    // the java array after the method returns
    // this occurs, for example, in the ADO wrappers
    for(i=num_args-1,j=0;0<=i;i--,j++) 
    {
      jobject arg = env->GetObjectArrayElement(vArg, i);
      VARIANT *v = extractVariant(env, arg);
      // reverse copy
      VariantCopy(v, &varr[j]);
      // clear out the temporary variant
      VariantClear(&varr[j]);
      env->DeleteLocalRef(arg);
    }
  }

  if (varr) CoTaskMemFree(varr);

  // check for error and display a somewhat verbose error message
  if (!SUCCEEDED(hr)) {
    // two buffers that may have to be freed later
    char *buf = NULL;
    char *dispIdAsName = NULL;
    // this method can get called with a name or a dispatch id
    // we need to handle both SF 1114159 
    if (name != NULL){
	    const char *nm = env->GetStringUTFChars(name, NULL);
	    buf = CreateErrorMsgFromInfo(hr, &excepInfo, nm);
	    env->ReleaseStringUTFChars(name, nm);
    } else {
		dispIdAsName = new char[256];
		// get the id string
		itoa (dispID,dispIdAsName,10);
		//continue on mostly as before
		buf = CreateErrorMsgFromInfo(hr,&excepInfo,dispIdAsName); 
    }
    
    // jacob-msg 3696 - SF 1053866
	if(hr == DISP_E_EXCEPTION)
	{
		if(excepInfo.scode != 0)
		{
			hr = excepInfo.scode;
		}
		else
		{
			hr = _com_error::WCodeToHRESULT(excepInfo.wCode);
		}
	}
	
    ThrowComFail(env, buf, hr);
    if (buf) delete buf;
    if (dispIdAsName) delete dispIdAsName;
    return NULL;
  }

  return newVariant;
}

}


