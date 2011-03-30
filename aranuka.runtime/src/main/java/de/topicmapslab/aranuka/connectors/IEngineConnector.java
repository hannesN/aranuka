/*******************************************************************************
 * Copyright 2010-2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.aranuka.connectors;

import java.util.Properties;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;

import de.topicmapslab.aranuka.Configuration;

/**
 * Connector interface.
 */
public interface IEngineConnector {
	
	/**
	 * Returns the topic map.
	 */
	public TopicMap createTopicMap();
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
	
	/**
	 * Sets the configuration which uses this connector 
	 * @param conf the {@link Configuration} instance
	 */
	public void setConfiguration(Configuration conf);
	
	/**
	 * Method which clears the topic map. It removes every construct in the topic map.
	 * 
	 * The result is a an empty topic map, which can be reused.
	 * 
	 * @param topicMap the topic map to clear
	 * 
	 */
	public void clearTopicMap(TopicMap topicMap);
}
