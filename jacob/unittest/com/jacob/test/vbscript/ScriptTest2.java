package com.jacob.test.vbscript;

import com.jacob.com.*;
import com.jacob.activeX.*;

/**
 * This example demonstrates how to make calls between
 * two different STA's.
 * First, to create an STA, you need to extend the STA class
 * and override its OnInit() method. This method will be called
 * in the STA's thread so you can use it to create your COM
 * components that will run in that STA.
 * If you then try to call methods on those components from other
 * threads (STA or MTA) - this will fail.
 * You cannot create a component in an STA and call its methods
 * from another thread.
 * You can use the DispatchProxy to get a proxy to any Dispatch
 * that lives in another STA. This object has to be created in the
 * STA that houses the Dispatch (in this case it's created in the
 * OnInit method). Then, another thread can call the toDispatch()
 * method of DispatchProxy to get a local proxy. At most ONE (!)
 * thread can call toDispatch(), and the call can be made only once.
 * This is because a IStream object is used to pass the proxy, and
 * it is only written once and closed when you read it.
 * If you need multiple threads to access a Dispatch pointer, then
 * create that many DispatchProxy objects.
 */
class ScriptTest2 extends STA
{
  public static ActiveXComponent sC;
  public static DispatchEvents de = null;
  public static Dispatch sControl = null;
  public static DispatchProxy sCon = null;

  public boolean OnInit()
  {
     try
     {
       System.out.println("OnInit");
       System.out.println(Thread.currentThread());
       String lang = "VBScript";
       sC = new ActiveXComponent("ScriptControl");
       sControl = (Dispatch)sC.getObject();

       // sCon can be called from another thread
       sCon = new DispatchProxy(sControl);

       Dispatch.put(sControl, "Language", lang);
       ScriptTestErrEvents te = new ScriptTestErrEvents();
       de = new DispatchEvents(sControl, te);
       return true;
     }
     catch (Exception e)
     {
       e.printStackTrace();
       return false;
     }
  }

  public void OnQuit()
  {
     System.out.println("OnQuit");
  }

  public static void main(String args[]) throws Exception
  {
    try {
      ComThread.InitSTA();
      ScriptTest2 script = new ScriptTest2();
      Thread.sleep(1000);

      // get a thread-local Dispatch from sCon
      Dispatch sc = sCon.toDispatch();

      // call a method on the thread-local Dispatch obtained
      // from the DispatchProxy. If you try to make the same
      // method call on the sControl object - you will get a
      // ComException.
      Variant result = Dispatch.call(sc, "Eval", args[0]);
      System.out.println("eval("+args[0]+") = "+ result);
      script.quit();
      System.out.println("called quit");
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
    }
  }
}

