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
 * This class creates the scaffolding for event callbacks.
 * 
 */
public class DispatchEvents extends JacobObject {
    int m_pConnPtProxy = 0;

    /**
     * create a permanent reference to this Variant so that
     * it never gets garbage collected.   The 
     * Dispatch proxies will keep a handle on this so
     * we don't really want it released
     */
    private static Variant prototypicalVariant = new VariantViaEvent();
    
    /**
     * @param src
     * @param sink
     */
    public DispatchEvents(Dispatch src, Object sink) {
        init(src, sink, prototypicalVariant);
    }

    /**
     * @param src
     * @param sink
     * @param progId
     */
    public DispatchEvents(Dispatch src, Object sink, String progId) {
        init2(src, sink, prototypicalVariant, progId);
    }

    /**
     * hooks up a connection point proxy by progId
     * event methods on the sink object will be called
     * by name with a signature of <name>(Variant[] args)
     * protoVariant is a sacrificial variant object so we don't have to do findClass in callbacks
     * @param src
     * @param sink
     * @param protoVariant
     */
    protected native void init(Dispatch src, Object sink, Object protoVariant);

    /**
     * hooks up a connection point proxy by progId
     * event methods on the sink object will be called
     * by name with a signature of <name>(Variant[] args)
     * protoVariant is a sacrificial variant object so we don't have to do findClass in callbacks
     * @param src
     * @param sink
     * @param protoVariant
     * @param progId
     */
    protected native void init2(Dispatch src, Object sink, Object protoVariant, String progId);

    /**
     *  now private so only this object can asccess
     *  was: call this to explicitly release the com object before gc
     * 
     */
    private native void release();
    
    /*
     *  (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() {
        safeRelease();
    }
    
    /*
     *  (non-Javadoc)
     * @see com.jacob.com.JacobObject#safeRelease()
     */
    public void safeRelease(){
        super.safeRelease();
        if (m_pConnPtProxy != 0){
            release();
            m_pConnPtProxy = 0;
        } else {
            // looks like a double release
            if (isDebugEnabled()){debug(this.getClass().getName()+":"+this.hashCode()+" double release");}
        }
    }

    static {
        System.loadLibrary("jacob");
    }
}