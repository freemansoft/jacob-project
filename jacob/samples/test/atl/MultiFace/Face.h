// Face.h: Definition of the Face class
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_FACE_H__9BF24413_B2E0_11D4_A695_00104BFF3241__INCLUDED_)
#define AFX_FACE_H__9BF24413_B2E0_11D4_A695_00104BFF3241__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "resource.h"       // main symbols

/////////////////////////////////////////////////////////////////////////////
// Face

class Face : 
	public IDispatchImpl<IFace1, &IID_IFace1, &LIBID_MULTIFACELib>, 
	public IDispatchImpl<IFace2, &IID_IFace2, &LIBID_MULTIFACELib>, 
	public IDispatchImpl<IFace3, &IID_IFace3, &LIBID_MULTIFACELib>, 
	public ISupportErrorInfo,
	public CComObjectRoot,
	public CComCoClass<Face,&CLSID_Face>
{
// IFace1
private:
    CComBSTR name1;

// IFace2
	CComBSTR name2;

// IFace3
	CComBSTR name3;

public:
	Face() {}
BEGIN_COM_MAP(Face)
	COM_INTERFACE_ENTRY2(IDispatch, IFace1)
	COM_INTERFACE_ENTRY(IFace1)
	COM_INTERFACE_ENTRY(IFace2)
	COM_INTERFACE_ENTRY(IFace3)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
END_COM_MAP()
//DECLARE_NOT_AGGREGATABLE(Face) 
// Remove the comment from the line above if you don't want your object to 
// support aggregation. 

DECLARE_REGISTRY_RESOURCEID(IDR_Face)
// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);



public:
	STDMETHOD(get_Face3Name)(/*[out, retval]*/ BSTR *pVal);
	STDMETHOD(put_Face3Name)(/*[in]*/ BSTR newVal);
	STDMETHOD(get_Face2Nam)(/*[out, retval]*/ BSTR *pVal);
	STDMETHOD(put_Face2Nam)(/*[in]*/ BSTR newVal);
	STDMETHOD(get_Face1Name)(/*[out, retval]*/ BSTR *pVal);
	STDMETHOD(put_Face1Name)(/*[in]*/ BSTR newVal);
};

#endif // !defined(AFX_FACE_H__9BF24413_B2E0_11D4_A695_00104BFF3241__INCLUDED_)
