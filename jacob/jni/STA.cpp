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

JNIEXPORT void JNICALL Java_com_jacob_com_STA_doMessagePump
  (JNIEnv *env, jobject obj)
{
  // store the current thread id so we can kill it
  jclass argClass = env->GetObjectClass(obj);
  jfieldID ajf = env->GetFieldID( argClass, "threadID", "I");
  jint threadID = (jint)GetCurrentThreadId();
  env->SetIntField(obj, ajf, threadID);

  MSG msg;

  ZeroMemory(&msg, sizeof(msg));
  msg.wParam = S_OK;

  while (GetMessage(&msg, NULL, 0, 0))
  {
    DispatchMessage(&msg);
  }
}

JNIEXPORT void JNICALL Java_com_jacob_com_STA_quitMessagePump
  (JNIEnv *env, jobject obj)
{
  jclass argClass = env->GetObjectClass(obj);
  jfieldID ajf = env->GetFieldID( argClass, "threadID", "I");
  jint threadID = env->GetIntField(obj, ajf);
  PostThreadMessage((DWORD)threadID, WM_QUIT, 0, 0);
}

}
