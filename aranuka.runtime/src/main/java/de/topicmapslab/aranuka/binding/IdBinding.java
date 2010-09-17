/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.binding;

import java.util.Map;

import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * Identifier field binding class.
 */
public class IdBinding extends AbstractFieldBinding {

	/**
	 * The type if the identifier.
	 */
	private IdType idtype;
	
	private boolean autogenerate;
	
	/**
	 * Constructor.
	 * @param prefixMap - The prefix map.
	 * @param parent - The topic binding to which the id belongs.
	 */
	public IdBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
		autogenerate = false; // default
	}

	/**
	 * Sets the id type.
	 * @param idtype - The id type.
	 */
	public void setIdtype(IdType idtype) {
		this.idtype = idtype;
	}

	/**
	 * Returns the id type.
	 * @return The id type
	 */
	public IdType getIdtype() {
	
		return idtype;
	}

	/**
	 * Sets the autogenerate flag
	 * 
	 * @param autogenerate the new flag value
	 */
	public void setAutogenerate(boolean autogenerate) {
	
		this.autogenerate = autogenerate;
	}
	
	/**
	 * Returns the state of the autogenerate flag
	 * @return 
	 */
	public boolean isAutogenerate() {
		return autogenerate;
	}
	
}
