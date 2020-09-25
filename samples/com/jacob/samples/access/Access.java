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
package com.jacob.samples.access;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 * 
 */
class Access {
	/**
	 * the main loop for the test
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ComThread.InitSTA();
		// original test used this
		// ActiveXComponent ax = new ActiveXComponent("DAO.PrivateDBEngine");
		// my xp box with a later release of access needed this
		ActiveXComponent ax = new ActiveXComponent("DAO.PrivateDBEngine.35");
		// this only works for access files pre-access-2000
		// this line doesn't work on my xp box in Eclipse
		// Dispatch db = open(ax, ".\\sample2.mdb");
		// this works when running in eclipse because the test cases run pwd
		// project root
		Dispatch db = open(ax, "samples/com/jacob/samples/access/sample2.mdb");
		String sql = "select * from MainTable";
		// make a temporary querydef
		Dispatch qd = Dispatch.call(db, "CreateQueryDef", "").toDispatch();
		// set the SQL string on it
		Dispatch.put(qd, "SQL", sql);
		Variant result = getByQueryDef(qd);
		// the 2-d safearray is transposed from what you might expect
		System.out.println("resulting array is " + result.toSafeArray());
		close(db);
		System.out.println("about to call ComThread.Release()");
		ComThread.Release();
	}

	/**
	 * Open a database
	 * 
	 * @param ax
	 * @param fileName
	 * @return dispatch object that was opened
	 */
	public static Dispatch open(ActiveXComponent ax, String fileName) {
		Variant f = new Variant(false);
		// open the file in read-only mode
		Variant[] args = new Variant[] { new Variant(fileName), f, f };
		Dispatch openDB = ax.invoke("OpenDatabase", args).toDispatch();
		return openDB;
	}

	/**
	 * Close a database
	 * 
	 * @param openDB
	 *            db to be closed
	 */
	public static void close(Dispatch openDB) {
		Dispatch.call(openDB, "Close");
	}

	/**
	 * Extract the values from the recordset
	 * 
	 * @param recset
	 * @return Variant that is the returned values
	 */
	public static Variant getValues(Dispatch recset) {
		Dispatch.callSub(recset, "moveFirst");
		Variant vi = new Variant(4096);
		Variant v = Dispatch.call(recset, "GetRows", vi);
		return v;
	}

	/**
	 * should return ?? for the passed in ??
	 * 
	 * @param qd
	 * @return Variant results of query?
	 */
	public static Variant getByQueryDef(Dispatch qd) {
		// get a reference to the recordset
		Dispatch recset = Dispatch.call(qd, "OpenRecordset").toDispatch();
		// get the values as a safe array
		String[] cols = getColumns(recset);
		for (int i = 0; i < cols.length; i++) {
			System.out.print(cols[i] + " ");
		}
		System.out.println("");
		Variant vals = getValues(recset);
		return vals;
	}

	/**
	 * gets the columns form the rec set
	 * 
	 * @param recset
	 * @return list of column names
	 */
	public static String[] getColumns(Dispatch recset) {
		Dispatch flds = Dispatch.get(recset, "Fields").toDispatch();
		int n_flds = Dispatch.get(flds, "Count").getInt();
		String[] s = new String[n_flds];
		Variant vi = new Variant();
		for (int i = 0; i < n_flds; i++) {
			vi.putInt(i);
			// must use the invoke method because this is a method call
			// that wants to have a Dispatch.Get flag...
			Dispatch fld = Dispatch.invoke(recset, "Fields", Dispatch.Get,
					new Object[] { vi }, new int[1]).toDispatch();
			Variant name = Dispatch.get(fld, "Name");
			s[i] = name.toString();
		}
		return s;
	}
}
