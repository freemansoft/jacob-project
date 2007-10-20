package com.jacob.test.events;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.DispatchEvents;
import com.jacob.com.InvocationProxy;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * This test was lifted from a forum posting and shows how you can't listen to
 * Excel events (added post 1.9.1 Eclipse Settings.) That test was modified make
 * this a MSWord event listener to demonstrate that the InvocationProxy code
 * works with MS Word Events This also uses the 1.10 InvocationProxy to receive
 * the events.
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class WordEventTest extends BaseTestCase {

	/**
	 * load up word, register for events and make stuff happen
	 * 
	 * @param args
	 */
	public void testCaptureWordEvents() {
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
				fail("No exception thrown but no dispatch returned for Word events");
			} else {
				// Yea!
				System.out.println("Successfully attached to " + pid);

			}
			// this is different from the ExcelEventTest because it uses
			// the jacob active X api instead of the Dispatch api
			System.out.println("version=" + axc.getPropertyAsString("Version"));
			axc.setProperty("Visible", true);
			ActiveXComponent documents = axc
					.getPropertyAsComponent("Documents");
			if (documents == null) {
				fail("unable to get documents");
			}
			axc.invoke("Quit", new Variant[] {});

		} catch (ComException cfe) {
			cfe.printStackTrace();
			fail("Failed to attach to " + pid + ": " + cfe.getMessage());

		}
		System.out
				.println("Someone needs to add some MSWord commands to this to "
						+ "make some on screen stuff happens so the tester "
						+ "thinks we tested something");
	}

	/**
	 * a class that receives messages from word
	 */
	public class WordEvents extends InvocationProxy {
		/**
		 * Constructor so we can create an instance that implements invoke()
		 */
		public WordEvents() {
		}

		/**
		 * override the invoke() method to log all the events without writing a
		 * bunch of code
		 */
		public Variant invoke(String methodName, Variant targetParameter[]) {
			System.out.println("Received event from Windows program"
					+ methodName);
			return null;
		}

	}
}