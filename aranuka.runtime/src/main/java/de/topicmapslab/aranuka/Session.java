/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmapi.core.TopicMap;

import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapException;
import de.topicmapslab.aranuka.exception.TopicMapIOException;
import de.topicmapslab.aranuka.exception.TopicMapInconsistentException;
import de.topicmapslab.aranuka.persist.TopicMapHandler;

public class Session {

	private static Logger logger = LoggerFactory.getLogger(Session.class);

	/**
	 * The list of {@link IPersistListener}.
	 */
	private List<IPersistListener> listeners;
	
	/**
	 * Instance of the configuration object. 
	 */
	private Configuration config;
	
	/**
	 * Instance of the topicMapHandler.
	 */
	TopicMapHandler topicMapHandler;

	/**
	 * Constructor
	 * @param config - The configuration which creates the session.
	 * @param lazyBinding - Flag triggering lazyBinding.
	 */
	public Session(Configuration config, boolean lazyBinding) throws AranukaException {
		
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
	public void persist(Object topicObject) throws AranukaException {
		
		boolean fireNotification = true; 
		getTopicMapHandler().persist(topicObject);
		if (fireNotification)
			notifyPersist(topicObject);
	}
	
	/**
	 * Writes the topic map either to a file specified by the FILENAME property or to the database backend if existing.
	 */
	public void flushTopicMap(){
		
		this.config.getConnector().flushTopicMap();
	}
	
	/**
	 * Returns all object of a specific class which can be retrieved from the topic map.
	 * All objects are newly created and can not be compared to already existing object representing the same topics.
	 * @param clazz - The class.
	 * @return Set of objects.
	 */
	public Set<Object> getAll(Class<?> clazz){
		
		try{
			
			return getTopicMapHandler().getTopicsByType(clazz);
		}
		catch (Exception e) {
			e.printStackTrace();
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
	public Object getBySubjectIdentifier(String si) throws AranukaException { 
		//	returns instance of topic with si as subject identifier
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
	public Object getBySubjectLocator(String sl) throws AranukaException {
		//	returns instance of topic with si as subject locator
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
	public Object getByItemIdentifier(String ii) throws AranukaException {
		//	returns instance of topic with si as item identifier
		
		return getTopicMapHandler().getObjectByItemIdentifier(ii);
	}
	
	/**
	 * Removes a specific topic from the topic map.
	 * 
	 * This method removes every construct using the type from the topic map.
	 * 
	 * @param object - The object representing the specific topic.
	 * @return True if a topic was removed, otherwise false.
	 * @throws TopicMapException 
	 * @throws ClassNotSpecifiedException 
	 * @throws NoSuchMethodException 
	 * @throws BadAnnotationException 
	 */
	public boolean remove(Object object) throws AranukaException{
		
		boolean result = getTopicMapHandler().removeTopic(object);
		
		if (result)
			notifyRemove(object);
		
		return result;
	}
	
	/**
	 * Count the number of topic of a specific type. NOT YET IMPLEMENTED
	 * @param clazz - The class representig the type.
	 */
	public void count(Class<?> clazz){ 
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the topic map.
	 * @throws AranukaException 
	 */
	public TopicMap getTopicMap() {
		
		try {
			return getTopicMapHandler().getTopicMap();
		} catch (AranukaException e) {
			return null;
		}
		
	}
	
	/**
	 * Adds a new persist listener. 
	 * 
	 * @param listener the new listener to add
	 */
	public void addPersistListener(IPersistListener listener) {
		if (listeners==null)
			listeners=new ArrayList<IPersistListener>();
		listeners.add(listener);
	}
	
	/**
	 * Removes a persist listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removePersistListener(IPersistListener listener) {
		if (getPersistListeners().contains(listener))
			listeners.remove(listener);
	}

	/**
	 * Notifies all registered listeners that the given model was persisted. 
	 * 
	 * @param model the model which was persisted
	 */
	private void notifyPersist(Object model) {
		int size = getPersistListeners().size();
		if (size==0)
			return;
		IPersistListener[] listeners = getPersistListeners().toArray(new IPersistListener[size]);
		for (IPersistListener l : listeners) {
			l.persisted(model);
		}
	}
	
	/**
	 * Notifies all registered listeners that the given model was persisted. 
	 * 
	 * @param model the model which was persisted
	 */
	private void notifyRemove(Object model) {
		int size = getPersistListeners().size();
		if (size==0)
			return;
		IPersistListener[] listeners = getPersistListeners().toArray(new IPersistListener[size]);
		for (IPersistListener l : listeners) {
			l.removed(model);
		}
	}
	
	/**
	 * Returns {@link #listeners} which contains the persist listeners or {@link Collections#emptyList()}.
	 * @return the list of listeners or an empty list
	 */
	private List<IPersistListener> getPersistListeners() {
		if (listeners==null)
			return Collections.emptyList();
		return listeners;
	}
	
	/**
	 * Returns the topic map handler.
	 * Creates a new on of not existing.
	 * @return The topic maps handler.
	 */
 	private TopicMapHandler getTopicMapHandler() throws AranukaException {

 		if(this.topicMapHandler == null) {
 			this.topicMapHandler = new TopicMapHandler(this.config, this.config.getConnector().createTopicMap());
 			this.topicMapHandler.setTopicMapSystem(this.config.getConnector().getTopicMapSystem());
 		}
 		 		
 		return this.topicMapHandler;
	}

 	/**
	 * Method which clears the topic map of this session.
	 *  
	 */
	public void clearTopicMap() {
		this.topicMapHandler.clearCache();
		this.config.getConnector().clearTopicMap(this.topicMapHandler.getTopicMap());		
	}

}
