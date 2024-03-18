package com.jacob.test.events;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * This test runs fine against jdk 1.4 and 1.5
 * 
 * This demonstrates the new event handling code in jacob 1.7 This example will
 * open up IE and print out some of the events it listens to as it navigates to
 * web sites. contributed by Niels Olof Bouvin mailto:n.o.bouvin@daimi.au.dk and
 * Henning Jae jehoej@daimi.au.dk
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */

public class IETest extends BaseTestCase {

	/**
	 * well known address we can navigate to
	 */
	private final String testUrls[] = {
			"http://sourceforge.net/projects/jacob-project",
			"http://www.google.com" };

	/**
	 * runs the IE test and feeds it commands
	 */
	public void testRunIECleanly() {
		runTheTest(true, testUrls);
	}

	/**
	 * runs the IE test and feeds it commands
	 */
	public void testRunIETerminateWithoutWait() {
		runTheTest(false, testUrls);
	}

	/**
	 * The actual work of running the test.
	 * 
	 * @param waitForQuit
	 * @param urls
	 */
	private void runTheTest(boolean waitForQuit, String[] urls) {
		// this line starts the pump but it runs fine without it
		ComThread.startMainSTA();
		// Run the test in a thread. Lets us test running out of "main" thread
		IETestThread aThread = new IETestThread(waitForQuit, urls);
		aThread.start();
		while (aThread.isAlive()) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// done with the sleep
			}
		}
		System.out.println("Main: Thread quit, about to quitMainSTA in thread "
				+ Thread.currentThread().getName());
		// this line only does something if startMainSTA() was called
		ComThread.quitMainSTA();
		System.out.println("Main: did quit main sta in thread "
				+ Thread.currentThread().getName());

		if (aThread.threadFailedWithException != null) {
			aThread.threadFailedWithException.printStackTrace();
			fail("caught an unexpected exception "
					+ aThread.threadFailedWithException);
		}
	}
}

class IETestThread extends Thread {
	/** flag that says we got a quit message from IE */
	public static boolean quitHandled = false;

	/**
	 * determines if we wait until last quit call back received before
	 * terminating
	 */
	private static boolean waitUntilReceivedQuitCallback = true;

	/**
	 * the places we should navigate to
	 */
	private static String[] targets = null;

	/**
	 * holds any caught exception so the main/test case can see them
	 */
	public Throwable threadFailedWithException = null;

	/**
	 * constructor for the test thread
	 * 
	 * @param beNeat
	 *            should we wait until quit received
	 * @param urls
	 *            the web pages we will navigate to
	 */
	public IETestThread(boolean beNeat, String urls[]) {
		super();
		waitUntilReceivedQuitCallback = beNeat;
		targets = urls;
	}

	/**
	 * Run through the addresses passed in via the constructor
	 */
	@Override
	public void run() {
		// pick a time that lets sourceforge respond (in msec)
		int delay = 3000;
		// pre-1.14 paired with statement below that blows up
		ComThread.InitMTA();
		ActiveXComponent ie = new ActiveXComponent(
				"InternetExplorer.Application");
		try {
			Dispatch.put(ie, "Visible", new Variant(true));
			Dispatch.put(ie, "AddressBar", new Variant(true));
			System.out.println("IETestThread: " + Dispatch.get(ie, "Path"));
			// Some version of IE broke this. Not sure which one
			// Dispatch.put(ie, "StatusText", new Variant("My Status Text"));

			System.out.println("IETestThread: About to hookup event listener");
			IEEvents ieE = new IEEvents();
			new DispatchEvents(ie, ieE, "InternetExplorer.Application.1");
			System.out.println("IETestThread: Did hookup event listener");

			for (String url : targets) {
				System.out.println("IETestThread: About to call navigate to "
						+ url);
				Dispatch.call(ie, "Navigate", new Variant(url));
				System.out.println("IETestThread: Did call navigate to " + url);
				try {
					Thread.sleep(delay);
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			threadFailedWithException = e;
			e.printStackTrace();
		} catch (Throwable re) {
			threadFailedWithException = re;
			re.printStackTrace();
		} finally {
			System.out.println("IETestThread: About to send Quit");
			ie.invoke("Quit", new Variant[] {});
			System.out.println("IETestThread: Did send Quit");
		}
		// a value is set to false if we try to crash VM by leaving before
		// callbacks all received
		if (waitUntilReceivedQuitCallback) {
			System.out
					.println("IETestThread: Waiting until we've received quit callback");
			// wait a little while for it to end
			while (!quitHandled) {
				try {
					Thread.sleep(delay / 10);
				} catch (InterruptedException e) {
				}
			}
			System.out.println("IETestThread: Received the OnQuit callback");
		} else {
			System.out.println("IETestThread: Not waiting for OnQuit callback");
		}
		System.out.println("IETestThread: Calling ComThread.Release in thread "
				+ Thread.currentThread().getName());
		ComThread.Release();
	}

	/**
	 * The events class must be publicly accessible for reflection to work. The
	 * list of available events is located at
	 * http://msdn2.microsoft.com/en-us/library/aa768280.aspx
	 */
	public class IEEvents {
		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void BeforeNavigate2(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): BeforeNavigate2 "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void CommandStateChange(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName()
					+ "): CommandStateChange " + args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void DocumentComplete(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): DocumentComplete "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void DownloadBegin(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): DownloadBegin "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void DownloadComplete(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): DownloadComplete "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void NavigateError(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): NavigateError "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void NavigateComplete2(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): NavigateComplete "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void NewWindow2(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): NewWindow2 "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void OnFullScreen(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): OnFullScreen "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void OnMenuBar(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): OnMenuBar "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void OnQuit(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): OnQuit "
					+ args.length + " parameters");
			IETestThread.quitHandled = true;
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void OnStatusBar(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): OnStatusBar "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void OnTheaterMode(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): OnTheaterMode "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void OnToolBar(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): OnToolBar "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void OnVisible(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): OnVisible "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void ProgressChange(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): ProgressChange "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void PropertyChange(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): PropertyChange "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void SetSecureLockIcon(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName()
					+ "): setSecureLockIcon " + args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void StatusTextChange(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): StatusTextChange "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void TitleChange(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): TitleChange "
					+ args.length + " parameters");
		}

		/**
		 * Internet explorer event this proxy can receive
		 * 
		 * @param args
		 *            the COM Variant objects that this event passes in.
		 */
		public void WindowClosing(Variant[] args) {
			System.out.println("IEEvents Received ("
					+ Thread.currentThread().getName() + "): WindowClosing "
					+ args.length + " parameters");
		}
	}

}
