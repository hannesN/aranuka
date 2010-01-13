package de.topicmapslab.aranuka.codegen.core.exception;

/**
 * 
 * @author Sven Krosse
 *
 */
public class POJOGenerationException extends TopicMap2JavaMapperException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public POJOGenerationException() {
	}

	public POJOGenerationException(String message) {
		super(message);
	}

	public POJOGenerationException(Throwable cause) {
		super(cause);
	}

	public POJOGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

}
