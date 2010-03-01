/**
 * 
 */
package de.topicmapslab.aranuka.connectors;

/**
 * This interface contains the constants for common properties. 
 * A driver should use them before creating its own.
 * 
 * @author Hannes Niederhausen
 *
 */
public interface IProperties {

	/**
	 * filename used for memory backends. This filename will be the name of the persisted topic map file.
	 */
	public static final String FILENAME = "filename";
	
	/**
	 * The base locator of the topic map
	 */
	public static final String BASE_LOCATOR = "base_locator";
}
