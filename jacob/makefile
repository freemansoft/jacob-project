JAVAC = d:\j2sdk1.4.2_06\bin\javac -O
JAR = d:\j2sdk1.4.2_06\bin\jar
COM_DIR = com\jacob\com
ACX_DIR = com\jacob\activeX

all: java jni
	@echo about to remove zipfiles
	del jacobSrc_18.zip jacobBin_18.zip
	@echo about to jar source
	@$(JAR) -cvf jacobSrc_18.zip .
	@echo about to jar bin
	@$(JAR) -cvf jacobBin_18.zip jacob.dll jacob.jar LICENSE.TXT README.TXT samples

java::
	$(JAVAC) $(COM_DIR)\*.java $(ACX_DIR)\*.java
	$(JAR) -cvf jacob.jar $(COM_DIR)\*.class $(ACX_DIR)\*.class

jni::
 	@cd jni
	@nmake -f makefile all
	@cd ..\
