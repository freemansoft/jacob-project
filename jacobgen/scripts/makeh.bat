@echo off
cls

set JAVA_HOME=c:\jdk1.2.2
set JAVAH=%JAVA_HOME%\bin\javah
set CLASSPATH=%JAVA_HOME%\lib\classes.zip;src;bin

%JAVAH% -d jni -jni it.bigatti.jacobgen.TypeLibInspector
pause
