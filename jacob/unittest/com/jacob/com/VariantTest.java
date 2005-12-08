package com.jacob.com;

/**
 * runs through some of the get and set methods on Variant
 */
class VariantTest {
	public static void main(String[] args) {
		System.out.println("Testing Started");
		Variant v = new Variant();
		v.putInt(10);
		if (v.toInt() != 10){
			System.out.println("int test failed");
		}
		v.putInt(10);
		if (v.toDouble() != 10.0){
			System.out.println("double test failed");
		}
		v.putString("1234.567");
		if (!"1234.567".equals(v.toString())){
			System.out.println("string test failed");
		}
		v.putBoolean(true);
		if (v.toBoolean() != true){
			System.out.println("failed boolean test(true)");
		}
		v.putBoolean(false);
		if (v.toBoolean() != false){
			System.out.println("failed boolean test(false)");
		}
		v.putCurrency(123456789123456789L);
		if (v.toCurrency()!=123456789123456789L){
			System.out.println("failed long test");
		}
		
		v.putNull();
		if (!v.isNull()){
			System.out.println("failed detecting set null");
		}
		v.putString("something other than null");
		if (v.isNull()){
			System.out.println("failed null replacement with string");
		}
		
		v.putEmpty();
		if (!v.isNull()){
			System.out.println("failed detecting set empty as null");
		}
		v.putString("something other than null");
		if (v.isNull()){
			System.out.println("failed empty replacement with string as isNull");
		}

		Variant v2 = new Variant();
		v2.putNothing();
		if (v2.getvt() != Variant.VariantDispatch){
			System.out.println("putNothing was supposed to set the type to VariantDispatch");
		}
		if (!v2.isNull()){
			System.out.println("putNothing is supposed to cause isNull() to return true");
		}
		
		System.out.println("Testing Complete");
		
	}
}
