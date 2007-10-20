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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The Running Object Table (ROT) maps each thread to a collection of all the
 * JacobObjects that were created in that thread. It always operates on the
 * current thread so all the methods are static and they implicitly get the
 * current thread.
 * <p>
 * The clearObjects method is used to release all the COM objects created by
 * Jacob in the current thread prior to uninitializing COM for that thread.
 * <p>
 * Prior to 1.9, manual garbage collection was the only option in Jacob, but
 * from 1.9 onward, setting the com.jacob.autogc system property allows the
 * objects referenced by the ROT to be automatically GCed. Automatic GC may be
 * preferable in systems with heavy event callbacks.
 * <p>
 * Is [ 1116101 ] jacob-msg 0284 relevant???
 */
public abstract class ROT {
	/**
	 * Manual garbage collection was the only option pre 1.9 Can staticly cache
	 * the results because only one value and we don't let it change during a
	 * run
	 */
	protected static final boolean USE_AUTOMATIC_GARBAGE_COLLECTION = "true"
			.equalsIgnoreCase(System.getProperty("com.jacob.autogc"));

	/**
	 * Suffix added to class name to make up property name that determines if
	 * this object should be stored in the ROT. This 1.13 "feature" makes it
	 * possible to cause VariantViaEvent objects to not be added to the ROT in
	 * event callbacks.
	 * <p>
	 * We don't have a static for the actual property because there is a
	 * different property for each class that may make use of this feature.
	 */
	protected static String PUT_IN_ROT_SUFFIX = ".PutInROT";

	/**
	 * A hash table where each element is another HashMap that represents a
	 * thread. Each thread HashMap contains the com objects created in that
	 * thread
	 */
	private static HashMap<String, Map<JacobObject, String>> rot = new HashMap<String, Map<JacobObject, String>>();

	/**
	 * adds a new thread storage area to rot
	 * 
	 * @return Map corresponding to the thread that this call was made in
	 */
	protected synchronized static Map<JacobObject, String> addThread() {
		// should use the id here instead of the name because the name can be
		// changed
		String t_name = Thread.currentThread().getName();
		if (rot.containsKey(t_name)) {
			// nothing to do
		} else {
			Map<JacobObject, String> tab = null;
			if (JacobObject.isDebugEnabled()) {
				JacobObject.debug("ROT: Automatic GC flag == "
						+ USE_AUTOMATIC_GARBAGE_COLLECTION);
			}
			if (!USE_AUTOMATIC_GARBAGE_COLLECTION) {
				tab = new HashMap<JacobObject, String>();
			} else {
				tab = new WeakHashMap<JacobObject, String>();
			}
			rot.put(t_name, tab);
		}
		return getThreadObjects(false);
	}

	/**
	 * returns the pool for this thread if it exists. can create a new one if
	 * you wish by passing in TRUE
	 * 
	 * @param createIfDoesNotExist
	 * @return Map the collection that holds the objects created in the current
	 *         thread
	 */
	protected synchronized static Map<JacobObject, String> getThreadObjects(
			boolean createIfDoesNotExist) {
		String t_name = Thread.currentThread().getName();
		if (!rot.containsKey(t_name) && createIfDoesNotExist) {
			addThread();
		}
		return rot.get(t_name);
	}

	/**
	 * Iterates across all of the entries in the Hashmap in the rot that
	 * corresponds to this thread. This calls safeRelease() on each entry and
	 * then clears the map when done and removes it from the rot. All traces of
	 * this thread's objects will disapear. This is called by COMThread in the
	 * tear down and provides a synchronous way of releasing memory
	 */
	protected synchronized static void clearObjects() {
		Map<JacobObject, String> tab = getThreadObjects(false);
		if (JacobObject.isDebugEnabled()) {
			JacobObject.debug("ROT: " + rot.keySet().size()
					+ " thread tables exist");
		}
		if (tab != null) {
			if (JacobObject.isDebugEnabled()) {
				JacobObject.debug("ROT: " + tab.keySet().size()
						+ " objects to clear in this thread ");
			}
			// walk the values
			Iterator<JacobObject> it = tab.keySet().iterator();
			while (it.hasNext()) {
				JacobObject o = it.next();
				if (o != null
				// can't use this cause creates a Variant if calling SafeAray
				// and we get an exception modifying the collection while
				// iterating
				// && o.toString() != null
				) {
					if (JacobObject.isDebugEnabled()) {
						if (o instanceof SafeArray) {
							// SafeArray create more objects when calling
							// toString()
							// which causes a concurrent modification exception
							// in HashMap
							JacobObject.debug("ROT: removing "
									+ o.getClass().getName());
						} else {
							// Variant toString() is probably always bad in here
							JacobObject.debug("ROT: removing " + o.hashCode()
									+ "->" + o.getClass().getName());
						}
					}
					o.safeRelease();
				}
				// used to be an iterator.remove() but why bother when we're
				// nuking them all anyway?
			}
			// empty the collection
			tab.clear();
			// remove the collection from rot
			rot.remove(Thread.currentThread().getName());
			if (JacobObject.isDebugEnabled()) {
				JacobObject.debug("ROT: thread table cleared and removed");
			}
		} else {
			if (JacobObject.isDebugEnabled()) {
				JacobObject.debug("ROT: nothing to clear!");
			}
		}
	}

	/**
	 * @deprecated the java model leave the responsibility of clearing up
	 *             objects to the Garbage Collector. Our programming model
	 *             should not require that the user specifically remove object
	 *             from the thread.
	 * 
	 * This will remove an object from the ROT
	 * @param o
	 */
	protected synchronized static void removeObject(JacobObject o) {
		String t_name = Thread.currentThread().getName();
		Map<JacobObject, String> tab = rot.get(t_name);
		if (tab != null) {
			tab.remove(o);
		}
		o.safeRelease();
	}

	/**
	 * adds an object to the HashMap for the current thread
	 * 
	 * @param o
	 */
	protected synchronized static void addObject(JacobObject o) {
		// check the system property to see if this class is put in the ROT
		// the default value is "true" which simulates the old behavior
		String shouldIncludeClassInROT = System.getProperty(o.getClass()
				.getName()
				+ PUT_IN_ROT_SUFFIX, "true");
		if (shouldIncludeClassInROT.equalsIgnoreCase("false")) {
			if (JacobObject.isDebugEnabled()) {
				JacobObject.debug("JacobObject: New instance of "
						+ o.getClass().getName() + " not added to ROT");
			}
		} else {
			Map<JacobObject, String> tab = getThreadObjects(false);
			if (tab == null) {
				// this thread has not been initialized as a COM thread
				// so make it part of MTA for backwards compatibility
				ComThread.InitMTA(false);
				tab = getThreadObjects(true);
			}
			if (JacobObject.isDebugEnabled()) {
				JacobObject.debug("ROT: adding " + o + "->"
						+ o.getClass().getName()
						+ " table size prior to addition:" + tab.size());
			}
			if (tab != null) {
				tab.put(o, null);
			}
		}
	}

	/**
	 * ROT can't be a subclass of JacobObject because of the way ROT pools are
	 * managed so we force a DLL load here by referncing JacobObject
	 */
	static {
		LibraryLoader.loadJacobLibrary();
	}

}
