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
import java.util.Vector;


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
    private static java.util.Map rot = new java.util.WeakHashMap();
    
    /**
     * A Reference queue allowing the Garbage Collector of informing us
     * of dereferenced objects.
     */
    private static java.lang.ref.ReferenceQueue rq = new java.lang.ref.ReferenceQueue();
        
    /**
     * Adds a new thread storage area to the ROT, keyed against the thread name
     *
     */
    protected static void addThread()
    {
      String t_name = Thread.currentThread().getName();
      if (rot.containsKey(t_name)) return;
      Vector v = new Vector();
      rot.put(t_name, v);
      debug(t_name+":addThread()");
    }

    /**
     * Release all the COM objects created by the current thread.
     * And dereference the thread from the ROT.
     * This method is called by {@link com.jacob.com.ComThread#Release()}.
     */
    protected static void clearObjects()
    {
      String t_name = Thread.currentThread().getName();
      Vector v = (Vector)rot.get(t_name);
      if (v != null)
      {
        // For each object created by this thread
        while (!v.isEmpty())
        {
          // Obtain the first object in the vector
          JacobObject o = (JacobObject)((java.lang.ref.WeakReference)v.elementAt(0)).get();
          
          // If this object is not null, release it
          if (o != null) 
          {
              o.release();
              debug(t_name + "release:"+o+"->"+o.getClass().getName());
          }
          // remove that object from our vector
          v.removeElementAt(0);
        }
        // Remove the vector from the ROT
        rot.remove(t_name);
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
      String t_name = Thread.currentThread().getName();
      debug(t_name + ":addObject("+o.getClass().getName()+"#"+(o!=null? (""+o.hashCode()):"null")+")");
      Vector v = (Vector)rot.get(t_name);
      if (v == null)
      {
        // this thread has not been initialized as a COM thread
        // so make it part of MTA for backwards compatibility
        ComThread.InitMTA(false);
        addThread();
        v = (Vector)rot.get(t_name);
      }
      if (v != null)
      {
        // Before we add any new object, we remove all references to object that have already 
        // been garbage collected. This will keep the ROT from growing infinitively for long 
        // lived threads.
        purgeGCObjects(t_name, v);
        
        // Add a WeakReference to the object to our vector. Using a WeakReference means that 
        // we won't stop the object being garbage collected when it gets dereferenced.
        v.addElement(new java.lang.ref.WeakReference(o,rq));
        
        debug(t_name+" has "+v.size()+" objects referenced");
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
        
        String t_name = Thread.currentThread().getName();
        Vector v = (Vector)rot.get(t_name);
        if (v != null)
        {
            java.util.Iterator it = v.iterator();
            while (it.hasNext()) 
            {
                java.lang.ref.WeakReference weak = (java.lang.ref.WeakReference)it.next();
                JacobObject o = (JacobObject)weak.get(); 
                //if (o != null && o.toString() != null)
                if (o != null)
                {
                    debug(t_name + "release:"+o+"->"+o.getClass().getName());
                    o.release();
                }
                it.remove();
            }
        }
    }
    
    /**
     * @see java.lang.ref.WeakReference
     * @see java.lang.ref.ReferenceQueue#poll()
     * 
     * Given a vector of WeakReferences, poll our ReferenceQueue for garbageCollected
     * object, and attempt to remove these objects from the vector.
     * 
     * @param istrThreadName : the name of the current thread, used only for debug purpose
     * @param ivThreadObjects : a vector of weakReferences to JacobObject created by a given thread
     */
    protected static void purgeGCObjects(String istrThreadName, Vector ivThreadObjects) 
    {
        int count = 0;
        java.lang.ref.WeakReference weak = (java.lang.ref.WeakReference)rq.poll();
        
        // For all weakReferences contained in our reference queue
        while(weak != null)
        {
            // get the JacobObject encapsulated by the weakReference.
            JacobObject o = (JacobObject)weak.get();
            if (o != null) 
            {
                // This SHOULD NEVER HAPPEN because the JacobObject jo has already been garbage collected.
                debug(istrThreadName + ":release("+((o!=null)? o.toString() : o.getClass().getName())+") !!!!! SHOULD NOT HAPPEN!");
                o.release();
            }

            if(ivThreadObjects!=null)
            {
                // Attempt to remove the JacobObject from the vector. If the vector
                // doesn't actually contain this object, nothing happens.
                ivThreadObjects.remove(weak);
                count++;
            }
            else
            {
                // This should not happen, but it is possible that while purgeGCObjects()
                // is running, the thread gets removed from the ROT.
                debug(istrThreadName+":purgeGCObjects() : thread removed from ROT!!!");
            }
            
            // get the next weakReference from our reference queue
            weak = (java.lang.ref.WeakReference)rq.poll();
        }  
        
        // For debug purpose, list how many dead references got purged
        if(count > 0)
        {
            debug(istrThreadName+":purgeGCObjects() : "+count+" dead references purged");
        }  
    }//purgeGCObjects()
    
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
