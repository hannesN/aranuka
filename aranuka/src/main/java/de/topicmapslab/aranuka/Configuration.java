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

import de.topicmapslab.aranuka.connectors.AbstractEngineConnector;
import de.topicmapslab.aranuka.connectors.IEngineConnector;
import de.topicmapslab.aranuka.connectors.IProperties;
import de.topicmapslab.aranuka.exception.BadAnnotationException;
import de.topicmapslab.aranuka.exception.ClassNotSpecifiedException;
import de.topicmapslab.aranuka.exception.TopicMapException;
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
	
	
	/**
	 * Constructor
	 * @param connectorClass - Used connector class.
	 */
	public Configuration(Class<?> connectorClass){
		
		if(connectorClass == null)
			throw new RuntimeException("Connector specification must not be null.");
				
		Object obj = null;
		
		try{
		
			obj = connectorClass.getConstructor().newInstance();

		}catch(Exception e){
			throw new RuntimeException("Can't instanciate connector.", e);
		}
		
		if(!(obj instanceof IEngineConnector))
			throw new RuntimeException("");
		
		this.connector = (IEngineConnector)obj;
		((AbstractEngineConnector) this.connector).setConfiguration(this);
	}
	
	/**
	 * Sets a property.
	 * @param key - Property identifier.
	 * @param value - Property value.
	 */
	public void setProperty(String key, String value){
		
		this.connector.setProperty(key, value);
		if (IProperties.BASE_LOCATOR.equals(key)) {
			addPrefix(key, value);
		}
	}

	/**
	 * Returns the connector.
	 */
 	IEngineConnector getConnector(){
		
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
	public Session getSession(boolean lasyBinding) throws BadAnnotationException, NoSuchMethodException, ClassNotSpecifiedException, TopicMapException{
		
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
		this.connector.setProperties(properties);
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
		return this.connector.getProperty(key);
	}
	
	
	
}
