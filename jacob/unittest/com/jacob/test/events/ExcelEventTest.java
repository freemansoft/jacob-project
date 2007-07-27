package com.jacob.test.events;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.InvocationProxy;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * This test was lifted from a forum posting and shows how you can't listen to
 * Excel events (added post 1.9.1 Eclipse Settings.)  This also uses the 1.9.1 
 * InvocationProxy to receive the events.
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
public class ExcelEventTest extends BaseTestCase {

	/**
	 * load up excel, register for events and make stuff happen
	 * @param args
	 */
	public void testExcelWithInvocationProxy() {
		String pid = "Excel.Application";
		String typeLibLocation = "C:\\Program Files\\Microsoft Office\\OFFICE11\\EXCEL.EXE";

		// Grab The Component.
		ActiveXComponent axc = new ActiveXComponent(pid);
		try {
			// Add a listener (doesn't matter what it is).
			DispatchEvents de;
			if (typeLibLocation == null) {
				de = new DispatchEvents(axc, new ExcelEvents());
			} else {
				de = new DispatchEvents(axc, new ExcelEvents(), pid,
						typeLibLocation);
			}
			if (de == null) {
				System.out
						.println("No exception thrown but no dispatch returned for Excel events");
			} else {
				// Yea!
				System.out.println("Successfully attached to " + pid);

			}

			System.out.println("version=" + axc.getProperty("Version"));
			System.out.println("version=" + Dispatch.get(axc, "Version"));
			axc.setProperty("Visible", true);
			Dispatch workbooks = axc.getPropertyAsComponent("Workbooks");
			Dispatch workbook = Dispatch.get(workbooks, "Add").toDispatch();
			Dispatch sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
			Dispatch a1 = Dispatch.invoke(sheet, "Range", Dispatch.Get,
					new Object[] { "A1" }, new int[1]).toDispatch();
			Dispatch a2 = Dispatch.invoke(sheet, "Range", Dispatch.Get,
					new Object[] { "A2" }, new int[1]).toDispatch();
			System.out.println("Inserting value into A1");
			System.out.println("Inserting calculation 2xA1 into A2");
			Dispatch.put(a1, "Value", "123.456");
			Dispatch.put(a2, "Formula", "=A1*2");
			System.out.println("Retrieved a1 from excel:" + Dispatch.get(a1, "Value"));
			System.out.println("Retrieved a2 from excel:" + Dispatch.get(a2, "Value"));
			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
			axc.invoke("Quit", new Variant[] {});

		} catch (ComException cfe) {
			cfe.printStackTrace();
			fail("Failed to attach to " + pid + ": "
					+ cfe.getMessage());

		}
	}


public class ExcelEvents extends InvocationProxy {
	/**
	 * Constructor so we can create an instance that implements invoke()
	 */
	public ExcelEvents() {
	}

	/**
	 * Override the invoke method to log all the events so that we don't have to 
	 * implement all of the specific events.
	 */
	public Variant invoke(String methodName, Variant targetParameter[]) {
		System.out.println("Received event from Windows program" + methodName);
		return null;
	}

}
}
