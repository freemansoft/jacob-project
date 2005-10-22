@echo off
cls

REM run this from the root directory of the Jacobgen project
REM it will spit out the interface classes for a dll you pass in as a parameter
REM sample command line while sitting in the JACOBGEN project directory
REM $ scripts/jacobgen.bat -destdir:foo -listfile:foo.txt -package:com.jacobgen.test "C:\Program Files\Common Files\Microsoft Shared\VBA\VBA6\VBE6.dll"
set JAVA_HOME=D:\j2sdk1.4.2_09
set JRE=%JAVA_HOME%\bin\java

set JACOBGEN_HOME=.
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\dt.jar;%JACOBGEN_HOME%\release\jacobgen.jar;%JACOBGEN_HOME%\lib\samskivert.jar
set PATH=%PATH%;%JACOBGEN_HOME%\release

rem echo %CLASSPATH%

%JRE% -Xint com.jacob.jacobgen.Jacobgen %1 %2 %3 %4 %5
pause
