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
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
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
      System.out.println("Main: Thread quit, about to quit main sta in thread "
    		  +Thread.currentThread().getName());
      // this line only does someting if startMainSTA() was called
      ComThread.quitMainSTA();
      System.out.println("Main: did quit main sta in thread "
    		  +Thread.currentThread().getName());
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
          System.out.println("IETestThread: " + Dispatch.get(ie, "Path"));
          Dispatch.put(ie, "StatusText", new Variant("My Status Text"));
    
          System.out.println("IETestThread: About to hookup event listener");
          IEEvents ieE = new IEEvents();
          new DispatchEvents((Dispatch) ie, ieE,"InternetExplorer.Application.1");
          System.out.println("IETestThread: Did hookup event listener");
          /// why is this here?  Was there some other code here in the past?
          Variant optional = new Variant();
          optional.putNoParam();
    
          System.out.println("IETestThread: About to call navigate to sourceforge");
          Dispatch.call(ie, "Navigate", new Variant("http://sourceforge.net/projects/jacob-project"));
          System.out.println("IETestThread: Did call navigate to sourceforge");
          try { Thread.sleep(delay); } catch (Exception e) {}
          System.out.println("IETestThread: About to call navigate to yahoo");
          Dispatch.call(ie, "Navigate", new Variant("http://groups.yahoo.com/group/jacob-project"));
          System.out.println("IETestThread: Did call navigate to yahoo");
          try { Thread.sleep(delay); } catch (Exception e) {}
        } catch (Exception e) {
          e.printStackTrace();
        } catch (Throwable re){
        	re.printStackTrace();
        } finally {
        	System.out.println("IETestThread: About to send Quit");
        	ie.invoke("Quit", new Variant[] {});
          	System.out.println("IETestThread: Did send Quit");
        }
        // this blows up when it tries to release a DispatchEvents object
        // I think this is because there is still one event we should get back
        // "OnQuit" that will came after we have released the thread pool
        // this is probably messed up because DispatchEvent object will have been
        // freed before the callback
        // commenting out ie.invoke(quit...) causes this to work without error
        // this code tries to wait until the quit has been handled but that doesn't work
        System.out.println("IETestThread: Waiting until we've received the quit callback");
        while (!quitHandled){
          try { Thread.sleep(delay/5);} catch (InterruptedException e) {}
        }
        System.out.println("IETestThread: Received the quit callback");
        // wait a little while for it to end
        //try {Thread.sleep(delay); } catch (InterruptedException e) {}
        System.out.println("IETestThread: about to call ComThread.Release in thread " +
        			Thread.currentThread().getName());

        ComThread.Release();
    }    

/**
 * the events class must be publicly accessable for reflection to work
 */
public class IEEvents 
{
    public void BeforeNavigate2(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): BeforeNavigate2 "+args.length);
    }

    public void CommandStateChange(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): CommandStateChange "+args.length);
    }

    public void DocumentComplete(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): DocumentComplete "+args.length);
    }

    public void DownloadBegin(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): DownloadBegin "+args.length);
    }

    public void DownloadComplete(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): DownloadComplete "+args.length);
    }

    public void NavigateComplete2(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): NavigateComplete "+args.length);
    }

    public void NewWindow2(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): NewWindow2 "+args.length);
    }

    public void OnFullScreen(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): OnFullScreen "+args.length);
    }

    public void OnMenuBar(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): OnMenuBar "+args.length);
    }

    public void OnQuit(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): OnQuit "+args.length);
      IETestThread.quitHandled = true;
    }

    public void OnStatusBar(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): OnStatusBar "+args.length);
    }

    public void OnTheaterMode(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): OnTheaterMode "+args.length);
    }

    public void OnToolBar(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): OnToolBar "+args.length);
    }

    public void OnVisible(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): OnVisible "+args.length);
    }

    public void ProgressChange(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): ProgressChange "+args.length);
    }

    public void PropertyChange(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): PropertyChange "+args.length);
    }

    public void StatusTextChange(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): StatusTextChange "+args.length);
    }

    public void TitleChange(Variant[] args) {
      System.out.println("IEEvents Received ("+Thread.currentThread().getName()+"): TitleChange "+args.length);
    }
}

}
