package com.jacob.com;

import java.util.Date;

/**
 * runs through some of the get and set methods on Variant
 * 
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
class VariantTest {
	public static void main(String[] args) {
		System.out.println("Testing Started");
		VariantTest testJig = new VariantTest();
		testJig.testUninitializedVariant();
		testJig.testToStringDoesNotConvert();
		testJig.testPutsAndGets();
		testJig.testSafeReleaseBoolean();
		testJig.testSafeReleaseConstant();
		testJig.testSafeReleaseString();
		testJig.testObjectIsAConstant();
		testJig.testSomeChangeVT();
		testJig.testByRefToJavaObject();
		testJig.testDispatchToJavaObject();
		System.out.println("Starting last test");
		testJig.testManyThreadedInit();
		System.out.println("Testing Complete");
		
	}
	
	/**
	 * dummy constructor
	 *
	 */
	public VariantTest(){
		
	}
	
	/**
	 * This verifies that toJavaObject() works for all of the
	 * main data types when they exist as a byRef version.
	 * <p>
	 * It compares the toJavaObject() for a byref against the
	 * toJavaObject() for the regular.
	 *
	 */
	private void testByRefToJavaObject(){
		Variant v = null;
		Variant vByRef = null;
		
		v = new Variant(new Float(53.3),false);
		vByRef = new Variant(new Float(53.3),true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())){
			System.out.println(v.toString() + " could not make type "
					+ v.getvt() +" and "+ vByRef.getvt() 
					+" java objects come out the same");
		}
		v = new Variant(new Double(53.3),false);
		vByRef = new Variant(new Double(53.3),true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())){
			System.out.println(v.toString() + " could not make type "
					+ v.getvt() +" and "+ vByRef.getvt() 
					+" java objects come out the same");
		}
		
		v = new Variant(new Boolean(true),false);
		vByRef = new Variant(new Boolean(true),true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())){
			System.out.println(v.toString() + " could not make type "
					+ v.getvt() +" and "+ vByRef.getvt() 
					+" java objects come out the same");
		}
		
		v = new Variant(new Integer(53),false);
		vByRef = new Variant(new Integer(53),true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())){
			System.out.println(v.toString() + " could not make type "
					+ v.getvt() +" and "+ vByRef.getvt() 
					+" java objects come out the same");
		}
		
		v = new Variant(new Short((short)53),false);
		vByRef = new Variant(new Short((short)53),true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())){
			System.out.println(v.toString() + " could not make type "
					+ v.getvt() +" and "+ vByRef.getvt() 
					+" java objects come out the same");
		}

		v = new Variant("53.33",false);
		vByRef = new Variant("53.33",true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())){
			System.out.println(v.toString() + " could not make type "
					+ v.getvt() +" and "+ vByRef.getvt() 
					+" java objects come out the same");
		}
		
		Date now = new Date();
		v = new Variant(now,false);
		vByRef = new Variant(now,true);
		if (!v.toJavaObject().equals(vByRef.toJavaObject())){
			System.out.println(v.toString() + " could not make type "
					+ v.getvt() +" and "+ vByRef.getvt() 
					+" java objects come out the same");
		}
		
		// need to do currency also
	}
	
	/**
	 * 4/2007 bug report toObject on dispatch tries to call getDispatchRef
	 * instead of getDispatch so toString() on dispatch blows up.
	 *
	 */
	private void testDispatchToJavaObject(){
		Variant v2 = new Variant();
		v2.putNothing();
		// this test fails even though the exact same code below works fine
		//v2.toJavaObject();
	}
	
	/**
	 * see what happens when we conver to by ref
	 *
	 */
	private void testSomeChangeVT(){
		Variant v;
		// the code shows e shouldn't need to use a returned Variant but the test says we do
		Variant vConverted;
		v = new Variant(53.3);
		short originalVT = v.getvt();
		short modifier;
		
		modifier = Variant.VariantShort;
		vConverted = v.changeType(modifier);
		if (vConverted.getvt() != modifier){
			System.out.println("Failed to change Variant "+originalVT
				+ " using mask "+modifier
				+ " resulted in "+vConverted.getvt()
				);
		}

		modifier = Variant.VariantString;
		vConverted = v.changeType(modifier);
		if (vConverted.getvt() != modifier){
			System.out.println("Failed to change Variant "+originalVT
				+ " using mask "+modifier
				+ " resulted in "+vConverted.getvt()
				);
		}

		// can't convert to byref!
		modifier = Variant.VariantByref | Variant.VariantShort; 
		vConverted = v.changeType(modifier);
		if (vConverted.getvt() == modifier){
			System.out.println("Should not have been able to change Variant "+originalVT
				+ " using mask "+modifier
				+ " resulted in "+vConverted.getvt()
				);
		}
	}
	
	/**
	 * make sure variant with no backing store works.
	 *
	 */
	private void testUninitializedVariant(){
		Variant v;
		// Variants created without parameters are auto set to VariantEmpty
		v = new Variant();
		try {
			if (v.getvt() == Variant.VariantEmpty){
				// successful
				// System.out.println("Variant initialized without parameters correctly set to empty");
			} else {
				throw new RuntimeException("getvt() on uninitialized variant shoud have returned VariantEmpty, instead returned "+v.getvt());
			}
		} catch (IllegalStateException ise){
			throw new RuntimeException("getvt() on uninitialized variant shoud have succeeded, but instead threw exception");
		}
		try {
			v.toString();
		} catch (IllegalStateException ise){
			System.out.println("toString() should never throw a runtime exception");
			throw new RuntimeException("toString() should not blow up even with uninitialized Variant");
		}
		
	}
	
	
	/**
	 * 
	 * verify the toString() method does not do type conversion
	 */
	private void testToStringDoesNotConvert(){
		Variant v;
		v = new Variant(true);
		v.toString();
		if (v.getvt() != Variant.VariantBoolean){
			throw new RuntimeException("toString() converted boolean to something else");
		} else {
			//System.out.println("toString() correctly does not convert type");
		}
		if (v.getBoolean() != true){
			System.out.println("toString() converted boolean true to "+ v.getBoolean());
		}
		v = new Variant(false);
		v.toString();
		if (v.getvt() != Variant.VariantBoolean){
			throw new RuntimeException("toString() converted boolean to something else");
		} else {
			//System.out.println("toString() correctly does not convert type");
		}
		if (v.getBoolean() != false){
			System.out.println("toString() converted boolean false to "+ v.getBoolean());
		}
	}
	
	private void testSafeReleaseBoolean(){
		Variant v;
		v = new Variant(true);
		//System.out.println("Newly created Variant ("+ v.getBoolean()+") "+
		//		"trying to create access violation but it doesn't seem to be easy");
		v.safeRelease();
		try {
			v.getBoolean();
			System.out.println("IllegalStateException should have been thrown when querying safeReleased object");
			throw new RuntimeException("test failed");
		} catch (IllegalStateException ise){
			//System.out.println("IllegalStateException correctly thrown after safeRelease");
		}
		v = new Variant(true);
		for ( int i = 0 ; i < 10; i ++){
			new Variant ("xxx"+i);
			new Variant(i);
			new Variant ("yyy"+i);
		}
		ComThread.Release();
		try {
			v.getBoolean();
			System.out.println("IllegalStateException should have been thrown when querying ComThread.Release");
			throw new RuntimeException("test failed");
		} catch (IllegalStateException ise){
			//System.out.println("IllegalStateException correctly thrown after ComThread.Release");
		}
	}
	
	/**
	 * verify the constant values aren't released with safeRelease
	 *
	 */
	private void testSafeReleaseConstant(){
		//System.out.println("Using Static constant Variant - should never throw access violation");
		Variant.VT_TRUE.safeRelease();
		if (Variant.VT_TRUE.getBoolean() != true){
			System.out.println("VT_TRUE has been broken by SafeRelease()");
			throw new RuntimeException("test failed");
		} else {
			//System.out.println("VT_TRUE survived SafeRelease()");
		}
		
		for ( int i = 0 ; i < 10; i ++){
			new Variant ("xxx"+i);
			new Variant(i);
			new Variant ("yyy"+i);
		}
		ComThread.Release();
		
		if (Variant.VT_TRUE.getBoolean() != true){
			System.out.println("VT_TRUE has been broken by ComThread.Release()");
			throw new RuntimeException("test failed");
		} else {
			//System.out.println("VT_TRUE survived ComThread.Release()");
		}
		
	}
	
	/**
	 * this used to try and and create an access violation but that
	 * didn't work and now the methods on the Variant are smarter about
	 * working after a release
	 *
	 */
    private void testSafeReleaseString(){
    	String mTestString = "Guitar Hero";
		Variant v = new Variant(mTestString);
		//System.out.println("Newly created Variant ("+ v.getString()+") "+
		//		"about to safe release and then access");
		v.safeRelease();
		try {
			v.getString();
			System.out.println("IllegalStateException should have been thrown when querying safeReleased object");
			throw new RuntimeException("test failed");
		} catch (IllegalStateException ise){
			//System.out.println("IllegalStateException correctly thrown after safeRelease");
		}
    }
	
    /**
     * verifies objectIsAConstant works as expected
     *
     */
    public void testObjectIsAConstant(){
    	Variant v = new Variant("d");
    	if (!v.objectIsAConstant(Variant.VT_FALSE)){
    		System.out.println("did not recognize VT_FALSE");
    	}
    	if (!v.objectIsAConstant(Variant.VT_TRUE)){
    		System.out.println("did not recognize VT_TRUE");
    	}
    	if (!v.objectIsAConstant(Variant.VT_MISSING)){
    		System.out.println("did not recognize VT_MISSING");
    	}
    	if (!v.objectIsAConstant(Variant.DEFAULT)){
    		System.out.println("did not recognize DEFAULT");
    	}
    	if (v.objectIsAConstant(new Variant(true))){
    		System.out.println("confused a boolean with VT_TRUE");
    	}
    	if (v.objectIsAConstant(new Variant(false))){
    		System.out.println("confused a boolean with VT_FALSE");
    	}
    	    	
    	
    }
    
	/**
	 * tests put and get methods looking for obvious defects
	 *
	 */
	private void testPutsAndGets(){
		Variant v = new Variant();
		v.putInt(10);
		if (v.getInt() != 10){
			System.out.println("int test failed");
		}
		v.putShort((short)10);
		if (v.getShort() != 10){
			System.out.println("short test failed");
		}
		v.putByte((byte)10);
		if (v.getByte() != 10){
			System.out.println("int test failed");
		}
		v.putFloat(10);
		if (v.getFloat() != 10.0){
			System.out.println("float test failed");
		}
		v.putDouble(10);
		if (v.getDouble() != 10.0){
			System.out.println("double test failed");
		}
		v.putString("1234.567");
		if (!"1234.567".equals(v.getString())){
			System.out.println("string test failed");
		}
		v.putBoolean(true);
		if (v.getBoolean() != true){
			System.out.println("failed boolean test(true)");
		}
		v.putBoolean(false);
		if (v.getBoolean() != false){
			System.out.println("failed boolean test(false)");
		}
		v.putCurrency(123456789123456789L);
		if (v.getCurrency()!=123456789123456789L){
			System.out.println("failed currency test");
		}
		
		Date ourDate = new Date();
		v.putDate(ourDate);
		Date retrievedDate = v.getJavaDate();
		if (!retrievedDate.equals(ourDate)){
			System.out.println("failed java date load and unload");
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
		// this line blows up in the test above
		if (v2.toJavaObject() == null){
			System.out.println("putNothing() followed by toJavaObject() should return a Dispatch");
		}
		
	}

	/**
	 * Spin up a lot of threads and have them all create variants
	 * 3/2007 there have been several reports in multi-threaded servers that show init() failing
	 *
	 */
	public void testManyThreadedInit(){
        VariantInitTestThread threads[] = new VariantInitTestThread[75];
        
		System.out.println("Starting thread test ("+threads.length
				+ " threads each creating 10000 objects)."
				+ " This may take 30 seconds or more.");
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new VariantInitTestThread("thread-" + i, 10000);
        }
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
        int numComplete = 0;
        while (numComplete < threads.length){
        	// give the works time to work
        	try {
        		Thread.sleep(333);
			} catch (InterruptedException ie){
				// do nothing
			}
        	numComplete = 0;
        	for ( int i = 0; i < threads.length; i++){
        		if (threads[i].isComplete){
        			numComplete++;
        		}
        	}
        	//System.out.print("["+numComplete+"/"+threads.length+"]");
        }
		System.out.println("Finished thread test");
	}
	
	/**
	 * a class to create variants in seperate threads
	 * @author joe
	 *
	 */
	class VariantInitTestThread extends Thread
	{
		private boolean isComplete = false;
		
	    private int initialRunSize = 0;
	    /**
	     * @param arg0
	     */
	    public VariantInitTestThread(String newThreadName, int iStartCount)
	    {
	        super(newThreadName);
	        initialRunSize = iStartCount;
	        
	    }

	    public boolean isComplete(){
	    	return isComplete;
	    }
	    /**
	     * Blow out a bunch of Variants
	     * 
	     * @see java.lang.Runnable#run()
	     */
	    public void run()
	    {
	    	for (int variantIndex = 0; variantIndex < initialRunSize; variantIndex ++ ){
	    		try {
	    			Thread.yield();
	    			Thread.sleep(0);
				} catch (InterruptedException ie){
					// do nothing
				}
				//System.out.println(Thread.currentThread().getName());
	    		Variant testSubject = new Variant(variantIndex);
	    		testSubject.getvt();
	    		testSubject.getInt();
	    	}
	    	isComplete = true;
	    }
	}
}
