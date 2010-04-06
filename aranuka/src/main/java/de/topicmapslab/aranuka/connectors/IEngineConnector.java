/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.connectors;

import java.util.Properties;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;

/**
 * Connector interface.
 */
public interface IEngineConnector {
	
	/**
	 * Returns the topic map.
	 */
	public TopicMap getTopicMap();
	/**
	 * Flush the topic map.
	 */
	public boolean flushTopicMap();
	/**
	 * Sets a property.
	 * @param propertyKey - The property identifier.
	 * @param propertyValue - The property value.
	 */
	public void setProperty(String propertyKey, String propertyValue);
	/**
	 * Gets a specific property.
	 * @param propertyKey - The property identifier.
	 * @return The value of the specific property.
	 */
	public String getProperty(String propertyKey);
	/**
	 * Sets all properties, overwrite current properties.
	 * @param properties - The properties.
	 */
	public void setProperties(Properties properties);
	/**
	 * Returns the topic map system.
	 */
	public TopicMapSystem getTopicMapSystem();
}
