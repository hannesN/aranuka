package de.topicmapslab.aranuka.binding;

import java.util.Map;

public class OccurrenceBinding extends AbstractFieldBinding {

	private String occurrenceType;
	private String dataType;

	public OccurrenceBinding(Map<String,String> prefixMap, TopicBinding parent) {
		super(prefixMap, parent);
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
		return "OccurrenceBinding [dataType=" + dataType + ", occurrenceType="
				+ occurrenceType + "]";
	}
}
