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
#ifndef _Included_com_jacob_jacobgen_JacobgenA
#define _Included_com_jacob_jacobgen_JacobgenA

void ExtractTypeLib( LPCTSTR pszFileName );
void EnumTypeLib( LPTYPELIB pITypeLib );
void ExtractTypeInfo( LPTYPEINFO pITypeInfo );
void EnumTypeInfoMembers( LPTYPEINFO pITypeInfo, LPTYPEATTR pTypeAttr);
void EnumParameters( ITypeInfo *pTypeInfo, FUNCDESC *f );

BSTR GetUserDefinedType( LPTYPEINFO pITypeInfo, TYPEDESC tdesc );

LPCTSTR GetTypeKindName( TYPEKIND typekind );
LPCTSTR GetInvokeKindName( INVOKEKIND invkind );
LPCTSTR GetVarTypeName( VARTYPE vt );
LPCTSTR GetVarKindName( VARKIND vk );
char * GetVarDefaultValue( VARIANT *pv );

void append1( char *string );
void append2( char *format, LPCTSTR s1 );
void append3( char *format, LPCTSTR s1, LPCTSTR s2 );

//Output buffer size
#define BUF_SIZE 5000000

#endif
