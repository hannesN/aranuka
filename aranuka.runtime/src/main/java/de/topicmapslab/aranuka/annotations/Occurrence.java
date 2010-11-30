/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * Annotation for an attribute which should be mapped to an occurrence
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Occurrence {
	
	/**
	 * The occurrence type. 
	 */
	String type() default ""; 
	
	/**
	 * The data type of the occurrence. By default it is based on the
	 * type of the annotated field. With this attribute the data type 
	 * can be overridden like, the choice of the {@link Date} mapping
	 * or the mapping of string containing world coordinates.  
	 */
	String datatype() default "";
	
}
