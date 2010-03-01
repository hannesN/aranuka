package de.topicmapslab.aranuka.test.annotations;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType13 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;

	// associations
	@Association(played_role="test:topic_type_01_role", other_role="test:counter_player_01_role", type="test:binary_association")
	private Object binaryAssociation;

	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}

	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}

	public Object getBinaryAssociation() {
		return binaryAssociation;
	}

	public void setBinaryAssociation(Object binaryAssociation) {
		this.binaryAssociation = binaryAssociation;
	}
	
	
}
