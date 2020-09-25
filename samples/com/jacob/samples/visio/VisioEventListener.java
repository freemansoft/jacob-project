package com.jacob.samples.visio;

import com.jacob.com.Variant;

/**
 * Created as part of sourceforge 1386454 to demonstrate returning values in
 * event handlers
 * 
 * @author miles@rowansoftware.net
 * 
 * There are many more Visio events available. See the Microsoft Office SDK
 * documentation. To receive an event, add a method to this interface whose name
 * matches the event name and has only one parameter, Variant[]. The JACOB
 * library will use reflection to call that method when an event is received.
 */
public interface VisioEventListener {

	public void BeforeQuit(Variant[] args);

	public void DocumentChanged(Variant[] args);

	public void DocumentCloseCanceled(Variant[] args);

	public void DocumentCreated(Variant[] args);

	public void DocumentOpened(Variant[] args);

	public void DocumentSaved(Variant[] args);

	public void DocumentSavedAs(Variant[] args);

	public Variant QueryCancelQuit(Variant[] args);
}
