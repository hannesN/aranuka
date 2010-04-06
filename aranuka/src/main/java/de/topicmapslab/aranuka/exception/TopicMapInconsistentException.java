/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.exception;

/**
 * Exception thrown in case the java model is inconsistent according to the topic map data model.
 * @author Christian Haß
 *
 */
public class TopicMapInconsistentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8807967619508659094L;

	/**
	 * Constructor.
	 * @param msg - The error message.
	 */
	public TopicMapInconsistentException(String msg) {
		super(msg);
	}
}
