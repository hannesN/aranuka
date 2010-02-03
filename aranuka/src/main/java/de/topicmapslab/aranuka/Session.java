package de.topicmapslab.aranuka;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinytim.mio.CTMTopicMapWriter;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;

import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapException;
import de.topicmapslab.aranuka.exception.TopicMapIOException;
import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;
import de.topicmapslab.aranuka.persist.TopicMapHandler;

/**
 * The session is used to persist and get objects from the topic map.
 * @author christian haß
 *
 */
public class Session {

	private static Logger logger = LoggerFactory.getLogger(Session.class);

	/**
	 * Instance of the configuration object.
	 */
	private Configuration config;
	
	/**
	 * Instance of the topicMapHandler.
	 */
	TopicMapHandler topicMapHandler;
	
	// --[ public methods ]------------------------------------------------------------------------------
	
	/**
	 * Constructor
	 * @param config - The configuration which creates the session.
	 * @param lazyBinding - Flag triggering lazyBinding.
	 */
	public Session(Configuration config, boolean lazyBinding) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapException{
		
		if(config == null)
			throw new RuntimeException("Config must not be null."); /// TODO change exception type
		
		this.config = config;
				
		if(!lazyBinding){
			
			logger.info("Create bindings at the beginning.");
			getTopicMapHandler().invokeBinding();
		}
	}

	/**
	 * Persists an object in the topic map.
	 * @param topicObject - The object.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws TopicMapException
	 */
	public void persist(Object topicObject) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapIOException, TopicMapInconsistentException, TopicMapException {
		
		getTopicMapHandler().persist(topicObject);
	}
	
	/**
	 * Writes the topic map either to a file specified by the FILENAME property or to the database backend if existing.
	 */
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
	
	/**
	 * Returns all object of a spcific class which can be retrieved from the topic map.
	 * All objects are newly created and can not be compared to allready existing object representing the same topics.
	 * @param clazz - The class.
	 * @return Set of objects.
	 */
	public Set<Object> getAll(Class<?> clazz){ //	returns all instances of the class in the topic map
		
		try{
			
			Set<Object> instances = getTopicMapHandler().getTopicsByType(clazz);

			
			return instances;
		}
		catch (Exception e) {
			return Collections.emptySet();
		}
	}
	
	/**
	 * Retrieves a specific object from the topic maps, identified by the subject identifier.
	 * @param si - The subject identifier.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapException
	 */
	public Object getBySubjectIdentifier(String si) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapException{ //	returns instance of topic with si as subject identifier
	
		return getTopicMapHandler().getObjectBySubjectIdentifier(si);
		
	}
	
	/**
	 * Retrieves a specific object from the topic maps, identified by the subject locator.
	 * @param sl - The subject locator.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapException
	 */
	public Object getBySubjectLocator(String sl) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapException{ //	returns instance of topic with si as subject locator
	
		return getTopicMapHandler().getObjectBySubjectLocator(sl);
	}
	
	/**
	 * Retrieves a specific object from the topic maps, identified by the item identifier.
	 * Note: It is not possible to retrieve associations this way.
	 * @param ii - The item identifier.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapException
	 */
	public Object getByItemIdentifier(String ii) throws TopicMapIOException, TopicMapInconsistentException, BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapException{ //	returns instance of topic with si as item identifier
		
		return getTopicMapHandler().getObjectByItemIdentifier(ii);
	}
	
	/**
	 * Removes a specific topic from the topic map. NOT YET IMPLEMENTED
	 * @param object - The object representing the specific topic.
	 * @return True if a topic was removed, otherwise false.
	 */
	public boolean remove(Object object){
		
		throw new UnsupportedOperationException();
		//return getTopicMapHandler().removeTopic(object);
		
	}
	
	/**
	 * Count the number of topic of a specific type. NOT YET IMPLEMENTED
	 * @param clazz - The class representig the type.
	 */
	public void count(Class<?> clazz){ 
		throw new UnsupportedOperationException();
	}
	
	// getter and setter
	
	// --[ private methods ]-------------------------------------------------------------------------------
	
	/**
	 * Returns the topic map handler.
	 * Creates a new on of not existing.
	 * @return The topic maps handler.
	 */
	private TopicMapHandler getTopicMapHandler() throws TopicMapException{

		if(this.topicMapHandler == null){
		
			try{

				TopicMap topicMap = null;
				
				// check if topic map exist
				if(this.config.getProperty(Property.FILENAME) != null){
					
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
					}else{
							
						topicMap = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(this.config.getBaseLocator());
					}
					 
				}else{
					
					topicMap = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap(this.config.getBaseLocator());
				}
	
				topicMapHandler = new TopicMapHandler(config, topicMap);

			}
			catch (Exception e) {
				throw new TopicMapException(e.getMessage(), e);
			}
		}
			
		
		return topicMapHandler;
	}

}
