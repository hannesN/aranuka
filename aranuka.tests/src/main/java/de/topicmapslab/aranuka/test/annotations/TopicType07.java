package de.topicmapslab.aranuka.test.annotations;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Name;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType07 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;
		
	// name
	@Name(type="test:int_name")
	private int intName;

	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}
	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}
	public int getIntName() {
		return intName;
	}
	public void setIntName(int intName) {
		this.intName = intName;
	}
	
	
}
