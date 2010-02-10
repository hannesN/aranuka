/**
 * 
 */
package de.topicmapslab.aranuka.codegen.core.exception;

/**
 * An excpetion used when the ontology is not compatible with aranuka codegen.
 * 
 * @author Hannes Niederhausen
 *
 */
public class InvalidOntologyException extends Exception {
    public InvalidOntologyException(String string) {
    	super(string);
    }

	private static final long serialVersionUID = 2672505663655152183L;
}
