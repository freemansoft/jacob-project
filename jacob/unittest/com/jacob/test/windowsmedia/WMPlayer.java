package com.jacob.test.windowsmedia;

/**
 * partial test program from the sourceforge bug report 1453161
 * that says you get a  random "can't map name to dispid" when
 * getting the URL from the player
 * <p>
 * May need to run with some command line options (including from inside Eclipse).  
 * Look in the docs area at the Jacob usage document for command line options.
 */
import com.jacob.activeX.ActiveXComponent;
import com.jacob.test.BaseTestCase;

public class WMPlayer extends BaseTestCase {

	public void testOpenWMPlayer() {
		ActiveXComponent wmp = null;
		wmp = new ActiveXComponent("WMPlayer.OCX");

		// the sourceforge posting didn't post all the code so this is all we
		// have we need some other information on how to set the document
		// so that we have a url to open
		for (int i = 0; i < 1000; i++) {
			System.out.println("the wmp url is "
					+ wmp.getProperty("URL").toString());
		}

	}

}
