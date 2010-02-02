package de.topicmapslab.aranuka.exception;

/**
 * Exception thrown in case a class which is not specified as annotated class is used.
 * @author Christian Haß
 *
 */
public class ClassNotSpecifiedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9023794452696943750L;

	/**
	 * Constructor.
	 * @param msg - The error message.
	 */
	public ClassNotSpecifiedException(String msg) {
		super(msg);
	}
}
