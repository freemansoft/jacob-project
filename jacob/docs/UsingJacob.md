Jacob is a Java library that lets Java applications communicate with Microsoft Windows DLLs or COM libraries. It does this through the use of a custom DLL that the Jacob Java classes communicate with via JNI. The Java library and dll isolate the Java developer from the underlying windows libraries so that the Java developer does not have to write custom JNI code.Jacob is not used for creating ActiveX plugins or other modules that live inside of Microsoft Windows applications.

***

## The Jacob Packages

The JACOB jar contains two main packages: the `com.jacob.com.*`> package and the `com.jacob.activeX` package. The `com.jacob.com.*` package contains classes map very closely to the com dispatch model with the `com.jacob.com.Dispatch` acting as the primary communication class. Dispatch operate as a function library with a set of static methods that map very closely to the C++ Dispatch APIs provided to the COM layer.`com.jacob.activex.ActiveXComponent` can be used in place of Dispatch to provide a more object like API. The only exception to this guideline is that the `ActiveXComponent` class is always used to make the initial connection to the target dll/COM component.

***

## Considerations when doing server side automation of office

Most office and many windows client type applications are not written to be used in high volume or multi-threaded server environment. There is a [support note](http://support.microsoft.com/kb/257757/) on the Microsoft web site that discusses some of the issues.

***

## Determining the API of the target application

Section not yet written.

***

## The Jacob DLL

Jacob.jar relies on a DLL file that it loads off of the library path or classpath. This means that you must either copy the appropriate jacob dll into your path or use VM options to add directory holding jacob dll to the path. Prior to 1.14M6, the jacob DLL name was always "jacob.dll". This made it hard to verify jacob was loading the correct dll when multiple copies of jacob were installed on a single system. It also was confusing on 64 bit systems where the 32 bit and 64 bit dlls have the same tames. Starting in 1.14M6, Jacob's library loader selects a dll with the appropriate name based on the jacob release and platform. The dll naming convention is:  
`jacob<platform>.<version.>.dll`

 Classloader issues

The code is written so that the jacob.dll is only loaded one time per classloader. This works fine in the standard application but can cause problems if jacob.jar is loaded from more than one class loader. This can happen in the situation where multiple jacob dependent web applications run in the same container like a web server or JWS runtime. In the case of a web server, Jacob is normally put in the application specific WEB-INF/lib directory. This is the "right" way to do it and works in most situations. But, if Jacob is put in the WEB-INF/lib directory of each application's war file for more than one application then a problem occurs. In this situation, the web server uses a different classloader for each application. This means that each application will attempt to load the jacob.dll and errors are generated. The only way around this at this time (1.11) is to put the jacob.jar in the common/lib because that classloader is inherited by all of the applications so the DLLs will only get loaded once. This problem is described in SF 1645463 and should be fixed in some future release, fix method and time not yet determined.

***

## Microsoft Visual C++ library dependencies.

Jacob 1.15 is build with VC++ 2005 statically linked into the DLL. This removes the need for a separate msvcr80.dll installation.

Jacob 1.13 is built with VC++ 2005 that creates a dependency on msvcr80.dll. Windows XP and later seem to already include the necessary components. NT/2000 and Server/2003 require that you download the Visual C 2005 redistributable package, vcredist_x86.exe from the Microsoft web site. Microsoft has a download available that supplies the necessary components. It is distributed as a redistributable package.

If you see the following message then you probably don't have the right C++ libraries.

<pre>	Exception in thread "main" java.lang.UnsatisfiedLinkError: C:\apps\...\jacob.dll: This application has 
	failed to start because the application configuration is incorrect. Reinstalling the application may fix this problem 
</pre>

[Visual C redistributable installer SP1](http://www.microsoft.com/downloads/details.aspx?familyid=200B2FD9-AE1A-4A14-984D-389C36F85647&displaylang=en)

***

## Jacob Command Line Settings

This library supports several different command line options:

<table border="1">
<tbody>
<tr>
<td colspan="3">
 dll path location and dll name customization
</td>
</tr>
<tr>
<td>   </td>
<td valign="top">
 java.library.path
</td>
<td>Standard Java property used to add the location of the jacob dll to the JVM's library path. (Added 1.11)Example: `-Djava.library.path=d:/jacob/release/x86`</td>
</tr>
<tr>
<td>   </td>
<td valign="top">
 jacob.dll.name
</td>
<td>Override the standard DLL name with a custom one. This stops jacob from using its 32bit/64bit detection and dll rendezvous logic. Sometimes used when Jacob is bundled with another application and the application wishes to tie the jacob dll version number to the application version number. (Added 1.14M7)Example: `-Djacob.dll.name=MyFunkyDllName.dll`</td>
</tr>
<tr>
<td>   </td>
<td valign="top">
 jacob.dll.name.x86 & jacob.dll.name.x64
</td>
<td>Override the standard 32 bit DLL name with custom ones. Sometimes used when Jacob is bundled with another application and the application wishes to tie the jacob dll version number to the application version number. (Added 1.14M7)Example to override 32 bit dll name: `-Djacob.dll.name.x86=MyFunkyDllName-32bit.dll`</td>
</tr>
<tr>
<td colspan="3">
 Memory Management
</td>
</tr>
<tr>
<td>   </td>
<td valign="top">
 com.jacob.autogc
</td>
<td>Determines if automatic garbage collection is enabled. This is the only way to free up objects created in event callbacks. Automatic garbage collection , based on Java gc rules, garbage collection can be enabled via the `com.java.autogc` command line option. _This feature was added in release 1.9 is not fully debugged._There are real reasons for managing the lifetime of JacobObjects on a per thread basis. Jacob normally manages the the com/Java object lifetime as described in the [JacobComLifetime.html](JacobComLifetime.html) document. Some users have run into situations where they wish to try and let the Java GC lifetime manage the lifetime of objects. This seems to usually be tied to long running threads or to objects created as part of event callbacks. Code was added to let users try and let the JVM manage the object life cycles even though the [JacobComLifetime.html](JacobComLifetime.html) document says this is a bad idea. Added 1.9.This value is cached at startup and cannot be changed on-the-fly via `System.setProperty();`The default value is _false_Example: `-Dcom.jacob.autogc=false`</td>
</tr>
<tr>
<td>   </td>
<td valign="top">
 com.jacob.includeAllClassesInROT
</td>
<td>Acts as master switch for and <class_name>.PutInROT. This property determines if the (experimental) PutInROT property is even checked. It was added in version 1.15 because the property check in PutInROT brok applets because they are not allowed to check system properties at run time. com.jacob.includeAllClassesInROT is checked at class initialization which is allowed.

The default value of this flag is _true_ which matches all behavior prior to 1.13 and the default behavior for 1.13 on

Setting this flag to false causes Jacob to check the and <class_name>.PutInROT property for every Jacob object that is created.
</td>
</tr>
<tr>
<td>   </td>
<td valign="top">
 <class_name>.PutInROT
</td>
<td>Lets a program specify that instances of certain classes are to not be inserted into the ROT. This experimental (1.13) feature provides a mechanism for freeing VariantViaEvent objects that are created in Event threads. There is normally no way to free those objects because the thread terminates outside of any normally MTA/STA Startup/Teardown code. Each event occurs in a new thread and creates a new ROT entry so they grow without bounds.This option may cause VM crashes in certain situations where windows memory is freed outside of the thread it was created in but empirical evidence shows there are situations where this great reduces the long running memory footprint of applications that process a lot of events. _This function is still experimental_. The functionality was added 1.13\. Some of this overlaps the experimental `com.jacob.autogc` introduced in 1.9\. See the ROT.java test program for an example of the effects of this option.This value is checked every time and can be changed on-the-fly via `System.setProperty();`Example: `System.setProperty("com.jacob.com.VariantViaVariant.PutInROT","false");`  
Example: `-Dcom.jacob.com.VariantViaVariant.PutInROT=false`</td>
</tr>
<tr>
<td colspan="3">
 Debugging and Troubleshooting
</td>
</tr>
<tr>
<td>   </td>
<td valign="top">
 com.jacob.debug
</td>
<td>Determines if debug output is enabled to standard out.This value is cached at startup and cannot be changed on-the-fly via `System.setProperty();`The default value is **false**Example: `-Dcom.jacob.debug=false`</td>
</tr>
<tr>
<td>   </td>
<td valign="top">
 -XCheck:jni
</td>
<td>This turns on additional JVM checking for JNI issues. This is not an actual JACOB system property but a property used by the JVM.The default is "no additional checking" Example: `-XCheck:jni`</td>
</tr>
</tbody>
</table>
***

## Finding the DLL version using windows command line

The jacob.dll file includes the jacob release number in the version field. Run the following from the command prompt `dumpbin /version jacob.dll` . The dll version number is stored in the "image version" field of the "OPTIONAL HEADER VALUES" section. This information from [The Microsoft msdn web site](http://msdn2.microsoft.com/en-gb/library/h88b7dc8(VS.71).aspx)

***

## Unit Tests

Jacob must know the location of the DLL when running the unit tests in Eclipse. The simplest way to do this is to add the dll path to the unit as a VM argument. The argument should be specified based on where you installed the jacob source package. If you have jacob unpacked in c:/dev/jacob and built using build.xml, then the vm arguments would be:  
`-Djava.library.path=c:/dev/jacob/release/x86` .Last Modified 4/2008 1.15