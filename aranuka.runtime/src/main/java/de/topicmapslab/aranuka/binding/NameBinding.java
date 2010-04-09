/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.binding;

import java.util.Map;

/**
 * Name field binding class.
 */
public class NameBinding extends AbstractFieldBinding {

	/**
	 * The subject identifier of the name type.
	 */
	private String nameType;
	
	/**
	 * Constructor.
	 * @param prefixMap - The prefix map.
	 * @param parent - The topic binding to which the name belongs.
	 */
	public NameBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}

	/**
	 * Returns the name type subject identifier as string.
	 * @return The identifier.
	 */
	public String getNameType() {
		return nameType;
	}

	/**
	 * Sets the name type subject identifier as string.
	 * @param nameIdentifier - The identifier.
	 */
	public void setNameType(String nameIdentifier) {
		this.nameType = nameIdentifier;
	}

}


