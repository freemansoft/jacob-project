package com.jacob.com;
import com.jacob.com.ROT;
/**
 * This trys to exercise ROT's garbage collecion
 * 
 * This will eventually be changed to a unit test.
 * 
 * Run in Eclipse with command line arguments
 * -Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=false
 */
public class ROTTest {

    public static void main(String args[]) throws Exception
    {
        int sizeAfterBuild = 0;
        int sizeBeforeGC = 0;
        int sizeAfterGC = 0;

        debug("creating 10,000 object sets");
        for ( int i = 0 ; i <= 10000; i++){
            SafeArray a1 = new SafeArray(Variant.VariantVariant, 2);
            a1.setVariant(0, new Variant("foo"));
            a1.setVariant(1, new Variant("bar"));
        }
        sizeAfterBuild = ROT.getThreadObjects(false).size();
        if (sizeAfterBuild < 10000){
            debug("Something got GC'd: "+sizeAfterBuild);
        } else if (sizeAfterBuild > 10000){
            debug("More than expected: "+sizeAfterBuild);
        } else {
            debug("They're all there");
        }
        // add more to the VM
        debug("Flooding Memory to force GC");
        for ( int i = 0 ; i <= 10000; i++){
            new String("this is just some text to see if we can force gc "+i);
        }
        // storage will hold weak references until the next JacobObject is created
        System.gc();
        sizeBeforeGC = ROT.getThreadObjects(false).size();
        debug("Objects left after flood and gc but before adding a new object that clean's up weak references: "+sizeBeforeGC);
        debug("Creating single object.  This adds one and causes ROT to clean up GC'd");
        new JacobObject();
        sizeAfterGC = ROT.getThreadObjects(false).size();
        debug("Objects left after adding one (caused weak ref objects to be removed): "+sizeAfterGC);
        new JacobObject();
        if (ROT.getThreadObjects(false).size() != sizeAfterGC+1){
            debug("Unexpected number of objects after adding only one more "+ROT.getThreadObjects(false).size());
        } else {
            debug("Found number expected after adding one more " +(sizeAfterGC+1) );
        }
        ROT.clearObjects();
        if (ROT.getThreadObjects(false) == null){
            debug("ROT pool was destroyed as expected after clearObjects called.");
        } else {
            debug("ROT pool for thread still exists when it shouldn't");
        }
        
        //========= part two ================================
        debug("Verifying doesn't blow up with double release");
        for ( int i = 0 ; i <= 10000; i++){
            new JacobObject();
        }
        // force safeRelease call on all objects
        ROT.clearObjects();
        // now force the gc to go collect them, running safeRelease again
        System.gc();
    }
    
    private static void debug(String message){
        System.out.println(Thread.currentThread().getName()+" "+message);
    }
}
