package de.topicmapslab.aranuka.engine;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEngineDriver implements IEngineDriver {

	private Map<String, String> properties;
	
	public void setProperty(String propertyKey, String propertyValue) {
		
		if(this.properties == null)
			this.properties = new HashMap<String, String>();
		
		this.properties.put(propertyKey, propertyValue);
		
	}
	
	protected String getProperty(String key){
		
		if(this.properties == null)
			return null;
		
		return this.properties.get(key);
	}
}
