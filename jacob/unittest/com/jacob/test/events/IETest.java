package com.jacob.test.events;

import com.jacob.com.*;
import com.jacob.activeX.*;
/**
 * This test runs fine against jdk.1.5.0_05 and kills the VM 
 * under any variant of 1.4.
 * 
 * This demonstrates the new event handling code in jacob 1.7
 * This example will open up IE and print out some of the events
 * it listens to as it havigates to web sites.
 * contributed by Niels Olof Bouvin mailto:n.o.bouvin@daimi.au.dk
 * and Henning Jae jehoej@daimi.au.dk
 * <P>
 * You can run this in eclipse with the command line options
 * <code> -Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=false </code>
 */

class IETest
{
    public static void main(String[] args)
    {
      // this line starts the pump but it runs fine without it
      ComThread.startMainSTA();
      // remove this line and it dies
      ///ComThread.InitMTA(true);
      IETestThread aThread = new IETestThread();
      aThread.start();
      while (aThread.isAlive()){
          try {
            Thread.sleep(1000);
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
	public static boolean quitHandled = false;
	
    public IETestThread(){
        super();
    }
    
    public void run()
    {
    	// this used to be 5 seconds but sourceforge is slow
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
          new DispatchEvents((Dispatch) ie, ieE,"InternetExplorer.Application.1");
          Variant optional = new Variant();
          optional.noParam();
    
          Dispatch.call(ie, "Navigate", new Variant("http://sourceforge.net/projects/jacob-project"));
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
        // commenting out ie.invoke(quit...) causes this to work without error
        // this code tries to wait until the quit has been handled but that doesn't work
        System.out.println("IETest: Waiting until we've received the quit callback");
        while (!quitHandled){
          try { Thread.sleep(delay/5);} catch (InterruptedException e) {}
        }
        // wait a little while for it to end
        try {Thread.sleep(delay); } catch (InterruptedException e) {}
        System.out.println("IETest: about to call release in thread " +
        			Thread.currentThread().getName());

        ComThread.Release();
    }    

/**
 * the events class must be publicly accessable for reflection to work
 */
public class IEEvents 
{
    public void BeforeNavigate2(Variant[] args) {
      System.out.println("IEEvents: BeforeNavigate2");
    }

    public void CommandStateChange(Variant[] args) {
      System.out.println("IEEvents: CommandStateChange");
    }

    public void DocumentComplete(Variant[] args) {
      System.out.println("IEEvents: DocumentComplete");
    }

    public void DownloadBegin(Variant[] args) {
      System.out.println("IEEvents: DownloadBegin");
    }

    public void DownloadComplete(Variant[] args) {
      System.out.println("IEEvents: DownloadComplete");
    }

    public void NavigateComplete2(Variant[] args) {
      System.out.println("IEEvents: NavigateComplete2");
    }

    public void NewWindow2(Variant[] args) {
      System.out.println("IEEvents: NewWindow2");
    }

    public void OnFullScreen(Variant[] args) {
      System.out.println("IEEvents: OnFullScreen");
    }

    public void OnMenuBar(Variant[] args) {
      System.out.println("IEEvents: OnMenuBar");
    }

    public void OnQuit(Variant[] args) {
      System.out.println("IEEvents: OnQuit");
      IETestThread.quitHandled = true;
    }

    public void OnStatusBar(Variant[] args) {
      System.out.println("IEEvents: OnStatusBar");
    }

    public void OnTheaterMode(Variant[] args) {
      System.out.println("IEEvents: OnTheaterMode");
    }

    public void OnToolBar(Variant[] args) {
      System.out.println("IEEvents: OnToolBar");
    }

    public void OnVisible(Variant[] args) {
      System.out.println("IEEvents: OnVisible");
    }

    public void ProgressChange(Variant[] args) {
      System.out.println("IEEvents: ProgressChange");
    }

    public void PropertyChange(Variant[] args) {
      System.out.println("IEEvents: PropertyChange");
    }

    public void StatusTextChange(Variant[] args) {
      System.out.println("IEEvents: StatusTextChange");
    }

    public void TitleChange(Variant[] args) {
      System.out.println("IEEvents: TitleChange");
    }
}

}
