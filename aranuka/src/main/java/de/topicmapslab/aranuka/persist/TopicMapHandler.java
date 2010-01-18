package de.topicmapslab.aranuka.persist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.Configuration;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;

// handles interaction with the topic map, i.e. creating topics and associations, updating, etc.
public class TopicMapHandler {

	private static Logger logger = LoggerFactory.getLogger(TopicMapHandler.class);
	
	private Configuration config; // the configuration
	private BindingHandler bindingHandler; // the binding handler
	private TopicMap topicMap; // the topic map
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	public TopicMapHandler(Configuration config){
		
		
		
		this.config = config;
		
	}
	
	public void invokeBinding()  throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		getBindingHandler().createBindingsForAllClasses();
		
		// test
		getBindingHandler().printBindings();
	}
	
	public void persist(Object topicObject){
		
		List<Object> topicToPersist = new ArrayList<Object>();
				
		topicToPersist.add(topicObject);
		
		persistTopics(topicToPersist);
		
	}
	
	
	public TopicMap getTopicMap() throws IOException /// TODO change exception type?
	{
		try{
			
			if(this.topicMap == null)
				this.topicMap = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(this.config.getBaseLocator());
			
			return this.topicMap;
		}
		catch (Exception e) {
			throw new IOException("Could not create topic map (" + e.getMessage() + ")");
		}
	}
	
	// --[ private methods ]-------------------------------------------------------------------------------

	private void persistTopics(List<Object> topicObjects){
		
		
	}
	
	
	
	
	// private getter and setter
	
	private BindingHandler getBindingHandler(){
		
		if(bindingHandler == null)
			bindingHandler = new BindingHandler(this.config);
		
		return bindingHandler;
		
	}

	
	
}
