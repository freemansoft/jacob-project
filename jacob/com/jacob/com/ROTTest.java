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
        int sizeAfterClearObjects = 0;
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
        for ( int i = 0 ; i <= 100000; i++){
            new String("this is just some text to see if we can force gc");
        }
        sizeAfterGC = ROT.getThreadObjects().size();
        if (sizeAfterGC < 10000){
            System.out.println("something got GC'd: "+sizeAfterGC);
        } else if (sizeAfterGC < 10000){
            System.out.println("More than expected: "+sizeAfterGC);
        } else {
            System.out.println("They're all there");
        }
        System.out.println("Creating single object");
        new JacobObject();
        if (ROT.getThreadObjects().size() != 1){
            System.out.println("should be one object left");
        } else {
            System.out.println("Still there as expected");
        }
        ROT.clearObjects();
        if (ROT.getThreadObjects() == null){
            System.out.println("ROT pool was destroyed as expected after cleObjects called.");
        } else {
            System.out.println("ROT pool for thread still exists when it shouldn't");
        }
    }
}
