package samples.test;

import com.jacob.com.*;

/*
 * This example uses the MathTest sample VB COM DLL under
 * the MathProj directory
 */
class math
{
  public static void main(String[] args)
  {
    System.runFinalizersOnExit(true);
    Dispatch test = new Dispatch("MathTest.Math");
    testEvents te = new testEvents();
    DispatchEvents de = new DispatchEvents(test, te);
    System.out.println(test.call("Add", 1, 2));
    System.out.println(test.call("Mult",1,2));
    Variant v = test.call("Mult", 2,2);
		// this should return false
		System.out.println("v.isNull="+v.isNull());
    v = test.call("getNothing");
		// these should return nothing
		System.out.println("v.isNull="+v.isNull());
		System.out.println("v.toDispatch="+v.toDispatch());
  }
}

class testEvents {
  public void DoneAdd(Variant[] args)
  {
    System.out.println("DoneAdd called in java");
  }
  public void DoneMult(Variant[] args)
  {
    System.out.println("DoneMult called in java");
  }
}
