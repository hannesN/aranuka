package de.topicmapslab.aranuka.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.topicmapslab.aranuka.enummerations.IdType;

/**
 * Annotation for field which represents identifier.
 * @author Christian Haß
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

	/**
	 * Type of the identifier, default is item identifier. 
	 */
	IdType type() default IdType.ITEM_IDENTIFIER;
}
