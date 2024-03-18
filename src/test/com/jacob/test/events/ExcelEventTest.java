package com.jacob.test.events;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.InvocationProxy;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * This test was lifted from a forum posting and shows how you can't listen to
 * Excel events (added post 1.9.1 Eclipse Settings.) This also uses the 1.9.1
 * InvocationProxy to receive the events. The test was modified in 1.14 to show
 * how to hook up multiple event listeners to various Excel components
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class ExcelEventTest extends BaseTestCase {

	/**
	 * load up excel, register for events and make stuff happen
	 * 
	 * @param args
	 */
	public void testExcelWithInvocationProxy() {
		ComThread.InitSTA();
		// we are going to listen to events on Application.
		// You can probably also listen Excel.Sheet and Excel.Chart
		String excelApplicationProgramId = "Excel.Application";
		String excelSheetProgramId = "Excel.Sheet";
		String typeLibLocation;
		// office 2003
		typeLibLocation = "C:\\Program Files\\Microsoft Office\\OFFICE11\\EXCEL.EXE";
		// office 2007
		typeLibLocation = "C:\\Program Files\\Microsoft Office\\OFFICE12\\EXCEL.EXE";
		// office 2013 32 bit
		typeLibLocation = "C:\\Program Files (x86)\\Microsoft Office\\Office14\\EXCEL.EXE";
		// Office 2013 32
		typeLibLocation = "C:\\Program Files (x86)\\Microsoft Office\\Office15\\EXCEL.EXE";
		// Office 2019 32
		typeLibLocation = "C:\\Program Files (x86)\\Microsoft Office\\root\\Office16\\EXCEL.EXE";
		
		// Grab The Component.
		ActiveXComponent axc = new ActiveXComponent(excelApplicationProgramId);
		hookupListener(axc, excelApplicationProgramId, typeLibLocation);

		try {

			System.out.println("version=" + axc.getProperty("Version"));
			System.out.println("version=" + Dispatch.get(axc, "Version"));
			axc.setProperty("Visible", true);
			Dispatch workbooks = axc.getPropertyAsComponent("Workbooks");
			Dispatch workbook = Dispatch.get(workbooks, "Add").toDispatch();
			Dispatch sheet = Dispatch.get(workbook, "ActiveSheet").toDispatch();
			System.out.println("Workbook: " + workbook);
			System.out.println("Sheet: " + sheet);
			if (typeLibLocation.contains("OFFICE11")) {
				// office 2007 throws crashes the VM
				System.out.println("Hooking up sheet listener");
				hookupListener(sheet, excelSheetProgramId, typeLibLocation);
			}
			System.out.println("Retrieving cells");
			Dispatch a1 = Dispatch.invoke(sheet, "Range", Dispatch.Get,
					new Object[] { "A1" }, new int[1]).toDispatch();
			Dispatch a2 = Dispatch.invoke(sheet, "Range", Dispatch.Get,
					new Object[] { "A2" }, new int[1]).toDispatch();
			System.out.println("Inserting value into A1");
			System.out.println("Inserting calculation 2xA1 into A2");
			Dispatch.put(a1, "Value", "123.456");
			Dispatch.put(a2, "Formula", "=A1*2");
			System.out.println("Retrieved a1 from excel:"
					+ Dispatch.get(a1, "Value"));
			System.out.println("Retrieved a2 from excel:"
					+ Dispatch.get(a2, "Value"));
			Variant f = new Variant(false);
			Dispatch.call(workbook, "Close", f);
			axc.invoke("Quit", new Variant[] {});

		} catch (ComException cfe) {
			cfe.printStackTrace();
			fail("Failed to attach to " + excelApplicationProgramId + ": "
					+ cfe.getMessage());
		}
		try {
			// the sleep is required to let everything clear out after the quit
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ComThread.Release();
	}

	/**
	 * extracted the listener hookup so we could try multiple listeners.
	 * 
	 * @param axc
	 * @param programId
	 * @param typeLibLocation
	 */
	private void hookupListener(Dispatch axc, String programId,
			String typeLibLocation) {
		// Add a listener (doesn't matter what it is).
		DispatchEvents applicationEvents;
		if (typeLibLocation == null) {
			applicationEvents = new DispatchEvents(axc, new ExcelEvents(
					programId));
		} else {
			applicationEvents = new DispatchEvents(axc, new ExcelEvents(
					programId), programId, typeLibLocation);
		}
		if (applicationEvents == null) {
			System.out
					.println("No exception thrown but no dispatch returned for Excel events");
		} else {
			// Yea!
			System.out
					.println("Successfully attached listener to " + programId);

		}
	}

	/**
	 * Proxy class to verify we receive expected events
	 */
	public class ExcelEvents extends InvocationProxy {

		private String listenerPrefix = "-";

		/**
		 * Constructor so we can create an instance that implements invoke()
		 * 
		 * @param interfaceIdentifier
		 *            a string that identifies which listener is speaking
		 */
		public ExcelEvents(String interfaceIdentifier) {
			listenerPrefix = interfaceIdentifier;
		}

		/**
		 * Override the invoke method to log all the events so that we don't
		 * have to implement all of the specific events.
		 */
		@Override
		public Variant invoke(String methodName, Variant targetParameter[]) {
			System.out.println("Received event from " + listenerPrefix + ": "
					+ methodName);
			return null;
		}

	}
}
