package com.jacob.com;
import com.jacob.com.ROT;
/**
 * This trys to exercise ROT's garbage collecion
 */
public class ROTTest {

    public static void main(String args[]) throws Exception
    {
        int sizeAfterBuild = 0;
        int sizeAfterGC = 0;

        System.out.println("creating 10,000 objects");
        for ( int i = 0 ; i <= 10000; i++){
            new JacobObject();
        }
        sizeAfterBuild = ROT.getThreadObjects().size();
        if (sizeAfterBuild < 10000){
            System.out.println("Something got GC'd: "+sizeAfterBuild);
        } else if (sizeAfterBuild < 10000){
            System.out.println("More than expected: "+sizeAfterBuild);
        } else {
            System.out.println("They're all there");
        }
        // add more to the VM
        System.out.println("Flooding Memory to force GC");
        for ( int i = 0 ; i <= 10000; i++){
            new String("this is just some text to see if we can force gc "+i);
        }
        // storage will hold weak references until the next JacobObject is created 
        System.out.println("Creating single object.  This adds one and causes ROT to clean up GC'd");
        new JacobObject();
        sizeAfterGC = ROT.getThreadObjects().size();
        System.out.println("Objects left: "+sizeAfterGC);
        new JacobObject();
        if (ROT.getThreadObjects().size() != sizeAfterGC+1){
            System.out.println("Unexpected number of objects "+ROT.getThreadObjects().size());
        } else {
            System.out.println("Found number expected " +(sizeAfterGC+1) );
        }
        ROT.clearObjects();
        if (ROT.getThreadObjects() == null){
            System.out.println("ROT pool was destroyed as expected after clearObjects called.");
        } else {
            System.out.println("ROT pool for thread still exists when it shouldn't");
        }
        
        //========= part two ================================
        System.out.println("Verifying doesn't blow up with double release");
        for ( int i = 0 ; i <= 10000; i++){
            new JacobObject();
        }
        ROT.clearObjects();
        for ( int i = 0 ; i <= 100000; i++){
            new String("this is just some text to see if we can force gc "+i);
        }
        System.gc();

    }
}
