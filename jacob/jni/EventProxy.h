/*
 * Copyright (c) 1999 Dan Adler, 315 E72 St. NY, NY, 10021, USA.
 * mailto:danadler@rcn.com. All rights reserved.
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
 * Redistribution of the JACOB software is not permitted as part of any
 * commercial product that is targeted primarily at Java developers.
 * Such products include, but are not limited to: Java virtual machines,
 * integrated development environments, code libraries, and application
 * server products. Licensing terms for such distribution may be
 * obtained from the copyright holder.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED 
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
#include <jni.h>
#include <windows.h>
#include <objbase.h>
#include <oleauto.h>
#include <olectl.h>
#include "stdafx.h"
#include "util.h"

/*
 * An instance of this class stands between a connection point
 * and a java object. When it gets invoked from the cp, it reflects
 * the call into the java object dynamically. The eventIID is passed
 * in as are the valid dispids and the corresponding names. A map
 * is created between the dispids and the java object's method in
 * the constructor. For now, all the java event methods have to have
 * the same signature: <name>(Variant[])
 */
class EventProxy : public IDispatch
{
private:
  LONG    m_cRef;
  CComPtr<IConnectionPoint> pCP; // the connection point
  DWORD   dwEventCookie; // connection point cookie
  jobject javaSinkObj;   // the java object to delegate calls
  jclass  javaSinkClass; // the java class of the object
  IID     eventIID;      // the interface iid passed in
  int     MethNum;
  CComBSTR *MethName;   // Array of method names
  DISPID   *MethID;     // Array of method ids
  jmethodID *JMethID;   // Array of java method ids
	JavaVM   *jvm;        // The java vm we are running
public:
  // constuct with a global JNI ref to a sink object
  // to which we will delegate event callbacks
  EventProxy(JNIEnv *jenv, jobject aSinkObj, 
        CComPtr<IConnectionPoint> pConn, 
        IID eventIID, CComBSTR *mName, DISPID *mID, int mNum);
  ~EventProxy();

  // IUnknown methods
  STDMETHODIMP_(ULONG) AddRef(void) 
  {
    return InterlockedIncrement(&m_cRef);
  }

  STDMETHODIMP_(ULONG) Release(void)
  {
    LONG res = InterlockedDecrement(&m_cRef);
    if (res == 0) delete this;
    return res;
  }

  STDMETHODIMP QueryInterface(REFIID rid, void **ppv);
   
  // IDispatch methods
  STDMETHODIMP GetTypeInfoCount(UINT *num) 
  {
    *num = 0;
    return S_OK;
  }

  STDMETHODIMP GetTypeInfo(UINT, LCID, ITypeInfo **pptInfo)
  {
    *pptInfo=NULL;
    return E_NOTIMPL;
  }

  // These are the actual supported methods
  STDMETHODIMP GetIDsOfNames(REFIID, OLECHAR **, UINT, LCID , DISPID *);
  STDMETHODIMP Invoke(DISPID, REFIID, LCID, WORD , DISPPARAMS *, VARIANT *, EXCEPINFO *, UINT *);
};
