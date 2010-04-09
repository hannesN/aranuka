/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
/**
 * 
 */
package de.topicmapslab.aranuka.connectors;

/**
 * This interface contains the constants for common properties. 
 * A driver should use them before creating its own.
 */
public interface IProperties {

	/**
	 * filename used for memory backends. This filename will be the name of the persisted topic map file.
	 */
	public static final String FILENAME = "filename";
	
	/**
	 * The base locator of the topic map
	 */
	public static final String BASE_LOCATOR = "base_locator";
}
