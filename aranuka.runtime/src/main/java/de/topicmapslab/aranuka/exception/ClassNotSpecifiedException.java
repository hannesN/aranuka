/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.exception;

/**
 * Exception thrown in case a class which is not specified as annotated class is used.
 */
public class ClassNotSpecifiedException extends Exception {

	private static final long serialVersionUID = -9023794452696943750L;

	/**
	 * Constructor.
	 * @param msg - The error message.
	 */
	public ClassNotSpecifiedException(String msg) {
		super(msg);
	}
}
