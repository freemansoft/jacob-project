#
#
#
RELEASE_MAJOR = 1_9
RELEASE_MINOR = -pre2
JDK = d:\j2sdk1.4.2_06
JAVAC = $(JDK)\bin\javac -O
JAR = $(JDK)\bin\jar
COM_DIR = com\jacob\com
ACX_DIR = com\jacob\activeX

help:
	@echo run "nmake build" to build the code 
	@echo run "nmake createZips" to remove obj and class files and create the zip files

build: java jni

#
# we really nee dtou build with ANT.  That would make
# it easy to exclude the CVS directories
#
createZips:: build clean
	echo This package contains Jacob release $(RELEASE_MAJOR)$(RELEASE_MINOR) > RELEASE.txt
    @echo about to jar source                    
    	@$(JAR) -cvf jacobSrc_$(RELEASE_MAJOR)$(RELEASE_MINOR).zip RELEASE.txt LICENSE.TXT com docs jni samples vstudio makefile
	@echo about to jar bin
	    @$(JAR) -cvf jacobBin_$(RELEASE_MAJOR)$(RELEASE_MINOR).zip RELEASE.txt jacob.dll jacob.jar jacobdll.jar LICENSE.TXT docs samples

# jacob.jar contains the java code
java::
	$(JAVAC) $(COM_DIR)\*.java $(ACX_DIR)\*.java
	$(JAR) -cvf jacob.jar $(COM_DIR)\*.class $(ACX_DIR)\*.class

# jacobdll.jar contains the dll and is only used for JWS users
jni::
 	@cd jni
	@nmake -f makefile all
	@cd ..
	$(JAR) -cvf jacobdll.jar jacob.dll

clean::	
	@echo about to cleanup compiled files and zipfiles
		if exist release rmdir /s /q release
        del jacobSrc_*.zip jacobBin_*.zip
        del $(COM_DIR)\*.class $(ACX_DIR)\*.class
        del jni\*.obj jni\jacob.dll jni\jacob.exp jni\jacob.lib
