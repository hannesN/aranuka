/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.topicmapslab.aranuka.connectors.IEngineConnector;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.AranukaException;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapException;
import de.topicmapslab.aranuka.osgi.RuntimeActivator;
import de.topicmapslab.aranuka.utils.TopicMapsUtils;

public class Configuration {

	/**
	 * Set of annotated classes.
	 */
	private Set<Class<?>> classes;
	
	/**
	 * Map of defined prefixes.
	 */
	private Map<String, String> prefixMap;
	
	/**
	 * Map of topic names. Key is the identifier while the value is the name.
	 */
	private Map<String,String> nameMap;
	
	/**
	 * The actual session.
	 */
	private Session session;

	/**
	 * The used connector.
	 */
	private IEngineConnector connector;
	
	private Properties properties;
	
	/**
	 * Sets a property.
	 * @param key - Property identifier.
	 * @param value - Property value.
	 */
	public void setProperty(String key, String value){
		if (this.properties==null)
			properties = new Properties();
		
		this.properties.setProperty(key, value);
		if (IProperties.BASE_LOCATOR.equals(key)) {
			addPrefix(key, value);
		}
	}

	/**
	 * Returns the connector.
	 */
	IEngineConnector getConnector() {
		if (this.connector == null) {
			String className = getProperty(IProperties.CONNECTOR_CLASS);
			
			if (className==null)
				throw new IllegalStateException("IProperties.CONNECTOR_CLASS property not found.");
			
			try {
				
				// trying to use the activator which throws an Error if the class isn't available which means no SOGi env
				try {
					this.connector = RuntimeActivator.getDefault().getConnector(className);
				} catch (Throwable t) {
					// do nothing
				}

				if (this.connector==null) {
					Class<?> clazz = getClass().getClassLoader().loadClass(className);
					Object obj = null;
					obj = clazz.getConstructor().newInstance();
	
					if (!(obj instanceof IEngineConnector))
						throw new RuntimeException("");
	
					this.connector = (IEngineConnector) obj;
				}
			} catch (Exception e) {
				throw new RuntimeException("Can't instanciate connector.", e);
			}
		}
		this.connector.setConfiguration(this);
		return this.connector;

	}
	
	/**
	 * Adds an annotated class.
	 * @param clazz - The class.
	 */
	public void addClass(Class<?> clazz) {
		if (classes == null) {
			classes = new HashSet<Class<?>>();
		}
		classes.add(clazz);
	}
	
	/**
	 * Returns all configured classes.
	 * @return Set of classes.
	 */
	public Set<Class<?>> getClasses(){
		if (classes==null)
			return Collections.emptySet();
		return classes;
	}

	
	/**
	 * Adds a new prefix to the configuration.
	 * If a prefix is used in the annotation it must be added to configuration, otherwise it will not be possible to resolve the iri.
	 * @param prefix - The predix string.
	 * @param uri - The represented iri.
	 */
	public void addPrefix(String prefix, String uri) {
		if (prefixMap == null)
			prefixMap = new HashMap<String, String>();
		
		if (!(uri.endsWith("/")) || (uri.endsWith("#")))
			uri+="/";
		
		prefixMap.put(prefix, uri);
	}
	
	/**
	 * Returns the prefix map.
	 * @return - The prefix map.
	 */
	public Map<String, String> getPrefixMap() {
		
		if (prefixMap == null)
			return Collections.emptyMap();
		
		return prefixMap;
	}
	
	/**
	 * Returns the session. If no session exist, a new one will be created.
	 * @param lasyBinding - Flag specifying whether lazy binding should be used or not. If not, all bindings for the configured classes will be created while the session is generated.
	 * @return The session.
	 * @throws BadAnnotationException
	 * @throws NoSuchMethodException
	 * @throws ClassNotSpecifiedException
	 * @throws TopicMapException
	 */
	public Session getSession(boolean lasyBinding) throws AranukaException {
		
		if(this.session == null)
			this.session = new Session(this, lasyBinding);

		return this.session;
		
	}

	/**
	 * Adds a topic name.
	 * @param identifier - The identifier of the topic.
	 * @param name - The name value.
	 */
	public void addName(String identifier, String name){
		
		if(this.nameMap == null)
			this.nameMap = new HashMap<String, String>();
		
		this.nameMap.put(identifier, name);
	}
	
	/**
	 * Returns the name for a specific topic.
	 * @param identifier - The identifier of the topic.
	 * @return The name or null if non specified.
	 */
	public String getName(String identifier){
		
		if(this.nameMap == null)
			return null;
		
		String name = this.nameMap.get(identifier);
		
		if(name != null)
			return name;
		
		if(this.getPrefixMap() == null)
			return null;

		for(Map.Entry<String,String> entry:this.nameMap.entrySet()){
			
			if(TopicMapsUtils.resolveURI(entry.getKey(),this.getPrefixMap()).equals(identifier)){
				return entry.getValue();
			}
		}
		
		return null;
		
	}
	
	/**
	 * Sets a name map, will overwrite current specified names.
	 * @param nameMap - The new name map.
	 */
	public void setNameMap(Map<String, String> nameMap) {
	
		this.nameMap = nameMap;
	}

	/**
	 * Sets properties. Will overwrite current specified properties.
	 * @param properties - The properties.
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
		Object val = properties.get(IProperties.BASE_LOCATOR);
		if (val!=null) {
			addPrefix(IProperties.BASE_LOCATOR, (String) val);
		}
	}
	
	/**
	 * Get a specific property.
	 * @param key - The key of the property.
	 * @return The property.
	 */
	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}
	
	
	
}
