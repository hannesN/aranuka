package de.topicmapslab.aranuka.codegen.core.exception;

/**
 * 
 * @author Sven Krosse
 *
 */
public class ClassfileWriterException extends TopicMap2JavaMapperException {

	private static final long serialVersionUID = 1L;

	public ClassfileWriterException() {
	}

	public ClassfileWriterException(String message) {
		super(message);
	}

	public ClassfileWriterException(Throwable cause) {
		super(cause);
	}

	public ClassfileWriterException(String message, Throwable cause) {
		super(message, cause);		
	}

}
