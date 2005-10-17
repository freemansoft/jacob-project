@echo off
cls

rem set JAVA_HOME=c:\jdk1.1.8
set JAVA_HOME=c:\Programmi\jdk1.3.1_01
set JAVAC=%JAVA_HOME%\bin\javac
set JAR=%JAVA_HOME%\bin\jar
set CLASSPATH=%JAVA_HOME%\lib\classes.zip;src;lib\samskivert.jar

%JAVAC% -d bin src\it\bigatti\jacobgen\ParameterItem.java
if errorlevel 1 goto fine

%JAVAC% -d bin src\it\bigatti\jacobgen\AliasGenerator.java
if errorlevel 1 goto fine

%JAVAC% -d bin src\it\bigatti\jacobgen\ClassGenerator.java
if errorlevel 1 goto fine

%JAVAC% -d bin src\it\bigatti\jacobgen\Jacobgen.java
if errorlevel 1 goto fine

%JAVAC% -d bin src\it\bigatti\jacobgen\TypeLibInspector.java
if errorlevel 1 goto fine

cd bin
%JAR% cvf ..\jacobgen.jar *.*
cd ..
del lib\jacobgen.jar >nul
move jacobgen.jar lib >nul
copy jni\Debug\jacobgendll.dll lib >nul

:fine
pause
