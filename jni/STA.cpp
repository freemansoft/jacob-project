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
