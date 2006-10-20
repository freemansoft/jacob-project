package com.jacob.com;

/**
 * This will eventually be changed to a unit test.
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
public class JacobObjectTest {

    public static void main(String args[]) throws Exception
    {
        System.out.println("build version is "+JacobObject.getBuildVersion());
        System.out.println("build date is "+JacobObject.getBuildDate());
    }    
}
