package de.topicmapslab.aranuka.codegen.layer.model;

import java.util.Set;

import org.tmapi.core.Locator;

import de.topicmapslab.aranuka.codegen.core.exception.TopicMap2JavaMapperException;

/**
 * 
 * @author Sven Krosse
 *
 */
public interface ITopicMapDAO<T> {

	public T find(Locator locator) throws TopicMap2JavaMapperException;
			
	public Set<T> retrieve() throws TopicMap2JavaMapperException; 
		
	public Class<?> getDAOClass();
}
