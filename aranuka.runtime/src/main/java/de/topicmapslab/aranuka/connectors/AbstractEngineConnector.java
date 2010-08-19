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
	 * {@inheritDoc}
	 */
	public void setProperty(String propertyKey, String propertyValue) {
		
		if(this.properties == null)
			this.properties = new Properties();
		
		this.properties.put(propertyKey, propertyValue);
		
	}
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getProperty(String key){
		
		if(this.properties == null)
			return configuration.getProperty(key);
		
		String result = (String) this.properties.get(key);
		if (result==null)
			return configuration.getProperty(key);
		
		return result;
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
		if (properties==null)
			properties = new Properties();
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
