/*
 * Copyright (c) 1999-2004 Sourceforge JACOB Project.
 * All rights reserved. Originator: Dan Adler (http://danadler.com).
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution. 
 * 3. Redistributions in any form must be accompanied by information on
 *    how to obtain complete source code for the JACOB software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jacob.com;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The Running Object Table (ROT) maps each thread to a vector of all the
 * JacobObjects that were created in that thread. It always operates on the
 * current thread so all the methods are static and they implicitly get the
 * current thread. Conceptually, this is similar to the ThreadLocal class of
 * Java 1.2 but we are also supporting Java 1.6 The clearObjects method is used
 * to release all the COM objects created by Jacob in the current thread prior
 * to uninitializing COM for that thread. If we leave this job to the garbage
 * collector, then finalize might get called from a separate thread which is not
 * initialized for COM, and also the component itself may have been freed.
 */
public abstract class ROT
{        
    /**
     * A hash table where each key is a thread name and each element 
     *  is a vector of WeakReferences to all the COM objects created in that thread.
     */
    private static Map rot = new WeakHashMap();
    
    /**
     * A Reference queue allowing the Garbage Collector of informing us
     * of dereferenced objects.
     */
    private static java.lang.ref.ReferenceQueue rq = new java.lang.ref.ReferenceQueue();
        
    
    /**
     * utility method used to get the thread name
     * @return
     */
    private static String getThreadName(){
        String theThreadName = Thread.currentThread().getName();
        return theThreadName;
    }
    
    /**
     * creates a thread storage area but does not init MTA or STA
     * for ComThread.  This is only called by ComThread when someone calls
     * initMTA or initSTA.  It is also called by this class
     * if someone starts creating JacobObjects without first 
     * initializing ComThread.xxx.  Note that COMThread first calls
     * initxxx before invoking this so I guess everyone else should do that
     * too.
     */
    protected static void addThread(){
        // cant use getThreadObjects() because that calls this
        if (rot.get(getThreadName())==null){
	        WeakHashMap newMap = new WeakHashMap(100);
	        rot.put(getThreadName(),newMap);        
	        debug(getThreadName()+":addThread()");
        }
    }
    
    /**
     * Returns the hash map for the current thread and creates one if necessary.
     * This remvoes the need for everyone to understand how the rot storage
     * is handled.
     * @return
     */
    protected static WeakHashMap getThreadObjects(){
        WeakHashMap matchingMap = (WeakHashMap)rot.get(getThreadName());
        if (matchingMap == null){
			// this thread has not been initialized as a COM thread
			// so make it part of MTA for backwards compatibility
            ComThread.InitMTA(false);
            addThread();
            matchingMap = (WeakHashMap)rot.get(getThreadName());
        }
        debug(getThreadName()+":addThread()");
        return matchingMap;
    }
    /**
     * Release all the COM objects created by the current thread.
     * And dereference the thread from the ROT.
     * This method is called by {@link com.jacob.com.ComThread#Release()}.
     */
    protected static void clearObjects()
    {
        WeakHashMap tab = getThreadObjects();
        // should never be null
        if (tab != null) {
            // empty the map for this thread
            Iterator threadTableKeyIterator = tab.keySet().iterator();
            while (threadTableKeyIterator.hasNext()) {
                // The key is the jacob object
                JacobObject o = (JacobObject) threadTableKeyIterator.next();
                // If this object is not null, release it
                if (o != null && o.toString() != null) {
                    debug(getThreadName() + "release:" + o + "->"
                            + o.getClass().getName());
                    o.release();
                }
                // remove the key
                threadTableKeyIterator.remove();
            }
            // Remove the vector from the ROT
            rot.remove(getThreadName());
        }
    }

    /**
     * Add a reference to a new object to the ROT, keyed against the current thread name.
     * This is called by {@link com.jacob.com.JacobObject#JacobObject()}
     * 
     * @param o : the JacobObject to add to the ROT
     */
    protected static void addObject(JacobObject o)
    {
      debug(getThreadName() + ":addObject("+o.getClass().getName()+"#"+(o!=null? (""+o.hashCode()):"null")+")");
      WeakHashMap tab = getThreadObjects();
      // tab will never equal null
      if (tab != null){
          tab.put(o,null);
      }
    }

    /**
     * @deprecated the java model leave the responsibility of clearing up objects 
     * to the Garbage Collector. Our programming model should not require that the
     * user specifically remove object from the thread.
     *  
     * Lets someone remove an entry for a thread
     * 
     * @param iObjectToRemove
     */
    protected static void removeObject(Object iObjectToRemove) 
    {
        if(iObjectToRemove == null)
        {
            return;
        }
        
        WeakHashMap tab = getThreadObjects();
        if (tab != null){
            // there was a reason for the toString but I don't remember what it was
	        if (iObjectToRemove != null && iObjectToRemove instanceof JacobObject)
	        {
	            debug(getThreadName() + "release:"+iObjectToRemove+"->"+iObjectToRemove.getClass().getName());
	            ((JacobObject)iObjectToRemove).release();
	        }
	        tab.remove(iObjectToRemove);
        }
    }
    
    // If this class gets loaded first, make sure we load the underlying JNI layer.
    static {
      System.loadLibrary("jacob");
    }
    
    /**
     * When things go wrong, it is usefull to be able to debug the ROT.
     */
    private static final boolean DEBUG = 
        "true".equalsIgnoreCase(System.getProperty("com.jacob.debug"));
    
    /**
     * Very basic debugging fucntion.
     * @param istrMessage
     */
    private static void debug(String istrMessage)
    {
        if(DEBUG)
        {
            System.out.println(istrMessage);
        }
    }
}
