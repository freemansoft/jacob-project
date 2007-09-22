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
#include "Dispatch.h"
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

void ThrowComFail(JNIEnv *env, const char* desc, jint hr)
{
  jclass failClass = env->FindClass("com/jacob/com/ComFailException");
  // call the constructor that takes hr and message
  jmethodID failCons = 
     env->GetMethodID(failClass, "<init>", "(ILjava/lang/String;)V");
  if (!desc) {
	  desc = "Java/COM Error";
  }
  jstring js = env->NewStringUTF(desc);
  jthrowable fail = (jthrowable)env->NewObject(failClass, failCons, hr, js);
  env->Throw(fail);
}

void ThrowComFailUnicode(JNIEnv *env, const wchar_t* desc, jint hr)
{
  if (!desc) {
	  ThrowComFail(env, "Java/COM Error", hr);
  }
  jclass failClass = env->FindClass("com/jacob/com/ComFailException");
  // call the constructor that takes hr and message
  jmethodID failCons = 
     env->GetMethodID(failClass, "<init>", "(ILjava/lang/String;)V");
  jstring js = env->NewString((const jchar *) desc, wcslen(desc));
  jthrowable fail = (jthrowable)env->NewObject(failClass, failCons, hr, js);
  env->Throw(fail);
}

// if env's are different throw on the 1st env
int CheckEnv(JNIEnv *env1, JNIEnv *env2)
{
  if (env1 != env2) {
    jclass failClass = env1->FindClass("com/jacob/com/WrongThreadException");
    // call the constructor that takes hr and message
    jmethodID failCons = 
       env1->GetMethodID(failClass, "<init>", "()V");
    env1->ThrowNew(failClass, "Wrong Thread");
		return 0;
  }
	return 1;
}

}
