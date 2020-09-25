There is an easy and elegant way to load DLLs from Applets and JavaWebStart Applications.

Both JavaWebStart and Applets support JNLP (Applets since 1.6.0_10 aka plugin2). 
Within a jnlp file it is possible to specify a nativelib and that's it!
So what do you need to do?

1. package the jacob-1.XX-xXX.dll into a jar file (root level of the jar, not into a subfolder) and put it into your applications lib folder next to your other libs (e.g. jacob.jar)
2. Specify all your libraries in your jnlp file like this:

```xml
<?xml version="1.0" encoding="UTF-8"?>
 <jnlp href="MyApplicaton.jnlp" spec="1.0+" version="1.1">
 		
 	<information>
       		<title>My cool Application or Applet</title>
		<vendor>nepatec GmbH &amp; Co. KG</vendor>
		<offline-allowed />
     	</information>
     	
	<security> 
		<all-permissions/> 
	</security> 

	<resources>
       		<j2se href="http://java.sun.com/products/autodl/j2se" version="1.6.0_10+" />
			<jar href="MyCoolApp.jar" main="true" version="1.0.0" />
			<jar href="jacob.jar" version="1.0.0" />
			<nativelib href="jacob-1.XX-xXX.jar"/>
	</resources>

	<!-- Use either this for an applet -->		
	<applet-desc 
         	name="MyCoolApp"
         	main-class="de.nepatec.app.MyCoolApplet"
         	width="265"
         	height="60">
	</applet-desc>

	<!-- or this for an web start application -->	
	<application-desc main-class="de.nepatec.app.MyCoolApp">
		<argument>some crazy arguments</argument>      
	</application-desc>
 </jnlp>
```

3. Sign all the jars or set up a policy file (cp. Applet Security below)
4. Deploy your application and start it via webstart or as an applet (from an webpage)


## General comments ##

* If you sign the jar files you need the <security> tag - when using a policy file it can be removed
* furthermore it is recommended that all libs are signed with an official certificate (e.g. from verisign, thawte etc) so that the browser doesn't pop a warning stating 'untrusted' application...
* to check the validity of your jnlp file the tool JaNeLA (link cp. sources below) is recommended.

## Sources ##
* [New Applet](https://jdk6.dev.java.net/plugin2/)
* [Applet JNLP](https://jdk6.dev.java.net/plugin2/jnlp/)
* [Applet Security](http://java.sun.com/developer/onlineTraining/Programming/JDCBook/appA.html)
* [JNLP API](http://www.oracle.com/technetwork/java/javase/index-141367.html)
* [JNLP Verifier](http://pscode.org/janela/)