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
 * An implementation of IEnumVariant based on code submitted by Thomas Hallgren
 * (mailto:Thomas.Hallgren@eoncompany.com)
 */
public class EnumVariant extends JacobObject implements java.util.Enumeration {
    private int m_pIEnumVARIANT;

    private final Variant[] m_recBuf = new Variant[1];

    // this only gets called from JNI
    //
    protected EnumVariant(int pIEnumVARIANT) {
        m_pIEnumVARIANT = pIEnumVARIANT;
    }

    /**
     * @param disp
     */
    public EnumVariant(Dispatch disp) {
        int[] hres = new int[1];
        Variant evv = Dispatch.invokev(disp, 
                Dispatch.DISPID_NEWENUM,
                Dispatch.Get, 
                new Variant[0], hres);
        if (evv.getvt() != Variant.VariantObject)
            //
            // The DISPID_NEWENUM did not result in a valid object
            //
            throw new ComFailException("Can't obtain EnumVARIANT");

        EnumVariant tmp = evv.toEnumVariant();
        m_pIEnumVARIANT = tmp.m_pIEnumVARIANT;
        tmp.m_pIEnumVARIANT = 0;
    }

    /**
     * Implements java.util.Enumeration
     * @return
     */
    public boolean hasMoreElements() {
        {
            if (m_recBuf[0] == null) {
                if (this.Next(m_recBuf) <= 0)
                    return false;
            }
            return true;
        }
    }

    /**
     * Implements java.util.Enumeration
     * @return
     */
    public Object nextElement() {
        Object last = m_recBuf[0];
        if (last == null) {
            if (this.Next(m_recBuf) <= 0)
                throw new java.util.NoSuchElementException();
            last = m_recBuf[0];
        }
        m_recBuf[0] = null;
        return last;
    }

    /**
     * Get next element in collection or null if at end
     * @return
     */
    public Variant Next() {
        if (hasMoreElements())
            return (Variant) nextElement();
        return null;
    }

    /**
     * @param receiverArray
     * @return
     */
    public native int Next(Variant[] receiverArray);

    /**
     * @param count
     */
    public native void Skip(int count);

    /**
     * 
     */
    public native void Reset();

    /* (non-Javadoc)
     * @see com.jacob.com.JacobObject#release()
     */
    public native void release();

    protected void finalize() {
        //System.out.println("EnumVariant finalize start");
        if (m_pIEnumVARIANT != 0)
            this.release();
        //System.out.println("EnumVariant finalize end");
    }
}