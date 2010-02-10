package de.topicmapslab.aranuka.exception;


/**
 * Exception thrown in case of an problem while reading or writing the topic map.
 * @author Christian Haß
 *
 */
public class TopicMapIOException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5220413582260150574L;

	/**
	 * Constructor.
	 * @param msg - The error message.
	 */
	public TopicMapIOException(String msg) {
		super(msg);
	}
}
