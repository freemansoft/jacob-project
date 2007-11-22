package com.jacob.test.safearray;

import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * SafeArrayTest Program
 * 
 * This is more of an exerciser. It doesn't verify that it gets back what it
 * expects like a junit test would
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 * 
 */
public class SafeArrayBasicTest extends BaseTestCase {

	/**
	 * 
	 */
	public void testShortSafeArray() {
		short sourceData[] = new short[] { 1, 2, 3 };
		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);

		saUnderTest.fromShortArray(sourceData);
		short[] extractedFromSafeArray = saUnderTest.toShortArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArray[i]);
		}

		assertEquals("single get failed: ", sourceData[2], saUnderTest
				.getShort(2));

		// test conversion
		int[] extractedFromSafeArrayInt = saUnderTest.toIntArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArrayInt[i]);
		}
		// test conversion
		double[] extractedFromSafeArrayDouble = saUnderTest.toDoubleArray();
		for (int i = 0; i < extractedFromSafeArrayDouble.length; i++) {
			assertEquals("" + i, new Double(sourceData[i]).doubleValue(),
					extractedFromSafeArrayDouble[i]);
		}
		// test conversion
		Variant extractedFromSafeArrayVariant[] = saUnderTest.toVariantArray();
		for (int i = 0; i < extractedFromSafeArrayVariant.length; i++) {
			assertEquals("" + i, sourceData[i],
					extractedFromSafeArrayVariant[i].getShort());
		}
	}

	/**
	 * 
	 */
	public void testIntSafeArray() {
		int sourceData[] = new int[] { 100000, 200000, 300000 };
		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);
		saUnderTest.fromIntArray(sourceData);
		int[] extractedFromSafeArray = saUnderTest.toIntArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArray[i]);
		}
		assertEquals("single get failed: ", sourceData[2], saUnderTest
				.getInt(2));

	}

	/**
	 * 
	 */
	public void testLongSafeArray() {
		long sourceData[] = new long[] { 2L << 40, 3L << 41, 4L << 42 };
		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);
		saUnderTest.fromLongArray(sourceData);
		long[] extractedFromSafeArray = saUnderTest.toLongArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArray[i]);
		}
		assertEquals("single get failed: ", sourceData[2], saUnderTest
				.getLong(2));

	}

	/**
	 * 
	 */
	public void testDoubleSafeArray() {
		double sourceData[] = new double[] { 1.5, 2.5, 3.5 };
		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);
		saUnderTest.fromDoubleArray(sourceData);
		double[] extractedFromSafeArray = saUnderTest.toDoubleArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArray[i]);
		}
		assertEquals("single get failed: ", sourceData[2], saUnderTest
				.getDouble(2));

	}

	/**
	 * 
	 */
	public void testFloatSafeArray() {
		float sourceData[] = new float[] { 1.5F, 2.5F, 3.5F };
		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);
		saUnderTest.fromFloatArray(sourceData);
		float[] extractedFromSafeArray = saUnderTest.toFloatArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArray[i]);
		}
	}

	/**
	 * 
	 */
	public void testBooleanSafeArray() {
		boolean sourceData[] = new boolean[] { true, false, true, false };
		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);
		saUnderTest.fromBooleanArray(sourceData);
		boolean[] extractedFromSafeArray = saUnderTest.toBooleanArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArray[i]);
		}
		assertEquals("single get failed: ", sourceData[2], saUnderTest
				.getBoolean(2));

		// test conversion
		Variant extractedFromSafeArrayVariant[] = saUnderTest.toVariantArray();
		for (int i = 0; i < extractedFromSafeArrayVariant.length; i++) {
			assertEquals("" + i, sourceData[i],
					extractedFromSafeArrayVariant[i].getBoolean());
		}
	}

	/**
	 * 
	 */
	public void testCharSafeArray() {
		char sourceData[] = new char[] { 'a', 'b', 'c', 'd' };
		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);
		saUnderTest.fromCharArray(sourceData);
		char[] extractedFromSafeArray = saUnderTest.toCharArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArray[i]);
		}
		assertEquals("single get failed: ", sourceData[2], saUnderTest
				.getChar(2));

	}

	/**
	 * 
	 */
	public void testStringSaveArray() {
		String sourceData[] = new String[] { "hello", "from", "java", "com" };
		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);
		saUnderTest.fromStringArray(sourceData);
		String[] extractedFromSafeArray = saUnderTest.toStringArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i], extractedFromSafeArray[i]);
		}
		assertEquals("single get failed: ", sourceData[2], saUnderTest
				.getString(2));

		// test conversion
		Variant extractedFromSafeArrayVariant[] = saUnderTest.toVariantArray();
		for (int i = 0; i < extractedFromSafeArrayVariant.length; i++) {
			assertEquals("" + i, sourceData[i],
					extractedFromSafeArrayVariant[i].getString());
		}
	}

	/**
	 * 
	 */
	public void testVariantSafeArray() {
		Variant sourceData[] = new Variant[] { new Variant(1),
				new Variant(2.3), new Variant("hi") };

		SafeArray saUnderTest = new SafeArray(Variant.VariantVariant, 3);
		saUnderTest.fromVariantArray(sourceData);
		Variant[] extractedFromSafeArray = saUnderTest.toVariantArray();
		for (int i = 0; i < extractedFromSafeArray.length; i++) {
			assertEquals("" + i, sourceData[i].toString(),
					extractedFromSafeArray[i].toString());
		}
		assertEquals("single get failed: ", sourceData[2].toString(),
				saUnderTest.getVariant(2).toString());

	}

	/**
	 * test method that verifies setting of bounds in multi-dimensional arrays
	 */
	public void testSafeArrayNumDimensions() {
		int[] lowerBounds = new int[] { 0, 0, 0 };
		int[] dimensionSizes = new int[] { 3, 3, 3 };

		SafeArray sa3x3 = new SafeArray(Variant.VariantVariant, lowerBounds,
				dimensionSizes);

		System.out.println("Num Dimensions = " + sa3x3.getNumDim());
		for (int safeArrayDimension = 1; safeArrayDimension <= sa3x3
				.getNumDim(); safeArrayDimension++) {
			int configArrayIndex = safeArrayDimension - 1;
			assertEquals("unexpected lower bound value ",
					lowerBounds[configArrayIndex], sa3x3
							.getLBound(safeArrayDimension));
			assertEquals("unexpeced upper bound value ",
					(dimensionSizes[configArrayIndex] - 1)
							+ lowerBounds[configArrayIndex], sa3x3
							.getUBound(safeArrayDimension));
		}
	}

	/**
	 * test the set and get method on multi-dimensional arrays
	 */
	public void testSafeArrayMultiDimension() {

		int[] lowerBounds = new int[] { 0, 0, 0 };
		int[] dimensionSizes = new int[] { 3, 3, 3 };

		SafeArray sa3x3 = new SafeArray(Variant.VariantVariant, lowerBounds,
				dimensionSizes);
		int[] indices = new int[] { 0, 0, 0 };

		for (int i = 0; i < 3; i++) {
			indices[0] = i;
			for (int j = 0; j < 3; j++) {
				indices[1] = j;
				for (int k = 0; k < 3; k++) {
					indices[2] = k;

					int fill = 0;
					fill = i * 100 + j * 10 + k;
					sa3x3.setInt(indices, fill);
					assertEquals(fill, sa3x3.getInt(indices));

					long fillLong = 0L;
					// Pick a number bigger than 2^31
					fillLong = 100000000000000L * fill;
					sa3x3.setLong(indices, fillLong);
					assertEquals(fillLong, sa3x3.getLong(indices));
				}
			}
		}
	}
}
