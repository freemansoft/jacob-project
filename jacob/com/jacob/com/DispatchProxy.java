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
 * If you need to pass a COM Dispatch object between STA threads, you
 * have to marshall the interface.
 * This class is used as follows: the STA that creates the Dispatch
 * object must construct an instance of this class. Another thread
 * can then call toDispatch() on that instance and get a Dispatch
 * pointer which has been marshalled.
 * WARNING: You can only call toDispatch() once! If you need to call
 * it multiple times (or from multiple threads) you need to construct
 * a separate DispatchProxy instance for each such case!
 */
public class DispatchProxy extends JacobObject
{
  public int m_pStream;

  public DispatchProxy(Dispatch localDispatch)
  {
    MarshalIntoStream(localDispatch);
  }

  public Dispatch toDispatch()
  {
    return MarshalFromStream();
  }

  private native void MarshalIntoStream(Dispatch d);
  private native Dispatch MarshalFromStream();
  public native void release();

  public void finalize()
  {
    if (m_pStream != 0) release();
  }
}
