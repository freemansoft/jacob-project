package com.jacob.samples.test;

import com.jacob.com.*;
import com.jacob.activeX.*;
/*
 * This demonstrates the new event handling code in jacob 1.7
 * This example will open up IE and print out some of the events
 * it listens to as it havigates to web sites.
 * contributed by Niels Olof Bouvin mailto:n.o.bouvin@daimi.au.dk
 * and Henning Jae jehoej@daimi.au.dk
 */

class IETest
{
    public static void main(String[] args)
    {
      // this line starts the pump but it runs fine without it
      //ComThread.startMainSTA();
      // remove this line and it dies
      ComThread.InitMTA(true);
      IETestThread aThread = new IETestThread();
      aThread.run();
      while (aThread.isAlive()){
          try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // doen with the sleep
            //e.printStackTrace();
        }
      }
      System.out.println("Thread quit, about to quit main sta");
      // this line only does someting if startMainSTA() was called
      ComThread.quitMainSTA();
      System.out.println("did quit main sta");
    }
}

class IETestThread extends Thread
{
    public IETestThread(){
        super();
    }
    
    public void run()
    {
        int delay = 5000; // msec
        // paired with statement below that blows up
        ComThread.InitMTA();
        ActiveXComponent ie = new ActiveXComponent("InternetExplorer.Application");
        try {
          Dispatch.put(ie, "Visible", new Variant(true));
          Dispatch.put(ie, "AddressBar", new Variant(true));
          System.out.println(Dispatch.get(ie, "Path"));
          Dispatch.put(ie, "StatusText", new Variant("My Status Text"));
    
          IEEvents ieE = new IEEvents();
          DispatchEvents de = new DispatchEvents((Dispatch) ie, ieE,"InternetExplorer.Application.1");
          Variant optional = new Variant();
          optional.noParam();
    
          Dispatch.call(ie, "Navigate", new Variant("http://www.danadler.com/jacob"));
          try { Thread.sleep(delay); } catch (Exception e) {}
          Dispatch.call(ie, "Navigate", new Variant("http://groups.yahoo.com/group/jacob-project"));
          try { Thread.sleep(delay); } catch (Exception e) {}
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          ie.invoke("Quit", new Variant[] {});
        }
        // this blows up when it tries to release a DispatchEvents object
        // I think this is because there is still one event we should get back
        // "OnQuit" that will came after we have released the thread pool
        // this is probably messed up because DispatchEvent object will have been
        // freed before the callback
        System.out.println("about to call release in thread");
        ComThread.Release();
    }    
}

class IEEvents 
{
    public void BeforeNavigate2(Variant[] args) {
      System.out.println("BeforeNavigate2");
    }

    public void CommandStateChanged(Variant[] args) {
      System.out.println("CommandStateChanged");
    }

    public void DocumentComplete(Variant[] args) {
      System.out.println("DocumentComplete");
    }

    public void DownloadBegin(Variant[] args) {
      System.out.println("DownloadBegin");
    }

    public void DownloadComplete(Variant[] args) {
      System.out.println("DownloadComplete");
    }

    public void NavigateComplete2(Variant[] args) {
      System.out.println("NavigateComplete2");
    }

    public void NewWindow2(Variant[] args) {
      System.out.println("NewWindow2");
    }

    public void OnFullScreen(Variant[] args) {
      System.out.println("OnFullScreen");
    }

    public void OnMenuBar(Variant[] args) {
      System.out.println("OnMenuBar");
    }

    public void OnQuit(Variant[] args) {
      System.out.println("OnQuit");
    }

    public void OnStatusBar(Variant[] args) {
      System.out.println("OnStatusBar");
    }

    public void OnTheaterMode(Variant[] args) {
      System.out.println("OnTheaterMode");
    }

    public void OnToolBar(Variant[] args) {
      System.out.println("OnToolBar");
    }

    public void OnVisible(Variant[] args) {
      System.out.println("OnVisible");
    }

    public void ProgressChange(Variant[] args) {
      System.out.println("ProgressChange");
    }

    public void PropertyChange(Variant[] args) {
      System.out.println("PropertyChange");
    }

    public void StatusTextChange(Variant[] args) {
      System.out.println("StatusTextChange");
    }

    public void TitleChange(Variant[] args) {
      System.out.println("TitleChange");
    }
}
