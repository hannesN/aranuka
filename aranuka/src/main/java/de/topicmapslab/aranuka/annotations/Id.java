package de.topicmapslab.aranuka.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
	IDTYPE type() default IDTYPE.ITEM_IDENTIFIER;
}
