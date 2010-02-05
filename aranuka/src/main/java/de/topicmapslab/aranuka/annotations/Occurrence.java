package de.topicmapslab.aranuka.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for an attribute which should be mapped to an occurrence
 * @author Christian Haß
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Occurrence {
	
	/**
	 * The occurrence type. 
	 */
	String type(); 
}
