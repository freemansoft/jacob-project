package com.jacob.com;
import com.jacob.com.ROT;
/**
 * This trys to exercise ROT's garbage collecion
 */
public class ROTTest {

    public static void main(String args[]) throws Exception
    {
        int sizeAfterBuild = 0;
        int sizeBeforeGC = 0;
        int sizeAfterGC = 0;

        System.out.println("creating 10,000 object sets");
        for ( int i = 0 ; i <= 10000; i++){
            SafeArray a1 = new SafeArray(Variant.VariantVariant, 2);
            a1.setVariant(0, new Variant("foo"));
            a1.setVariant(1, new Variant("bar"));
        }
        sizeAfterBuild = ROT.getThreadObjects().size();
        if (sizeAfterBuild < 10000){
            System.out.println("Something got GC'd: "+sizeAfterBuild);
        } else if (sizeAfterBuild > 10000){
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
        System.gc();
        sizeBeforeGC = ROT.getThreadObjects().size();
        System.out.println("Objects left after flood and gc but before adding a new object that clean's up weak references: "+sizeBeforeGC);
        System.out.println("Creating single object.  This adds one and causes ROT to clean up GC'd");
        new JacobObject();
        sizeAfterGC = ROT.getThreadObjects().size();
        System.out.println("Objects left after adding one (caused weak ref objects to be removed): "+sizeAfterGC);
        new JacobObject();
        if (ROT.getThreadObjects().size() != sizeAfterGC+1){
            System.out.println("Unexpected number of objects after adding only one more "+ROT.getThreadObjects().size());
        } else {
            System.out.println("Found number expected after adding one more " +(sizeAfterGC+1) );
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
        // force safeRelease call on all objects
        ROT.clearObjects();
        // now force the gc to go collect them, runnign safeRelease again
        System.gc();
    }
}
