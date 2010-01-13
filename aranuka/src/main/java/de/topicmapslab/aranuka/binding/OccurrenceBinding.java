package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.Map;

import org.tmapi.core.Topic;

public class OccurrenceBinding extends AbstractFieldBinding {

	private String occurrenceType;

	public OccurrenceBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}

	public void persist(Topic topic, Object topicObject) {

		/// TODO can occurrence be an array or collection
		
//		if (this.isArray())
//
//			for (Object obj : (Object[]) this.getValue(topicObject))
//				createOccurrence(topic, obj.toString());
//
//		else if (this.isCollection())
//
//			for (Object obj : (Collection<Object>) this.getValue(topicObject))
//				createOccurrence(topic, obj.toString());
//
//		else

		if (this.getValue(topicObject) != null)
			createOccurrence(topic, this.getValue(topicObject).toString());

	}
	
	private void createOccurrence(Topic topic, String occurrence){
		
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
