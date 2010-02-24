package de.topicmapslab.aranuka.engine;

import org.tmapi.core.TopicMap;


public interface IEngineDriver {
	
	public TopicMap getTopicMap();
	public boolean flushTopicMap();
		
}
