JAVAC = d:\j2sdk1.4.2_06\bin\javac -O
JAR = d:\j2sdk1.4.2_06\bin\jar -cvf jacob.jar
COM_DIR = com\jacob\com
ACX_DIR = com\jacob\activeX

all: java jni
	rm -f jacobSrc_18.zip jacobBin_18.zip
	jar -cvf jacobSrc_18.zip .
	jar -cvf jacobBin_18.zip jacob.dll jacob.jar LICENSE.TXT README.TXT samples

java:	jacob.jar
	$(JAVAC) $(COM_DIR)\*.java $(ACX_DIR)\*.java
	$(JAR) $(COM_DIR)\*.class $(ACX_DIR)\*.class

jni: jni\jacob.dll
  cd jni
	nmake -f makefile all
	cd ..\
