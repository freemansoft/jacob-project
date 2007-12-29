/*
 * Jacobgen.h
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
#include <jni.h>
/* Header for class com_jacob_jacobgen_TypeLibInspector */

#ifndef _Included_com_jacob_jacobgen_TypeLibInspector
#define _Included_com_jacob_jacobgen_TypeLibInspector
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_jacob_jacobgen_TypeLibInspector
 * Method:    queryInterface
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_jacob_jacobgen_TypeLibInspector_queryInterface
  (JNIEnv *, jobject, jstring);

void ExtractTypeLib( LPWSTR pszFileName );
void EnumTypeLib( LPTYPELIB pITypeLib );
void ExtractTypeInfo( LPTYPEINFO pITypeInfo );
void EnumTypeInfoMembers( LPTYPEINFO pITypeInfo, LPTYPEATTR pTypeAttr);
void EnumParameters( ITypeInfo *pTypeInfo, FUNCDESC *f );

BSTR GetUserDefinedType( LPTYPEINFO pITypeInfo, TYPEDESC tdesc );

LPCTSTR GetTypeKindName( TYPEKIND typekind );
LPCTSTR GetInvokeKindName( INVOKEKIND invkind );
LPCTSTR GetVarTypeName( VARTYPE vt );
LPCTSTR GetVarKindName( VARKIND vk );
LPTSTR GetVarDefaultValue( VARIANT *pv );

void append1( LPTSTR string );
void append2( LPTSTR format, LPCTSTR s1 );
void append2b( LPTSTR format, BSTR s1);
void append2c ( LPTSTR format, LPCWSTR s1);
void append3( LPTSTR format, BSTR bstr1, LPCTSTR s2 );

//Output buffer size
#define BUF_SIZE 5000000


#ifdef __cplusplus
}
#endif
#endif
