/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.exception;

/**
 * Exception thrown in case an interaction with the topic map went wrong.
 */
public class TopicMapException extends Exception {
	
	private static final long serialVersionUID = 4177758602546789715L;

	/**
	 * Constructor.
	 * @param msg - The error message.
	 */
	public TopicMapException(String msg) {
		super(msg);
	}
	
	/**
	 * Constructor.
	 * @param msg - The error message.
	 * @param e - The original exception.
	 */
	public TopicMapException(String msg, Throwable e) {
		super(msg, e);
	}
}
