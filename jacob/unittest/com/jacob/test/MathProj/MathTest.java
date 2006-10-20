package com.jacob.test.MathProj;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.*;

/**
 * This example uses the MathTest sample VB COM DLL under
 * the MathProj directory
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
class MathTest {
	public static void main(String[] args) {
		MathTest me = new MathTest();
		me.runTest();
	}
	
	public MathTest(){
	}
	
	public void runTest(){
		// deprecated
		// System.runFinalizersOnExit(true);
		Dispatch test = new ActiveXComponent("MathTest.Math");
		TestEvents te = new TestEvents();
		DispatchEvents de = new DispatchEvents(test, te);
		if (de == null) {
			System.out
					.println("null returned when trying to create DispatchEvents");
		}
		System.out.println(Dispatch.call(test, "Add", new Variant(1),
				new Variant(2)));
		System.out.println(Dispatch.call(test, "Mult", new Variant(2),
				new Variant(2)));
		Variant v = Dispatch.call(test, "Mult", new Variant(2), new Variant(2));
		// this should return false
		System.out.println("v.isNull=" + v.isNull());
		v = Dispatch.call(test, "getNothing");
		// these should return nothing
		System.out.println("v.isNull=" + v.isNull());
		System.out.println("v.toDispatch=" + v.toDispatch());
	}

public class TestEvents {
	public void DoneAdd(Variant[] args) {
		System.out.println("DoneAdd called in java");
	}

	public void DoneMult(Variant[] args) {
		System.out.println("DoneMult called in java");
	}
}

}
