package com.jacob.test.vbscript;

import com.jacob.com.*;
import com.jacob.activeX.*;

/**
 * Here we create the ScriptControl component in a separate MTA thread
 * and then call the Eval method from the main thread. The main thread
 * must also be an MTA thread. If you try to create it as an STA
 * then you will not be able to make calls into a component running
 * in another thread.
 */
class ScriptTest3ActiveX extends Thread
{
  public static ActiveXComponent sC;
  public static DispatchEvents de = null;
  public static boolean quit = false;

  public void run()
  {
     try
     {
       ComThread.InitMTA();
       System.out.println("OnInit");
       String lang = "VBScript";
       sC = new ActiveXComponent("ScriptControl");
       sC.setProperty("Language", lang);
       ScriptTestErrEvents te = new ScriptTestErrEvents();
       de = new DispatchEvents(sC, te);
       System.out.println("sControl="+sC);
       while (!quit) sleep(100);
       ComThread.Release();
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
     finally
     {
       System.out.println("worker thread exits");
     }
  }

  public static void main(String args[]) throws Exception
  {
    try {
      ComThread.InitMTA();
      ScriptTest3ActiveX script = new ScriptTest3ActiveX();
      script.start();
      Thread.sleep(1000);

      Variant result = sC.invoke("Eval", args[0]);
      System.out.println("eval("+args[0]+") = "+ result);
      System.out.println("setting quit");
      ScriptTest3ActiveX.quit = true;
    } catch (ComException e) {
      e.printStackTrace();
    }
    finally
    {
      System.out.println("main done");
      ComThread.Release();
    }
  }
}

