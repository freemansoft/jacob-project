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

/**
 * A class that implements a Single Threaded Apartment.
 * Users will subclass this and override OnInit() and OnQuit()
 * where they will create and destroy a COM component that wants to
 * run in an STA other than the main STA.
 */
public class STA extends Thread
{
    public int threadID;

		public STA()
		{
		  start(); // start the thread
		}

    public void run()
    {
      // init COM
      ComThread.InitSTA();
      if (OnInit())
      {
        // this call blocks in the win32 message loop
        // until quitMessagePump is called
        doMessagePump();
      }
      OnQuit();
      // uninit COM
      ComThread.Release();
    }

    /**
     * Override this method to create and initialize any COM
     * component that you want to run in this thread. If anything
     * fails, return false to terminate the thread.
     */
    public boolean OnInit()
    {
      return true;
    }

    /**
     * Override this method to destroy any resource
     * before the thread exits and COM in uninitialized
     */
    public void OnQuit()
    {
    }

    public void quit()
    {
      quitMessagePump();
    }

    /**
     * run a message pump for the main STA
     */
    public native void doMessagePump();

    /**
     * quit message pump for the main STA
     */
    public native void quitMessagePump();

    static {
      System.loadLibrary("jacob");
    }
}
