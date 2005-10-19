/*
 * Jacobgen.cpp
 * Copyright (C) 2000 Massimiliano Bigatti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <ole2.h>
#include <tchar.h>
#include "TypeLibInspector.h"



/**
 * Java_com_jacob_jacobgen_TypeLibInspector_queryInterface
 *
 * Main Java function
 */
JNIEXPORT jbyteArray JNICALL Java_com_jacob_jacobgen_TypeLibInspector_queryInterface
(JNIEnv *env, jobject jobj, jstring name) {

	jbyteArray jb;
	jboolean iscopy;
	int cchWideChar;
	LPWSTR wname;
	LPCSTR sname;

	buffer = NULL;
	sname = env->GetStringUTFChars( name, &iscopy );
	cchWideChar = strlen( sname ) * 2;
	wname = (LPWSTR)malloc( cchWideChar );

	//Perform conversion from non Unicode to Unicode string
	int i = MultiByteToWideChar(CP_ACP,0,sname,WC_SEPCHARS,wname,cchWideChar);

	CoInitialize( 0 );
	ExtractTypeLib( wname );
	CoUninitialize();

	if( buffer == NULL ) {
		buffer = (char * )malloc( 1 );
		if( buffer != NULL )
			*buffer = '\0';
	}

	//Construct return Java byte array
	size_t dim = strlen( buffer );
	jb = env->NewByteArray( dim );
	env->SetByteArrayRegion( jb, 0, dim, (jbyte *)buffer );

	return jb;
}
}