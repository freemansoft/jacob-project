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
  if (!desc) desc = "Java/COM Error";
  jstring js = env->NewStringUTF(desc);
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
