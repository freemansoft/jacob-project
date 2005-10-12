/*
 * Copyright (c) 1999-2004 Sourceforge JACOB Project. All rights reserved.
 * Originator: Dan Adler (http://danadler.com).
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Redistributions in any form must
 * be accompanied by information on how to obtain complete source code for the
 * JACOB software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.jacob.com;

/**
 * Represents a COM level thread 
 * This is an abstract class because all the methods are static
 * and no instances are ever created.
 */
public abstract class ComThread {
    private static final int MTA = 0x0;

    private static final int STA = 0x2;

    /**
     * Comment for <code>haveSTA</code>
     */
    public static boolean haveSTA = false;

    /**
     * Comment for <code>mainSTA</code>
     */
    public static MainSTA mainSTA = null;

    /**
     * Initialize the current java thread to be part of the Multi-threaded COM
     * Apartment
     */
    public static synchronized void InitMTA() {
        InitMTA(false);
    }

    /**
     * Initialize the current java thread to be an STA
     */
    public static synchronized void InitSTA() {
        InitSTA(false);
    }

    /**
     * Initialize the current java thread to be part of the Multi-threaded COM
     * Apartment, if createMainSTA is true, create a separate MainSTA thread
     * that will house all Apartment Threaded components
     * @param createMainSTA
     */
    public static synchronized void InitMTA(boolean createMainSTA) {
        Init(createMainSTA, MTA);
    }

    /**
     * Initialize the current java thread to be an STA COM Apartment, if
     * createMainSTA is true, create a separate MainSTA thread that will house
     * all Apartment Threaded components
     * @param createMainSTA
     */
    public static synchronized void InitSTA(boolean createMainSTA) {
        Init(createMainSTA, STA);
    }

    /**
     * 
     */
    public static synchronized void startMainSTA() {
        mainSTA = new MainSTA();
        haveSTA = true;
    }

    /**
     * 
     */
    public static synchronized void quitMainSTA() {
        if (mainSTA != null)
            mainSTA.quit();
    }

    /**
     * Initialize the current java thread to be part of the MTA/STA COM
     * Apartment
     * @param createMainSTA
     * @param mode
     */
    public static synchronized void Init(boolean createMainSTA, int mode) {
        if (createMainSTA && !haveSTA) {
            // if the current thread is going to be in the MTA and there
            // is no STA thread yet, then create a main STA thread
            // to avoid COM creating its own
            startMainSTA();
        }
        if (JacobObject.isDebugEnabled()){JacobObject.debug("ComThread: before Init: "+mode);}
        doCoInitialize(mode);
        if (JacobObject.isDebugEnabled()){JacobObject.debug("ComThread: after Init: "+mode);}
        ROT.addThread();
        if (JacobObject.isDebugEnabled()){JacobObject.debug("ComThread: after ROT.addThread: "+mode); }
    }

    /**
     * Call CoUninitialize to release this java thread from COM
     */
    public static synchronized void Release() {
        if (JacobObject.isDebugEnabled()){JacobObject.debug("ComThread: before clearObjects"); }
        ROT.clearObjects();
        if (JacobObject.isDebugEnabled()){JacobObject.debug("ComThread: before UnInit"); }
        doCoUninitialize();
        if (JacobObject.isDebugEnabled()){JacobObject.debug("ComThread: after UnInit"); }
    }
    
    /**
     * @deprecated the java model leave the responsibility of clearing up objects 
     * to the Garbage Collector. Our programming model should not require that the
     * user specifically remove object from the thread.
     * 
     * This will remove an object from the ROT
     * @param o
     */
    public static synchronized void RemoveObject(JacobObject o)
    {
        ROT.removeObject(o);    
    }

    /**
     * @param threadModel
     */
    public static native void doCoInitialize(int threadModel);

    /**
     * 
     */
    public static native void doCoUninitialize();

    static {
        System.loadLibrary("jacob");
    }
}