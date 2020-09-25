package com.jacob.samples.visio;

/**
 * Created as part of sourceforge 1386454 to demonstrate returning values in
 * event handlers
 * 
 * @author miles@rowansoftware.net
 * 
 * This extends runtime exception so that we can be sloppy and not put catch
 * blocks everywhere
 */
public class VisioException extends Exception {
	/**
	 * Totally dummy value to make Eclipse quit complaining
	 */
	private static final long serialVersionUID = 1L;

	public VisioException(String msg) {
		super(msg);
	}

	public VisioException(Throwable cause) {
		super(cause);
	}
}
