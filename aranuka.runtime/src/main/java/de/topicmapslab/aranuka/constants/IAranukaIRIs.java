/**
 * 
 */
package de.topicmapslab.aranuka.constants;

/**
 * This interface contains a base locator which should be used to deserialize XTM2.0 .
 * <p>
 * All id attributes will be transformed into an item identifier while reading XTM2.0 . 
 * Aranuka needs to distinguish these identififers to ignore them when setting the ids to an
 * object. To accomplish this aranuka checks if the item identifier starts with ITEM_IDENTIFIER_PREFIX.
 * </p>
 * <p>
 * It is advised to use {@link IAranukaIRIs#ITEM_IDENTIFIER_PREFIX} as base locator for the topic maps reader.
 * </p>
 * @author Hannes Niederhausen
 *
 */
public interface IAranukaIRIs {
	public final static String ITEM_IDENTIFIER_PREFIX = "http://aranujka.topicmapslab.de/itemidentifier/";
}
