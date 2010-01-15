package de.topicmapslab.aranuka.binding;

import java.util.Map;

import org.tmapi.core.Topic;

import de.topicmapslab.aranuka.exception.BadAnnotationException;

public abstract class AbstractTopicFieldBinding extends AbstractFieldBinding {

	public AbstractTopicFieldBinding(Map<String,String> prefixMap, AbstractClassBinding parent) {
		super(prefixMap, parent);
	}
	
	// abstract persist method
	public abstract void persist(Topic topic, Object object) throws BadAnnotationException; 
	
}
