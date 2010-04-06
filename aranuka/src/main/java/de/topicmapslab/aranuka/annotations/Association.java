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

/**
 * Annotation to map an attribute to an association.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Association {
	
	/**
	 * Defines whether an topic referenced by an association should be persited as well.
	 * @return
	 */
	boolean persistOnCascade() default false;
	
	/**
	 * The type of the association, default base_locator:is-attribute-of.
	 */
	String type() default "base_locator:is-attribute-of";
	
	/**
	 * The role type played by "this", default base_locator:attributed.
	 */
	String played_role() default "base_locator:attributed";
	
	/**
	 * The role type of the other object, default base_locator:attribute, not needed for unary associations.
	 */
	String other_role() default "base_locator:attribute";
}
