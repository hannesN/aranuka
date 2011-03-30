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
	 * {@inheritDoc}
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
		
	protected String getJDBCDriver(String dbms) {
		if ("h2".equals(dbms))
			return "org.h2.Driver";
		
		if ("postgresql".equals(dbms))
			return "org.postgresql.Driver";
		
		if ("mysql".equals(dbms))
			return "com.mysql.jdbc.Driver";
		
		throw new IllegalArgumentException("Unknown DB String");
	}
	
	public void clearTopicMap(org.tmapi.core.TopicMap topicMap) {
		
		
	};
}
