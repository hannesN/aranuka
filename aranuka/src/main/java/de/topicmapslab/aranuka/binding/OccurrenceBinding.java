package de.topicmapslab.aranuka.binding;

import java.util.Map;

import org.tmapi.core.Topic;



public class OccurrenceBinding extends AbstractFieldBinding {

	private String occurrenceType;
	
	public OccurrenceBinding(TopicBinding parent) {
		super(parent);
	}
	
	public void persist(Topic topic, Object topicObject, Map<String,String> prefixMap){
		
	}
	
	public String getOccurrenceType() {
		return occurrenceType;
	}


	public void setOccurrenceType(String occurrenceTypeIdentifier) {
		this.occurrenceType = occurrenceTypeIdentifier;
	}

	@Override
	public String toString() {
		return "OccurrenceBinding [occurrenceType=" + occurrenceType + "]";
	}
	
	
}
