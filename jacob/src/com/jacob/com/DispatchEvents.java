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
 * Every instance of tis acts as a wrapper around some java object
 * that wants callbacks from the microsoft side.  It represents
 * the connection between Java and COM for callbacks.
 * <p>
 * The callback mechanism will take any event that it receives and
 * try and find a java method with the same name that accepts the
 * Variant... as a parameter.  It will then wrap the call back data
 * in the Variant array and call the java method of the object
 * that this DispatchEvents object was initialized with.
 * 
 */
public class DispatchEvents extends JacobObject {
    
    /**
     * pointer to an MS data struct.
     * The COM layer knows the name of this variable and puts the windows
     * memory pointer here.
     */
    int m_pConnPtProxy = 0;
    /**
     * the wrapper for the event sink.
     * This object is the one that will be sent a message when an event
     * occurs in the MS layer.  Normally, the InvocationProxy will forward
     * the messages to a wrapped object that it contains.
     */
    InvocationProxy mInvocationProxy = null;

    
    /**
     * A constructor for those that want to supply their own InvocationProxy.
     * or subclass (future implementations may take an interface).
     * This lets someone distribute their events to other objects
     * at the single method dispatch point.
     * <p>
     * Most users of this class will use the other constructors!
     * 
     * @param sourceOfEvent the Dispatch object that will send events
     * @param pInvocationProxy the proxy object that expects to receive the
     * 	events for some other object
     */
    public DispatchEvents(Dispatch sourceOfEvent, InvocationProxy pInvocationProxy){
    	mInvocationProxy = pInvocationProxy;
    	if (mInvocationProxy != null){
    		init(sourceOfEvent, mInvocationProxy);
    	} else {
    		if (JacobObject.isDebugEnabled()){
    			JacobObject.debug("Cannot register null invocation proxy for events");
    		}
    		throw new IllegalArgumentException("Cannot register null invocation proxy for events");
    	}
    }
    
    /**
     * Creates the event callback linkage between the the
     * MS program represented by the Dispatch object and the
     * Java object that will receive the callback.
     * @param sourceOfEvent Dispatch object who's MS app will generate callbacks
     * @param eventSink Java object that wants to receive the events
     */
    public DispatchEvents(Dispatch sourceOfEvent, Object eventSink) {
		if (JacobObject.isDebugEnabled()){
			System.out.println(
					"DispatchEvents: Registering "+ eventSink + "for events ");
		}
    	mInvocationProxy = new InvocationProxy(eventSink);
    	if (mInvocationProxy != null){
    		init(sourceOfEvent, mInvocationProxy);
    	} else {
    		if (JacobObject.isDebugEnabled()){
    			JacobObject.debug("Cannot register null event sink for events");
    		}
    		throw new IllegalArgumentException("Cannot register null event sink for events");
    	}    		
    }

    /**
     * Creates the event callback linkage between the the
     * MS program represented by the Dispatch object and the
     * Java object that will receive the callback.
     * @param sourceOfEvent Dispatch object who's MS app will generate callbacks
     * @param eventSink Java object that wants to receive the events
     * @param progId ???
     */
    public DispatchEvents(Dispatch sourceOfEvent, Object eventSink, String progId) {
		if (JacobObject.isDebugEnabled()){
			System.out.println(
					"DispatchEvents: Registering "+ eventSink + "for events ");
		}
    	mInvocationProxy = new InvocationProxy(eventSink);
    	if (mInvocationProxy != null) {
	        init2(sourceOfEvent, mInvocationProxy, progId);
		} else {
			if (JacobObject.isDebugEnabled()){
				JacobObject.debug("Cannot register null event sink for events");
			}
    		throw new IllegalArgumentException("Cannot register null event sink for events");
		}
    }

    /**
     * hooks up a connection point proxy by progId
     * event methods on the sink object will be called
     * by name with a signature of <name>(Variant[] args)
     * @param src
     * @param sink
     */
    protected native void init(Dispatch src, Object sink);

    /**
     * hooks up a connection point proxy by progId
     * event methods on the sink object will be called
     * by name with a signature of <name>(Variant[] args)
     * @param src
     * @param sink
     * @param progId
     */
    protected native void init2(Dispatch src, Object sink, String progId);

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
    	if (mInvocationProxy!=null){
    		mInvocationProxy.clearTarget();
    	}
        mInvocationProxy = null;
        super.safeRelease();
        if (m_pConnPtProxy != 0){
            release();
            m_pConnPtProxy = 0;
        } else {
            // looks like a double release
            if (isDebugEnabled()){debug("DispatchEvents:"+this.hashCode()+" double release");}
        }
    }

    static {
        System.loadLibrary("jacob");
    }
}