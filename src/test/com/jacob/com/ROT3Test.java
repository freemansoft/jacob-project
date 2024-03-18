package com.jacob.com;

import com.jacob.test.BaseTestCase;

/**
 * This tries to exercise ROT's garbage collection This is named this way
 * because the build.xml ignores files ending in Test when building the binary
 * zip file
 * 
 * This will eventually be changed to a unit test.
 * 
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class ROT3Test extends BaseTestCase {

	/**
	 * runs a multi-threaded test
	 */
	public void testROTVersion3() {
		ROT3TestThread threads[] = new ROT3TestThread[4];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new ROT3TestThread("thread-" + i, 3000 + i * 10);
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
	}

	/**
	 * This will try and exercise the thread support in the ROT
	 */

	public class ROT3TestThread extends Thread {
		private java.util.List<Variant> variansCreatedInThisThread;

		private int initialRunSize = 0;

		/**
		 * @param arg0
		 * @param iStartCount
		 *            the number of initial threads
		 */
		public ROT3TestThread(String arg0, int iStartCount) {
			super(arg0);
			initialRunSize = iStartCount;

		}

		/**
		 * A semi-complex series of steps to put the ROT under stress. 1)
		 * discard half the objects we've created 2) if size is greater than 1
		 * but not a even number, add 1 new object 3) stop when size is 1.
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@SuppressWarnings("deprecation")
		public void run() {
			// something that keeps object references around
			// so the gc can't collect them
			// we need to create these in the thread so they end up in the right
			// ROT table
			variansCreatedInThisThread = new java.util.ArrayList<Variant>(
					initialRunSize);
			for (int i = 0; i < initialRunSize; i++) {
				// create the object
				Variant aNewVariant = new Variant(getName() + "_" + i);
				// create a hard reference to it
				variansCreatedInThisThread.add(aNewVariant);
			}

			while (variansCreatedInThisThread.size() > 1) {
				String message = "";
				message = getName() + " Workingset="
						+ variansCreatedInThisThread.size()
						+ " ROT threadObject hashCode: "
						+ ROT.getThreadObjects(true).hashCode();
				message += " size before mods and gc "
						+ ROT.getThreadObjects(true).size() + ")";
				// If there are more than 10 objects in our cache then add 1/4
				// of that again
				if (variansCreatedInThisThread.size() > 10) {
					message += " (adding) ";
					// add an additional 1/4 of our current number
					for (int i = 0; i < variansCreatedInThisThread.size() / 4; i++) {
						// add a new object
						Variant aNewVariant = new Variant(getName() + "_*"
								+ variansCreatedInThisThread.size());
						variansCreatedInThisThread.add(aNewVariant);
					}
				}
				// now iterate across 1/2 the objects in our list
				message += " (removing)  ";
				for (int i = variansCreatedInThisThread.size(); i > 0; i--) {
					// removing every other one?
					if (i % 2 == 0) {
						// remove the reference so gc can get it
						if (!ROT.USE_AUTOMATIC_GARBAGE_COLLECTION) {
							// uses deprecated API to set up a special situation
							// because this is an ROT test
							ROT.removeObject(variansCreatedInThisThread
									.get(i - 1));
						}
						variansCreatedInThisThread.remove(i - 1);
					}

				}

				message += " (after mods " + ROT.getThreadObjects(true).size()
						+ ")";
				// comm
				if (!ROT.USE_AUTOMATIC_GARBAGE_COLLECTION) {
					ROT.clearObjects();
				}
				System.gc();
				try {
					// vain attempt at letting the gc run
					Thread.sleep(200);
				} catch (InterruptedException ie) {

				}
				message += " (after gc " + ROT.getThreadObjects(true).size()
						+ ")";
				message += " Should see GC if debug turned on...";
				System.out.println(message);
			}
		}

		/**
		 * Another test would be to overide this to always return the same name.
		 * That would really screw the ROT!
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return super.toString();
		}
	}
}
