/* this file contains the actual definitions of */
/* the IIDs and CLSIDs */

/* link this file in with the server and any clients */


/* File created by MIDL compiler version 5.01.0164 */
/* at Sun Nov 05 01:12:47 2000
 */
/* Compiler settings for D:\jacob_15\samples\test\atl\MultiFace\MultiFace.idl:
    Oicf (OptLev=i2), W1, Zp8, env=Win32, ms_ext, c_ext
    error checks: allocation ref bounds_check enum stub_data 
*/
//@@MIDL_FILE_HEADING(  )
#ifdef __cplusplus
extern "C"{
#endif 


#ifndef __IID_DEFINED__
#define __IID_DEFINED__

typedef struct _IID
{
    unsigned long x;
    unsigned short s1;
    unsigned short s2;
    unsigned char  c[8];
} IID;

#endif // __IID_DEFINED__

#ifndef CLSID_DEFINED
#define CLSID_DEFINED
typedef IID CLSID;
#endif // CLSID_DEFINED

const IID IID_IFace1 = {0x9BF2440F,0xB2E0,0x11D4,{0xA6,0x95,0x00,0x10,0x4B,0xFF,0x32,0x41}};


const IID IID_IFace2 = {0x9BF24410,0xB2E0,0x11D4,{0xA6,0x95,0x00,0x10,0x4B,0xFF,0x32,0x41}};


const IID IID_IFace3 = {0x9BF24411,0xB2E0,0x11D4,{0xA6,0x95,0x00,0x10,0x4B,0xFF,0x32,0x41}};


const IID LIBID_MULTIFACELib = {0x9BF24403,0xB2E0,0x11D4,{0xA6,0x95,0x00,0x10,0x4B,0xFF,0x32,0x41}};


const CLSID CLSID_Face = {0x9BF24412,0xB2E0,0x11D4,{0xA6,0x95,0x00,0x10,0x4B,0xFF,0x32,0x41}};


#ifdef __cplusplus
}
#endif

