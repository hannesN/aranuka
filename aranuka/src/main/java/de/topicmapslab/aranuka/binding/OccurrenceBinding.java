package de.topicmapslab.aranuka.binding;

import java.util.Collection;
import java.util.Map;

import org.tmapi.core.Topic;

public class OccurrenceBinding extends AbstractFieldBinding {

	private String occurrenceType;
	private String dataType;

	public OccurrenceBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
	}

	@SuppressWarnings("unchecked")
	public void persist(Topic topic, Object topicObject) {

		if (this.isArray())

			for (Object obj : (Object[]) this.getValue(topicObject))
				createOccurrence(topic, obj.toString());

		else if (this.isCollection())

			for (Object obj : (Collection<Object>) this.getValue(topicObject))
				createOccurrence(topic, obj.toString());

		else

		if (this.getValue(topicObject) != null)
			createOccurrence(topic, this.getValue(topicObject).toString());

	}
	
	private void createOccurrence(Topic topic, String occurrence){
		/// TODO
	}

	public String getOccurrenceType() {
		return occurrenceType;
	}

	public void setOccurrenceType(String occurrenceTypeIdentifier) {
		this.occurrenceType = occurrenceTypeIdentifier;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "OccurrenceBinding [occurrenceType=" + occurrenceType + "]";
	}

}
