/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.exception;


/**
 * Exception thrown in case of an problem while reading or writing the topic map.
 */
public class TopicMapIOException extends Exception {
	
	private static final long serialVersionUID = -5220413582260150574L;

	/**
	 * Constructor.
	 * @param msg - The error message.
	 */
	public TopicMapIOException(String msg) {
		super(msg);
	}
}
