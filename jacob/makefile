RELEASE = 1_9
JAVAC = d:\j2sdk1.4.2_06\bin\javac -O
JAR = d:\j2sdk1.4.2_06\bin\jar
COM_DIR = com\jacob\com
ACX_DIR = com\jacob\activeX

all: java jni
	@echo about to remove zipfiles
		echo This package contains Jacob release $(RELEASE) > RELEASE.TXT
        del jacobSrc_*.zip jacobBin_*.zip
        del com\jacob\com\*.class
        del jni\*.obj jni\jacob.dll jni\jacob.exp jni\jacob.lib
        @echo about to jar source                    
        @$(JAR) -cvf jacobSrc_$(RELEASE).zip RELEASE.TXT LICENSE.TXT com docs jni samples vstudio makefile
	@echo about to jar bin
        @$(JAR) -cvf jacobBin_$(RELEASE).zip RELEASE.TXT jacob.dll jacob.jar LICENSE.TXT docs samples

java::
	$(JAVAC) $(COM_DIR)\*.java $(ACX_DIR)\*.java
	$(JAR) -cvf jacob.jar $(COM_DIR)\*.class $(ACX_DIR)\*.class

jni::
 	@cd jni
	@nmake -f makefile all
	@cd ..\
