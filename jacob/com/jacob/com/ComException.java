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
 * Standard exception thrown by com jni code when there is a problem
 */
public abstract class ComException extends RuntimeException {
    // Fields
    /** TODO: what is this field */
    protected int hr;
    /** TODO: what is this field */
    protected int m_helpContext;
    /** TODO: what is this field */
    protected String m_helpFile;
    /** TODO: what is this field */
    protected String m_source;

    /**
     * constructor
     *  
     */
    public ComException() {
        super();
    }

    /**
     * constructor with error code?
     * 
     * @param newHr ??
     */
    public ComException(int newHr) {
        super();
        this.hr = newHr;
    }

    /**
     * @param newHr
     * @param description
     */
    public ComException(int newHr, String description) {
        super(description);
        this.hr = newHr;
    }

    /**
     * @param newHr
     * @param source
     * @param helpFile
     * @param helpContext
     */
    public ComException(int newHr, String source, String helpFile,
            int helpContext) {
        super();
        this.hr = newHr;
        m_source = source;
        m_helpFile = helpFile;
        m_helpContext = helpContext;
    }

    /**
     * @param newHr
     * @param description
     * @param source
     * @param helpFile
     * @param helpContext
     */
    public ComException(int newHr, String description, String source,
            String helpFile, int helpContext) {
        super(description);
        this.hr = newHr;
        m_source = source;
        m_helpFile = helpFile;
        m_helpContext = helpContext;
    }

    /**
     * @param description
     */
    public ComException(String description) {
        super(description);
    }

    /**
     * @return int representation of the help context
     */
    // Methods
    public int getHelpContext() {
        return m_helpContext;
    }

    /**
     * @return String ??? help file
     */
    public String getHelpFile() {
        return m_helpFile;
    }

    /**
     * @return int hr result ??
     */
    public int getHResult() {
        return hr;
    }

    /**
     * @return String source ??
     */
    public String getSource() {
        return m_source;
    }
}