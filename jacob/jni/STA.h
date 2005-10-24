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
/* Header for class STA */

#ifndef _Included_com_jacob_com_STA
#define _Included_com_jacob_com_STA
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_jacob_com_STA
 * Method:    doMessagePump
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_STA_doMessagePump
  (JNIEnv *, jobject);

/*
 * Class:     com_jacob_com_STA
 * Method:    quitMessagePump
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_jacob_com_STA_quitMessagePump
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
