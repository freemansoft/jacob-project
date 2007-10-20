package com.jacob.test.safearray;

import com.jacob.com.ComFailException;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import com.jacob.test.BaseTestCase;

/**
 * A safe array contents test (old test)
 * 
 * <p>
 * May need to run with some command line options (including from inside
 * Eclipse). Look in the docs area at the Jacob usage document for command line
 * options.
 */
public class SafeArrayContents extends BaseTestCase {

	public static void printArray(boolean a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public static void printArray(int a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public static void printArray(short a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public static void printArray(byte a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public static void printArray(double a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public static void printArray(float a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public static void printArray(String a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public static void printArray(Variant a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public static void printArray(char a[]) {
		System.out.print("[");
		for (int i = 0; i < a.length; i++) {
			System.out.print(" " + a[i] + " ");
		}
		System.out.println("]");
	}

	public void testSafeArrayContents() {
		// int
		System.out.println("Int");
		SafeArray ia = new SafeArray(Variant.VariantInt, 4);
		System.out.println("elem size:" + ia.getElemSize());
		int iack[] = new int[] { 100000, 200000, 300000, 400000 };
		printArray(iack);
		ia.fromIntArray(iack);
		iack = ia.toIntArray();
		printArray(iack);

		int i4[] = new int[4];
		ia.getInts(0, 4, i4, 0);
		printArray(i4);

		SafeArray ia2 = new SafeArray(Variant.VariantInt, 4);
		ia2.setInts(0, 4, i4, 0);
		iack = ia2.toIntArray();
		printArray(iack);

		// double
		System.out.println("Double");
		SafeArray da = new SafeArray(Variant.VariantDouble, 4);
		System.out.println("elem size:" + da.getElemSize());
		double dack[] = new double[] { 123.456, 456.123, 1234567.89, 12.3456789 };
		printArray(dack);
		da.fromDoubleArray(dack);
		dack = da.toDoubleArray();
		printArray(dack);

		double d4[] = new double[4];
		da.getDoubles(0, 4, d4, 0);
		printArray(d4);

		SafeArray da2 = new SafeArray(Variant.VariantDouble, 4);
		da2.setDoubles(0, 4, d4, 0);
		dack = da2.toDoubleArray();
		printArray(dack);

		// float
		System.out.println("Float");
		SafeArray fa = new SafeArray(Variant.VariantFloat, 4);
		System.out.println("elem size:" + fa.getElemSize());
		float fack[] = new float[] { 123.456F, 456.123F, 1234567.89F,
				12.3456789F };
		printArray(fack);
		fa.fromFloatArray(fack);
		fack = fa.toFloatArray();
		printArray(fack);

		float f4[] = new float[4];
		fa.getFloats(0, 4, f4, 0);
		printArray(f4);

		SafeArray fa2 = new SafeArray(Variant.VariantFloat, 4);
		fa2.setFloats(0, 4, f4, 0);
		fack = fa2.toFloatArray();
		printArray(fack);

		// boolean
		System.out.println("Boolean");
		SafeArray ba = new SafeArray(Variant.VariantBoolean, 4);
		System.out.println("elem size:" + ba.getElemSize());
		boolean back[] = new boolean[] { true, false, true, false };
		printArray(back);
		ba.fromBooleanArray(back);
		back = ba.toBooleanArray();
		printArray(back);

		boolean b4[] = new boolean[4];
		ba.getBooleans(0, 4, b4, 0);
		printArray(b4);

		SafeArray ba2 = new SafeArray(Variant.VariantBoolean, 4);
		ba2.setBooleans(0, 4, b4, 0);
		back = ba2.toBooleanArray();
		printArray(back);

		// char
		System.out.println("Char");
		SafeArray ca = new SafeArray(Variant.VariantShort, 4);
		System.out.println("elem size:" + ca.getElemSize());
		char cack[] = new char[] { 'a', 'b', 'c', 'd' };
		printArray(cack);
		ca.fromCharArray(cack);
		cack = ca.toCharArray();
		printArray(cack);

		char c4[] = new char[4];
		ca.getChars(0, 4, c4, 0);
		printArray(c4);

		SafeArray ca2 = new SafeArray(Variant.VariantShort, 4);
		ca2.setChars(0, 4, c4, 0);
		cack = ca2.toCharArray();
		printArray(cack);

		// short
		System.out.println("Short");
		SafeArray sha = new SafeArray(Variant.VariantShort, 4);
		System.out.println("elem size:" + sha.getElemSize());
		short shack[] = new short[] { 1000, 2000, 3000, 4000 };
		printArray(shack);
		sha.fromShortArray(shack);
		shack = sha.toShortArray();
		printArray(shack);

		short sh4[] = new short[4];
		sha.getShorts(0, 4, sh4, 0);
		printArray(sh4);

		SafeArray sha2 = new SafeArray(Variant.VariantShort, 4);
		sha2.setShorts(0, 4, sh4, 0);
		shack = sha2.toShortArray();
		printArray(shack);

		// string
		System.out.println("String");
		SafeArray sa = new SafeArray(Variant.VariantString, 4);
		System.out.println("elem size:" + sa.getElemSize());
		String sack[] = new String[] { "aa", "bb", "cc", "dd" };
		printArray(sack);
		sa.fromStringArray(sack);
		sack = sa.toStringArray();
		printArray(sack);

		String s4[] = new String[4];
		sa.getStrings(0, 4, s4, 0);
		printArray(s4);

		SafeArray sa2 = new SafeArray(Variant.VariantString, 4);
		sa2.setStrings(0, 4, s4, 0);
		sack = sa2.toStringArray();
		printArray(sack);

		// variant
		System.out.println("Variant");
		SafeArray va = new SafeArray(Variant.VariantVariant, 4);
		System.out.println("elem size:" + va.getElemSize());
		Variant vack[] = new Variant[] { new Variant(1), new Variant(2.3),
				new Variant(true), new Variant("four"), };
		printArray(vack);
		va.fromVariantArray(vack);
		vack = va.toVariantArray();
		printArray(vack);

		Variant v4[] = new Variant[4];
		va.getVariants(0, 4, v4, 0);
		printArray(v4);

		SafeArray va2 = new SafeArray(Variant.VariantVariant, 4);
		va2.setVariants(0, 4, v4, 0);
		vack = va2.toVariantArray();
		printArray(vack);

		// byte
		System.out.println("Byte");
		SafeArray bba = new SafeArray(Variant.VariantByte, 4);
		System.out.println("elem size:" + bba.getElemSize());
		byte bback[] = new byte[] { 0x1, 0x2, 0x3, 0x4 };
		printArray(bback);
		bba.fromByteArray(bback);
		bback = bba.toByteArray();
		printArray(bback);

		byte bb4[] = new byte[4];
		bba.getBytes(0, 4, bb4, 0);
		printArray(bb4);

		SafeArray bba2 = new SafeArray(Variant.VariantByte, 4);
		bba2.setBytes(0, 4, bb4, 0);
		bback = bba2.toByteArray();
		printArray(bback);

		try {
			// this should throw ComException
			bba2.fromCharArray(new char[] { 'a' });
			fail("Failed to catch expected exception");
		} catch (ComFailException cfe) {
			// do nothing
			// cfe.printStackTrace();
		}
	}
}
