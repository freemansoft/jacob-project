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

import java.util.Hashtable;
import java.util.Iterator;
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
public abstract class ROT {
    /**
     * A hash table where each element is another 
     * Hashtable that represents a thread.  
     * Each thread hashtable contains the com objects created
     * in that thread
     */
    private static Hashtable rot = new Hashtable();

    /**
     * adds a new thread storage area to rot
     *
     */
    protected static void addThread() {
        String t_name = Thread.currentThread().getName();
        if (rot.contains(t_name))
            return;
        Hashtable tab = new Hashtable();
        rot.put(t_name,tab);
    }

    /**
     * Iterates across all the entries in rot 
     * each of which is a Hashtable.  This
     * releases each entry in the hashtable
     * before removing the hashtable from rot
     *
     */
    protected static void clearObjects() {
        String t_name = Thread.currentThread().getName();
        Hashtable tab = (Hashtable)rot.get(t_name);
        if (tab != null){
            Iterator it = tab.values().iterator();
            while (it.hasNext()) {
                JacobObject o = (JacobObject) it.next();
                if (o != null && o.toString() != null){
                    //System.out.println(t_name + "
                    // release:"+o+"->"+o.getClass().getName());
                    o.release();
                }
                it.remove();
            }
            rot.remove(t_name);
        }
    }

    /**
     * Lets someone remove an entry for a thread
     * 
     * @param o
     */
    protected static void removeObject(Object o) {
        String t_name = Thread.currentThread().getName();
        Hashtable tab = (Hashtable) rot.get(t_name);
        if (tab != null) {
            tab.remove(new Integer(o.hashCode()));
        }
    }
    
    /**
     * adds an object to the hashtable for the current thread
     * @param o
     */
    protected static void addObject(JacobObject o) {
        String t_name = Thread.currentThread().getName();
        //System.out.println(t_name + " add:"+o+"->"+o.getClass().getName());
        Hashtable tab = (Hashtable) rot.get(t_name);
        if (tab == null) {
            // this thread has not been initialized as a COM thread
            // so make it part of MTA for backwards compatibility
            ComThread.InitMTA(false);
            addThread();
            tab = (Hashtable) rot.get(t_name);
        }
        if (tab != null) {
            tab.put(new Integer(o.hashCode()),o);
        }
    }

    static {
        System.loadLibrary("jacob");
    }
}