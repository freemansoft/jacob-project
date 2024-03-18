package com.jacob.test.vbscript;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * In this case the component is created and used in the same thread and it's an
 * Apartment Threaded component, so we call InitSTA.
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class ScriptTest extends BaseTestCase {

	public void testStupidSpeedTest() {
		String lang = "VBScript";
		ActiveXComponent sC = new ActiveXComponent("ScriptControl");
		Dispatch sControl = sC.getObject();
		Dispatch.put(sControl, "Language", lang);
		for (int i = 0; i < 10000; i++) {
			Dispatch.call(sControl, "Eval", "1+1");
		}
	}

	public void testCreatingDispatchEvents() {
		ComThread.InitSTA(true);
		DispatchEvents de = null;
		Dispatch sControl = null;

		try {
			String scriptCommand = getSampleVPScriptForEval();
			String lang = "VBScript";
			ActiveXComponent sC = new ActiveXComponent("ScriptControl");
			sControl = sC.getObject();
			Dispatch.put(sControl, "Language", lang);
			ScriptTestErrEvents te = new ScriptTestErrEvents();
			de = new DispatchEvents(sControl, te);
			if (de == null) {
				System.out
						.println("Received null when trying to create new DispatchEvents");
			}
			Variant result = Dispatch.call(sControl, "Eval", scriptCommand);
			// call it twice to see the objects reused
			result = Dispatch.call(sControl, "Eval", scriptCommand);
			// call it 3 times to see the objects reused
			result = Dispatch.call(sControl, "Eval", scriptCommand);
			System.out.println("eval(" + scriptCommand + ") = " + result);
		} catch (ComException e) {
			e.printStackTrace();
			fail("Caught Exception " + e);
		} finally {
			Integer I = null;
			for (int i = 1; i < 1000000; i++) {
				I = new Integer(i);
			}
			System.out.println(I);
			ComThread.Release();
			ComThread.quitMainSTA();
		}
	}
}
