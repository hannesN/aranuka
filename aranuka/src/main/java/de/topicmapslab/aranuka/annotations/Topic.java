package de.topicmapslab.aranuka.annotations;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for an class which is meant to be an topic type.
 * @author Christian Haß
 *
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Topic {
	
	/**
	 * the name of the topic
	 */
	String name() default "";
	
	/**
	 * the subject identifier of the topic, default is path of the class
	 */
	String[] subject_identifier() default {};
	
}
