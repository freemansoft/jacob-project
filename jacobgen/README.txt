============================================
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

TODO
----
Javadoc generation
Events
Structures
Better Array support
VT_PTR support

============================================
Jacobgen
Version 0.4
Copyright (C) 2001-2002 Massimiliano Bigatti
============================================
HISTORY (**)

Version 0.4 (04/03/02)
----------------------
+ UPGRADE: support for middle argument list optional parameters (with Variant.noParam())
+ UPGRADE: support for out-parameters (thanks to Hannes Reisinger)
+ UPGRADE: MSMQ 2.0 support. Almost.
+ UPGRADE: now you can check tlbs using TypeLibInspector.main(), see testJacobgendll.bat
+ BUG-FIX: corrected class attributes (only for VAR_CONST)
+ BUG-FIX: corrected return code for SafeArray type (thanks to Bill Kelemen)
+ UPGRADE: added AliasGenerator to create TKIND_ALIAS classes (reported by Richard Range)
+ UPGRADE: alias of enums support

Version 0.3 (04/01/02)
----------------------
+ BUG-FIX: change to Dispatch.call() to support more than 8 method parameters
+ UPGRADE: COM Date support (thanks to rev)
+ UPGRADE: CLASSPATH searching for type referenced outside the component. 
           Warning: classes searching is based on class name. If different jars or
           packgages contains the same class the result is unknown.
+ BUG-FIX: better Enum identification. Now works with Excel8
+ BUG-FIX: Excel8 caused VM crash due to buffer size limitations
+ BUG-FIX: the generator didn't produce last class in TLB file (thanks to Liz Heine)

Version 0.2
Version 0.1

(**) Dates are in italian format (dd/mm/yy)
  