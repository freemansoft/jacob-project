For documentation see: http://users.rcn.com/danadler/jacob/

The java code is in com\jacob\*.

The C++ code is in .\jni.

To build, run:

nmake -f makefile

That will first compile the JAVA files and build the JAR file, and
then compile the C++ code and build the DLL.

Building the jni:

The following are hardcoded into the makefile, so you may have to
change it. I developed this with the C++ compiler and ATL version
that ship with VC++ 6.0, so I'm not sure if different versions will
work.

I have compiled this against JDK 1.1.6 and 1.2.2 as well as Microsoft
SDK for Java 3.2 as the JDK setting.

DEST_DIR is a destination directory into which to copy the final DLL.

JDK = d:\j2sdk1.4.2_06
DEST_DIR = d:\jacob
MSDEVDIR = d:\apps\\"Microsoft Visual Studio"\VC98
