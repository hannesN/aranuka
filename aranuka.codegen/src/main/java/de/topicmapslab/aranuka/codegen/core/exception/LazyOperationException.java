package de.topicmapslab.aranuka.codegen.core.exception;

/**
 * 
 * @author Sven Krosse
 *
 */
public class LazyOperationException extends TopicMap2JavaMapperException {

	private static final long serialVersionUID = 1L;

	public LazyOperationException() {
	}

	public LazyOperationException(String message) {
		super(message);
	}

	public LazyOperationException(Throwable cause) {
		super(cause);
	}

	public LazyOperationException(String message, Throwable cause) {
		super(message, cause);
	}

}
