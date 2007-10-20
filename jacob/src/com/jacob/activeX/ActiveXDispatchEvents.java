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
package com.jacob.activeX;

import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.InvocationProxy;

/**
 * RELEASE 1.12 EXPERIMENTAL.
 * <p>
 * Use this exactly like the DispatchEvents class. This class plugs in an
 * ActiveXInvocationProxy instead of an InvocationProxy. It is the
 * ActiveXInvocationProxy that implements the reflection calls and invoke the
 * found java event callbacks. See ActiveXInvocationProxy for details.
 * 
 * 
 */
public class ActiveXDispatchEvents extends DispatchEvents {

	/**
	 * This is the most commonly used constructor.
	 * <p>
	 * Creates the event callback linkage between the the MS program represented
	 * by the Dispatch object and the Java object that will receive the
	 * callback.
	 * 
	 * @param sourceOfEvent
	 *            Dispatch object who's MS app will generate callbacks
	 * @param eventSink
	 *            Java object that wants to receive the events
	 */
	public ActiveXDispatchEvents(Dispatch sourceOfEvent, Object eventSink) {
		super(sourceOfEvent, eventSink, null);
	}

	/**
	 * None of the samples use this constructor.
	 * <p>
	 * Creates the event callback linkage between the the MS program represented
	 * by the Dispatch object and the Java object that will receive the
	 * callback.
	 * 
	 * @param sourceOfEvent
	 *            Dispatch object who's MS app will generate callbacks
	 * @param eventSink
	 *            Java object that wants to receive the events
	 * @param progId
	 *            ???
	 */
	public ActiveXDispatchEvents(Dispatch sourceOfEvent, Object eventSink,
			String progId) {
		super(sourceOfEvent, eventSink, progId, null);
	}

	/**
	 * Creates the event callback linkage between the the MS program represented
	 * by the Dispatch object and the Java object that will receive the
	 * callback.
	 * 
	 * <pre>
	 * &gt;ActiveXDispatchEvents de = 
	 * 			new ActiveXDispatchEvents(someDispatch,someEventHAndler,
	 * 				&quot;Excel.Application&quot;,
	 * 				&quot;C:\\Program Files\\Microsoft Office\\OFFICE11\\EXCEL.EXE&quot;);
	 * 
	 * @param sourceOfEvent Dispatch object who's MS app will generate callbacks
	 * @param eventSink Java object that wants to receive the events
	 * @param progId , mandatory if the typelib is specified
	 * @param typeLib The location of the typelib to use
	 * 
	 */
	public ActiveXDispatchEvents(Dispatch sourceOfEvent, Object eventSink,
			String progId, String typeLib) {
		super(sourceOfEvent, eventSink, progId, typeLib);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jacob.com.DispatchEvents#getInvocationProxy(java.lang.Object)
	 */
	protected InvocationProxy getInvocationProxy(Object pTargetObject) {
		InvocationProxy newProxy = new ActiveXInvocationProxy();
		newProxy.setTarget(pTargetObject);
		return newProxy;
	}

}
