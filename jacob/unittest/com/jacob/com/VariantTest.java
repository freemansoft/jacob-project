package com.jacob.com;

/**
 * runs through some of the get and set methods on Variant
 */
class VariantTest {
	public static void main(String[] args) {
		//deprecated
		//System.runFinalizersOnExit(true);
		Variant v = new Variant();
		v.putInt(10);
		System.out.println("got=" + v.toInt());
		v.putInt(10);
		System.out.println("got=" + v.toDouble());
		v.putString("1234.567");
		System.out.println("got=" + v.toString());
		v.putBoolean(true);
		System.out.println("got=" + v.toBoolean());
		v.putBoolean(false);
		System.out.println("got=" + v.toBoolean());
		v.putCurrency(123456789123456789L);
		System.out.println("got=" + v.toCurrency());
	}
}
