package com.jacob.com;

import com.jacob.activeX.ActiveXComponent;

/**
 * This exercises the two Dispatch factor methods that let you 
 * control whether you create a new running COM object or connect to an existing one
 * 
 * -Djava.library.path=d:/jacob/release -Dcom.jacob.autogc=false -Dcom.jacob.debug=true
 *
 * @author joe
 *
 */
public class ActiveXComponentFactoryTest {
	public static void main(String args[]) throws Exception {
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
				System.out.println("was unable able to connect to MSWord after previous startup");
			} else {
				System.out.println("Correctly could connect to running MSWord");
				// stop it so we can fail trying to connect to a running
				mTryConnectingThird.invoke("Quit",new Variant[] {});
				System.out.println("    Word stopped");
			}
			Thread.sleep(2000);
			ActiveXComponent mTryConnectingFourth = ActiveXComponent.connectToActiveInstance(mApplicationId);
			if (mTryConnectingFourth != null ){
				System.out.println("Was able to connect to MSWord that was stopped");
				mTryConnectingFourth.invoke("Quit",new Variant[] {});
			} else {
				System.out.println("Correctly could not connect to running MSWord");
			}
		} catch (ComException e) {
			e.printStackTrace();
		} finally {
			System.out.println("About to sleep for 2 seconds so we can bask in the glory of this success");
			Thread.sleep(2000);
			ComThread.Release();
			ComThread.quitMainSTA();
		}
	}
}
