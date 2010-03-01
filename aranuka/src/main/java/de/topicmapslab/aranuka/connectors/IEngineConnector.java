package de.topicmapslab.aranuka.connectors;

import java.util.Properties;

import org.tmapi.core.TopicMap;


public interface IEngineConnector {
	
	public TopicMap getTopicMap();
	public boolean flushTopicMap();
	public void setProperty(String propertyKey, String propertyValue);
	public void setProperties(Properties properties);
		
}
