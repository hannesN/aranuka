package de.topicmapslab.aranuka.connectors;

import java.util.Map;
import java.util.Properties;

import de.topicmapslab.aranuka.Configuration;

public abstract class AbstractEngineConnector implements IEngineConnector {

	private Properties properties;
	
	private Configuration configuration;
	
	
	final public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setProperty(String propertyKey, String propertyValue) {
		
		if(this.properties == null)
			this.properties = new Properties();
		
		this.properties.put(propertyKey, propertyValue);
		
	}
	
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
	 * @return the properties
	 */
	protected Properties getProperties() {
		return properties;
	}

	protected Map<String, String> getPrefixMap() {
		return configuration.getPrefixMap();
	}
	
	
}
