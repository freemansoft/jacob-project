package com.jacob.com;

/**
 * This trys to exercise ROT's garbage collecion
 * This is named this way because the build.xml
 * ignores files ending in Test when building the binary zip file
 * 
 * This will eventually be changed to a unit test.
 * 
 * Run in Eclipse with command line arguments
 * -Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=false
 */
public class ROT3Test
{

    public static void main(String args[]) throws Exception
    {
        ROT3TestThread threads[] = new ROT3TestThread[4];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new ROT3TestThread("thread-" + i, 3000+i*10);
        }
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
    }
}

/**
 * This wil try and exercise the thread support in the ROT 
 **/

class ROT3TestThread extends Thread
{
    private java.util.List ThreadObjects;

    private boolean DivideMode = true;

    private int initialRunSize = 0;
    /**
     * @param arg0
     */
    public ROT3TestThread(String arg0, int iStartCount)
    {
        super(arg0);
        initialRunSize = iStartCount;
        
    }

    /**
     * A semi-complexe serie of steps to put the ROT under stress. 1) discard
     * half the objects we've created 2) if size is greater than 1 but not a
     * even number, add 1 new object 3) stop when size is 1.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        // something that keeps object references around
        // so the gc can't collect them
        // we need to create these in the thread so they end up in the right ROT table
        ThreadObjects = new java.util.ArrayList(initialRunSize);
        for (int i = 0; i < initialRunSize; i++)
        {
            // create the object
            Variant aNewVariant = new Variant(getName() + "_" + i);
            // create a hard reference to it
            ThreadObjects.add(aNewVariant);
        }

        while (ThreadObjects.size() > 1)
        {
            String message = "";
            message = getName()+" Workingset=" +ThreadObjects.size()
            	+" ROT: "+ROT.getThreadObjects(true).hashCode();
			message += "before mods and gc "+ROT.getThreadObjects(true).size()+")";
            // if there is an odd number of objects greater than 2
            if (ThreadObjects.size() > 10)
            {
                message+= " ++ ";
                for ( int i = 0 ; i < ThreadObjects.size()/4 ; i++){
	                // add a new object
	                Variant aNewVariant = new Variant(getName() + "_*" + ThreadObjects.size());
	                ThreadObjects.add(aNewVariant);
                }
            }
            // now iterate across all the objects in our list
            message += " --  ";
            for (int i = ThreadObjects.size(); i > 0; i--)
            {
                // removing every other one?
                if (i % 2 == 0)
                {
                    // remove the reference so gc can get it
                    if (!ROT.USE_AUTOMATIC_GARBAGE_COLLECTION){
                        ROT.removeObject((JacobObject)ThreadObjects.get(i-1));
                    }
                    ThreadObjects.remove(i-1);
                }

            }
            
			message += " (after mods "+ROT.getThreadObjects(true).size()+")";
			// comm
			if (!ROT.USE_AUTOMATIC_GARBAGE_COLLECTION){
			    ROT.clearObjects();
			}
			System.gc();
			try {
			Thread.sleep(2000);
			} catch (InterruptedException ie){
			    
			}
			message += " (after gc "+ROT.getThreadObjects(true).size()+")";
			message += " Should see GC if debug turned on...";
			System.out.println(message);
        }
    }

    /**
     * Another test would be to overide this to always return the same name.
     * That would really screw the ROT!
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return super.toString();
    }
}