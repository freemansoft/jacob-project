package com.jacob.samples.office;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;

/**
 * Snippet to show Visio print dialog
 * <p>
 * Sample submitted by fatbuttlarry in SourceForge 1803140 as part of bug report
 * <p>
 * Tested with Java 6.0SE and MS Office 2003 ** Note: 1010 = VB's
 * visCmdFilePrint constant
 */
public class VisioPrintTest {

	/**
	 * Runs the print ant lets the user say ok or cancel. Note the funky Visio
	 * behavior if someone hits the cancel button
	 * 
	 */
	public void testPrintDialog() {
		ActiveXComponent oActiveX = new ActiveXComponent("Visio.Application");
		Dispatch oDocuments = oActiveX.getProperty("Documents").toDispatch();
		// create a blank document
		Dispatch.call(oDocuments, "Add", "");
		try {
			Dispatch.call(oActiveX, "DoCmd", new Integer(1010)).getInt();
			System.out.println("User hit the ok button.");
		} catch (ComFailException e) {
			System.out.println("User hit the cancel button: " + e);
		} finally {
			oActiveX.invoke("Quit");
		}
		return;
	}

	/**
	 * quick main() to test this
	 * 
	 * @param args
	 *            standard command line arguments
	 */
	public static void main(String[] args) {
		VisioPrintTest testObject = new VisioPrintTest();
		testObject.testPrintDialog();
	}
}
