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
#include "DispatchCallback.h"
#include "CallbackProxy.h"
// Win32 support for Ole Automation
#include <wchar.h>
#include <string.h>
#include <atlbase.h>
#include <objbase.h>
#include <oleauto.h>
#include <olectl.h>

#include "util.h"

extern "C"
{

#define PROXY_FLD "m_pConnPtProxy"

	// defined below
	BOOL GetEventIID(IUnknown*, IID*, CComBSTR **, DISPID **, int *, LPOLESTR);
	BOOL GetEventIIDForTypeLib(BSTR, IID*, CComBSTR **, DISPID **, int *, LPOLESTR);
	BOOL getClassInfoFromProgId(LPOLESTR bsProgId, LPTYPEINFO *pClassInfo);
	BOOL MapEventIIDs(IID*, CComBSTR **, DISPID **, int *, LPOLESTR, LPTYPEINFO);

	// extract a CallbackProxy* from a jobject
	CallbackProxy *CallbackProxyExtractProxy(JNIEnv *env, jobject arg)
	{
		jclass argClass = env->GetObjectClass(arg);
		jfieldID ajf = env->GetFieldID(argClass, PROXY_FLD, "J");
		jlong anum = env->GetLongField(arg, ajf);
		CallbackProxy *v = (CallbackProxy *)anum;
		return v;
	}

	/*
	* pushes the CallbackProxy (*cbp) into tje jobject in the PROXY_FLD location
	*/
	void CallbackProxyPutProxy(JNIEnv *env, jobject arg, CallbackProxy *cbp)
	{
		jclass argClass = env->GetObjectClass(arg);
		jfieldID ajf = env->GetFieldID(argClass, PROXY_FLD, "J");
		jlong anum = env->GetLongField(arg, ajf);
		env->SetLongField(arg, ajf, (jlong)cbp);
	}


	/*
	* Class:     com_jacob_com_DispatchCallback
	* Method:    init3
	* Signature: (Lcom/jacob/com/Dispatch;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V
	*/
	JNIEXPORT void JNICALL Java_com_jacob_com_DispatchCallback_init3
	(JNIEnv *env, jobject _this, jobject sink)
	{
		CallbackProxy *cbp = new CallbackProxy(env, sink);
		// need to store ep on _this, in case it gets collected
		CallbackProxyPutProxy(env, _this, cbp);
	}

	/*
	* Class:     DispatchCallback
	* Method:    release
	* Signature: ()V
	*/
	JNIEXPORT void JNICALL Java_com_jacob_com_DispatchCallback_release
	(JNIEnv *env, jobject _this)
	{
		CallbackProxy *ep = CallbackProxyExtractProxy(env, _this);
		if (ep) {
			ep->Release();
			CallbackProxyPutProxy(env, _this, NULL);
		}
	}

	/*
	* Class:     com_jacob_com_DispatchCallback
	* Method:    registerEvent
	* Signature: (I;Ljava/lang/String;)V
	*/
	JNIEXPORT void JNICALL Java_com_jacob_com_DispatchCallback_registerEvent
	(JNIEnv *env, jobject _this, jint _id, jstring _methodeName)
	{
		if (NULL == _methodeName) {
			ThrowComFail(env, "The mehtod name should not be null.", -1);
			return;
		}

		if (0 == _id) {
			ThrowComFail(env, "The dispatch id should be 0.", -1);
			return;
		}

		CallbackProxy *ep = CallbackProxyExtractProxy(env, _this);
		if (ep) {
			DISPID dispId = _id;
			const char *methodeName = env->GetStringUTFChars(_methodeName, NULL);
			ep->RegisterEvent(dispId, methodeName);
		}
	}
}
