package de.topicmapslab.aranuka.codegen.core.exception;

/**
 * 
 * @author Sven Krosse
 *
 */
public class InitializationException extends TopicMap2JavaMapperException {

	private static final long serialVersionUID = 1L;

	public InitializationException() {
	}

	public InitializationException(String message) {
		super(message);
	}

	public InitializationException(Throwable cause) {
		super(cause);
	}

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
