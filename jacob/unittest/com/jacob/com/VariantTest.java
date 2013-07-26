package com.jacob.com;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.jacob.test.BaseTestCase;

/**
 * runs through some of the get and set methods on Variant
 * 
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class VariantTest extends BaseTestCase {

	/**
	 * This verifies that toJavaObject() works for all of the main data types
	 * when they exist as a byRef version.
	 * <p>
	 * It compares the toJavaObject() for a byref against the toJavaObject() for
	 * the regular.
	 * 
	 */
	public void testByRefToJavaObject() {
		Variant v = null;
		Variant vByRef = null;

		v = new Variant(new Float(53.3), false);
		vByRef = new Variant(new Float(53.3), true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}
		v = new Variant(new Double(53.3), false);
		vByRef = new Variant(new Double(53.3), true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}

		v = new Variant(new Boolean(true), false);
		vByRef = new Variant(new Boolean(true), true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}

		v = new Variant(new Integer(53), false);
		vByRef = new Variant(new Integer(53), true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}

		v = new Variant(new Short((short) 53), false);
		vByRef = new Variant(new Short((short) 53), true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}

		v = new Variant("53.33", false);
		vByRef = new Variant("53.33", true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}

		// Ugh, you have to pick a magic number whose scale is less than 28
		// 53.53 had a scale of 64 and 53.52 had a scale of 47
		BigDecimal testDecimal = new BigDecimal(53.50);
		v = new Variant(testDecimal, false);
		vByRef = new Variant(testDecimal, true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}

		Date now = new Date();
		v = new Variant(now, false);
		vByRef = new Variant(now, true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}
	}

	/**
	 * try and test VT_I8. This should only work on 64 bit machines
	 */
	public void testLong() {
		Variant v = null;
		Variant vByRef = null;

		long longNumber = 1L << 40;
		v = new Variant(new Long(longNumber), false);
		vByRef = new Variant(new Long(longNumber), true);
		assertEquals("Could recover long number " + longNumber, v.getLong(),
				longNumber);
		assertEquals("Could not make long number " + longNumber
				+ " come out the same for get and getByRef()",
				v.toJavaObject(), vByRef.toJavaObject());
		v = new Variant("" + longNumber);
		v.changeType(Variant.VariantLongInt);
		assertEquals("Conversion from string to long didn't work ",
				v.getLong(), longNumber);
	}

	/**
	 * do some testing around currencies
	 */
	public void testCurrencyHandling() {
		Variant v = null;
		Variant vByRef = null;

		// need to do currency also
		// currency is an integer scaled up by 10,000 to give 4 digits to the
		// right of the decimal
		int currencyScale = 10000;
		long twentyThousand = 20000 * currencyScale;
		Currency twentyThousandAsCurrency = new Currency(twentyThousand);
		v = new Variant(twentyThousandAsCurrency, false);
		vByRef = new Variant(twentyThousandAsCurrency, true);
		if (!(v.toJavaObject() instanceof Currency)) {
			fail("v.toJavaObject was not Long for currency but was: "
					+ v.toJavaObject());
		}
		if (!v.toJavaObject().equals(vByRef.toJavaObject())) {
			fail(v.toString() + " could not make type " + v.getvt() + " and "
					+ vByRef.getvt() + " java objects come out the same");
		}
		long twentyThousandDotSeven = twentyThousand + 700;
		Currency twentyThousandDotSevenAsCurrency = new Currency(
				twentyThousandDotSeven);
		// use the primitive constructor
		v = new Variant(twentyThousandDotSevenAsCurrency);
		assertEquals("failed test with " + twentyThousandDotSeven,
				twentyThousandDotSeven, v.getCurrency().longValue());

	}

	/**
	 * 4/2007 bug report toObject on dispatch tries to call getDispatchRef
	 * instead of getDispatch so toString() on dispatch blows up.
	 * 
	 */
	public void testDispatchToJavaObject() {
		Variant v2 = new Variant();
		v2.putNothing();
		// this test fails even though the exact same code below works fine
		// v2.toJavaObject();
	}

	/**
	 * see what happens when we conver to by ref
	 * 
	 */
	public void testSomeChangeVT() {
		Variant v;
		// the code shows e shouldn't need to use a returned Variant but the
		// test says we do
		Variant vConverted;
		v = new Variant(53.3);
		short originalVT = v.getvt();
		short modifier;

		modifier = Variant.VariantShort;
		vConverted = v.changeType(modifier);
		if (vConverted.getvt() != modifier) {
			fail("Failed to change Variant " + originalVT + " using mask "
					+ modifier + " resulted in " + vConverted.getvt());
		}

		modifier = Variant.VariantString;
		vConverted = v.changeType(modifier);
		if (vConverted.getvt() != modifier) {
			fail("Failed to change Variant " + originalVT + " using mask "
					+ modifier + " resulted in " + vConverted.getvt());
		}

		// can't convert to byref!
		modifier = Variant.VariantByref | Variant.VariantShort;
		vConverted = v.changeType(modifier);
		if (vConverted.getvt() == modifier) {
			fail("Should not have been able to change Variant " + originalVT
					+ " using mask " + modifier + " resulted in "
					+ vConverted.getvt());
		}
	}

	/**
	 * make sure variant with no backing store works.
	 * 
	 */
	public void testUninitializedVariant() {
		Variant v;
		// Variants created without parameters are auto set to VariantEmpty
		v = new Variant();
		try {
			if (v.getvt() == Variant.VariantEmpty) {
				// successful
				// System.out.println("Variant initialized without parameters
				// correctly set to empty");
			} else {
				throw new RuntimeException(
						"getvt() on uninitialized variant shoud have returned VariantEmpty, instead returned "
								+ v.getvt());
			}
		} catch (IllegalStateException ise) {
			throw new RuntimeException(
					"getvt() on uninitialized variant shoud have succeeded, but instead threw exception");
		}
		try {
			v.toString();
		} catch (IllegalStateException ise) {
			fail("toString() should never throw a runtime exception");
			throw new RuntimeException(
					"toString() should not blow up even with uninitialized Variant");
		}

	}

	/**
	 * 
	 * verify the toString() method does not do type conversion
	 */
	public void testToStringDoesNotConvert() {
		Variant v;
		v = new Variant(true);
		v.toString();
		if (v.getvt() != Variant.VariantBoolean) {
			throw new RuntimeException(
					"toString() converted boolean to something else");
		} else {
			// fail("toString() correctly does not convert type");
		}
		if (v.getBoolean() != true) {
			fail("toString() converted boolean true to " + v.getBoolean());
		}
		v = new Variant(false);
		v.toString();
		if (v.getvt() != Variant.VariantBoolean) {
			throw new RuntimeException(
					"toString() converted boolean to something else");
		} else {
			// fail("toString() correctly does not convert type");
		}
		if (v.getBoolean() != false) {
			fail("toString() converted boolean false to " + v.getBoolean());
		}
	}

	/**
	 * Exercise ToString special cases
	 */
	public void testToStringEmptyValues() {
		Variant v;
		// create an empty variant
		v = new Variant();
		// check date per
		v.changeType(Variant.VariantDate);
		assertEquals("null", v.toString());
		v.putDate(new Date());
		assertNotNull(v.toString());
		assertFalse("null".equals(v.toString()));

		v.changeType(Variant.VariantInt);
		v.putInt(1);

		assertEquals("1", v.toString());
		v.changeType(Variant.VariantEmpty);
		assertEquals("null", v.toString());
		v.changeType(Variant.VariantNull);
		assertEquals("null", v.toString());
		v.changeType(Variant.VariantError);
		assertEquals("null", v.toString());
	}

	/**
	 * Verify that booleans can be released. Part of the suite that checks all
	 * types.
	 */
	public void testSafeReleaseBoolean() {
		Variant v;
		v = new Variant(true);
		// System.out.println("Newly created Variant ("+ v.getBoolean()+") "+
		// "trying to create access violation but it doesn't seem to be easy");
		v.safeRelease();
		try {
			v.getBoolean();
			fail("IllegalStateException should have been thrown when querying safeReleased object");
			throw new RuntimeException("test failed");
		} catch (IllegalStateException ise) {
			// System.out.println("IllegalStateException correctly thrown after
			// safeRelease");
		}
		v = new Variant(true);
		for (int i = 0; i < 10; i++) {
			new Variant("xxx" + i);
			new Variant(i);
			new Variant("yyy" + i);
		}
		ComThread.Release();
		try {
			v.getBoolean();
			fail("IllegalStateException should have been thrown when querying ComThread.Release");
			throw new RuntimeException("test failed");
		} catch (IllegalStateException ise) {
			// System.out.println("IllegalStateException correctly thrown after
			// ComThread.Release");
		}
	}

	/**
	 * verify the constant values aren't released with safeRelease
	 * 
	 */
	public void testSafeReleaseConstant() {
		// System.out.println("Using Static constant Variant - should never
		// throw access violation");
		Variant.VT_TRUE.safeRelease();
		if (Variant.VT_TRUE.getBoolean() != true) {
			fail("VT_TRUE has been broken by SafeRelease()");
			throw new RuntimeException("test failed");
		} else {
			// System.out.println("VT_TRUE survived SafeRelease()");
		}

		for (int i = 0; i < 10; i++) {
			new Variant("xxx" + i);
			new Variant(i);
			new Variant("yyy" + i);
		}
		ComThread.Release();

		if (Variant.VT_TRUE.getBoolean() != true) {
			fail("VT_TRUE has been broken by ComThread.Release()");
			throw new RuntimeException("test failed");
		} else {
			// System.out.println("VT_TRUE survived ComThread.Release()");
		}

	}

	/**
	 * this used to try and and create an access violation but that didn't work
	 * and now the methods on the Variant are smarter about working after a
	 * release
	 * 
	 */
	public void testSafeReleaseString() {
		String mTestString = "Guitar Hero";
		Variant v = new Variant(mTestString);
		// System.out.println("Newly created Variant ("+ v.getString()+") "+
		// "about to safe release and then access");
		v.safeRelease();
		try {
			v.getString();
			fail("IllegalStateException should have been thrown when querying safeReleased object");
			throw new RuntimeException("test failed");
		} catch (IllegalStateException ise) {
			// System.out.println("IllegalStateException correctly thrown after
			// safeRelease");
		}
	}

	/**
	 * verifies objectIsAConstant works as expected
	 * 
	 */
	public void testObjectIsAConstant() {
		Variant v = new Variant("d");
		if (!v.objectIsAConstant(Variant.VT_FALSE)) {
			fail("did not recognize VT_FALSE");
		}
		if (!v.objectIsAConstant(Variant.VT_TRUE)) {
			fail("did not recognize VT_TRUE");
		}
		if (!v.objectIsAConstant(Variant.VT_MISSING)) {
			fail("did not recognize VT_MISSING");
		}
		if (!v.objectIsAConstant(Variant.DEFAULT)) {
			fail("did not recognize DEFAULT");
		}
		if (v.objectIsAConstant(new Variant(true))) {
			fail("confused a boolean with VT_TRUE");
		}
		if (v.objectIsAConstant(new Variant(false))) {
			fail("confused a boolean with VT_FALSE");
		}

	}

	/**
	 * tests put and get methods looking for obvious defects
	 * 
	 */
	public void testPutsAndGets() {
		Variant v = new Variant();

		v.putInt(10);
		assertEquals("int test failed", 10, v.getInt());

		v.putShort((short) 20);
		assertEquals("short test failed", (short) 20, v.getShort());

		v.putByte((byte) 30);
		assertEquals("byte test failed", (byte) 30, v.getByte());

		v.putFloat(40);
		if (v.getFloat() != 40.0) {
			fail("float test failed");
		}

		v.putDouble(50);
		if (v.getDouble() != 50.0) {
			fail("double test failed");
		}

		v.putString("1234.567");
		assertEquals("string test failed", "1234.567", v.getString());

		v.putBoolean(true);
		assertEquals("failed boolean test(true)", true, v.getBoolean());

		v.putBoolean(false);
		assertEquals("failed boolean test(false)", false, v.getBoolean());

		long originalValue = 123456789123456789L;
		v.putCurrency(new Currency(originalValue));
		assertEquals("failed currency test", 123456789123456789L, v
				.getCurrency().longValue());

		BigDecimal testDecimal = new BigDecimal("22.222");
		v.putDecimal(testDecimal);
		assertEquals("failed BigDecimal test", testDecimal, v.getDecimal());

		Date ourDate = new Date();
		v.putDate(ourDate);
		Date retrievedDate = v.getJavaDate();
		if (!retrievedDate.equals(ourDate)) {
			fail("failed java date load and unload");
		}

		v.putNull();
		if (!v.isNull()) {
			fail("failed detecting set null");
		}
		v.putString("something other than null");
		if (v.isNull()) {
			fail("failed null replacement with string");
		}

		v.putEmpty();
		if (!v.isNull()) {
			fail("failed detecting set empty as null");
		}
		v.putString("something other than null");
		if (v.isNull()) {
			fail("failed empty replacement with string as isNull");
		}

		Variant v2 = new Variant();
		v2.putNothing();
		if (v2.getvt() != Variant.VariantDispatch) {
			fail("putNothing was supposed to set the type to VariantDispatch");
		}
		if (!v2.isNull()) {
			fail("putNothing is supposed to cause isNull() to return true");
		}
		// this line blows up in the test above
		if (v2.toJavaObject() == null) {
			fail("putNothing() followed by toJavaObject() should return a Dispatch");
		}

	}

	/**
	 * verify decimal works right
	 */
	public void testDecimalConversion() {
		Variant v = new Variant();
		v.changeType(Variant.VariantDecimal);
		for (int i = 10; i >= -10; i--) {
			v.putDecimal(new BigDecimal(i));
			// first see if we can get it back as decimal
			assertEquals("conversion back to decimal failed " + i,
					new BigDecimal(i), v.getDecimal());
			v.changeType(Variant.VariantFloat);
			// now see if a float conversion would work
			assertEquals("conversion to float failed " + i, new Float(i),
					v.getFloat());
			// now convert it back to decimal for reassignment
			v.changeType(Variant.VariantDecimal);
			assertTrue("Failed conversion of type back to Decimal " + i,
					v.getvt() == Variant.VariantDecimal);
		}

	}

	/**
	 * for(BigDecimal i in 79228162514264337593543950330.0 ..
	 * 79228162514264337593543950341.0) { com.jacob.com.Variant dv = new
	 * com.jacob.com.Variant(i, false) println i + " : " + dv.getDecimal() }
	 * 
	 */
	public void testLargeDecimals() {
		// the largest decimal number, not in hex is
		// 7922816251426433759354395033.0
		BigInteger theStartDigits = new BigInteger("ffffffffffffffffffffff00",
				16);
		BigInteger theMaxDigits = new BigInteger("ffffffffffffffffffffffff", 16);
		BigDecimal startDecimal = new BigDecimal(theStartDigits);
		BigDecimal endDecimal = new BigDecimal(theMaxDigits);
		BigDecimal incrementDecimal = new BigDecimal(1);
		BigDecimal testDecimal = startDecimal;
		Variant testVariant;
		while (endDecimal.compareTo(testDecimal) >= 0) {
			testVariant = new Variant(testDecimal, false);
			BigDecimal result = testVariant.getDecimal();
			assertEquals(testDecimal, result);
			testDecimal = testDecimal.add(incrementDecimal);
		}
		// test Decimal is now too large
		try {
			new Variant(testDecimal, false);
		} catch (IllegalArgumentException iae) {
			// System.out.println("Caught expected exception");
		}
		// lets try something different. we can call putVariant with rounding
		// enabled
		testVariant = new Variant();
		testVariant.changeType(Variant.VariantDecimal);
		try {
			testVariant.putDecimal(endDecimal.setScale(30));
			fail("Should have thrown exception with scale of 30 and no rounding");
		} catch (IllegalArgumentException iae) {
			// should have caught this exception
		}
		// now we test with a negative scale. Note that you can't do with
		// without some magic, in this case scientific notation
		try {
			testVariant.putDecimal(new BigDecimal("700E24"));
			assertTrue(new BigDecimal("700E24").compareTo(testVariant
					.getDecimal()) == 0);
		} catch (IllegalArgumentException iae) {
			// should have caught this exception
		}

		testVariant.putDecimal(VariantUtilities
				.roundToMSDecimal(new BigDecimal("700E24")));
		// use compareTo because it takes into account varying scales
		assertTrue(new BigDecimal("700E24").compareTo(testVariant.getDecimal()) == 0);

		// This passes because the number is within range.
		testVariant.putDecimal(endDecimal);

		// this should pass because we have rounding turned on
		// it turns out the max number gets more digits when
		// it's scale is set to 30. so we can't use the max number when there is
		// a scale
		BigDecimal modifiedDecimal = endDecimal;
		System.out.println("integer piece starts                       as "
				+ modifiedDecimal.unscaledValue().toString(16) + " scale=: "
				+ modifiedDecimal.scale());
		System.out.println("integer piece after rounding without scale is "
				+ VariantUtilities.roundToMSDecimal(modifiedDecimal)
						.unscaledValue().toString(16) + " scale=: "
				+ modifiedDecimal.scale());
		System.out.println("integer piece after rounding with scale 30 is "
				+ VariantUtilities
						.roundToMSDecimal(modifiedDecimal.setScale(30))
						.unscaledValue().toString(16) + " scale=: "
				+ modifiedDecimal.scale());
		try {
			testVariant.putDecimal(VariantUtilities
					.roundToMSDecimal(modifiedDecimal.setScale(30)));
			fail("should have thrown an exception for a number whose scale "
					+ "change created too many digits to be represented.");
		} catch (IllegalArgumentException iae) {
			// should catch an exception here because the rounding after scale
			// change would have made the number too large
		}

		System.out.println("");
		modifiedDecimal = endDecimal.subtract(incrementDecimal);
		System.out.println("integer piece starts                       as "
				+ modifiedDecimal.unscaledValue().toString(16) + " scale=: "
				+ modifiedDecimal.scale());
		System.out.println("integer piece after rounding without scale is "
				+ VariantUtilities.roundToMSDecimal(modifiedDecimal)
						.unscaledValue().toString(16) + " scale=: "
				+ modifiedDecimal.scale());
		System.out.println("integer piece after rounding with scale 30 is "
				+ VariantUtilities
						.roundToMSDecimal(modifiedDecimal.setScale(30))
						.unscaledValue().toString(16) + " scale=: "
				+ modifiedDecimal.scale());
		testVariant.putDecimal(VariantUtilities
				.roundToMSDecimal(modifiedDecimal.setScale(30)));
		System.out.println("");
	}

	/**
	 * Spin up a lot of threads and have them all create variants 3/2007 there
	 * have been several reports in multi-threaded servers that show init()
	 * failing
	 * 
	 */
	public void testManyThreadedInit() {
		VariantInitTestThread threads[] = new VariantInitTestThread[75];

		System.out.println("Starting thread test (" + threads.length
				+ " threads each creating 10000 objects)."
				+ " This may take 30 seconds or more.");
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new VariantInitTestThread("thread-" + i, 10000);
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
		int numComplete = 0;
		while (numComplete < threads.length) {
			// give the works time to work
			try {
				Thread.sleep(333);
			} catch (InterruptedException ie) {
				// do nothing
			}
			numComplete = 0;
			for (int i = 0; i < threads.length; i++) {
				if (threads[i].isComplete) {
					numComplete++;
				}
			}
			// System.out.print("["+numComplete+"/"+threads.length+"]");
		}
		System.out.println("Finished thread test");
	}

	/**
	 * a class to create variants in separate threads
	 * 
	 */
	class VariantInitTestThread extends Thread {
		private boolean isComplete = false;

		private int initialRunSize = 0;

		/**
		 * @param newThreadName
		 *            the name for the thread
		 * @param iStartCount
		 *            number of threads to start with
		 */
		public VariantInitTestThread(String newThreadName, int iStartCount) {
			super(newThreadName);
			initialRunSize = iStartCount;

		}

		/**
		 * getter so master can see if thread is done
		 * 
		 * @return state of complete flag
		 */
		public boolean isComplete() {
			return isComplete;
		}

		/**
		 * Blow out a bunch of Variants
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			for (int variantIndex = 0; variantIndex < initialRunSize; variantIndex++) {
				try {
					Thread.yield();
					Thread.sleep(0);
				} catch (InterruptedException ie) {
					// do nothing
				}
				// System.out.println(Thread.currentThread().getName());
				Variant testSubject = new Variant(variantIndex);
				testSubject.getvt();
				testSubject.getInt();
			}
			isComplete = true;
		}
	}

	/**
	 * there was a bitwise masking error that let booleans be seen as dispatch
	 * objects Bug Report SF3065265
	 */
	public void testGetDispatch() {
		Variant testVariant = new Variant();
		testVariant.putBooleanRef(true);
		try {
			// throws IllegalStateException if Jacob detects the type
			// throws some other bad exception if COM blows up failing the
			// conversion
			testVariant.getDispatchRef();
			fail("Should not have converted boolean to dispatch");
		} catch (IllegalStateException e) {
			// yeah! can't get dispatch from boolean
		}
	}

	/**
	 * there was a bitwise masking error that let booleans be seen as dispatch
	 * objects Bug Report SF3065265
	 */
	public void testGetError() {
		Variant testVariant = new Variant();
		testVariant.putErrorRef(3);
		try {
			// throws IllegalStateException if Jacob detects the type
			// throws some other bad exception if COM blows up failing the
			// conversion
			testVariant.getStringRef();
			fail("Should not have converted error to string");
		} catch (IllegalStateException e) {
			// yeah! can't get dispatch from boolean
		}
	}

	/**
	 * Verify SF 3435567 null and empty behavior change
	 */
	public void testGetNullString() {
		Variant testVariant = new Variant();
		testVariant.putNull();
		assertNull(testVariant.getString());
		testVariant.putEmpty();
		assertNull(testVariant.getString());
		testVariant.putString("dog");
		assertEquals("dog", testVariant.getString());
	}
}
