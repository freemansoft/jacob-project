/*
* Copyright (c) 1999-2004 Sourceforge JACOB Project.
* All rights reserved. Originator: Dan Adler (http://danadler.com).
* Get more information about JACOB at http://sourceforge.net/projects/jacob-project
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.jacob.com;

/**
 *
 */
public class DispatchCallback extends JacobObject {

    /**
     * pointer to an MS data struct. The COM layer knows the name of this
     * variable and puts the windows memory pointer here.
     */
    long m_pConnPtProxy = 0;

    /**
     * the wrapper for the event sink. This object is the one that will be sent
     * a message when an event occurs in the MS layer. Normally, the
     * InvocationProxy will forward the messages to a wrapped object that it
     * contains.
     */
    InvocationProxy mInvocationProxy = null;

    private Dispatch dispatch = null;

    public DispatchCallback(Object eventSink) {
        if (JacobObject.isDebugEnabled()) {
            System.out.println("DispatchCallback: Registering " + eventSink
                    + "for calbacks ");
        }

        if (eventSink instanceof InvocationProxy) {
            this.mInvocationProxy = (InvocationProxy) eventSink;
        } else {
            this.mInvocationProxy = this.getInvocationProxy(eventSink);
        }
        if (this.mInvocationProxy != null) {
            this.init3(this.mInvocationProxy);
        } else {
            if (JacobObject.isDebugEnabled()) {
                JacobObject.debug("Cannot register null event sink for events");
            }
            throw new IllegalArgumentException(
                    "Cannot register null event sink for events");
        }

        this.dispatch = new Dispatch(this.m_pConnPtProxy) {};
    }

    /**
     * Returns an instance of the proxy configured with pTargetObject as its
     * target
     *
     * @param pTargetObject
     * @return InvocationProxy an instance of the proxy this DispatchEvents will
     *         send to the COM layer
     */
    protected InvocationProxy getInvocationProxy(Object pTargetObject) {
        final InvocationProxy newProxy = new InvocationProxyAllVariants();
        newProxy.setTarget(pTargetObject);
        return newProxy;
    }

    /**
     * hooks up a connection point proxy by progId event methods on the sink
     * object will be called by name with a signature of <name>(Variant[] args)
     *
     * You must specify the location of the typeLib.
     *
     * @param sink
     *            the object that will receive the messages
     */
    private native void init3(Object sink);

    /**
     * now private so only this object can asccess was: call this to explicitly
     * release the com object before gc
     *
     */
    private native void release();

    protected native void registerEvent(int eventCode, String methodName);

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() {
        this.safeRelease();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jacob.com.JacobObject#safeRelease()
     */
    @Override
    public void safeRelease() {
        if (this.mInvocationProxy != null) {
            this.mInvocationProxy.setTarget(null);
        }
        this.mInvocationProxy = null;
        super.safeRelease();
        if (this.m_pConnPtProxy != 0) {
            this.release();
            this.m_pConnPtProxy = 0;
        } else {
            // looks like a double release
            if (isDebugEnabled()) {
                debug("DispatchEvents:" + this.hashCode() + " double release");
            }
        }
        this.dispatch = null;
    }

    protected Dispatch dispatch() {
        return this.dispatch;
    }
}
