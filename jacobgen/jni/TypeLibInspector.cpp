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
// W2A conversion for converting BSTR to LPCTSTR
#include <atlbase.h>
#include <atlconv.h>
#include "TypeLibInspector.h"

extern "C" 
{
LPTSTR buffer;
LPTSTR current_position;
// SF 1515405 added floting point reference to force load of floting point library
// the append2 down below uses floating point but the compiler doesn't recognize that
float f=1.0f; 


/**
 * Java_com_jacob_jacobgen_TypeLibInspector_queryInterface
 *
 * Main Java function
 */
JNIEXPORT jbyteArray JNICALL Java_com_jacob_jacobgen_TypeLibInspector_queryInterface
(JNIEnv *env, jobject jobj, jstring name) {

	jbyteArray jb;
	jboolean iscopy;
	LPWSTR wname;
	LPCSTR sname;

	buffer = NULL;
	// could we just get the unicode characters to bein with?
	sname = env->GetStringUTFChars( name, &iscopy );
  
    const int size = MultiByteToWideChar(CP_ACP, 0, sname, -1, NULL, 0);   
    wname = (LPWSTR) malloc(sizeof(WCHAR)*size);   

    memset(wname, 0, sizeof(WCHAR)*size); 

	MultiByteToWideChar(CP_ACP, 0, sname, -1, wname, size);
 
	printf("converted %s to %ls\n",sname,wname);

	CoInitialize( 0 );
	printf("calling ExtractTypeLib with %ls",wname);
	ExtractTypeLib( wname );
	CoUninitialize();

	if( buffer == NULL ) {
		buffer = (char * )malloc( 1 );
		if( buffer != NULL ){
			*buffer = '\0';
		}
	}

	//Construct return Java byte array
	size_t dim = strlen( buffer );
	jb = env->NewByteArray( dim );
	env->SetByteArrayRegion( jb, 0, dim, (jbyte *)buffer );

	return jb;
}
/**
 * ExtractTypeLib
 *
 * Extract type lib information from specified file. It produces 
 * formatted output that the Generator is able to convert in source
 * java files.
 *
 */
void ExtractTypeLib( LPWSTR pszFileName )
{
	LPTYPELIB pITypeLib;

	printf("trying to extract library %ls\n", pszFileName);
	buffer = (LPTSTR )malloc( BUF_SIZE );
	if( buffer == NULL )
	{
		_tprintf( _T("Jacobgen: unable to allocate temporary buffer\n") );
		return;
	}
	current_position = buffer;
	HRESULT hr = LoadTypeLib( pszFileName, &pITypeLib );
	if ( S_OK != hr )
	{
		append1( "ERROR: LoadTypeLib failed" );
		return;
	}

	EnumTypeLib( pITypeLib );
	pITypeLib->Release();
}

void EnumTypeLib( LPTYPELIB pITypeLib )
{
	UINT tiCount = pITypeLib->GetTypeInfoCount();
	printf("enumerating %d\n",tiCount);
	//Extract Type lib name
	BSTR pLibName;
	pITypeLib->GetDocumentation(-1, &pLibName, NULL, 0, NULL );
	append2b("TYPELIB %ls\n", pLibName );

    //hr = ptlib->GetDocumentation(-1, &pTypeLibrary->m_bstrName, &pTypeLibrary->m_bstrDocumentation,
    //         &pTypeLibrary->m_ulHelpContext, &pTypeLibrary->m_bstrHelpFile);  

	for ( UINT i = 0; i < tiCount; i++ )
	{
		LPTYPEINFO pITypeInfo;

		HRESULT hr = pITypeLib->GetTypeInfo( i, &pITypeInfo );

		if ( S_OK == hr )
		{
			ExtractTypeInfo( pITypeInfo );
							
			pITypeInfo->Release();
		}
	}
}

void ExtractTypeInfo( LPTYPEINFO pITypeInfo )
{
	HRESULT hr;

	BSTR theGuid = SysAllocString(L"{00000000-0000-0000-0000-000000000000}");

	BSTR pszTypeInfoName;
	hr = pITypeInfo->GetDocumentation(MEMBERID_NIL, &pszTypeInfoName, 0, 0, 0);
	if ( S_OK != hr )
		return;
	printf("retrieved documentation %ls\n",pszTypeInfoName);
	TYPEATTR * pTypeAttr;
	hr = pITypeInfo->GetTypeAttr( &pTypeAttr );
	if ( S_OK != hr )
	{
		printf("failed to get attribute!!!!!!!\n");
		SysFreeString( pszTypeInfoName );
		SysFreeString( theGuid );

		return;
	}

	//append3("CLASS %ls;%ls\n", pszTypeInfoName,
	append3("CLASS %ls;%s\n", pszTypeInfoName,
		GetTypeKindName(pTypeAttr->typekind) );

	StringFromGUID2(pTypeAttr->guid, theGuid, 39);
	append2b("GUID;%ls",theGuid );
	append1("\n");

	if( pTypeAttr->typekind == TKIND_ALIAS ) {
		TYPEDESC tdesc;
		tdesc = pTypeAttr->tdescAlias;

		if( tdesc.vt == VT_PTR ) {
			tdesc = *tdesc.lptdesc;
		}

		if( tdesc.vt == VT_USERDEFINED ) {
			BSTR pszRefFuncName = GetUserDefinedType( pITypeInfo, tdesc );
			if( pszRefFuncName ) {
				append2b("EXTENDS;%ls\n",pszRefFuncName );
				SysFreeString( pszRefFuncName );
			}
		} else {
			append2("%EXTENDS;%ls\n",GetVarTypeName( tdesc.vt ) );
		}
	} else
		EnumTypeInfoMembers( pITypeInfo, pTypeAttr );

	SysFreeString( pszTypeInfoName );
	SysFreeString( theGuid );
		
	pITypeInfo->ReleaseTypeAttr( pTypeAttr );
}

void EnumTypeInfoMembers( LPTYPEINFO pITypeInfo, LPTYPEATTR pTypeAttr  )
{
	FUNCDESC * pFuncDesc;
	BSTR pszFuncName;		
	ELEMDESC elemdesc;
	TYPEDESC tdesc;

	if ( pTypeAttr->cImplTypes ) {
		for ( unsigned i = 0; i < pTypeAttr->cImplTypes; i++ ) {
			
			LPTYPEINFO pImplInfo;
			HREFTYPE hRefType;

			pITypeInfo->GetRefTypeOfImplType( i, &hRefType );
			pITypeInfo->GetRefTypeInfo( hRefType, &pImplInfo );

			if( pImplInfo != NULL ) {
				pImplInfo->GetDocumentation(MEMBERID_NIL, &pszFuncName,0,0,0);
				append2b("EXTENDS;%ls",pszFuncName );
			}
			append1("\n");
			SysFreeString( pszFuncName );
		}
	}

	if ( pTypeAttr->cFuncs ) {
		for ( unsigned i = 0; i < pTypeAttr->cFuncs; i++ ) {
			
			pITypeInfo->GetFuncDesc( i, &pFuncDesc );
			pITypeInfo->GetDocumentation(pFuncDesc->memid, &pszFuncName,0,0,0);

			append2("%ls;", GetInvokeKindName(pFuncDesc->invkind) );

			elemdesc = pFuncDesc->elemdescFunc;
			//PARAMDESC pdesc;					//PENDING
			//pdesc = elemdesc.paramdesc;
			tdesc = elemdesc.tdesc;

			//If type type is pointer, dereference
			if( tdesc.vt == VT_PTR ) {
				tdesc = *tdesc.lptdesc;
			}

			//Check for user defined types
			//append2("%ls",GetVarTypeName( tdesc.vt ) );
			if( tdesc.vt == VT_USERDEFINED ) {
				//append1( "^" );
				BSTR pszRefFuncName = GetUserDefinedType( pITypeInfo, tdesc );
				if( pszRefFuncName ) {
					append2b("%ls",pszRefFuncName );
					SysFreeString( pszRefFuncName );
				}
			} else
				append2("%ls",GetVarTypeName( tdesc.vt ) );

			append2b(" ;%ls;",pszFuncName );

			//Extract function parameters
			EnumParameters( pITypeInfo, pFuncDesc );
				
			pITypeInfo->ReleaseFuncDesc( pFuncDesc );						
			SysFreeString( pszFuncName );
		}
	}

	if ( pTypeAttr->cVars )
	{
		
		for ( unsigned i = 0; i < pTypeAttr->cVars; i++ )
		{
			VARDESC * pVarDesc;
			
			pITypeInfo->GetVarDesc( i, &pVarDesc );
			
			BSTR pszVarName;		
			pITypeInfo->GetDocumentation(pVarDesc->memid, &pszVarName,0,0,0);
			VARIANT *pvarValue = pVarDesc->lpvarValue;

			//_tprintf( _T("    %ls\n"), pszVarName );
			append2("%ls;", GetVarKindName( pVarDesc->varkind ) );
			append2b("%ls;",pszVarName );

			if( pVarDesc->varkind == VAR_CONST ) {
				if(pvarValue!=NULL)
					append2("%ls;", GetVarTypeName( pvarValue->vt ) );
				else
					append1("UNKNOWN" );

				LPTSTR s = GetVarDefaultValue( pvarValue  );
				append1( s );
				free( (void *)s );
			} else {
				if( pVarDesc->varkind == VAR_DISPATCH ) {

					elemdesc = pVarDesc->elemdescVar;
					//PARAMDESC pdesc;					//PENDING
					//pdesc = elemdesc.paramdesc;
					tdesc = elemdesc.tdesc;

					//If type type is pointer, dereference
					if( tdesc.vt == VT_PTR ) {
						tdesc = *tdesc.lptdesc;
					}

					//Check for user defined types
					//append2("%ls",GetVarTypeName( tdesc.vt ) );
					if( tdesc.vt == VT_USERDEFINED ) {
						//append1( "^" );
						BSTR pszRefFuncName = GetUserDefinedType( pITypeInfo, tdesc );
						if( pszRefFuncName ) {
							append2b("%ls",pszRefFuncName );
							SysFreeString( pszRefFuncName );
						}
					} else {
						append2("%ls",GetVarTypeName( tdesc.vt ) );
					}
				}
			}

			append1("\n");

			pITypeInfo->ReleaseVarDesc( pVarDesc );						
			SysFreeString( pszVarName );
		}
	}

}

/**
 * For a USERDEFINED typedesc, extract the referred type
 */
BSTR GetUserDefinedType( LPTYPEINFO pITypeInfo, TYPEDESC tdesc ) {
	HREFTYPE htype;
	LPTYPEINFO ppTInfo = NULL;
	HRESULT hr;
	BSTR pszRefFuncName = NULL;

	if( tdesc.vt == VT_USERDEFINED ) {
		htype = tdesc.hreftype;

		hr = pITypeInfo->GetRefTypeInfo( htype, &ppTInfo );
		if( ppTInfo )
			ppTInfo->GetDocumentation(MEMBERID_NIL, &pszRefFuncName, 0, 0, 0);
	}

	return pszRefFuncName;
}

void EnumParameters( ITypeInfo *pTypeInfo, FUNCDESC *pFuncDesc ) {
	TYPEDESC tdesc;

	unsigned int cMaxNames = pFuncDesc->cParams;
	if (cMaxNames > 0)
		cMaxNames++;
	unsigned int pcNames;

	BSTR rgBstrNames[ 100 ];
	MEMBERID pMemId[ 100 ];

	pTypeInfo->GetNames( pFuncDesc->memid, rgBstrNames, cMaxNames, &pcNames );
	pTypeInfo->GetIDsOfNames( rgBstrNames, pcNames, pMemId );

	append1( "[" );
	if (pcNames > 0) {

		for ( unsigned k = 1; k < pcNames; k++ )
		{
			BSTR pszParName = rgBstrNames[ k ];
			
			PARAMDESC pd = pFuncDesc->lprgelemdescParam[k-1].paramdesc;

			if( pd.wParamFlags != 0 ) {
				append1( "{" );

				if( pd.wParamFlags & PARAMFLAG_FIN )
					append1( "in-" );
				if( pd.wParamFlags & PARAMFLAG_FOUT )
					append1( "out-" );
				if( pd.wParamFlags & PARAMFLAG_FRETVAL )
					append1("retval-" );
				if( pd.wParamFlags & PARAMFLAG_FOPT )
					append1("optional-" );

				append1( "}" );
			} 

			/*
			VARTYPE vt = pFuncDesc->lprgelemdescParam[k].tdesc.vt;
			if( vt == VT_PTR ) {
				TYPEDESC *pPointedAt = pFuncDesc->lprgelemdescParam[k].tdesc.lptdesc;
				vt = pPointedAt->vt;
				append2( "%ls", GetVarTypeName( vt ) );
			} else if( vt == VT_SAFEARRAY ) {
				TYPEDESC *pPointedAt = pFuncDesc->lprgelemdescParam[k].tdesc.lptdesc;
				vt = pPointedAt->vt;
				append2( "SAFEARRAY(%ls)", GetVarTypeName( vt ) );
			} else {
				append2( "%ls", GetVarTypeName( vt ) );
			}
			*/


			tdesc = pFuncDesc->lprgelemdescParam[k-1].tdesc;

			bool isPointer = false;
			VARTYPE vt = NULL;
			if( tdesc.vt == VT_PTR ) {
				isPointer = true;
				tdesc = *tdesc.lptdesc;

				TYPEDESC *pPointedAt = tdesc.lptdesc;
				if (pPointedAt) {
					vt = pPointedAt->vt;
				}
			}
			if (vt) {
				append2( "%ls", GetVarTypeName( vt ) );
			} else {
				//Check for user defined types
				if( tdesc.vt == VT_USERDEFINED ) {
					BSTR pszRefFuncName = GetUserDefinedType( pTypeInfo, tdesc );
					if( pszRefFuncName ) {
						append2b("%ls",pszRefFuncName );
						SysFreeString( pszRefFuncName );
					}
				} else { 
					if (isPointer && (tdesc.vt == VT_VARIANT)) {
						append1("VariantVariant");
					} else {
						append2("%ls",GetVarTypeName( tdesc.vt ) );
					}
				}
			}

			if( k < pcNames-1 )
				append2b( " %ls,", rgBstrNames[k] );
			else
				append2b( " %ls", rgBstrNames[k] );
			
		}
	}
	append1("]\n" );
}

/**
 * Append a char array to destination buffer
 */
void append1( LPTSTR string ) {
	LPTSTR s;
	
	s = string;
	while( *s != '\0' ) {		//TODO: add bound check
		*current_position = *s;
		s++;
		current_position++;
	}

	*current_position = '\0';
}

void append2b ( LPTSTR format, BSTR s1){
	append2c(format,s1);
}

void append2c ( LPTSTR format, LPCWSTR s1){
	LPTSTR s;
	//printf("append2c: '%s' %ls -- ",format, s1);
	size_t dim = (wcslen( s1 )+ strlen(format)+1) * 2;
	//printf("allocating %d -- ", dim);
	//size_t dim = _tcslen( s1 ) * sizeof(_TCHAR) * 2;

	if( (dim > 0) ) {
		s = (LPTSTR )malloc( dim );
		if( s != NULL ) {
			*(s + dim - 1) = '\0';
			sprintf( s, format, s1 );
			//printf("generated %s\n", s);
			append1( s );
			free( s );
		}
	}
}

/**
 * Copy a generic text string to a char array and copies it
 * to destination array
 */
void append2( LPTSTR format, LPCTSTR s1 ) {
	//printf("append2 %s\n",s1);
	USES_CONVERSION;
	append2c(format,A2W(s1));
}

/**
 * Copy two generic text strings to a char array and copies it
 * to destination array
 */
void append3( LPTSTR format, BSTR s1, LPCTSTR s2 ) {
	LPCWSTR wideS2;
	LPTSTR s;
	USES_CONVERSION;
	wideS2 = A2W(s2);
	int formatLength = strlen(format);
	int width1 = wcslen(s1);
	int width2 = wcslen(wideS2);
	size_t dim = ( formatLength+ width1 + width2 +1  ) * 2;
	//size_t dim = ( _tcslen( s1 ) + _tcslen( wideS2 ) ) * sizeof(_TCHAR) * 2;

	if( width1 > 0 && width2 > 0 ) {
		s = (LPTSTR )malloc( dim );
		if( s != NULL ) {
			*(s + dim - 1) = '\0';
			sprintf( s, format, s1, s2 );
			append1( s );
			free( s );
		}
	}
}


#define CASE_STRING( x ) case x: s = _T(#x); break;

LPCTSTR GetTypeKindName( TYPEKIND typekind )
{
	LPTSTR s = _T("<unknown>");
	
	switch( typekind )
	{
    	CASE_STRING( TKIND_ENUM )
    	CASE_STRING( TKIND_RECORD )
    	CASE_STRING( TKIND_MODULE )
    	CASE_STRING( TKIND_INTERFACE )
    	CASE_STRING( TKIND_DISPATCH )
    	CASE_STRING( TKIND_COCLASS )
    	CASE_STRING( TKIND_ALIAS )
    	CASE_STRING( TKIND_UNION )
	}
		printf("found type %s\n");
	
	return s;
}

LPCTSTR GetInvokeKindName( INVOKEKIND invkind )
{
	LPTSTR s = _T("<unknown>");
	
	switch( invkind )
	{
    	CASE_STRING( INVOKE_FUNC )
		CASE_STRING( INVOKE_PROPERTYGET )
    	CASE_STRING( INVOKE_PROPERTYPUT )
    	CASE_STRING( INVOKE_PROPERTYPUTREF )
	}	

	return s;
}

LPCTSTR GetVarTypeName( VARTYPE vt )
{
	LPTSTR s = _T("");
	//LPTSTR s = _T("");
	
	switch( vt )
	{
    	CASE_STRING( VT_EMPTY )
    	CASE_STRING( VT_NULL )
    	CASE_STRING( VT_I2 )
    	CASE_STRING( VT_I4 )
    	CASE_STRING( VT_R4 )
    	CASE_STRING( VT_R8 )
    	CASE_STRING( VT_CY )
    	CASE_STRING( VT_DATE )
    	CASE_STRING( VT_BSTR )
    	CASE_STRING( VT_DISPATCH )	// IDispatch FAR*
    	CASE_STRING( VT_ERROR )		// Scodes
    	CASE_STRING( VT_BOOL )
    	CASE_STRING( VT_VARIANT )
    	CASE_STRING( VT_UNKNOWN )	// IUnknown FAR*
    	CASE_STRING( VT_I1 ) // Signed char.
    	CASE_STRING( VT_UI1 ) // Unsigned char.
    	CASE_STRING( VT_UI2 ) // Unsigned short.
    	CASE_STRING( VT_UI4 ) // Unsigned short. 
    	CASE_STRING( VT_I8 ) // Signed 64-bit int.
    	CASE_STRING( VT_UI8 ) // Unsigned 64-bit int.
    	CASE_STRING( VT_INT ) // Signed machine int.
    	CASE_STRING( VT_UINT ) // Unsigned machine int.
    	CASE_STRING( VT_VOID ) // C-style void.
    	CASE_STRING( VT_HRESULT )                                    
    	CASE_STRING( VT_PTR ) // Pointer type.
    	CASE_STRING( VT_SAFEARRAY ) // Use VT_ARRAY in VARIANT.
    	CASE_STRING( VT_CARRAY ) // C-style array.
    	CASE_STRING( VT_USERDEFINED ) // User-defined type.
    	CASE_STRING( VT_LPSTR ) // Null-terminated string.
    	CASE_STRING( VT_LPWSTR ) // Wide null-terminated string.
    	CASE_STRING( VT_FILETIME ) // FILETIME.
    	CASE_STRING( VT_BLOB ) // Length-prefixed bytes.
    	CASE_STRING( VT_STREAM ) // Name of the stream follows.
    	CASE_STRING( VT_STORAGE ) // Name of the storage follows.
    	CASE_STRING( VT_STREAMED_OBJECT ) // Stream contains an object.
    	CASE_STRING( VT_STORED_OBJECT ) // Storage contains an object.
    	CASE_STRING( VT_BLOB_OBJECT ) // Blob contains an object.
    	CASE_STRING( VT_CF ) // Clipboard format.
    	CASE_STRING( VT_CLSID ) // A class ID.
    	CASE_STRING( VT_VECTOR ) // Simple counted array.
    	CASE_STRING( VT_ARRAY )// SAFEARRAY*.
    	CASE_STRING( VT_BYREF )
    	CASE_STRING( VT_RESERVED )
		
		//CASE_STRING( VT_USERDEFINED )
    	//CASE_STRING( VT_RESERVED )
    	//CASE_STRING( VT_BYREF )		// A pointer to data is passed
    	//CASE_STRING( VT_ARRAY )		// A safe array of the data is passed
	}

	return s;
}

LPCTSTR GetVarKindName( VARKIND vk )
{
	LPTSTR s = _T("<unknown>");
	
	switch( vk )
	{
    	CASE_STRING( VAR_PERINSTANCE )
		CASE_STRING( VAR_STATIC )
		CASE_STRING( VAR_CONST )
		CASE_STRING( VAR_DISPATCH )
	}

	return s;
}

LPTSTR  GetVarDefaultValue( VARIANT *pv )
{
	LPTSTR buf = (LPTSTR )malloc( 128 );
	
	if( buf != NULL ) {
		switch( pv->vt )
		{
			case VT_I4: sprintf(buf, "%ld", pv->lVal ); break;
		}
	}

	return buf;
}

}