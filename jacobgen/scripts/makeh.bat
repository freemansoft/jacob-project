@echo off
cls
REM This file uses javah to create the jni headers for a class
REM It will use the verbose naming convention

set JAVA_HOME=D:\j2sdk1.4.2_09
set JAVAH=%JAVA_HOME%\bin\javah
set CLASSPATH=%JAVA_HOME%\lib\classes.zip;src;release

%JAVAH% -d jni -jni com.jacob.jacobgen.TypeLibInspector
pause
