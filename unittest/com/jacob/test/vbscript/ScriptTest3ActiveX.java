package com.jacob.test.vbscript;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.ComThread;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * Here we create the ScriptControl component in a separate MTA thread and then
 * call the Eval method from the main thread. The main thread must also be an
 * MTA thread. If you try to create it as an STA then you will not be able to
 * make calls into a component running in another thread.
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class ScriptTest3ActiveX extends BaseTestCase {
	public static ActiveXComponent sC;

	public static DispatchEvents de = null;

	public static boolean quit = false;

	public void testYetAnotherScriptTest() {
		try {
			ComThread.InitMTA();
			ScriptTest3ActiveXInner script = new ScriptTest3ActiveXInner();
			script.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				// should we get this?
			}

			Variant result = sC.invoke("Eval", getSampleVPScriptForEval());
			System.out.println("eval(" + getSampleVPScriptForEval() + ") = "
					+ result);
			System.out.println("setting quit");
			ScriptTest3ActiveX.quit = true;
		} catch (ComException e) {
			e.printStackTrace();
			fail("Caught ComException " + e);
		} finally {
			System.out.println("main done");
			ComThread.Release();
		}
	}

	public class ScriptTest3ActiveXInner extends Thread {
		public void run() {
			try {
				ComThread.InitMTA();
				System.out.println("OnInit");
				String lang = "VBScript";
				sC = new ActiveXComponent("ScriptControl");
				sC.setProperty("Language", lang);
				ScriptTestErrEvents te = new ScriptTestErrEvents();
				de = new DispatchEvents(sC, te);
				System.out.println("sControl=" + sC);
				while (!quit) {
					sleep(100);
				}
				ComThread.Release();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("worker thread exits");
			}
		}

	}
}
