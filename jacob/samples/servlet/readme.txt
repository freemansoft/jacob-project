This sample runs in Weblogic 5.1 as a servlet.

1. Compile this file (make sure you have jdk1.2 installed or the
   javax.servlet.* classes in your classpath).
2. Make sure the weblogic policy file allows native access. The easiest
   way is to replace the contents with this:

grant codeBase "file:d:/weblogic/-" {
  permission java.security.AllPermission;
};

grant codeBase "file:/c:/classes/-" {
  permission java.security.AllPermission;
};


grant codeBase "file:${java.home}/lib/ext/-" {
  permission java.security.AllPermission;
};

grant { 
  permission java.security.AllPermission;
};

3. Add the servlet to the weblogic.properties file:

weblogic.httpd.register.JacobScript=JacobScript

4. Either add your CLASSPATH to weblogic.classpath in startWebLogic.cmd
   or copy the com directory into weblogic/myserver/servletclasses

5. Copy the jacob/samples/servlet/* into weblogic/myserver/servletclasses
6. Start weblogic

7. Type the url: http://localhost:7001/JacobScript into the browser
   (If you run on port 7001)

8. Enter a VBScript expression like:
   1+2
   Now
   "hello" & " world"
   etc.
   and watch the MS Script control (which you must have installed)
   evaluate and return the result.
