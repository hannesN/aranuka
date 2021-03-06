/*******************************************************************************
 * Copyright 2010-2011 Hannes Niederhausen, Topic Maps Lab
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	 * 
	 * @param config
	 *            - The configuration which creates the session.
	 * @param lazyBinding
	 *            - Flag triggering lazyBinding.
	 */
	public Session(Configuration config, boolean lazyBinding) throws AranukaException {

		if (config == null)
			throw new AranukaException("Config must not be null.");

		this.config = config;

		if (!lazyBinding) {

			logger.info("Create bindings at the beginning.");
			getTopicMapHandler().invokeBinding();
		}
	}

	/**
	 * Persists an object in the topic map.
	 * 
	 * @param topicObject
	 *            - The object.
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
	public void flushTopicMap() {

		this.config.getConnector().flushTopicMap();
	}

	/**
	 * Returns all object of a specific class which can be retrieved from the topic map. All objects are newly created
	 * and can not be compared to already existing object representing the same topics.
	 * 
	 * @param clazz
	 *            - The class.
	 * @return Set of objects.
	 */
	public <E> Set<E> getAll(Class<E> clazz) throws AranukaException {

		try {

			return getTopicMapHandler().getTopicsByType(clazz);
		} catch (AranukaException e) {
			throw e;
		} catch (Exception e) {
			throw new AranukaException(e);
		}
	}

	/**
	 * Retrieves a specific object from the topic maps, identified by the subject identifier.
	 * 
	 * @param si
	 *            - The subject identifier.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapException
	 */
	@SuppressWarnings("unchecked")
	public <E> E getBySubjectIdentifier(String si) throws AranukaException {
		// returns instance of topic with si as subject identifier
		return (E) getTopicMapHandler().getObjectBySubjectIdentifier(si);

	}

	/**
	 * Retrieves a specific object from the topic maps, identified by the subject locator.
	 * 
	 * @param sl
	 *            - The subject locator.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapException
	 */
	@SuppressWarnings("unchecked")
	public <E> E getBySubjectLocator(String sl) throws AranukaException {
		// returns instance of topic with si as subject locator
		return (E) getTopicMapHandler().getObjectBySubjectLocator(sl);
	}

	/**
	 * Retrieves a specific object from the topic maps, identified by the item identifier. Note: It is not possible to
	 * retrieve associations this way.
	 * 
	 * @param ii
	 *            - The item identifier.
	 * @return The object or null if not found.
	 * @throws TopicMapIOException
	 * @throws TopicMapInconsistentException
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapException
	 */
	@SuppressWarnings("unchecked")
	public <E> E getByItemIdentifier(String ii) throws AranukaException {
		// returns instance of topic with si as item identifier

		return (E) getTopicMapHandler().getObjectByItemIdentifier(ii);
	}

	/**
	 * Removes a specific topic from the topic map.
	 * 
	 * This method removes every construct using the type from the topic map.
	 * 
	 * @param object
	 *            - The object representing the specific topic.
	 * @return True if a topic was removed, otherwise false.
	 * @throws TopicMapException
	 * @throws ClassNotSpecifiedException
	 * @throws NoSuchMethodException
	 * @throws BadAnnotationException
	 */
	public boolean remove(Object object) throws AranukaException {

		boolean result = getTopicMapHandler().removeTopic(object);

		if (result)
			notifyRemove(object);

		return result;
	}

	/**
	 * Count the number of topic of a specific type. NOT YET IMPLEMENTED
	 * 
	 * @param clazz
	 *            - The class representing the type.
	 * @return the number of topics of this type
	 * @throws AranukaException if something went wrong.
	 */
	public long count(Class<?> clazz) throws AranukaException {
		return getTopicMapHandler().countTopics(clazz);
	}

	/**
	 * Clears the object cache of the session. 
	 * @throws AranukaException 
	 */
	public void clearCache() throws AranukaException {
		getTopicMapHandler().clearCache();
	}
	
	/**
	 * Returns the topic map.
	 * 
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
	 * @param listener
	 *            the new listener to add
	 */
	public void addPersistListener(IPersistListener listener) {
		if (listeners == null)
			listeners = new ArrayList<IPersistListener>();
		listeners.add(listener);
	}

	/**
	 * Removes a persist listener
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removePersistListener(IPersistListener listener) {
		if (getPersistListeners().contains(listener))
			listeners.remove(listener);
	}

	/**
	 * Notifies all registered listeners that the given model was persisted.
	 * 
	 * @param model
	 *            the model which was persisted
	 */
	private void notifyPersist(Object model) {
		int size = getPersistListeners().size();
		if (size == 0)
			return;
		IPersistListener[] listeners = getPersistListeners().toArray(new IPersistListener[size]);
		for (IPersistListener l : listeners) {
			l.persisted(model);
		}
	}

	/**
	 * Notifies all registered listeners that the given model was persisted.
	 * 
	 * @param model
	 *            the model which was persisted
	 */
	private void notifyRemove(Object model) {
		int size = getPersistListeners().size();
		if (size == 0)
			return;
		IPersistListener[] listeners = getPersistListeners().toArray(new IPersistListener[size]);
		for (IPersistListener l : listeners) {
			l.removed(model);
		}
	}

	/**
	 * Returns {@link #listeners} which contains the persist listeners or {@link Collections#emptyList()}.
	 * 
	 * @return the list of listeners or an empty list
	 */
	private List<IPersistListener> getPersistListeners() {
		if (listeners == null)
			return Collections.emptyList();
		return listeners;
	}

	/**
	 * Returns the topic map handler. Creates a new on of not existing.
	 * 
	 * @return The topic maps handler.
	 */
	private TopicMapHandler getTopicMapHandler() throws AranukaException {

		if (this.topicMapHandler == null) {
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

	public List<Object> getObjectsByQuery(String tmqlQuery) throws AranukaException {
		return getTopicMapHandler().getObjectsByQuery(tmqlQuery);
	}
}
