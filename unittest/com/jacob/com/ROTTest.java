package com.jacob.com;

import com.jacob.test.BaseTestCase;

/**
 * This tries to exercise ROT's garbage collection
 * 
 * This will eventually be changed to a unit test.
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class ROTTest extends BaseTestCase {

	/**
	 * verify the SystemProperty (classname).PutInROT functions as expected. A
	 * value of false means instances of the class are not put in the ROT Any o
	 * ther value means they are
	 */
	public void testDontFillROTSystemProperty() {
		debug("testDontFillROTSystemProperty: started");
		// Make sure the class is loaded before running any of the tests
		// class to load and any pre-defined Variants (FALSE and TRUE) to be
		// created immediately
		VariantViaEvent.class.getName();
		if (ROT.getThreadObjects(true).entrySet().size() < 1) {
			debug("Failure: ROT should have objects in it as soon as Variant class loaded.");
		}

		System.setProperty(VariantViaEvent.class.getName()
				+ ROT.PUT_IN_ROT_SUFFIX, "false");
		int countPriorToTest = ROT.getThreadObjects(true).entrySet().size();
		new VariantViaEvent();
		int countAfterAddWithoutROT = ROT.getThreadObjects(true).entrySet()
				.size();
		if (countAfterAddWithoutROT != countPriorToTest) {
			debug("Failure: count prior: " + countPriorToTest
					+ " and count after without ROT was: "
					+ countAfterAddWithoutROT);
		}

		System.setProperty(VariantViaEvent.class.getName()
				+ ROT.PUT_IN_ROT_SUFFIX, "true");
		new VariantViaEvent();
		int countAfterAddWithROT = ROT.getThreadObjects(true).entrySet().size();
		if (countAfterAddWithROT != (countPriorToTest + 1)) {
			debug("Failure: count prior: " + countPriorToTest
					+ " and count after with ROT was: " + countAfterAddWithROT);
		}
		debug("testDontFillROTSystemProperty: completed");
	}

	/**
	 * Needs documentation. This test looks broken
	 * 
	 */
	public void testGCBehavior() {
		int sizeBeforeBuild = 0;
		int sizeAfterBuild = 0;
		int sizeBeforeGC = 0;
		int sizeAfterGC = 0;
		int loopSize = 10000;
		int sizeExpectedAfterBuild = 0;

		debug("testGCBehavior: started");
		debug("creating 10,000 object sets");
		// cause classes to get loaded and any static instances to be created
		SafeArray.class.getName();
		Variant.class.getName();
		sizeBeforeBuild = ROT.getThreadObjects(false).size();
		sizeExpectedAfterBuild = ((loopSize * 3) + sizeBeforeBuild);
		for (int i = 0; i < loopSize; i++) {
			SafeArray a1 = new SafeArray(Variant.VariantVariant, 2);
			a1.setVariant(0, new Variant("foo"));
			a1.setVariant(1, new Variant("bar"));
		}
		sizeAfterBuild = ROT.getThreadObjects(false).size();
		if (sizeAfterBuild < sizeExpectedAfterBuild) {
			debug("Something got GC'd: " + sizeAfterBuild);
		} else if (sizeAfterBuild > sizeExpectedAfterBuild) {
			debug("More: " + sizeAfterBuild + " than expected: "
					+ sizeExpectedAfterBuild);
		} else {
			debug("They're all there");
		}
		// add more to the VM
		debug("Flooding Memory to force GC");
		for (int i = 0; i <= loopSize * 2; i++) {
			new String("this is just some text to see if we can force gc " + i);
		}
		// storage will hold weak references until the next JacobObject is
		// created
		System.gc();
		sizeBeforeGC = ROT.getThreadObjects(false).size();
		debug("Objects left after flood and gc but before adding a new object that clean's up weak references: "
				+ sizeBeforeGC);
		debug("Creating single object.  This adds one and causes ROT to clean up GC'd");
		new JacobObject();
		sizeAfterGC = ROT.getThreadObjects(false).size();
		debug("Objects left after adding one (caused weak ref objects to be removed): "
				+ sizeAfterGC);
		new JacobObject();
		if (ROT.getThreadObjects(false).size() != sizeAfterGC + 1) {
			debug("Unexpected number of objects after adding only one more "
					+ ROT.getThreadObjects(false).size());
		} else {
			debug("Found number expected after adding one more "
					+ (sizeAfterGC + 1));
		}
		ROT.clearObjects();
		if (ROT.getThreadObjects(false) == null) {
			debug("ROT pool was destroyed as expected after clearObjects called.");
		} else {
			debug("ROT pool for thread still exists when it shouldn't");
		}

		// ========= part two ================================
		debug("Verifying doesn't blow up with double release");
		for (int i = 0; i <= 10000; i++) {
			new JacobObject();
		}
		// force safeRelease call on all objects
		ROT.clearObjects();
		// now force the gc to go collect them, running safeRelease again
		System.gc();
		debug("testGCBehavior: finished");
	}

	private static void debug(String message) {
		System.out.println(Thread.currentThread().getName() + " " + message);
	}
}
