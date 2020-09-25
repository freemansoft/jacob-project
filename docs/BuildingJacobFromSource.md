# Overview

JACOB is built on windows machines using ANT, most commonly from inside of Eclipse. The main steps for getting a working Jacob build are:

1.  Check out the source code or unpack the source zip file from sourceforge
2.  Install the Development Environment
3.  Configure the build by creating a _compilation_tools.properties_ file.
4.  Run Eclipse and load the project into eclipse
5.  Open the build.xml file in Eclipse and run the default ant target

# Repository Organization

Unpack the source archive zip file or check the files out of CVS into d:\jacob or some other familiar place. Source Java and JNI files are located in separate packages from the unit tests and the samples.

|||
|-|-|
|   docs      | documentation
|   jni       | c++ code
|   lib       | libraries needed to compile unit tests
|   release   | a directory built by the ant script where jacob is constructed
|   samples   | sample programs
|   src       | Jacob Java source files
|   unittest  | JUnit 4.12 unit test programs. Run from the test target in build.xml
|   vstudio   | some out of date VC++ project files
|   bulid.xml | the ant build script. It can be run from inside Eclipse

The Servlet examples that required j2ee libraries to compile have temporarily been removed.

# Development Environment

The simplest build environment includes MS Visual Studio 16.0 (Studio 2019), Eclipse 2020.09 with the C/C++ module and JDK 1.8. In that situation, you would just create the _compilation_tools.properties_ using the example at the top of build.xml as a template.

*   Microsoft Visual Studio 2019 Community Edition. Installs to C:\ProgramFiles (X86) 
    * MSVC 
    * Windows 10 SDK
    * C++ MFC CLI Modules - don't know which o fthese are needed
*   Eclipse from www.eclipse.org as the Java IDE.
*   Eclipse C/C++ plugin can be used for C coding in place of VC++ IDE.
*   Java JDK 1.8 , latest available

|||||||
|--- |--- |--- |--- |--- |--- |
|Release  |C Version                    |Java Version|ANT Version            |Eclipse Version Used|generated DLLs|
|up to 1.6|VC 98 (6.0)                  |?           |MAKE                   |?                   |32 bit|
|1.7      |VC 98 (6.0)                  |1.4 (48)    |1.?                    |?                   |32 bit|
|1.8      |VC 98 (6.0)                  |1.4 (48)    |1.?                    |?                   |32 bit|
|1.9      |VC 98 (6.0)                  |1.4 (48)    |1.?                    |?                   |32 bit|
|1.10     |VC 98 (6.0)                  |1.4 (48)    |1.?                    |3.??                |32 bit|
|1.11     |VC 98 (6.0) & 2003 64bit libs|1.4.? (48)  |1.6.?                  |3.2.1               |32 and 64 bit|
|1.12     |VC 98 (6.0) & 2003 64bit libs|1.4.2 (48)  |1.6.5                  |3.2.2               |32 and 64 bit|
|1.13     |VC 2005 (8)                  |1.4.2 (48)  |1.7.0                  |3.3                 |32 and 64 bit|
|1.14     |VC 2005 (8)                  |1.5.0 (49)  |1.7.0                  |3.3                 |32 and 64 bit|
|1.15     |VC 2005 (8)                  |1.5.0 (49)  |1.7.0                  |3.4                 |32 and 64 bit|
|1.17     |VC 2005 (8)                  |1.5.0 (49)  |1.8.4  Eclipse Embedded|4.3                 |32 and 64 bit|
|1.18     |VS 2013 (12) Windows SDK 7.1A|1.6.0 (50)  |1.8.4  Eclipse Embedded|4.3                 |32 and 64 bit|
|1.19     |VS 2013 (12) Windows SDK 7.1A|1.8.0 (52)  |1.10.1 Eclipse Provided|4.7                 |32 and 64 bit|
|1.20     |VS 2019 (16) Windows SDK 10  |1.8.0 (52)  |1.10.8 Eclipse Provided|2020 09             |32 and 64 bit|

Microsoft Visual Studio 2019 supports 64 bit builds. so no additional tools are required.  

# Build Process

The build process is based on ANT. You can run ANT from inside of eclipse or from the command line. Running from inside eclipse means you don't have any installation, pathing or configuration to do. You can just open the xml, select the target in the "Outline" pane, right mouse and then "run as ant" on the selected target.The ant process is driven off of a configuration file named `compilation_tools.properties` that describes the locations of the JDK and Microsoft C++ tools. The `build.xml` file in the root directory contains examples of the contents of this file. There are two main ant targets.

*   **ant default** executes the following steps when using the default target.
    *   Build the Java code
    *   Build the jni code
    *   create the dll
    *   create jar file
*   **ant packageRelease** runs the above listed steps and then
    *   builds the javadoc
    *   builds a source zip
    *   builds a binary zip with the javadoc
*   **test** runs all the tests
    *   One of the Excel unit tests is hard coded against Office 2019 32 bit. 

# Eclipse Java IDE

You can open the jacob-project in Eclipse.

1.  Open Eclipse
2.  File-->New-->Other...
3.  Java --> Java Project form Existing Ant Buildfile
4.  Click "next" to go to "Create a Java Project from an Ant Buildfile"
5.  Browse to and select build.xml in the project directory
6.  Select any of the javac tasks. This wall cause that one source directory to be added as a eclipse source directory.

Eclipse users have to do some minor tweaks to their project if they want to use the integrated build process. This is because the unit tests are files located in the "unittest" directory while the project source files themselves are in "src" the root directory. By default, eclipse will add the entire project as source. This messes up the package naming. In addition, the build directory should be set to be the same place the ANT build puts the compiled java classes. A couple small tweaks to the build path fix these problems:

1.  Open up the project properties and go to the "Java Build Path" properties panel.
2.  Remove the root of the project from the build path if it is there
3.  Add / verify the following folders are in the build path. Add them with "link source..." if they are missing `samples`, `src` and `unittest` to the build path in the Source tab.
4.  Add junit as a library "Add Library...Junit...Junit 4"
5.  Exclude *.txt from each of the newly added folders.
6.  Set the default build output directory to `jacob-project/release/java`
7.  Open "Windows-->Show View-->Project Explorer"

## Troubleshooting Build Problems

*   Symptom: Build Failed can't find javac compiler. JAVA_HOME does not point to the JDK Problem:  
    The java library is pointed at a jre insted of the jdk. Update Project..Properties..Java Build Path to point at the JDK
*   Symptom: The jar is built but no dlls were compiled.  
    Problem: compilation_tools.properties does not have the correct location for the Microsoft tools.
*   Sympton: Can't find jni.h or can't find C++ compiler  
    Problem: compilation_tools.properties configured incorrectly. Either paths are wrong or the separator is wrong. It requires two backslashes for a separator.
*   Symptom: Ant fails with the message `Could not create task or type of type: junit.`.  
    Problem: junit.jar must be copied from this project to the $ANT_HOME/lib directory.

# Compilation_tools.properties
See build.xml for a sample
# Running Samples and Tests

Samples and JUnit test programs can be found in the source jar or in sourceforge/git. The programs can be run from a bat file or from inside the Eclipse IDE. The java library path variable must be set to include the directory the jacob.dll is in. The simplest way to do that is to add it as a command line option. The following assume that your jacob development area is located in c:\dev\jacob:

```							
    -Djava.library.path=c:/dev/jacob/release/x86 
    -Dcom.jacob.autogc=false 
    -Dcom.jacob.debug=false 
    -Xcheck:jni
```

JUnit test programs can be individually run from inside eclipse or en-masse via the `ant test` target.

# Git Bash environment configuration

Example `setenv.sh` environment configuration for windows machine for a gitbash terminal as of 2020/09

```
JAVA_HOME="/c/Program Files/Amazon Corretto/jdk1.8.0_265"
ANT_HOME="/c/Users/joe/.p2/pool/plugins/org.apache.ant_1.10.8.v20200515-1239"
PATH=$ANT_HOME/bin:$JAVA_HOME/bin:$PATH
export PATH
export ANT_HOME
export JAVA_HOME
```

***

Last Modified 09/2020 1.19
Converted from HTML to md 9/2020