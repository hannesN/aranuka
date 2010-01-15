package de.topicmapslab.aranuka.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map an attribute to an association.
 * @author Christian Haß
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Association {
	
	/**
	 * The kind of the association, default BINARY. 
	 */
	ASSOCIATIONKIND kind() default ASSOCIATIONKIND.BINARY;
	
	/**
	 * The type of the association, default base_locator:is-attribute-of.
	 */
	String type() default "base_locator:is-attribute-of";
	
	/**
	 * The role type played by "this", default base_locator:attribute.
	 */
	String played_role() default "base_locator:attribute";
	
	/**
	 * The role type of the other object, default base_locator:attributed, not needed for unary associations.
	 */
	String other_role() default "base_locator:attributed";
}
