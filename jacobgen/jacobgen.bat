@echo off
cls

rem set JAVA_HOME=c:\jdk1.1.8
set JAVA_HOME=c:\Programmi\jdk1.3.1_01
set JRE=%JAVA_HOME%\bin\java

set JACOBGEN_HOME=D:\personale\progetti\Jacobgen
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\classes.zip;%JACOBGEN_HOME%\lib\jacobgen.jar;%JACOBGEN_HOME%\lib\samskivert.jar
set PATH=%PATH%;%JACOBGEN_HOME%\lib

rem echo %CLASSPATH%

%JRE% -Xint it.bigatti.jacobgen.Jacobgen %1 %2 %3 %4 %5
pause
