package de.topicmapslab.aranuka.annotations;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map an class to an association.
 * @author Christian Haß
 *
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssociationContainer {

}
