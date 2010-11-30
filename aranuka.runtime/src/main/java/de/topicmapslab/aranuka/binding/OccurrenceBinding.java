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
 * Occurrence field binding class.
 */
public class OccurrenceBinding extends AbstractFieldBinding {

	/**
	 * The subject identifier of the occurrence type as string.
	 */
	private String occurrenceType;
	/**
	 * The subject identifier of the used data type, i.e. the corresponding xml data type.
	 */
	private String dataType;

	/**
	 * Constructor.
	 * @param prefixMap - The prefix map.
	 * @param parent - The topic binding to which the field belongs.
	 */
	public OccurrenceBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}

	/**
	 * Returns the subject identifier of the occurrence type as string.
	 * @return - The identifier.
	 */
	public String getOccurrenceType() {
		return occurrenceType;
	}

	/**
	 * Sets the subject identifier of the occurrence type.
	 * @param occurrenceTypeIdentifier - The identifier as string.
	 */
	public void setOccurrenceType(String occurrenceTypeIdentifier) {
		this.occurrenceType = occurrenceTypeIdentifier;
	}

	/**
	 * Returns the subject identifier of the datatype.
	 * @return - The identifier as string.
	 */
	public String getDataType() {
		return dataType;
	}

	/** 
	 * Sets the subject identifier of the datatype.
	 * @param dataType - The identifier as string.
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
