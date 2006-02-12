package com.jacob.com;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.DispatchEvents;

/**
 * This test was lifted from a forum posting and shows how you can't listen to
 * Excel events (added post 1.9.1 Eclipse Settings.)  This also uses the 1.9.1 
 * InvocationProxy to receive the events.
 * <p> supported command line options with default values are 
 * -Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=false
 * -Dcom.jacob.debug=false
 */
public class WordEventTest extends InvocationProxy {

	/**
	 * load up excel, register for events and make stuff happen
	 * @param args
	 */
	public static void main(String args[]) {
		String pid = "Word.Application";
		String typeLibLocation = null;

		// Grab The Component.
		ActiveXComponent axc = new ActiveXComponent(pid);
		try {
			// Add a listener (doesn't matter what it is).
			DispatchEvents de;
			if (typeLibLocation == null) {
				de = new DispatchEvents(axc, new WordEventTest());
			} else {
				de = new DispatchEvents(axc, new WordEventTest(), pid,
						typeLibLocation);
			}
			if (de == null) {
				System.out
						.println("No exception thrown but no dispatch returned for Word events");
			} else {
				// Yea!
				System.out.println("Successfully attached to " + pid);

			}
			// this is different from the ExcelEventTest because it uses
			// the jacob active X api instead of the Dispatch api
			System.out.println("version=" + axc.getPropertyAsString("Version"));
			axc.setProperty("Visible",true);
			ActiveXComponent documents = axc.getPropertyAsComponent("Documents");
			if (documents == null){
				System.out.println("unable to get documents");
			}
			axc.invoke("Quit", new Variant[] {});

		} catch (ComException cfe) {
			cfe.printStackTrace();
			System.out.println("Failed to attach to " + pid + ": "
					+ cfe.getMessage());

		}
	}

	/**
	 * dummy consturctor to create an InvocationProxy that wraps nothing
	 */
	public WordEventTest() {
	}

	/**
	 * override the invoke method to loga ll the events
	 */
	public Variant invoke(String methodName, Variant targetParameter[]) {
		System.out.println("Received event from Windows program" + methodName);
		return null;
	}

}
