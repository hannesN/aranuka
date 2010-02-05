package de.topicmapslab.aranuka.exception;

/**
 * Exception thrown in case a annotation is not correct specified.
 * @author Christian Haß
 *
 */
public class BadAnnotationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8889087697945031778L;

	/**
	 * Constructor.
	 * @param msg - The error message.
	 */
	public BadAnnotationException(String msg) {
		super(msg);
	}
	
}
