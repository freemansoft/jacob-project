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
    System.out.println(Dispatch.call(test, "Add", new Variant(1), new Variant(2)));
    System.out.println(Dispatch.call(test, "Mult", new Variant(2), new Variant(2)));
    Variant v = Dispatch.call(test, "Mult", new Variant(2), new Variant(2));
		// this should return false
		System.out.println("v.isNull="+v.isNull());
    v = Dispatch.call(test, "getNothing");
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
