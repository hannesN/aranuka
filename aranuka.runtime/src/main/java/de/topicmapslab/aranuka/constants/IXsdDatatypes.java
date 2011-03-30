/*******************************************************************************
 * Copyright 2010-2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
