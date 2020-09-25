package com.jacob.samples.visio;

import com.jacob.com.Variant;

/**
 * Created as part of sourceforge 1386454 to demonstrate returning values in
 * event handlers
 * 
 * @author miles@rowansoftware.net
 * 
 * You can subclass this class and only implement the methods you're interested
 * in
 */
public class VisioEventAdapter implements VisioEventListener {

	VisioApp app = null;

	public VisioEventAdapter(VisioApp pApp) {
		app = pApp;
		System.out.println("Event listener constructed");
	}

	public void BeforeQuit(Variant[] args) {
	}

	public void DocumentChanged(Variant[] args) {
		System.out.println("documentChanged()");
	}

	public void DocumentCloseCanceled(Variant[] args) {
	}

	public void DocumentCreated(Variant[] args) {
	}

	public void DocumentOpened(Variant[] args) {
		System.out.println("DocumentOpened()");
	}

	public void DocumentSaved(Variant[] args) {
	}

	public void DocumentSavedAs(Variant[] args) {
	}

	public Variant QueryCancelDocumentClose(Variant[] args) {
		System.out.println("QueryCancelDocumentClose()");
		return new Variant(false);
	}

	/**
	 * we don't actually let it quit. We block it so that we don't have to
	 * relaunch when we look at a new document
	 */
	public Variant QueryCancelQuit(Variant[] args) {
		// these may throw VisioException
		System.out
				.println("Saving document, hiding and telling visio not to quit");
		try {
			app.save();
			app.setVisible(false);
		} catch (VisioException ve) {
			System.out.println("ailed to openFile()");
			ve.printStackTrace();
		}
		return new Variant(true);
	}
}
