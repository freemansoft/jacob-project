JAVAC = c:\j2sdk1.4.2_07\bin\javac -O
JAR = c:\j2sdk1.4.2_07\bin\jar -cvf jacob.jar
COM_DIR = com\jacob\com
ACX_DIR = com\jacob\activeX

java:	jacob.jar
  cd src
	$(JAVAC) $(COM_DIR)\*.java $(ACX_DIR)\*.java
	$(JAR) $(COM_DIR)\*.class $(ACX_DIR)\*.class

jni: jni\jacob.dll
  cd jni
	nmake -f makefile all
	cd ..\
