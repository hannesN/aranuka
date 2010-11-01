/**
 * 
 */
package de.topicmapslab.aranuka.exception;

/**
 * 
 * The exception to encapsulate other exceptions
 * 
 * @author Hannes Niederhausen
 *
 */
public class AranukaException extends Exception {
	private static final long serialVersionUID = -885912261015606243L;

	public AranukaException() {
		super();
	}

	public AranukaException(String message, Throwable cause) {
		super(message, cause);
	}

	public AranukaException(String message) {
		super(message);
	}

	public AranukaException(Throwable cause) {
		super(cause);
	}

	
	
}
