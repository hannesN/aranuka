package de.topicmapslab.aranuka.test.annotations;

import de.topicmapslab.aranuka.annotations.Association;
import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType12 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;
	
	// associations
	@Association(played_role="test:topic_type_01_role", type="test:unnary_association")
	private int unaryAssociation;

	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}

	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}

	public int getUnaryAssociation() {
		return unaryAssociation;
	}

	public void setUnaryAssociation(int unaryAssociation) {
		this.unaryAssociation = unaryAssociation;
	}
	
	
	
}
