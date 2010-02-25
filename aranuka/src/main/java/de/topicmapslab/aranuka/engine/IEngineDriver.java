package de.topicmapslab.aranuka.engine;

import java.util.Properties;

import org.tmapi.core.TopicMap;


public interface IEngineDriver {
	
	public TopicMap getTopicMap();
	public boolean flushTopicMap();
	public void setProperty(String propertyKey, String propertyValue);
	public void setProperties(Properties properties);
		
}
