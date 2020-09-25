package com.jacob.test.events;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.activeX.ActiveXDispatchEvents;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * This test runs fine against jdk 1.4 and 1.5
 * 
 * This demonstrates the new event handling code in jacob 1.7 This example will
 * open up IE and print out some of the events it listens to as it havigates to
 * web sites. contributed by Niels Olof Bouvin mailto:n.o.bouvin@daimi.au.dk and
 * Henning Jae jehoej@daimi.au.dk
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 * 
 * @TODO: THIS TEST HANGS under windows 10 on whatever version it is for 2020/09
 */

public class IETestActiveXProxy extends BaseTestCase {

	/**
	 * the main test method that builds up the connection and runs the test
	 */
	public void testIEActiveProxyCallback() {
		// this line starts the pump but it runs fine without it
		ComThread.startMainSTA();
		// remove this line and it dies
		// /ComThread.InitMTA(true);
		IETestActiveProxyThread aThread = new IETestActiveProxyThread();
		aThread.start();
		while (aThread.isAlive()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// done with the sleep
				// e.printStackTrace();
			}
		}
		System.out
				.println("Main: Thread quit, about to quit main sta in thread "
						+ Thread.currentThread().getName());
		// this line only does something if startMainSTA() was called
		ComThread.quitMainSTA();
		System.out.println("Main: did quit main sta in thread "
				+ Thread.currentThread().getName());
		if (aThread.threadFailedWithException != null) {
			fail("caught an unexpected exception "
					+ aThread.threadFailedWithException);
		}
	}
}

class IETestActiveProxyThread extends Thread {
	/** says that the quit message has been received from the target application */
	public static boolean quitHandled = false;

	/**
	 * holds any caught exception so the main/test case can see them
	 */
	public Throwable threadFailedWithException = null;

	/** the thread's constructor */
	public IETestActiveProxyThread() {
		super();
	}

	public void run() {
		// this used to be 5 seconds but sourceforge is slow
		int delay = 5000; // msec
		// paired with statement below that blows up
		System.out.println("IETestActiveProxyThread: InitMTA()");
		ComThread.InitMTA();
		System.out.println("IETestActiveProxyThread: Creating ActiveXComponent for IE");
		ActiveXComponent ie = new ActiveXComponent(
				"InternetExplorer.Application");
		try {
			Dispatch.put(ie, "Visible", new Variant(true));
			Dispatch.put(ie, "AddressBar", new Variant(true));
			System.out.println("IETestActiveProxyThread: "
					+ Dispatch.get(ie, "Path"));
			Dispatch.put(ie, "StatusText", new Variant("My Status Text"));

			System.out
					.println("IETestActiveProxyThread: About to hookup event listener");
			IEEventsActiveProxy ieE = new IEEventsActiveProxy();
			new ActiveXDispatchEvents(ie, ieE, "InternetExplorer.Application.1");
			System.out
					.println("IETestActiveProxyThread: Did hookup event listener");
			// / why is this here? Was there some other code here in the past?
			Variant optional = new Variant();
			optional.putNoParam();

			System.out
					.println("IETestActiveProxyThread: About to call navigate to sourceforge");
			Dispatch.call(ie, "Navigate", new Variant(
					"http://sourceforge.net/projects/jacob-project"));
			System.out
					.println("IETestActiveProxyThread: Did call navigate to sourceforge");
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
			System.out
					.println("IETestActiveProxyThread: About to call navigate to yahoo");
			Dispatch.call(ie, "Navigate", new Variant(
					"http://groups.yahoo.com/group/jacob-project"));
			System.out
					.println("IETestActiveProxyThread: Did call navigate to yahoo");
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
		} catch (Exception e) {
			threadFailedWithException = e;
			e.printStackTrace();
		} catch (Throwable re) {
			threadFailedWithException = re;
			re.printStackTrace();
		} finally {
			System.out.println("IETestActiveProxyThread: About to send Quit");
			ie.invoke("Quit", new Variant[] {});
			System.out.println("IETestActiveProxyThread: Did send Quit");
		}
		// this blows up when it tries to release a DispatchEvents object
		// I think this is because there is still one event we should get back
		// "OnQuit" that will came after we have released the thread pool
		// this is probably messed up because DispatchEvent object will have
		// been
		// freed before the callback
		// commenting out ie.invoke(quit...) causes this to work without error
		// this code tries to wait until the quit has been handled but that
		// doesn't work
		System.out
				.println("IETestActiveProxyThread: Waiting until we've received the quit callback");
		while (!quitHandled) {
			try {
				Thread.sleep(delay / 5);
			} catch (InterruptedException e) {
			}
		}
		System.out
				.println("IETestActiveProxyThread: Received the quit callback");
		// wait a little while for it to end
		// try {Thread.sleep(delay); } catch (InterruptedException e) {}
		System.out
				.println("IETestActiveProxyThread: about to call ComThread.Release in thread "
						+ Thread.currentThread().getName());

		ComThread.Release();
	}

	/**
	 * The events class must be publicly accessable for reflection to work. The
	 * list of available events is located at
	 * http://msdn2.microsoft.com/en-us/library/aa768280.aspx
	 */
	public class IEEventsActiveProxy {

		public void BeforeNavigate2(Dispatch pDisp, String url, Integer flags,
				String targetFrameName, Variant postData, String headers,
				Boolean cancel) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): BeforeNavigate2 "
					+ url);
		}

		public void CommandStateChange(Integer command, Boolean enable) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName()
					+ "): CommandStateChange " + command);
		}

		public void DocumentComplete(Dispatch pDisp, String url) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): DocumentComplete "
					+ url);
		}

		public void DownloadBegin() {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): DownloadBegin ");
		}

		public void DownloadComplete() {
			System.out
					.println("IEEventsActiveProxy Received ("
							+ Thread.currentThread().getName()
							+ "): DownloadComplete ");
		}

		public void NavigateComplete2(Dispatch pDisp, String url) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): NavigateComplete "
					+ url);
		}

		public void NavigateError(Dispatch pDispatch, String url,
				String targetFrameName, Integer statusCode, Boolean Cancel) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): NavigateError "
					+ statusCode);
		}

		public void NewWindow2(Dispatch pDisp, Boolean cancel) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): NewWindow2 "
					+ pDisp);
		}

		public void OnFullScreen(Boolean fullScreen) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): OnFullScreen "
					+ fullScreen);
		}

		public void OnMenuBar(Boolean menuBar) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): OnMenuBar "
					+ menuBar);
		}

		public void OnQuit() {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): OnQuit ");
			IETestActiveProxyThread.quitHandled = true;
		}

		public void OnStatusBar(Boolean statusBar) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): OnStatusBar "
					+ statusBar);
		}

		public void OnTheaterMode(Boolean theaterMode) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): OnTheaterMode "
					+ theaterMode);
		}

		public void OnToolBar(Boolean onToolBar) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): OnToolBar "
					+ onToolBar);
		}

		public void OnVisible(Boolean onVisible) {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): onVisible "
					+ onVisible);
		}

		public void ProgressChange() {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): ProgressChange ");
		}

		public void PropertyChange() {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): PropertyChange ");
		}

		public void SetSecureLockIcon(Integer secureLockIcon) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName()
					+ "): setSecureLockIcon " + secureLockIcon);
		}

		public void StatusTextChange() {
			System.out
					.println("IEEventsActiveProxy Received ("
							+ Thread.currentThread().getName()
							+ "): StatusTextChange ");
		}

		public void TitleChange() {
			System.out.println("IEEventsActiveProxy Received ("
					+ Thread.currentThread().getName() + "): TitleChange ");
		}

		public void WindowClosing(Boolean isChildWindow) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): WindowClosing "
					+ isChildWindow);
		}
	}

}
