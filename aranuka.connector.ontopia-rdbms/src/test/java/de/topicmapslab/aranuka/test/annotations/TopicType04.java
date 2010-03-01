package de.topicmapslab.aranuka.test.annotations;

import de.topicmapslab.aranuka.annotations.Id;
import de.topicmapslab.aranuka.annotations.Topic;
import de.topicmapslab.aranuka.enummerations.IdType;

@Topic(subject_identifier="test:topic_type", name="Topic Type")
public class TopicType04 {

	// subject identifier
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String stringSubjectIdentifier;
	
	@Id(type=IdType.SUBJECT_IDENTIFIER)
	private String string2SubjectIdentifier;
		


	public String getStringSubjectIdentifier() {
		return stringSubjectIdentifier;
	}
	public void setStringSubjectIdentifier(String stringSubjectIdentifier) {
		this.stringSubjectIdentifier = stringSubjectIdentifier;
	}
	
	public String getString2SubjectIdentifier() {
		return string2SubjectIdentifier;
	}
	public void setString2SubjectIdentifier(String string2SubjectIdentifier) {
		this.string2SubjectIdentifier = string2SubjectIdentifier;
	}
	
}
