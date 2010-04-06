/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.constants;

/**
 * Interface to define constants.
 */
public interface IXsdDatatypes {

	public static final String XSD =  "http://www.w3.org/2001/XMLSchema#";
	
	/**
	 * XML datatype for string.
	 */
	public static final String XSD_STRING = XSD+"string";
	/**
	 * XML datatype for integer.
	 */
	public static final String XSD_INTEGER = XSD+"integer";
	/**
	 * XML datatype for date.
	 */
	public static final String XSD_DATE = XSD+"date";
	/**
	 * XML datatype for boolean.
	 */
	public static final String XSD_BOOLEAN = XSD+"boolean";
	/**
	 * XML datatype for float.
	 */
	public static final String XSD_FLOAT = XSD+"float";
	/**
	 * XML datatype for double.
	 */
	public static final String XSD_DOUBLE = XSD+"double";
	/**
	 * XML datatype for long.
	 */
	public static final String XSD_LONG = XSD+"long";
}
