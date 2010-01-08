package de.topicmapslab.aranuka.binding;


public class OccurrenceBinding extends AbstractFieldBinding {

	private String occurrenceType;
	
	public OccurrenceBinding(TopicBinding parent) {
		super(parent);
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
