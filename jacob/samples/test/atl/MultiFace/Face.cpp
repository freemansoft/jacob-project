// Face.cpp : Implementation of CMultiFaceApp and DLL registration.

#include "stdafx.h"
#include "MultiFace.h"
#include "Face.h"

/////////////////////////////////////////////////////////////////////////////
//

STDMETHODIMP Face::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* arr[] = 
	{
		&IID_IFace1,
		&IID_IFace2,
		&IID_IFace3,
	};

	for (int i=0;i<sizeof(arr)/sizeof(arr[0]);i++)
	{
		if (InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}

STDMETHODIMP Face::get_Face1Name(BSTR *pVal)
{
	// TODO: Add your implementation code here
	*pVal = name1;
	return S_OK;
}

STDMETHODIMP Face::put_Face1Name(BSTR newVal)
{
	// TODO: Add your implementation code here
	name1 = newVal;
	return S_OK;
}

STDMETHODIMP Face::get_Face2Nam(BSTR *pVal)
{
	// TODO: Add your implementation code here
	*pVal = name2;
	return S_OK;
}

STDMETHODIMP Face::put_Face2Nam(BSTR newVal)
{
	// TODO: Add your implementation code here
	name2 = newVal;
	return S_OK;
}

STDMETHODIMP Face::get_Face3Name(BSTR *pVal)
{
	// TODO: Add your implementation code here
	*pVal = name3;
	return S_OK;
}

STDMETHODIMP Face::put_Face3Name(BSTR newVal)
{
	// TODO: Add your implementation code here
	name3 = newVal;
	return S_OK;
}
