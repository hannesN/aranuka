package de.topicmapslab.aranuka;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinytim.mio.CTMTopicMapReader;
import org.tinytim.mio.CTMTopicMapWriter;
import org.tmapi.core.FactoryConfigurationException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;
import de.topicmapslab.aranuka.persist.TopicMapHandler;

public class Session {

	private static Logger logger = LoggerFactory.getLogger(Session.class);
	
	private Configuration config;
	TopicMapHandler topicMapHandler;
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	public Session(Configuration config, boolean leazyBinding) throws BadAnnotationException, ClassNotSpecifiedException, NoSuchMethodException, IOException, FactoryConfigurationException, TMAPIException{
		
		if(config == null)
			throw new RuntimeException("Config must not be null."); /// TODO change exception type
		
		this.config = config;
				
		if(!leazyBinding){
			
			logger.info("Create bindings at the beginning.");
			getTopicMapHandler().invokeBinding();
		}
	}

	public void persist(Object topicObject) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, IOException, TopicMapInconsistentException, FactoryConfigurationException, TMAPIException {
		
		getTopicMapHandler().persist(topicObject);
	}
	
	public void flushTopicMap(){
		
		CTMTopicMapWriter writer;
		
		try {
			
			String filename = config.getProperty(Property.FILENAME);
			logger.info("Writing to "+filename);
			writer = new CTMTopicMapWriter(new FileOutputStream(filename), config.getBaseLocator());
			writer.setExportItemIdentifiers(true);

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
	
	public Set<Object> getAll(Class<?> clazz){ //	returns all instances of the class in the topic map
		
		try{
			
			Set<Object> instances = getTopicMapHandler().getTopicsByType(clazz);

			
			return instances;
		}
		catch (Exception e) {
			return Collections.emptySet();
		}
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
	
	private TopicMapHandler getTopicMapHandler() throws IOException, FactoryConfigurationException, TMAPIException{

		if(this.topicMapHandler == null){
			
			TopicMap topicMap = null;
			
			// check if topic map exist
			if(this.config.getProperty(Property.FILENAME) != ""){
				
				 File f = new File(this.config.getProperty(Property.FILENAME));
				 if(f.exists()){
				 
					 logger.info("Load existing topic map from " + this.config.getProperty(Property.FILENAME));
					 
					 TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
					 TopicMapSystem system = factory.newTopicMapSystem();

					 topicMap = system.createTopicMap(this.config.getProperty(Property.BASE_LOCATOR));
					 
					 CTMTopicMapReader reader = new CTMTopicMapReader(topicMap, f);
					 reader.read();
					 
					 if(topicMap == null)
						 throw new IOException("Reade topic map file " + this.config.getProperty(Property.FILENAME) + " failed!");
				 }
			}
			
			if(topicMap != null)
				topicMapHandler = new TopicMapHandler(config, topicMap);
			else
				topicMapHandler = new TopicMapHandler(config);
		}
			
		
		return topicMapHandler;
	}

}
