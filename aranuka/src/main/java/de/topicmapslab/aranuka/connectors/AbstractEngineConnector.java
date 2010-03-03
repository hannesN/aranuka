package de.topicmapslab.aranuka.connectors;

import java.util.Properties;

public abstract class AbstractEngineConnector implements IEngineConnector {

	private Properties properties;
	
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
	 * @return the properties
	 */
	protected Properties getProperties() {
		return properties;
	}
	
	/**
	 * {@inheritedDoc}
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
