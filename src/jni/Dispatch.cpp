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
    jfieldID ajf = env->GetFieldID(argClass, DISP_FLD, "J");
    jlong anum = env->GetLongField(arg, ajf);
    IDispatch *v = (IDispatch *)anum;
    return v;
  }

  /**
   * This method finds an interface rooted on the passed in dispatch object.
   * This creates a new Dispatch object so it is NOT reliable
   * in the event callback thread of a JWS client where the root class loader
   * does not have com.jacob.com.Dispatch in its classpath
   */
  JNIEXPORT jobject JNICALL Java_com_jacob_com_Dispatch_QueryInterface(JNIEnv *env, jobject _this, jstring _iid)
  {
    // get the current IDispatch
    IDispatch *pIDispatch = extractDispatch(env, _this);
    if (!pIDispatch)
      return NULL;
    // if we used env->GetStringChars() would that let us drop the conversion?
    const char *siid = env->GetStringUTFChars(_iid, NULL);
    USES_CONVERSION;
    LPOLESTR bsIID = A2W(siid);
    env->ReleaseStringUTFChars(_iid, siid);
    IID iid;
    HRESULT hr = IIDFromString(bsIID, &iid);
    if (FAILED(hr))
    {
      ThrowComFail(env, "Can't get IID from String", hr);
      return NULL;
    }

    // try to call QI on the passed IID
    IDispatch *disp;
    hr = pIDispatch->QueryInterface(iid, (void **)&disp);
    if (FAILED(hr))
    {
      ThrowComFail(env, "QI on IID from String Failed", hr);
      return NULL;
    }

    jclass autoClass = env->FindClass("com/jacob/com/Dispatch");
    jmethodID autoCons = env->GetMethodID(autoClass, "<init>", "(J)V");
    // construct a Dispatch object to return
    // I am copying the pointer to java
    // jacob-msg 1817 - SF 1053871 :  QueryInterface already called AddRef!!
    // if (disp) disp->AddRef();
    // jacobproject/bug/132
    jobject newAuto = env->NewObject(autoClass, autoCons, (jlong)(uintptr_t)disp);
    return newAuto;
  }

  /**
   * starts up a new instance of the requested program (progId)
   * and connects to it.  does special code if the progid
   * is of the alternate format (with ":")
   **/
  JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_createInstanceNative(JNIEnv *env, jobject _this, jstring _progid)
  {
    jclass clazz = env->GetObjectClass(_this);
    jfieldID jf = env->GetFieldID(clazz, DISP_FLD, "J");

    // if we used env->GetStringChars() would that let us drop the conversion?
    const char *progid = env->GetStringUTFChars(_progid, NULL);
    CLSID clsid;
    HRESULT hr;
    IUnknown *punk = NULL;
    IDispatch *pIDispatch;
    USES_CONVERSION;
    LPOLESTR bsProgId = A2W(progid);
    if (strchr(progid, ':'))
    {
      env->ReleaseStringUTFChars(_progid, progid);
      // it's a moniker
      hr = CoGetObject(bsProgId, NULL, IID_IUnknown, (LPVOID *)&punk);
      if (FAILED(hr))
      {
        ThrowComFail(env, "Can't find moniker", hr);
        return;
      }
      IClassFactory *pIClass;
      // if it was a clsid moniker, I may have a class factory
      hr = punk->QueryInterface(IID_IClassFactory, (void **)&pIClass);
      if (!SUCCEEDED(hr))
        goto doDisp;
      punk->Release();
      // try to create an instance
      hr = pIClass->CreateInstance(NULL, IID_IUnknown, (void **)&punk);
      if (FAILED(hr))
      {
        ThrowComFail(env, "Can't create moniker class instance", hr);
        return;
      }
      pIClass->Release();
      goto doDisp;
    }
    env->ReleaseStringUTFChars(_progid, progid);
    // Now, try to find an IDispatch interface for progid
    hr = CLSIDFromProgID(bsProgId, &clsid);
    if (FAILED(hr))
    {
      ThrowComFail(env, "Can't get object clsid from progid", hr);
      return;
    }
    // standard creation
    hr = CoCreateInstance(clsid, NULL, CLSCTX_LOCAL_SERVER | CLSCTX_INPROC_SERVER, IID_IUnknown, (void **)&punk);
    if (!SUCCEEDED(hr))
    {
      ThrowComFail(env, "Can't co-create object", hr);
      return;
    }
  doDisp:

    // now get an IDispatch pointer from the IUnknown
    hr = punk->QueryInterface(IID_IDispatch, (void **)&pIDispatch);
    if (!SUCCEEDED(hr))
    {
      ThrowComFail(env, "Can't QI object for IDispatch", hr);
      return;
    }
    // CoCreateInstance called AddRef
    punk->Release();
    env->SetLongField(_this, jf, (jlong)pIDispatch);
  }

  /**
   * attempts to connect to an running instance of the requested program
   * This exists solely for the factory method connectToActiveInstance.
   **/
  JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_getActiveInstanceNative(JNIEnv *env, jobject _this, jstring _progid)
  {
    jclass clazz = env->GetObjectClass(_this);
    jfieldID jf = env->GetFieldID(clazz, DISP_FLD, "J");

    // if we used env->GetStringChars() would that let us drop the conversion?
    const char *progid = env->GetStringUTFChars(_progid, NULL);
    CLSID clsid;
    HRESULT hr;
    IUnknown *punk = NULL;
    IDispatch *pIDispatch;
    USES_CONVERSION;
    LPOLESTR bsProgId = A2W(progid);
    env->ReleaseStringUTFChars(_progid, progid);
    // Now, try to find an IDispatch interface for progid
    hr = CLSIDFromProgID(bsProgId, &clsid);
    if (FAILED(hr))
    {
      ThrowComFail(env, "Can't get object clsid from progid", hr);
      return;
    }
    // standard connection
    // printf("trying to connect to running %ls\n",bsProgId);
    hr = GetActiveObject(clsid, NULL, &punk);
    if (!SUCCEEDED(hr))
    {
      ThrowComFail(env, "Can't get active object", hr);
      return;
    }
    // now get an IDispatch pointer from the IUnknown
    hr = punk->QueryInterface(IID_IDispatch, (void **)&pIDispatch);
    if (!SUCCEEDED(hr))
    {
      ThrowComFail(env, "Can't QI object for IDispatch", hr);
      return;
    }
    // GetActiveObject called AddRef
    punk->Release();
    env->SetLongField(_this, jf, (jlong)pIDispatch);
  }

  /**
   * starts up a new instance of the requested program (progId).
   * This exists solely for the factory method connectToActiveInstance.
   **/
  JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_coCreateInstanceNative(JNIEnv *env, jobject _this, jstring _progid)
  {
    jclass clazz = env->GetObjectClass(_this);
    jfieldID jf = env->GetFieldID(clazz, DISP_FLD, "J");

    // if we used env->GetStringChars() would that let us drop the conversion?
    const char *progid = env->GetStringUTFChars(_progid, NULL);
    CLSID clsid;
    HRESULT hr;
    IUnknown *punk = NULL;
    IDispatch *pIDispatch;
    USES_CONVERSION;
    LPOLESTR bsProgId = A2W(progid);
    env->ReleaseStringUTFChars(_progid, progid);
    // Now, try to find an IDispatch interface for progid
    hr = CLSIDFromProgID(bsProgId, &clsid);
    if (FAILED(hr))
    {
      ThrowComFail(env, "Can't get object clsid from progid", hr);
      return;
    }
    // standard creation
    hr = CoCreateInstance(clsid, NULL, CLSCTX_LOCAL_SERVER | CLSCTX_INPROC_SERVER, IID_IUnknown, (void **)&punk);
    if (!SUCCEEDED(hr))
    {
      ThrowComFail(env, "Can't co-create object", hr);
      return;
    }
    // now get an IDispatch pointer from the IUnknown
    hr = punk->QueryInterface(IID_IDispatch, (void **)&pIDispatch);
    if (!SUCCEEDED(hr))
    {
      ThrowComFail(env, "Can't QI object for IDispatch", hr);
      return;
    }
    // CoCreateInstance called AddRef
    punk->Release();
    env->SetLongField(_this, jf, (jlong)pIDispatch);
  }

  /**
   * release method
   */
  JNIEXPORT void JNICALL Java_com_jacob_com_Dispatch_release(JNIEnv *env, jobject _this)
  {
    jclass clazz = env->GetObjectClass(_this);
    jfieldID jf = env->GetFieldID(clazz, DISP_FLD, "J");
    jlong num = env->GetLongField(_this, jf);

    IDispatch *disp = (IDispatch *)num;
    if (disp)
    {
      disp->Release();
      env->SetLongField(_this, jf, 0ll);
    }
  }

  static HRESULT
  name2ID(IDispatch *pIDispatch, const char *prop, DISPID *dispid, long lcid)
  {
    HRESULT hresult;
    USES_CONVERSION;
    LPOLESTR propOle = A2W(prop);
    hresult = pIDispatch->GetIDsOfNames(IID_NULL, (LPOLESTR *)&propOle, 1, lcid, dispid);
    return hresult;
  }

  JNIEXPORT jintArray JNICALL Java_com_jacob_com_Dispatch_getIDsOfNames(JNIEnv *env, jclass clazz, jobject disp, jint lcid, jobjectArray names)
  {
    IDispatch *pIDispatch = extractDispatch(env, disp);
    if (!pIDispatch)
      return NULL;

    int l = env->GetArrayLength(names);
    int i;
    LPOLESTR *lps = (LPOLESTR *)CoTaskMemAlloc(l * sizeof(LPOLESTR));
    DISPID *dispid = (DISPID *)CoTaskMemAlloc(l * sizeof(DISPID));
    for (i = 0; i < l; i++)
    {
      USES_CONVERSION;
      jstring s = (jstring)env->GetObjectArrayElement(names, i);
      // if we used env->GetStringChars() would that let us drop the conversion?
      const char *nm = env->GetStringUTFChars(s, NULL);
      LPOLESTR nmos = A2W(nm);
      env->ReleaseStringUTFChars(s, nm);
      lps[i] = nmos;
      env->DeleteLocalRef(s);
    }
    HRESULT hr = pIDispatch->GetIDsOfNames(IID_NULL, lps, l, lcid, dispid);
    if (FAILED(hr))
    {
      CoTaskMemFree(lps);
      CoTaskMemFree(dispid);
      char buf[1024];
      strcpy_s(buf, "Can't map names to dispid:");
      for (i = 0; i < l; i++)
      {
        USES_CONVERSION;
        jstring s = (jstring)env->GetObjectArrayElement(names, i);
        const char *nm = env->GetStringUTFChars(s, NULL);
        strcat_s(buf, nm);
        env->ReleaseStringUTFChars(s, nm);
        env->DeleteLocalRef(s);
      }
      ThrowComFail(env, buf, hr);
      return NULL;
    }
    jintArray iarr = env->NewIntArray(l);
    // SF 1511033 -- the 2nd parameter should be 0 and not i!
    env->SetIntArrayRegion(iarr, 0, l, dispid);
    CoTaskMemFree(lps);
    CoTaskMemFree(dispid);
    return iarr;
  }

  static char *BasicToCharString(const BSTR inBasicString)
  {
    char *charString = NULL;
    const size_t charStrSize = ::SysStringLen(inBasicString) + 1;
    if (charStrSize > 1)
    {
      charString = new char[charStrSize];
      size_t convertedSize;
      ::wcstombs_s(&convertedSize, charString, charStrSize, inBasicString, charStrSize);
    }
    else
    {
      charString = ::_strdup("");
    }
    return charString;
  }

  static wchar_t *CreateErrorMsgFromResult(HRESULT inResult)
  {
    wchar_t *msg = NULL;
    ::FormatMessageW(FORMAT_MESSAGE_ALLOCATE_BUFFER |
                         FORMAT_MESSAGE_FROM_SYSTEM,
                     NULL, inResult, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), (LPWSTR)&msg, 0, NULL);
    if (msg == NULL)
    {
      const wchar_t *message_text = L"An unknown COM error has occured.";
      size_t bufferLength = (wcslen(message_text) + 1) * sizeof(wchar_t);
      msg = (wchar_t *)::LocalAlloc(LPTR, bufferLength);
      wcscpy_s(msg, bufferLength, message_text);
    }
    // SF 3435567 add HRESULT to error message
    size_t bufferLength = (wcslen(msg) + 100) * sizeof(wchar_t);
    wchar_t *plus = (wchar_t *)::LocalAlloc(LPTR, bufferLength);
    // Had to force this to wide/unicode. We must be missing a macro or setting somewhere
    wsprintfW(plus, L"%x / %s", inResult, msg);

    ::LocalFree(msg);

    return plus;
  }

  static wchar_t *CreateErrorMsgFromInfo(HRESULT inResult, EXCEPINFO *ioInfo,
                                         const char *methName)
  {
    wchar_t *msg = NULL;
    size_t methNameWSize = 0;

    mbstowcs_s(&methNameWSize, NULL, 0, methName, _TRUNCATE);

    wchar_t *methNameW = new wchar_t[methNameWSize];

    mbstowcs_s(NULL, methNameW, methNameWSize, methName, _TRUNCATE);

    // If this is a dispatch exception (triggered by an Invoke message),
    // then we have to take some additional steps to process the error
    // message.
    if (inResult == DISP_E_EXCEPTION)
    {
      // Check to see if the server deferred filling in the exception
      // information.  If so, make the call to populate the structure.
      if (ioInfo->pfnDeferredFillIn != NULL)
        (*(ioInfo->pfnDeferredFillIn))(ioInfo);

      // Build the error message from exception information content.
      int sourceLen = SysStringLen(ioInfo->bstrSource);
      int descLen = SysStringLen(ioInfo->bstrDescription);
      const size_t MSG_LEN = ::wcslen(methNameW) + sourceLen + descLen + 128;
      msg = new wchar_t[MSG_LEN];
      ::wcsncpy_s(msg, MSG_LEN, L"Invoke of: ", wcslen(L"Invoke of: "));
      ::wcsncat_s(msg, MSG_LEN, methNameW, wcslen(methNameW));
      ::wcsncat_s(msg, MSG_LEN, L"\nSource: ", wcslen(L"\nSource: "));
      ::wcsncat_s(msg, MSG_LEN, ioInfo->bstrSource, sourceLen);
      ::wcsncat_s(msg, MSG_LEN, L"\nDescription: ", wcslen(L"\nDescription: "));
      ::wcsncat_s(msg, MSG_LEN, ioInfo->bstrDescription, descLen);
      ::wcsncat_s(msg, MSG_LEN, L"\n", wcslen(L"\n"));
    }
    else
    {
      wchar_t *msg2 = CreateErrorMsgFromResult(inResult);
      const size_t MSG_LEN = ::wcslen(methNameW) + ::wcslen(msg2) + 256;
      msg = new wchar_t[MSG_LEN];
      ::wcsncpy_s(msg, MSG_LEN,
                  L"A COM exception has been encountered:\nAt Invoke of: ",
                  wcslen(L"A COM exception has been encountered:\nAt Invoke of: "));
      ::wcsncat_s(msg, MSG_LEN, methNameW, wcslen(methNameW));
      ::wcsncat_s(msg, MSG_LEN, L"\nDescription: ", wcslen(L"\nDescription: "));
      ::wcsncat_s(msg, MSG_LEN, msg2, wcslen(msg2));
      // jacob-msg 1075 - SF 1053872 : Documentation says "use LocalFree"!!
      // delete msg2;
      LocalFree(msg2);
    }
    delete methNameW;
    return msg;
  }

#define SETDISPPARAMS(dp, numArgs, pvArgs, numNamed, pNamed) \
  {                                                          \
    (dp).cArgs = numArgs;                                    \
    (dp).rgvarg = pvArgs;                                    \
    (dp).cNamedArgs = numNamed;                              \
    (dp).rgdispidNamedArgs = pNamed;                         \
  }

#define SETNOPARAMS(dp) SETDISPPARAMS(dp, 0, NULL, 0, NULL)

  JNIEXPORT jobject JNICALL Java_com_jacob_com_Dispatch_invokev(JNIEnv *env, jclass clazz,
                                                                jobject disp, jstring name, jint dispid,
                                                                jint lcid, jint wFlags, jobjectArray vArg, jintArray uArgErr)
  {
    DISPPARAMS dispparams;
    EXCEPINFO excepInfo;
    // Sourceforge Bug Tracker 2935662 uninitialized data can be not NULL with bad results
    excepInfo.pfnDeferredFillIn = NULL;

    IDispatch *pIDispatch = extractDispatch(env, disp);
    if (!pIDispatch)
      return NULL;

    int dispID = dispid;
    if (name != NULL)
    {
      const char *nm = env->GetStringUTFChars(name, NULL);
      HRESULT hr = name2ID(pIDispatch, nm, (long *)&dispID, lcid);
      env->ReleaseStringUTFChars(name, nm);
      if (FAILED(hr))
      {
        char buf[1024];
        sprintf_s(buf, 1024, "Can't map name to dispid: %s", nm);
        ThrowComFail(env, buf, -1);
        return NULL;
      }
    }

    int num_args = env->GetArrayLength(vArg);
    int i, j;
    VARIANT *varr = NULL;
    if (num_args)
    {
      varr = (VARIANT *)CoTaskMemAlloc(num_args * sizeof(VARIANT));
      /* reverse args for dispatch */
      for (i = num_args - 1, j = 0; 0 <= i; i--, j++)
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
    DISPID dispidPropertyPut = DISPID_PROPERTYPUT;

    // determine how to dispatch
    switch (wFlags)
    {
    case DISPATCH_PROPERTYGET: // GET
    case DISPATCH_METHOD:      // METHOD
    case DISPATCH_METHOD | DISPATCH_PROPERTYGET:
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
    if (count != 0)
    {
      jint *uAE = env->GetIntArrayElements(uArgErr, NULL);
      hr = pIDispatch->Invoke(dispID, IID_NULL,
                              lcid, (WORD)wFlags, &dispparams, v, &excepInfo, (unsigned int *)uAE); // SF 1689061
      env->ReleaseIntArrayElements(uArgErr, uAE, 0);
    }
    else
    {
      hr = pIDispatch->Invoke(dispID, IID_NULL,
                              lcid, (WORD)wFlags, &dispparams, v, &excepInfo, NULL); // SF 1689061
    }
    if (num_args)
    {
      // to account for inouts, I need to copy the inputs back to
      // the java array after the method returns
      // this occurs, for example, in the ADO wrappers
      for (i = num_args - 1, j = 0; 0 <= i; i--, j++)
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

    if (varr)
      CoTaskMemFree(varr);

    // check for error and display a somewhat verbose error message
    if (!SUCCEEDED(hr))
    {
      // two buffers that may have to be freed later
      wchar_t *buf = NULL;
      char *dispIdAsName = NULL;
      // this method can get called with a name or a dispatch id
      // we need to handle both SF 1114159
      if (name != NULL)
      {
        const char *nm = env->GetStringUTFChars(name, NULL);
        buf = CreateErrorMsgFromInfo(hr, &excepInfo, nm);
        env->ReleaseStringUTFChars(name, nm);
      }
      else
      {
        // the dispid was passed in not the name.
        // make space for the id string
        dispIdAsName = new char[256];
        // get the id string
        _itoa_s(dispID, dispIdAsName, 256, 10);
        // continue on mostly as before
        buf = CreateErrorMsgFromInfo(hr, &excepInfo, dispIdAsName);
      }

      // jacob-msg 3696 - SF 1053866
      if (hr == DISP_E_EXCEPTION)
      {
        if (excepInfo.scode != 0)
        {
          hr = excepInfo.scode;
        }
        else
        {
          hr = _com_error::WCodeToHRESULT(excepInfo.wCode);
        }
      }

      ThrowComFailUnicode(env, buf, hr);
      // https://github.com/freemansoft/jacob-project/issues/40
      delete[] buf;
      delete[] dispIdAsName;
      return NULL;
    }

    return newVariant;
  }

  /*
   * Wait method added so folks could wait until a com server terminated
   */
  JNIEXPORT jint JNICALL Java_com_jacob_com_Dispatch_hasExited(JNIEnv *env, jclass clazz, jobject disp, jint dispid, jint lcid)
  {
    IDispatch *pIDispatch = extractDispatch(env, disp);
    if (!pIDispatch)
    {
      // should we return 0?
      return NULL;
    }
    ITypeInfo *v;
    HRESULT hr = pIDispatch->GetTypeInfo(dispid, lcid, &v);
    if (hr == RPC_E_SERVERCALL_RETRYLATER || hr == RPC_E_CALL_REJECTED || hr == 0)
    {
      return 0;
    }
    else
    {
      return 1;
    }
  }
}
