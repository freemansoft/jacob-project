package com.jacob.com;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.DispatchEvents;

/**
 * This test was lifted from a forum posting and shows how you can't listen to
 * Excel events added post 1.9.1 Eclipse Settings...
 * -Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=false
 * -Dcom.jacob.debug=true
 */
public class ExcelEventTest {

	public static void main(String args[]) {

		listenTo("Word.Application",null);

		// Create an Excel Listener
		listenTo("Excel.Application",
				"C:\\Program Files\\Microsoft Office\\OFFICE11\\EXCEL.EXE");
	}

	private static void listenTo(String pid, String typeLibLocation) {

		// Grab The Component.
		ActiveXComponent axc = new ActiveXComponent(pid);
		try {
			// Add a listener (doesn't matter what it is).
			DispatchEvents de;
			if (typeLibLocation == null){
				de = new DispatchEvents(axc, new ExcelEventTest());
			} else {
				de = new DispatchEvents(axc, new ExcelEventTest(),
					pid,typeLibLocation);
			}
			if (de == null){
				System.out.println(
					"No exception thrown but now dispatch returned for Excel events");
			}
			// Yea!
			System.out.println("Successfully attached to " + pid);
		} catch (ComFailException e) {
			e.printStackTrace();
			System.out.println("Failed to attach to " + pid + ": "
					+ e.getMessage());

		}
	}
}
