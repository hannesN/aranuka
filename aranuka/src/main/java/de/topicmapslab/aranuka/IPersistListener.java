/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/

package de.topicmapslab.aranuka;

/**
 * This listener is used to get informed if a model has changed and was persisted.
 * 
 * @author Hannes Niederhausen
 *
 */
public interface IPersistListener {

	
	/**
	 * Notifies that the given model was persisted by the session.
	 * 
	 * @param model the persisted model
	 */
	public void persisted(Object model);
	
	/**
	 * Notifies that the given model was removed by the session.
	 * 
	 * @param model the removed model
	 */
	public void removed(Object model);
}
