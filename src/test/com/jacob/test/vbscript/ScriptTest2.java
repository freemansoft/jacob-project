package com.jacob.test.vbscript;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.DispatchProxy;
import com.jacob.com.STA;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * This example demonstrates how to make calls between two different STA's.
 * First, to create an STA, you need to extend the STA class and override its
 * OnInit() method. This method will be called in the STA's thread so you can
 * use it to create your COM components that will run in that STA. If you then
 * try to call methods on those components from other threads (STA or MTA) -
 * this will fail. You cannot create a component in an STA and call its methods
 * from another thread. You can use the DispatchProxy to get a proxy to any
 * Dispatch that lives in another STA. This object has to be created in the STA
 * that houses the Dispatch (in this case it's created in the OnInit method).
 * Then, another thread can call the toDispatch() method of DispatchProxy to get
 * a local proxy. At most ONE (!) thread can call toDispatch(), and the call can
 * be made only once. This is because a IStream object is used to pass the
 * proxy, and it is only written once and closed when you read it. If you need
 * multiple threads to access a Dispatch pointer, then create that many
 * DispatchProxy objects.
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */

public class ScriptTest2 extends BaseTestCase {
	public void testScript2() {
		try {
			ComThread.InitSTA();
			ScriptTestSTA script = new ScriptTestSTA();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				// should we get this?
			}

			String scriptCommand = getSampleVPScriptForEval();
			// get a thread-local Dispatch from sCon
			Dispatch sc = script.sCon.toDispatch();

			// call a method on the thread-local Dispatch obtained
			// from the DispatchProxy. If you try to make the same
			// method call on the sControl object - you will get a
			// ComException.
			Variant result = Dispatch.call(sc, "Eval", scriptCommand);
			System.out.println("eval(" + scriptCommand + ") = " + result);
			script.quit();
			System.out.println("called quit");
		} catch (ComException e) {
			e.printStackTrace();
			fail("caught exception" + e);
		} finally {
			Integer I = null;
			for (int i = 1; i < 1000000; i++) {
				I = new Integer(i);
			}
			System.out.println(I);
			ComThread.Release();
		}
	}

	public class ScriptTestSTA extends STA {

		public DispatchEvents de = null;

		public Dispatch sControl = null;

		public DispatchProxy sCon = null;

		public boolean OnInit() {
			try {
				System.out.println("OnInit");
				System.out.println(Thread.currentThread());
				String lang = "VBScript";
				sControl = new ActiveXComponent("ScriptControl");

				// sCon can be called from another thread
				sCon = new DispatchProxy(sControl);

				Dispatch.put(sControl, "Language", lang);
				ScriptTestErrEvents te = new ScriptTestErrEvents();
				de = new DispatchEvents(sControl, te);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		public void OnQuit() {
			System.out.println("OnQuit");
		}

	}

}
