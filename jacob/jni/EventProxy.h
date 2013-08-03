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
#include <jni.h>
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
  int     connected;
  LONG    m_cRef;		// a reference counter
  CComPtr<IConnectionPoint> pCP; // the connection point
  DWORD   dwEventCookie; // connection point cookie
  jobject javaSinkObj;   // the java object to delegate calls

  IID     eventIID;      // the interface iid passed in
  int     MethNum;		// number of methods in the callback interface
  CComBSTR *MethName;   // Array of method names
  DISPID   *MethID;     // Array of method ids, used to match invokations to method names
  JavaVM   *jvm;        // The java vm we are running
  void convertJavaVariant(VARIANT *java, VARIANT *com);
  void Connect(JNIEnv *env);
public:
  // constuct with a global JNI ref to a sink object
  // to which we will delegate event callbacks
  EventProxy(JNIEnv *jenv, 
  		jobject aSinkObj, 
        CComPtr<IConnectionPoint> pConn, 
        IID eventIID, 
        CComBSTR *mName, 
        DISPID *mID, 
        int mNum);
  ~EventProxy();

  // IUnknown methods
  STDMETHODIMP_(ULONG) AddRef(void) 
  {
    LONG res = InterlockedIncrement(&m_cRef);
    return res;
  }

  STDMETHODIMP_(ULONG) Release(void)
  {
    LONG res = InterlockedDecrement(&m_cRef);
    if (res == 0) {
    	delete this;
    }
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
  // SF 3412922 make public to support cleanup from DispatchEvents
  void Disconnect();

};
