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
