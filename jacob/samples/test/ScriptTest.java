package samples.test;

import com.jacob.com.*;
import com.jacob.activeX.*;

/**
 * In this case the component is created and used in the same thread
 * and it's an Apartment Threaded component, so we call InitSTA.
 */
class ScriptTest
{
  public static void main(String args[]) throws Exception
  {
		ComThread.InitSTA(true);
    DispatchEvents de = null;
    Dispatch sControl = null;

    try {
      String lang = "VBScript";
      ActiveXComponent sC = new ActiveXComponent("ScriptControl");
      sControl = sC.getObject();
      sControl.setProperty("Language", lang);
      errEvents te = new errEvents();
      de = new DispatchEvents(sControl, te);
      sControl.setProperty("Eval", args[0]);
      // call it twice to see the objects reused
      Variant result = sControl.call("Eval", args[0]);
      // call it 3 times to see the objects reused
      result = sControl.call("Eval", args[0]);
      System.out.println("eval("+args[0]+") = "+ result);
    } catch (ComException e) {
      e.printStackTrace();
    }
    finally
    {
      Integer I = null;
      for(int i=1;i<1000000;i++)
      {
        I = new Integer(i);
      }
      System.out.println(I);
			ComThread.Release();
			ComThread.quitMainSTA();
    }
  }
}

class errEvents {
  public void Error(Variant[] args)
  {
    System.out.println("java callback for error!");
  }
  public void Timeout(Variant[] args)
  {
    System.out.println("java callback for error!");
  }
}
