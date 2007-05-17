package com.jacob.com;

/**
 * This will eventually be changed to a unit test.
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * If so, then try these
 * <pre>
 *      -Djava.library.path=d:/jacob/release/x86 
 *      -Dcom.jacob.autogc=false 
 *      -Dcom.jacob.debug=false 
 *      -Xcheck:jni
 *  </pre>
 */
public class JacobObjectTest {

    public static void main(String args[]) throws Exception
    {
    	JacobObjectTest testJig = new JacobObjectTest();
    	testJig.testBuildVersion();
    }
    
    public void testBuildVersion(){
        System.out.println("build version is "+JacobObject.getBuildVersion());
        System.out.println("build date is "+JacobObject.getBuildDate());
    }    
    
    
}
