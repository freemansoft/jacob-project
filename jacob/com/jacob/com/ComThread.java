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
import java.util.Vector;

public abstract class ComThread
{
    private static final int MTA = 0x0; 
    private static final int STA = 0x2; 

    public static boolean haveSTA = false;
    public static MainSTA mainSTA = null;

    /**
     * Initialize the current java thread to be part of the 
     * Multi-threaded COM Apartment
     */
    public static synchronized void InitMTA()
    {
      InitMTA(false);
    }

    /**
     * Initialize the current java thread to be an STA
     */
    public static synchronized void InitSTA()
    {
      InitSTA(false);
    }

    /**
     * Initialize the current java thread to be part of the 
     * Multi-threaded COM Apartment, if createMainSTA is true,
     * create a separate MainSTA
     * thread that will house all Apartment Threaded components
     */
    public static synchronized void InitMTA(boolean createMainSTA)
    {
      Init(createMainSTA, MTA);
    }

    /**
     * Initialize the current java thread to be an STA
     * COM Apartment, if createMainSTA is true,
     * create a separate MainSTA
     * thread that will house all Apartment Threaded components
     */
    public static synchronized void InitSTA(boolean createMainSTA)
    {
      Init(createMainSTA, STA);
    }

    public static synchronized void startMainSTA()
    {
      mainSTA = new MainSTA();
      haveSTA = true;
    }

    public static synchronized void quitMainSTA()
    {
      if (mainSTA != null) mainSTA.quit();
    }

    /**
     * Initialize the current java thread to be part of the 
     * MTA/STA COM Apartment
     */
    public static synchronized void Init(boolean createMainSTA, int mode)
    {
      if (createMainSTA && !haveSTA)
      {
        // if the current thread is going to be in the MTA and there
        // is no STA thread yet, then create a main STA thread
        // to avoid COM creating its own
        startMainSTA();
      }
      //System.out.println("before Init: "+mode);
      doCoInitialize(mode);
      //System.out.println("after Init");
      ROT.addThread();
      //System.out.println("after ROT.addThread");
    }

    /**
     * Call CoUninitialize to release this java thread from COM
     */
    public static synchronized void Release()
    {
      //System.out.println("before clearObjects");
      ROT.clearObjects();
      //System.out.println("before UnInit");
      doCoUninitialize();
      //System.out.println("after UnInit");
    }

    public static native void doCoInitialize(int threadModel);
    public static native void doCoUninitialize();

    static {
      System.loadLibrary("jacob");
    }
}
