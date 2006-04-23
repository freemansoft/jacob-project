package com.jacob.com;

/**
 * runs through some of the get and set methods on Variant
 * 
 * -Djava.library.path=d:/jacob/release -Dcom.jacob.debug=false
 */
class VariantTest {
	public static void main(String[] args) {
		System.out.println("Testing Started");
		VariantTest testJig = new VariantTest();
		testJig.testPutsAndGets();
		testJig.testSafeReleaseBoolean();
		testJig.testSafeReleaseConstant();
		testJig.testSafeReleaseString();
		
		System.out.println("Testing Complete");
		
	}
	
	/**
	 * dummy constructor
	 *
	 */
	public VariantTest(){
		
	}
	
	private void testSafeReleaseBoolean(){
		Variant v = new Variant(true);
		System.out.println("Newly created Variant ("+ v.getBoolean()+") "+
				"trying to create access violation but it doesn't seem to be easy");
		v.safeRelease();
		if (v.getBoolean() != true){
			System.out.println("Variant value ("+v.getBoolean()+") "
					+"has been broken by SafeRelease()");
		} else {
			System.out.println("Variant value ("+v.getBoolean()+") "
					+"has survived SafeRelease()");
		}
		for ( int i = 0 ; i < 20000; i ++){
			new Variant ("xxx"+i);
			new Variant(i);
			new Variant ("yyy"+i);
		}
		ComThread.Release();
		if (v.getBoolean() != true){
			System.out.println("Variant value ("+v.getBoolean()+") "
					+"has been broken by ComThread.Release()");
		} else {
			System.out.println("Variant value ("+v.getBoolean()+") "
					+"has been survived by ComThread.Release()");
		}
	}
	
	private void testSafeReleaseConstant(){
		System.out.println("Using Static constant Variant - should never throw access violation");
		Variant.VT_TRUE.safeRelease();
		if (Variant.VT_TRUE.getBoolean() != true){
			System.out.println("VT_TRUE has been broken by SafeRelease()");
		} else {
			System.out.println("VT_TRUE survived SafeRelease()");
		}
		for ( int i = 0 ; i < 20000; i ++){
			new Variant ("xxx"+i);
			new Variant(i);
			new Variant ("yyy"+i);
		}
		ComThread.Release();
		if (Variant.VT_TRUE.getBoolean() != true){
			System.out.println("VT_TRUE has been broken by ComThread.Release()");
		} else {
			System.out.println("VT_TRUE survived ComThread.Release()");
		}
		
	}
	
    private void testSafeReleaseString(){
    	String mTestString = "Guitar Hero";
		Variant v = new Variant(mTestString);
		System.out.println("Newly created Variant ("+ v.getString()+") "+
				"trying to create access violation but it doesn't seem to be easy");
		v.safeRelease();
		if (v.getString() == null || !v.getString().equals(mTestString)){
			System.out.println("Variant value ("+v.getString()+") "
					+"has been broken by SafeRelease()");
		} else {
			System.out.println("Variant value ("+v.getString()+") "
					+"has survived SafeRelease()");
		}
    }
		
	/**
	 * tests put and get methods looking for obvious defects
	 *
	 */
	private void testPutsAndGets(){
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
		
		
	}
}
