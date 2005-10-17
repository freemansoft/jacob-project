Jacobgen
Version 0.4
Copyright (C) 2001-2002 Massimiliano Bigatti
============================================
README


FEATURES
--------
+ TLB/OLB/DLL inspection via native code
+ IN/OUT/OPTIONAL parameters support

+ Office8/Excel8/Word8 wrapper generation
+ MSMQ 2.0 wrapper generation (Almost)
+ Lotus Domino wrapper generation (Almost)


NOTES
-----
+ SafeArray by reference are passed as arrays
+ MsoRGBType mapped to int
+ Enum types mapped to int


KNOWN LIMITATIONS
-----------------
+ EVENTS and STRUCTURES. Not supported
+ VT_PTR type is not well supported


Application LIMITATIONS
----
+ Acrobat Reader. Jacobgen is unable to parse Acrobat Reader OCX. I'm still investigating
  this issue. By now you need to wrap it inside an another COM component.


CREDITS
-------
+ Jacobgen out parameters are contrib of Hannes Reisinger
+ Jacobgen now depends on Sam Skivert library and viztool (only some classes are
  provided with Jacobgen - license is GPL and LGPL)
