package de.topicmapslab.aranuka;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinytim.mio.CTMTopicMapWriter;

import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;
import de.topicmapslab.aranuka.persist.TopicMapHandler;

public class Session {

	private static Logger logger = LoggerFactory.getLogger(Session.class);
	
	private Configuration config;
	TopicMapHandler topicMapHandler;
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	public Session(Configuration config, boolean leazyBinding) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException{
		
		if(config == null)
			throw new RuntimeException("Config must not be null."); /// TODO change exception type
		
		this.config = config;
				
		if(!leazyBinding){
			
			logger.info("Create bindings at the beginning.");
			getTopicMapHandler().invokeBinding();
		}
	}

	public void persist(Object topicObject) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, IOException, TopicMapInconsistentException {
		
		getTopicMapHandler().persist(topicObject);
	}
	
	public void flushTopicMap(){
		
		CTMTopicMapWriter writer;
		try {
			
			String filename = config.getProperty(Property.FILENAME);
			logger.info("Writing to "+filename);
			writer = new CTMTopicMapWriter(new FileOutputStream(filename), config.getBaseLocator());

			Map<String, String> prefixMap = config.getPrefixMap();
			for (String key : prefixMap.keySet()) {
				writer.addPrefix(key, prefixMap.get(key));
			}
			
			String bl = config.getBaseLocator();
			for (Class<?> clazz : config.getClasses()) {
				String prefix = bl+clazz.getName().replaceAll("\\.", "/")+"/";
				writer.addPrefix(clazz.getSimpleName().toLowerCase(), prefix);
			}

			writer.write(getTopicMapHandler().getTopicMap());
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public void getAll(Class<?> clazz){ //	returns all instances of the class in the topic map
		
	}
	
	public void getBySubjectIdentifier(String si){ //	returns instance of topic with si as subject identifier
		
	}
	
	public void getBySubjectLocator(String sl){ //	returns instance of topic with si as subject locator
		
	}
	
	public void getByItemIdentifier(String si){ //	returns instance of topic with si as item identifier
		
	}
	
	public void remove(Object object){ //	removes instance from the topic map
		
	}
	
	public void count(Class<?> clazz){ 
		
	}
	
	// getter and setter
	
	// --[ private methods ]-------------------------------------------------------------------------------
	
	private TopicMapHandler getTopicMapHandler(){
		
		if(this.topicMapHandler == null)
			topicMapHandler = new TopicMapHandler(config);
		
		return topicMapHandler;
	}

}
