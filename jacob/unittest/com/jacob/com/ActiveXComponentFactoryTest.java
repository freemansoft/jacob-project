package com.jacob.com;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.test.BaseTestCase;

/**
 * This exercises the two Dispatch factor methods that let you 
 * control whether you create a new running COM object or connect to an existing one
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
public class ActiveXComponentFactoryTest extends BaseTestCase {
	
	public void testActiveXComponentFactory(){
		ComThread.InitSTA(true);
		try {
			System.out.println("This test only works if MS Word is NOT already running");
			String mApplicationId = "Word.Application";
			ActiveXComponent mTryConnectingFirst = ActiveXComponent.connectToActiveInstance(mApplicationId);
			if (mTryConnectingFirst != null ){
				mTryConnectingFirst.invoke("Quit",new Variant[] {});
				System.out.println("Was able to connect to MSWord when hadn't started it");
			} else {
				System.out.println("Correctly could not connect to running MSWord");
			}
			System.out.println("    Word Starting");
			ActiveXComponent mTryStartingSecond = ActiveXComponent.createNewInstance(mApplicationId);
			if (mTryStartingSecond == null){
				System.out.println("was unable to start up MSWord ");
			} else {
				System.out.println("Correctly could start MSWord");
			}
			ActiveXComponent mTryConnectingThird = ActiveXComponent.connectToActiveInstance(mApplicationId);
			if (mTryConnectingThird == null ){
				fail("Was unable able to connect to MSWord after previous startup");
			} else {
				System.out.println("Stopping MSWord");
				// stop it so we can fail trying to connect to a running
				mTryConnectingThird.invoke("Quit",new Variant[] {});
			}
			Thread.sleep(2000);
			ActiveXComponent mTryConnectingFourth = ActiveXComponent.connectToActiveInstance(mApplicationId);
			if (mTryConnectingFourth != null ){
				mTryConnectingFourth.invoke("Quit",new Variant[] {});
				fail("Was able to connect to MSWord that was stopped");
			} else {
				System.out.println("Correctly could not connect to running MSWord");
			}
		} catch (InterruptedException ie){
		} catch (ComException e) {
			e.printStackTrace();
			fail("Caught COM exception");
		} finally {
			//System.out.println("About to sleep for 2 seconds so we can bask in the glory of this success");
			//Thread.sleep(2000);
			ComThread.Release();
			ComThread.quitMainSTA();
		}
	}
}
