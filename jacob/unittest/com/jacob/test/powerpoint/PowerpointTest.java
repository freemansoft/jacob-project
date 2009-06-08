package com.jacob.test.powerpoint;

/** 
 * $Id$ 
 * 
 * This is really more of a multi threaded tester 
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.test.BaseTestCase;

/**
 * 
 * power point test program posted to sourceforge to demonstrate memory problem.
 * The submitter stated they had the problem on windows 2000 with office 2000 I
 * have been unable to duplicate on windows XP with office 2003. I am adding
 * this to the tree just in case we need to come back to it.
 * <P>
 * This test was modified for office 2007 to synchronize communication with Excel.
 * Office 2003 didn't require this.
 * <p>
 * This relies on BaseTestCase to provide the root path to the file under test
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class PowerpointTest extends BaseTestCase {
	private static final int NUM_THREADS = 5;
	protected static final int NUM_ITERATIONS = 50;

	/**
	 * main program that lets us run this as a test
	 * 
	 * @param args
	 */
	public void testPowerpoint() {
		ComThread.InitMTA();

		ActiveXComponent component = new ActiveXComponent(
				"Powerpoint.Application");
		Dispatch comPowerpoint = component.getObject();

		try {
			PowerpointTestThread[] threads = new PowerpointTestThread[NUM_THREADS];
			for (int i = 0; i < NUM_THREADS; i++) {
				threads[i] = new PowerpointTestThread(i + 1, comPowerpoint);
				threads[i].start();
			}

			boolean allThreadsFinished = false;
			while (!allThreadsFinished) {
				allThreadsFinished = true;
				for (int i = 0; i < NUM_THREADS; i++) {
					if (threads[i].isAlive()) {
						allThreadsFinished = false;
						break;
					}
				}
				if (!allThreadsFinished) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// no op
					}
				}
			}

			Dispatch.call(comPowerpoint, "Quit");
			for (int i = 0; i < NUM_THREADS; i++) {
				if (threads[i].threadFailedWithException != null) {
					fail("caught unexpected exception in thread "
							+ threads[i].threadFailedWithException);
				}
			}
		} finally {
			ComThread.Release();
		}

	}

	/**
	 * the thread class that runs power point
	 */
	public class PowerpointTestThread extends Thread {
		/**
		 * holds any caught exception so the main/test case can see them
		 */
		public Throwable threadFailedWithException = null;

		private int threadID;
		private Dispatch comPowerpoint;

		/**
		 * thread constructor
		 * 
		 * @param threadID
		 * @param comPowerpoint
		 */
		public PowerpointTestThread(int threadID, Dispatch comPowerpoint) {
			super("TestThread " + threadID);
			this.threadID = threadID;
			this.comPowerpoint = comPowerpoint;
		}

		public void run() {
			System.out.println("Thread \"" + Thread.currentThread().getName()
					+ "\" started");
			System.out.flush();
			ComThread.InitMTA();
			try {
				for (int i = 0; i < NUM_ITERATIONS; i++) {
					if (i % 10 == 0) {
						System.out.println(Thread.currentThread().getName()
								+ ": Iteration " + i);
						System.out.flush();
					}
					// office 2003 seems to have been able to handle more
					// multi-threaded requests than office 2007
					// office 2003 could handle 5 threads @ 50 iterations
					// office 2007 can only handle 1 thread at a time
					synchronized(comPowerpoint){
					Dispatch comPresentations = Dispatch.get(comPowerpoint,
					"Presentations").toDispatch();
					Dispatch comPresentation = Dispatch.call(
							comPresentations,
							"Open",
							getWindowsFilePathToPackageResource("test"
									+ threadID + ".ppt", this.getClass()),
							new Integer(0), new Integer(0), new Integer(0))
							.toDispatch();
					Dispatch.call(comPresentation, "Close");
					}
				}
			} catch (ComFailException cfe) {
				threadFailedWithException = cfe;
				System.err.println(Thread.currentThread().getName()
						+ "\" while working on: "
						+ getWindowsFilePathToPackageResource("test" + threadID
								+ ".ppt", this.getClass()));
				cfe.printStackTrace();
			} catch (Exception e) {
				threadFailedWithException = e;
				System.err.println("Error in Thread \""
						+ Thread.currentThread().getName() + "\":");
				e.printStackTrace();
			} finally {
				ComThread.Release();
				System.out.println("Thread \""
						+ Thread.currentThread().getName() + "\" finished");
				System.out.flush();
			}
		}

	}
}