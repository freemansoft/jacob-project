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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The Running Object Table (ROT) maps each thread to a collection of all the
 * JacobObjects that were created in that thread. It always operates on the
 * current thread so all the methods are static and they implicitly get the
 * current thread. 
 * <p>
 * The clearObjects method is used to release all the COM objects created by Jacob 
 * in the current thread prior to uninitializing COM for that thread. 
 * <p>
 * Prior to 1.9, manual garbage collection was the only option in Jacob, but
 * from 1.9 onward, setting the com.jacob.autogc system property
 * allows the objects referenced by the ROT to be automatically GCed.
 * <p>
 * TODO : explain when automatic GC is preferable, and when it isn't. 
 * Is [ 1116101 ] jacob-msg 0284 relevant???
 */
public abstract class ROT {
    /**
     * Manual garbage collection was the only option pre 1.9
     */
    protected static final boolean USE_AUTOMATIC_GARBAGE_COLLECTION = 
        "true".equalsIgnoreCase(System.getProperty("com.jacob.autogc"));

    /**
     * A hash table where each element is another 
     * HashMap that represents a thread.  
     * Each thread HashMap contains the com objects created
     * in that thread
     */
    private static HashMap rot = new HashMap();

    /**
     * adds a new thread storage area to rot
     * @return Map corresponding to the thread that this call was made in
     */
    protected static Map addThread() {
        String t_name = Thread.currentThread().getName();
        if (rot.containsKey(t_name)){
            // nothing to do
        } else {
            Map tab = null;
            if (JacobObject.isDebugEnabled()){ JacobObject.debug("Automatic GC flag == "+USE_AUTOMATIC_GARBAGE_COLLECTION);}
            if (!USE_AUTOMATIC_GARBAGE_COLLECTION){
                tab = new HashMap();
            } else {
                tab = new WeakHashMap();
            }
            rot.put(t_name,tab);
        }
        return getThreadObjects(false);
    }

    
    /**
     * returns the pool for this thread if it exists.  can create a new
     * one if you wish by passing in TRUE
     * @param createIfDoesNotExist
     * @return Map the collection that holds the objects created in the current thread
     */
    protected static Map getThreadObjects(boolean createIfDoesNotExist) {
        String t_name = Thread.currentThread().getName();
        if (!rot.containsKey(t_name) && createIfDoesNotExist){
            addThread();
        }
        return (Map)rot.get(t_name);
    }
    
    /**
     * Iterates across all of the entries in the Hashmap in the rot
     * that corresponds to this thread.  
     * This calls safeRelease() on  each entry and then 
     * clears the map when done and removes it from the rot.
     * All traces of this thread's objects will disapear.
     * This is called by COMThread in the tear down and provides a 
     * synchronous way of releasing memory
     */
    protected static void clearObjects() {
        Map tab = getThreadObjects(false);
        if (JacobObject.isDebugEnabled()){ JacobObject.debug("starting clear objects");}
        if (tab != null){
            // walk the values
            Iterator it;
            if (USE_AUTOMATIC_GARBAGE_COLLECTION){
                it = tab.keySet().iterator();
            } else {
                it = tab.values().iterator();
            }
            while (it.hasNext()) {
                JacobObject o = (JacobObject) it.next();
                if (o != null  
                    // can't use this cause creates a Variant if calling SafeAray 
                    // and we get an exceptin modifying the collection while iterating
                    // && o.toString() != null
                ){
                    if (JacobObject.isDebugEnabled()){ JacobObject.debug(" removing "+o.getClass().getName());}
                    o.safeRelease();
                }
                // used to be an iterator.remove() but why bother when we're nuking them all anyway?
            }
            // empty the collection
            tab.clear();
            // remove the collection from rot
            rot.remove(Thread.currentThread().getName());
        } else {
            if (JacobObject.isDebugEnabled()){ JacobObject.debug("nothing to clear!");}
        }
    }

    /**
     * generates the key used to insert object into the thread's list of objects.
     * @param targetMap the map we need the key for.  Used to make sure we create a compatabile key
     * @param o JacobObject we need key for
     * @return some key compatabile with hashmaps
     */
    protected static Object getMapKey(Map targetMap, JacobObject o){
        if (targetMap instanceof WeakHashMap){
            return o;
        } else {
            return new Integer(o.hashCode());
        }
    }
    
    /**
     * generates the object used to insert object into the thread's list of objects.
     * @param targetMap the map we need the key for.  Used to make sure we create a compatabile key
     * @param o JacobObject we need key for
     * @return some compatabile with hashmaps (null for weak has map)
     */
    protected static Object getMapValue(Map targetMap, JacobObject o){
        if (targetMap instanceof WeakHashMap){
            return null;
        } else {
            return o;
        }
    }
    
    /**
    * @deprecated the java model leave the responsibility of clearing up objects 
    * to the Garbage Collector. Our programming model should not require that the
    * user specifically remove object from the thread.
    * 
    * This will remove an object from the ROT
    * @param o
    */
    protected static void removeObject(JacobObject o) {
        String t_name = Thread.currentThread().getName();
        Map tab = (Map) rot.get(t_name);
        if (tab != null) {
            tab.remove(getMapKey(tab,o));
        }
        o.safeRelease();
    }
    
    /**
     * adds an object to the HashMap for the current thread
     * @param o
     */
    protected static void addObject(JacobObject o) {
        String t_name = Thread.currentThread().getName();
        if (JacobObject.isDebugEnabled()){ JacobObject.debug(" add:"+o+"->"+o.getClass().getName());}
        Map tab = getThreadObjects(false);
        if (tab == null) {
            // this thread has not been initialized as a COM thread
            // so make it part of MTA for backwards compatibility
            ComThread.InitMTA(false);
            tab = getThreadObjects(true);
        }
        if (tab != null) {
            tab.put(getMapKey(tab,o),getMapValue(tab,o));
            if (JacobObject.isDebugEnabled()){ JacobObject.debug(" ROT thread table size after addition is :"+tab.size());}
        }
    }

    static {
        System.loadLibrary("jacob");
    }
    
}