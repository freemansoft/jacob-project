package samples.test;

import com.jacob.com.*;
import com.jacob.activeX.*;

/**
 * In this case the component is created and used in the same thread
 * and it's an Apartment Threaded component, so we call InitSTA.
 */
class ScriptTestActiveX
{
  public static void main(String args[]) throws Exception
  {
		ComThread.InitSTA(true);
    DispatchEvents de = null;

    try {
      String lang = "VBScript";
      ActiveXComponent sC = new ActiveXComponent("ScriptControl");
      sC.setProperty("Language",lang);
      ScriptTestErrEvents te = new ScriptTestErrEvents();
      de = new DispatchEvents(sC, te);
      Variant result;
      result = sC.invoke("Eval",args[0]);
      // call it twice to see the objects reused
      result = sC.invoke("Eval",args[0]);
      // call it 3 times to see the objects reused
      result = sC.invoke("Eval",args[0]);
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

