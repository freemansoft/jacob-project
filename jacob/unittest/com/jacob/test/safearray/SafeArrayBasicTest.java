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
	public void testBasicSafeArray() {
		// System.runFinalizersOnExit(true);
		SafeArray sa = new SafeArray(Variant.VariantVariant, 3);

		sa.fromShortArray(new short[] { 1, 2, 3 });
		System.out.println("sa short=" + sa);
		int[] ai = sa.toIntArray();
		for (int i = 0; i < ai.length; i++) {
			System.out.println("toInt=" + ai[i]);
		}
		double[] ad = sa.toDoubleArray();
		for (int i = 0; i < ad.length; i++) {
			System.out.println("toDouble=" + ad[i]);
		}
		sa.fromIntArray(new int[] { 100000, 200000, 300000 });
		System.out.println("sa int=" + sa);
		ai = sa.toIntArray();
		for (int i = 0; i < ai.length; i++) {
			System.out.println("toInt=" + ai[i]);
		}
		ad = sa.toDoubleArray();
		for (int i = 0; i < ad.length; i++) {
			System.out.println("toDouble=" + ad[i]);
		}
		Variant av[] = sa.toVariantArray();
		for (int i = 0; i < av.length; i++) {
			System.out.println("toVariant=" + av[i]);
		}
		sa.fromDoubleArray(new double[] { 1.5, 2.5, 3.5 });
		System.out.println("sa double=" + sa);
		sa.fromFloatArray(new float[] { 1.5F, 2.5F, 3.5F });
		System.out.println("sa float=" + sa);
		sa.fromBooleanArray(new boolean[] { true, false, true, false });
		System.out.println("sa bool=" + sa);
		av = sa.toVariantArray();
		for (int i = 0; i < av.length; i++) {
			System.out.println("toVariant=" + av[i]);
		}
		sa.fromCharArray(new char[] { 'a', 'b', 'c', 'd' });
		System.out.println("sa char=" + sa);
		sa.fromStringArray(new String[] { "hello", "from", "java", "com" });
		System.out.println("sa string=" + sa);
		av = sa.toVariantArray();
		for (int i = 0; i < av.length; i++) {
			System.out.println("toVariant=" + av[i]);
		}
		sa.fromVariantArray(new Variant[] { new Variant(1), new Variant(2.3),
				new Variant("hi") });
		System.out.println("sa variant=" + sa);

	}

	/**
	 * test method that verifies multi dimensional support
	 */
	public void testSafeArrayNumDimensions() {
		int[] lowerBounds = new int[] { 0, 0, 0 };
		int[] dimensionSizes = new int[] { 3, 3, 3 };

		SafeArray sa3x3 = new SafeArray(Variant.VariantVariant, lowerBounds,
				dimensionSizes);

		System.out.println("Num Dimensions = " + sa3x3.getNumDim());
		for (int i = 1; i <= sa3x3.getNumDim(); i++) {
			System.out.println("Dimension number = " + i);
			System.out.println("Lower bound = " + sa3x3.getLBound(i));
			System.out.println("Upper bound = " + sa3x3.getUBound(i));
		}

		int fill = 0;
		int[] indices = new int[] { 0, 0, 0 };

		for (int i = 0; i < 3; i++) {
			indices[0] = i;
			for (int j = 0; j < 3; j++) {
				indices[1] = j;
				for (int k = 0; k < 3; k++) {
					indices[2] = k;
					fill = i * 100 + j * 10 + k;
					sa3x3.setInt(indices, fill);
					System.out.println("sa[" + i + "][" + j + "][" + k + "] = "
							+ sa3x3.getInt(indices));
				}
			}
		}
	}
}
