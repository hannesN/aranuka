/*******************************************************************************
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 *  
 * @author Christian Haß
 * @author Hannes Niederhausen
 ******************************************************************************/
package de.topicmapslab.aranuka.connectors;

import java.util.Map;
import java.util.Properties;

import de.topicmapslab.aranuka.Configuration;

public abstract class AbstractEngineConnector implements IEngineConnector {

	/**
	 * Properties object.
	 */
	private Properties properties;
	
	/**
	 * Configuration object.
	 */
	private Configuration configuration;
	
	/**
	 * Sets the configuration object.
	 * @param configuration - The configuration.
	 */
	final public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setProperty(String propertyKey, String propertyValue) {
		
		if(this.properties == null)
			this.properties = new Properties();
		
		this.properties.put(propertyKey, propertyValue);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getProperty(String key){
		
		if(this.properties == null)
			return null;
		
		return (String) this.properties.get(key);
	}
	
	/**
	 * {@inheritedDoc}
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * Returns the properties.
	 * @return Properties object.
	 */
	protected Properties getProperties() {
		return properties;
	}

	/**
	 * Returns the prefix map.
	 * @return Prefix map object.
	 */
	protected Map<String, String> getPrefixMap() {
		return configuration.getPrefixMap();
	}
		
}