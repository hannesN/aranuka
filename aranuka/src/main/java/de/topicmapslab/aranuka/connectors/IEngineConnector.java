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


public interface IEngineConnector {
	
	public TopicMap getTopicMap();
	public boolean flushTopicMap();
	public void setProperty(String propertyKey, String propertyValue);
	public String getProperty(String propertyKey);
	public void setProperties(Properties properties);
	public TopicMapSystem getTopicMapSystem();
}
