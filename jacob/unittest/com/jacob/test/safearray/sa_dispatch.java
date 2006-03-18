package com.jacob.test.safearray;

import com.jacob.com.*;
import com.jacob.activeX.*;

class sa_dispatch
{
	public static void main(String args[])
	{
	  // deprecated
	  //System.runFinalizersOnExit(true);

    try {
      String lang = "VBScript";
      ActiveXComponent sC = new ActiveXComponent("ScriptControl");
      Dispatch sControl = (Dispatch)sC.getObject();
      Dispatch.put(sControl, "Language", lang);

      Variant result = Dispatch.call(sControl, "Eval", args[0]);
      System.out.println("eval("+args[0]+") = "+ result);

			// wrap the script control in a variant
			Variant v = new Variant(sControl);

			// create a safe array of type dispatch
      SafeArray sa = new SafeArray(Variant.VariantDispatch, 1);

			// put the variant in the array
			sa.setVariant(0, v);

			// take it back out
			Variant v2 = sa.getVariant(0);
			Dispatch d = v2.toDispatch();

			// make sure you can call eval on it
      result = Dispatch.call(d, "Eval", args[0]);
      System.out.println("eval("+args[0]+") = "+ result);
    } catch (ComException e) {
      e.printStackTrace();
    }
  }
}
