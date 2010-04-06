/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.exception;

/**
 * Exception thrown in case a annotation is not correct specified.
 */
public class BadAnnotationException extends Exception {

	private static final long serialVersionUID = -8889087697945031778L;

	/**
	 * Constructor.
	 * @param msg - The error message.
	 */
	public BadAnnotationException(String msg) {
		super(msg);
	}
	
}
