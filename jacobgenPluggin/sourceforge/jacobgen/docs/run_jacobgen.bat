@echo off
cls

REM run this from the root directory of the Jacobgen project
REM it will spit out the interface classes for a dll you pass in as a parameter
REM sample command line while sitting in the JACOBGEN project directory
REM
REM The following command built a sample in the jacob directory I have 
REM installed near my jacobgen project directory.
REM $ docs/run_jacobgen.bat -destdir:"..\jacob\samples" -listfile:"jacobgenlog.txt" -package:com.jacobgen.microsoft.msword "C:\Program Files\Microsoft Office\OFFICE11\MSWORD.OLB"
REM 
REM
set JAVA_HOME=D:\jdk1.5.0_11
set JRE=%JAVA_HOME%\bin\java

set JACOBGEN_HOME=.
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\dt.jar;%JACOBGEN_HOME%\release\java\jacobgen.jar;%JACOBGEN_HOME%\lib\viztool.jar
REM put the dll in the path where we can find it
set PATH=%PATH%;%JACOBGEN_HOME%\release\x86

rem echo %CLASSPATH%

%JRE% -Xint com.jacob.jacobgen.Jacobgen %1 %2 %3 %4 %5
pause
