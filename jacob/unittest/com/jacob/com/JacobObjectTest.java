package com.jacob.com;

/**
 * @author joe
 *
 * This will eventually be changed to a unit test.
 * 
 * Run in Eclipse with command line arguments
 * -Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=false
 */
public class JacobObjectTest {

    public static void main(String args[]) throws Exception
    {
        System.out.println("build version is "+JacobObject.getBuildVersion());
        System.out.println("build date is "+JacobObject.getBuildDate());
    }    
}
