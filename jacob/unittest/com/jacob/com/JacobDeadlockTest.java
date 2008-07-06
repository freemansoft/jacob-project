package com.jacob.com;

import com.jacob.test.BaseTestCase;

/**
 * Sourceforge defect report 1986987 July 2008. This test case demonstrated a
 * deadlock issue.
 * <UL>
 * <LI>One thread attempts to create an object in a thread where InitMTA has
 * not been called. This results in ROT.addObject being called which then calls
 * ComThread.InitMTA
 * <LI>One thread attempts to call ComThread.release() which then calls ROT
 * .clear objects.
 * </UL>
 * The result is one thread with a call sequence ComThread--ROT and the other
 * with a sequence ROT--ComThread resulting in deadlock.
 * <p>
 * This test will fail with debug logging turned on because of the amount of
 * time it takes to write the debug output.
 * 
 * @author jsamarziya
 * 
 */
public class JacobDeadlockTest extends BaseTestCase {
	private static final long TIMEOUT = 5000l;

	/** Thread component */
	public static class TestThread extends Thread {
		private final int id;
		private final boolean initCOM;
		private final boolean writeOutput;

		/**
		 * constructor for ThestThread
		 * 
		 * @param id
		 * @param initCOM
		 * @param writeOutput
		 * 
		 */
		public TestThread(int id, boolean initCOM, boolean writeOutput) {
			this.id = id;
			this.initCOM = initCOM;
			this.writeOutput = writeOutput;
		}

		@Override
		public void run() {
			for (int i = 0; i < 1000; i++) {
				log("iteration " + i);
				if (initCOM) {
					log("Initializing COM thread");
					ComThread.InitMTA(false);
				}
				log("Creating JacobObject");
				new JacobObject();
				log("Releasing COM thread");
				ComThread.Release();
			}
			log("Exiting Java Thread");
		}

		private void log(String message) {
			if (writeOutput) {
				System.out.println(Thread.currentThread().getName()
						+ ": TestThread[" + id + "] " + " " + " - " + message);
			}
		}
	}

	/**
	 * This test shows that if ComThread.Init() is called explicitly, no problem
	 * occurs.
	 * 
	 * @throws InterruptedException
	 */
	public void testShowNoProblemIfCOMIsInitialized()
			throws InterruptedException {
		runTest(2, true, false);
		runTest(100, true, false);
	}

	/**
	 * This test shows that if only one thread is creating COM objects, no
	 * problem occurs.
	 * 
	 * @throws InterruptedException
	 */
	public void testShowNoProblemIfSingleThreaded() throws InterruptedException {
		runTest(1, false, false);
		runTest(1, true, false);
	}

	/**
	 * Runs the test with two threads, which don't initialize the COM thread.
	 * 
	 * This test will always fail.
	 * 
	 * @throws InterruptedException
	 */
	public void testShowDeadlockProblem() throws InterruptedException {
		runTest(2, false, true);
	}

	private void runTest(int numberOfThreads, boolean initCOM,
			boolean writeOutput) throws InterruptedException {
		Thread[] threads = new Thread[numberOfThreads];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new TestThread(i, initCOM, writeOutput);
			threads[i].start();
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join(TIMEOUT);
			if (threads[i].isAlive()) {
				fail("thread " + i + " failed to finish in " + TIMEOUT
						+ " milliseconds");
			}
		}
	}
}
