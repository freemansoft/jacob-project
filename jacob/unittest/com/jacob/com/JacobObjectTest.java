package com.jacob.com;

import com.jacob.test.BaseTestCase;

/**
 * This will eventually be changed to a unit test.
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
public class JacobObjectTest extends BaseTestCase {

    
    public void testBuildVersion(){
        System.out.println("build version is "+JacobObject.getBuildVersion());
        System.out.println("build date is "+JacobObject.getBuildDate());
    }    
    
    
}
